/*
 * Codename Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.render.texture;

import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.render.utility.Triplet;
import de.matthiasmann.twl.utils.PNGDecoder;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {
    int textureID;
    int length; //describes the amount of loaded textures
    private final int TEXTURE_PACK_RES = 16;
    private HashMap<String, Integer> textureCoordinates;
    int imageLength = 1;

    public TextureLoader(long window) {
        try {
            textureCoordinates = new HashMap<>();
            loadTextures(Config.homeDir + "assets/minecraft/textures/block");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        PNGDecoder decoder = null;
        try {
            decoder = new PNGDecoder(new FileInputStream(
                    Config.homeDir + "assets/allTextures.png"));
            ByteBuffer buf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            textureID = bindTexture(buf, decoder.getWidth(), decoder.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int makeGreen(int rgb) {
        // this method has some bugs but it looks cool so let's just say it is an intended mechanic
        Triplet<Float, Float, Float> rgbValues = getRGBTriplet(rgb);
        float brightness = getBrightness(rgbValues);
        rgbValues = multiply(new Triplet<>(94f / 255f, 157f / 255f, 52f / 255f), brightness);
        return getRGBInt(rgbValues);
    }

    private static Triplet<Float, Float, Float> multiply(Triplet<Float, Float, Float> rgbValues, float value) {
        rgbValues.item1 *= value;
        rgbValues.item2 *= value;
        rgbValues.item3 *= value;
        return rgbValues;
    }

    private static int getRGBInt(Triplet<Float, Float, Float> rgbValues) {
        int red = (int) (rgbValues.item1 * 255);
        int green = (int) (rgbValues.item2 * 255);
        int blue = (int) (rgbValues.item3 * 255);
        return ((red << 16) | (green << 8) | blue);
    }

    static Triplet<Float, Float, Float> getRGBTriplet(int rgb) {
        float red = (float) ((rgb >>> 16) & 0xFF) / 16f;
        float green = (float) ((rgb >> 8) & 0xFF) / 16f;
        float blue = (float) ((rgb) & 0xFF) / 16f;
        return new Triplet<>(red, green, blue);
    }

    private static float getBrightness(Triplet<Float, Float, Float> rgbValues) {
        return .2126f * rgbValues.item1 + .7152f * rgbValues.item2 + .0722f * rgbValues.item3;
    }

    private void loadTextures(String textureFolder) throws IOException {
        // Any animated block will be stationary
        File[] textureFiles = new File(textureFolder).listFiles();

        if (textureFiles == null) {
            throw new IOException("Failed to load textures: Texture folder empty");
        }
        List<Pair<BufferedImage, String>> allTextures = new ArrayList<>();
        for (int i = 0; i < textureFiles.length; i++) {
            String fileName = textureFiles[i].getName();
            String textureName = fileName.substring(0, fileName.lastIndexOf('.'));
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
            if (fileExtension.equals("png")) {
                InputStream textureInputStream = new FileInputStream(textureFiles[i]);
                BufferedImage img = ImageIO.read(textureInputStream);
                allTextures.add(new Pair<>(img, textureName));
            }
            //else we have a .mcmeta file describing animated blocks
        }

        // CONVERT ALL THE IMAGES INTO A SINGLE, VERY LONG IMAGE
        // greatly improves performance in opengl
        // TEXTURE_PACK_RESxTEXTURE_PACK_RES textures only
        length = allTextures.size();

        while (length * TEXTURE_PACK_RES > imageLength) imageLength *= 2; //figure out the right length for the image

        BufferedImage totalImage = new BufferedImage(imageLength, TEXTURE_PACK_RES, BufferedImage.TYPE_4BYTE_ABGR);
        for (int xPos = 0; xPos < length; xPos++) {
            //copy the image into a part of the long image
            BufferedImage img = allTextures.get(xPos).getKey();
            for (int y = 0; y < TEXTURE_PACK_RES; y++) {
                for (int xPixel = 0; xPixel < TEXTURE_PACK_RES; xPixel++) {
                    int rgb = img.getRGB(xPixel, y);
                    if (allTextures.get(xPos).getValue().equals("grass_block_top")) {
                        rgb = makeGreen(rgb);
                    }
                    totalImage.setRGB(xPos * TEXTURE_PACK_RES + xPixel, y, rgb);
                }
            }
            String textureName = allTextures.get(xPos).getValue();
            textureCoordinates.put(textureName, xPos);
        }

        try {
            // save our long image to reload it later
            File outputFile = new File(Config.homeDir + "assets/allTextures.png");
            ImageIO.write(totalImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int bindTexture(ByteBuffer buf, int width, int height) {
        buf.flip();
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width,
                height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
        //disable smoothing out of textures
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        return textureID;
    }

    public Pair<Float, Float> getTexture(String name) {
        // returns the start and end u-coordinatea of a specific texture to access it
        String textureName = name;
        if (textureName.contains("block/"))
            textureName = textureName.substring(textureName.lastIndexOf('/') + 1);

        Integer pos = textureCoordinates.get(textureName);
        if (pos == null) {
            // the texture does not exist
            throw new IllegalArgumentException(String.format("could not find texture %s", textureName));
        }

        return new Pair<Float, Float>(
                (float) pos / ((float) imageLength / (float) TEXTURE_PACK_RES),
                (float) (pos + 1) / ((float) imageLength / (float) TEXTURE_PACK_RES)
        );
    }

    public int getTextureID() {
        return textureID;
    }
}

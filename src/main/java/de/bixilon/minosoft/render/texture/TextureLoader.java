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
import de.matthiasmann.twl.utils.PNGDecoder;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {
    private final int TEXTURE_PACK_RES = 16;
    private final HashMap<String, HashMap<String, Integer>> textureCoordinates;
    int textureID;
    float step;
    int totalTextures = 0;
    HashMap<String, HashMap<String, BufferedImage>> images;

    public TextureLoader(HashMap<String, HashSet<String>> textures, HashMap<String, HashMap<String, float[]>> tints) {
        textureCoordinates = new HashMap<>();
        images = new HashMap<>();
        for (String mod : textures.keySet()) {
            loadTextures(mod, textures.get(mod), tints.get(mod));
        }
        combineTextures();
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(
                    Config.homeDir + "assets/allTextures.png"));
            ByteBuffer buf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            textureID = bindTexture(buf, decoder.getWidth(), decoder.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(5);
        }
    }

    private static void tintImage(BufferedImage image, float[] tintColor) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int r = (int) (color.getRed() * tintColor[0]);
                int g = (int) (color.getGreen() * tintColor[1]);
                int b = (int) (color.getBlue() * tintColor[2]);
                int rgba = (color.getAlpha() << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgba);
            }
        }
    }

    private void loadTextures(String mod, HashSet<String> textureNames, HashMap<String, float[]> tint) {
        HashMap<String, BufferedImage> modTextureMap = new HashMap<>();
        for (String textureName : textureNames) {
            String path = Config.homeDir + "assets/" + mod + "/textures/" + textureName + ".png";
            try {
                BufferedImage image = ImageIO.read(new File(path));
                if (tint.containsKey(textureName)) {
                    tintImage(image, tint.get(textureName));
                }
                modTextureMap.put(textureName, image);
            } catch (IOException e) {
                System.out.println(textureName);
                System.out.println(path);
                e.printStackTrace();
                System.exit(6);
            }
            totalTextures++;
        }
        images.put(mod, modTextureMap);
    }

    private void combineTextures() {
        // CONVERT ALL THE IMAGES INTO A SINGLE, VERY LONG IMAGE
        // greatly improves performance in opengl
        // TEXTURE_PACK_RESxTEXTURE_PACK_RES textures only
        int imageLength = 1;
        while (totalTextures * TEXTURE_PACK_RES > imageLength) {
            imageLength *= 2; //figure out the right length for the image
        }
        BufferedImage totalImage = new BufferedImage(imageLength, TEXTURE_PACK_RES,
                BufferedImage.TYPE_4BYTE_ABGR);

        int currentPos = 0;
        for (Map.Entry<String, HashMap<String, BufferedImage>> mod : images.entrySet()) {
            HashMap<String, Integer> modMap = new HashMap<>();
            for (Map.Entry<String, BufferedImage> texture : mod.getValue().entrySet()) {
                for (int y = 0; y < TEXTURE_PACK_RES; y++) {
                    for (int xPixel = 0; xPixel < TEXTURE_PACK_RES; xPixel++) {
                        int rgb = texture.getValue().getRGB(xPixel, y);
                        totalImage.setRGB(currentPos * TEXTURE_PACK_RES + xPixel, y, rgb);
                    }
                }
                modMap.put(texture.getKey(), currentPos++);
            }
            textureCoordinates.put(mod.getKey(), modMap);
        }

        try {
            File outputFile = new File(Config.homeDir + "assets/allTextures.png");
            ImageIO.write(totalImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        step = (float) 1 / (float) imageLength * TEXTURE_PACK_RES;
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

    public Pair<Float, Float> getTexture(String mod, String textureName) {
        // returns the start and end u-coordinate of a specific texture to access it
        HashMap<String, Integer> modMap = textureCoordinates.get(mod);
        if (modMap == null) {
            System.out.println("no mod " + mod + " loaded");
            System.exit(9);
        }
        Integer pos = modMap.get(textureName);

        if (pos == null) {
            System.out.println("failed to find texture " + textureName);
            System.exit(10);
        }

        return new Pair<>(
                pos * step,
                (pos + 1) * step
        );
    }

    public int getTextureID() {
        return textureID;
    }

    public float getStep() {
        return step;
    }
}

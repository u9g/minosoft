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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {
    Map<String, UVPolygon> blockTextures = new HashMap<>();
    int textureID;
    int length; //describes the amount of loaded textures
    int pow2Length = 1;
    private HashMap<String, Integer> textureCoordinates;


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
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
        textureID = bindTexture(buf, decoder.getWidth(), decoder.getHeight());
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
            //else we have a .mcmeta file
        }

        // CONVERT ALL THE IMAGES INTO A SINGLE, VERY LONG IMAGE
        // greatly improves performance in opengl
        // 16x16 textures only
        length = allTextures.size();

        while (length * 16 > pow2Length) pow2Length *= 2;

        BufferedImage totalImage = new BufferedImage(pow2Length * 16, 16, BufferedImage.TYPE_4BYTE_ABGR);
        for (int xPos = 0; xPos < length; xPos++) {
            //copy the image into a part of the long image
            BufferedImage img = allTextures.get(xPos).getKey();
            for (int y = 0; y < 16; y++) {
                for (int xPixel = 0; xPixel < 16; xPixel++) {
                    int rgb = img.getRGB(xPixel, y);
                    totalImage.setRGB(xPos * 16 + xPixel, y, rgb);
                }
            }
            String textureName = allTextures.get(xPos).getValue();
            textureCoordinates.put(textureName, xPos);
        }

        try {
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
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width,
                height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
        //disable smoothing out of textures
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        return textureID;
    }

    public Pair<Float, Float> getTexture(String name) {
        Integer pos = textureCoordinates.get(name);
        return new Pair<Float, Float>(
                (float) pos / (float) pow2Length,
                (float) (pos + 1) / (float) pow2Length
        );
    }

    public int getTextureID() {
        return textureID;
    }
}

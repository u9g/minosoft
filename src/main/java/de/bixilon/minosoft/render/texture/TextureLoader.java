/*
 * Minosoft
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

import de.bixilon.minosoft.config.StaticConfiguration;
import de.bixilon.minosoft.data.assets.AssetsManager;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;
import de.matthiasmann.twl.utils.PNGDecoder;

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
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {
    private final HashMap<String, Integer> textureCoordinates;
    private final CountDownLatch countDownLatch;
    private HashMap<String, BufferedImage> images;
    private int textureID;
    private float step;
    private int totalTextures = 0;

    public TextureLoader(HashSet<String> textures, HashMap<String, float[]> tints) {
        countDownLatch = new CountDownLatch(1);
        textureCoordinates = new HashMap<>();
        loadTextures(textures, tints);
        combineTextures();
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(StaticConfiguration.HOME_DIRECTORY + "assets/allTextures.png"));
            ByteBuffer buf = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            bindTexture(buf, decoder.getWidth(), decoder.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(5);
        }
    }

    private static void tintImage(BufferedImage image, float[] tintColor) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y), true);
                int r = (int) (color.getRed() * tintColor[0]);
                int g = (int) (color.getGreen() * tintColor[1]);
                int b = (int) (color.getBlue() * tintColor[2]);
                int rgba = (color.getAlpha() << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgba);
            }
        }
    }

    private void loadTextures(HashSet<String> textureNames, HashMap<String, float[]> tint) {
        HashMap<String, BufferedImage> modTextureMap = new HashMap<>();
        for (String textureName : textureNames) {
            if (textureName.contains("overlay") || textureName.isBlank()) {
                continue;
            }
            String fileName = textureName;
            if (fileName.startsWith("blocks")) {
                fileName = "block" + fileName.substring("blocks".length());
            }
            try {
                BufferedImage image = ImageIO.read(AssetsManager.readAssetAsStream(String.format("minecraft/textures/%s.png", fileName))); // TODO: modding
                if (tint != null && tint.containsKey(textureName)) {
                    tintImage(image, tint.get(textureName));
                }
                modTextureMap.put(textureName, image);
            } catch (IOException e) {
                Log.fatal(String.format("An error occurred while loading texture %s (fileName=%s): %s", textureName, fileName, e.getLocalizedMessage()));
                e.printStackTrace();
                System.exit(6);
            }
            totalTextures++;
        }
        images = modTextureMap;
    }

    private void combineTextures() {
        // converts all single textures into a very wide image. Improves performance in opengl
        // TEXTURE_PACK_RESxTEXTURE_PACK_RES textures only
        int imageLength = Integer.highestOneBit(totalTextures * RenderConstants.TEXTURE_PACK_RESOLUTION) * 2;
        BufferedImage totalImage = new BufferedImage(imageLength, RenderConstants.TEXTURE_PACK_RESOLUTION, BufferedImage.TYPE_4BYTE_ABGR);

        int currentPos = 0;
        for (Map.Entry<String, BufferedImage> texture : images.entrySet()) {
            for (int y = 0; y < RenderConstants.TEXTURE_PACK_RESOLUTION; y++) {
                for (int xPixel = 0; xPixel < RenderConstants.TEXTURE_PACK_RESOLUTION; xPixel++) {
                    int rgb = texture.getValue().getRGB(xPixel, y);
                    totalImage.setRGB(currentPos * RenderConstants.TEXTURE_PACK_RESOLUTION + xPixel, y, rgb);
                }
            }
            textureCoordinates.put(texture.getKey(), currentPos++);
        }

        try {
            File outputFile = new File(StaticConfiguration.HOME_DIRECTORY + "assets/allTextures.png");
            ImageIO.write(totalImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        step = (float) 1 / (float) imageLength * RenderConstants.TEXTURE_PACK_RESOLUTION;
    }

    private void bindTexture(ByteBuffer buf, int width, int height) {
        buf.flip();
        GameWindow.queue(() -> {
            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);
            //disable smoothing out of textures
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            this.textureID = textureID;
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public float getTexture(String textureName) {
        if (textureName.contains("overlay") || textureName.isBlank()) {
            return -1;
        }
        // returns the start and end u-coordinate of a specific texture to access it
        Integer pos = textureCoordinates.get(textureName);
        return pos * step;
    }

    public int getTextureID() {
        return textureID;
    }

    public float getStep() {
        return step;
    }
}

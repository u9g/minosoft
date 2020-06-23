package de.bixilon.minosoft.render;

import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.logging.Log;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {
    Map<String, Integer> blockTextures = new HashMap<>();

    public TextureLoader(long window) {

        try {
            glfwMakeContextCurrent(window);
            loadTextures(Config.homeDir + "assets/minecraft/textures/block");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTextures(String textureFolder) throws IOException {
        File[] textureFiles = new File(textureFolder).listFiles();

        if (textureFiles == null) {
            throw new IOException("Failed to load textures: Texture folder empty");
        }

        for (File textureFile : textureFiles) {
            String filename = textureFile.getName();
            String fileExtension = filename.substring(filename.lastIndexOf('.') + 1);
            if (fileExtension.equals("png")) {
                InputStream textureInputStream = new FileInputStream(textureFile);
                try {
                    PNGDecoder decoder = new PNGDecoder(textureInputStream);
                    ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
                    decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
                    buf.flip();
                    int textureID = glGenTextures();
                    glBindTexture(GL_TEXTURE_2D, textureID);
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                            decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
                    glGenerateMipmap(GL_TEXTURE_2D);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    // get the filename without extension
                    String textureName = filename.replaceFirst("[.][^.]+$", "");
                    blockTextures.put("block/" + textureName, textureID);
                } catch (Exception e) {
                    //The file is a .mcmeta file
                    // TODO: Parse metatdata files
                }
            }
        }
        Log.game(String.format("Loaded %d textures", blockTextures.size()));
    }

    public int getTexture(String name) {
        return blockTextures.get(name);
    }
}

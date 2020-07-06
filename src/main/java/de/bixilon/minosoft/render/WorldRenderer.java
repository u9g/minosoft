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

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.game.datatypes.blocks.Block;
import de.bixilon.minosoft.game.datatypes.world.*;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.face.FaceOrientation;
import de.bixilon.minosoft.render.face.FacePosition;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.Map;

import static de.bixilon.minosoft.render.face.RenderConstants.faceDir;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public class WorldRenderer {
    private final TextureLoader textureLoader;

    private final HashMap<FacePosition, String> faces;
    private final int vbo;
    private final ArrayFloatList vertPos;
    private final ArrayFloatList textPos;

    public WorldRenderer() {
        textureLoader = new TextureLoader(MainWindow.getOpenGLWindow().getWindow());
        faces = new HashMap<>();
        vertPos = new ArrayFloatList();
        textPos = new ArrayFloatList();
        vbo = glGenBuffers();
    }

    public void prepareChunkBulk(HashMap<ChunkLocation, Chunk> chunks) {
        for (Map.Entry<ChunkLocation, Chunk> set : chunks.entrySet()) {
            prepareChunk(set.getKey(), set.getValue());
        }
    }

    public void prepareChunk(ChunkLocation location, Chunk chunk) {
        if (Math.abs(location.getX()) > 1 | Math.abs(location.getZ()) > 1) {
            return;
        }
        int xOffset = location.getX() * 16;
        int zOffset = location.getZ() * 16;
        for (Map.Entry<Byte, ChunkNibble> set : chunk.getNibbles().entrySet()) {
            for (Map.Entry<ChunkNibbleLocation, Block> blockEntry : set.getValue().getBlocks().entrySet()) {
                prepareBlock(new BlockPosition(blockEntry.getKey().getX() + xOffset,
                                (short) (blockEntry.getKey().getY() + set.getKey() * 16), blockEntry.getKey().getZ() + zOffset),
                        blockEntry.getValue());
            }


            // only gives nibbles with at least 1 block inside (not air)
            /*
            for (byte x = 0; x < 16; x++) {
                for (byte y = 0; y < 16; y++) {
                    for (byte z = 0; z < 16; z++) {
                        Block block = set.getValue().getBlock(x, y, z);

                        prepareBlock(new BlockPosition(xOffset + x,
                                        (short) (set.getKey() * 16 + y), zOffset + z),
                                        block);
                    }
                }
            }
             */
        }
    }

    public void prepareBlock(BlockPosition position, Block block) {
        if (block == Block.AIR)
            return;

        for (FaceOrientation orientation : FaceOrientation.values()) {
            BlockPosition neighbour = position.add(faceDir[orientation.getId()]);

            if (neighbour.getY() >= 0)
                if (MainWindow.getConnection().getPlayer().getWorld().getBlock(neighbour) != Block.AIR)
                    continue;

            FacePosition facePosition = new FacePosition(position, orientation);
            if (!faces.containsKey(facePosition)) {
                synchronized (faces) {
                    faces.put(facePosition, "dirt");
                    facePosition.addVertecies(vertPos, textPos,
                            textureLoader.getTexture("dirt"));
                }
            }
        }
    }
    /*
    private void drawFullFace(FacePosition position, String textureName) {
        int textureID = textureLoader.getTexture(textureName);
        BlockPosition blockPosition = position.getBlockPosition();
        glPushMatrix();
        glTranslatef(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        glBindTexture(GL_TEXTURE_2D, textureID);
        glBegin(GL_QUADS);
        float[][] vertex = FACE_VERTEX[position.getFaceOrientation().getId()];
        for (int i = 0; i < 4; i++) {
            glTexCoord2f(UV[i][0], UV[i][1]);
            glVertex3f(vertex[i][0], vertex[i][1], vertex[i][2]);
        }
        glEnd();
        glPopMatrix();
        glFlush();
    }
     */

    public void draw() {
        synchronized (faces) {
            double start = glfwGetTime();
            glPushMatrix();
            //test();

            /*
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            float[] floatArray = vertices.toArray();
            glBufferData(GL_ARRAY_BUFFER,
                    (FloatBuffer) BufferUtils.createFloatBuffer(vertices.size()).put(
                            floatArray).flip(), GL_STATIC_DRAW);

            glBindTexture(GL_TEXTURE_2D, textureLoader.getTextureID());
            glVertexPointer(3, GL_FLOAT, 28, 0L);
            glTexCoordPointer(2, GL_FLOAT,28, 0L);
            glDrawArrays(GL_QUADS, 0, floatArray.length);
             */
            //glPushMatrix();
            //glEnable(GL_TEXTURE_2D);
            //glColor3f (1.0f, 0.0f, 0.0f);
            //glEnable(GL_TEXTURE_2D);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glBegin(GL_QUADS);
            glBindTexture(GL_TEXTURE_2D, textureLoader.getTextureID());

            for (int i = 0; i < textPos.size() / 2; i++) {
                glTexCoord2f(textPos.get(i * 2), textPos.get(i * 2 + 1));
                glVertex3f(vertPos.get(i * 3), vertPos.get(i * 3 + 1),
                        vertPos.get(i * 3 + 2));
            }
            glEnd();

            double time = glfwGetTime() - start;
            if (time > 1) {
                Log.warn(String.format("frame took: %s s amount of faces drawn: %d", time, faces.size()));
            }
        }
    }
}

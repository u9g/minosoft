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
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.game.datatypes.world.Chunk;
import de.bixilon.minosoft.game.datatypes.world.ChunkLocation;
import de.bixilon.minosoft.game.datatypes.world.ChunkNibble;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.face.FaceOrientation;
import de.bixilon.minosoft.render.face.FacePosition;

import java.util.HashMap;
import java.util.Map;

import static de.bixilon.minosoft.render.RenderConstants.FACE_VERTEX;
import static de.bixilon.minosoft.render.RenderConstants.UV;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final TextureLoader textureLoader;

    private final HashMap<FacePosition, String> faces;

    public WorldRenderer() {
        textureLoader = new TextureLoader(MainWindow.getOpenGLWindow().getWindow());
        faces = new HashMap<>();
    }

    public void prepareChunkBulk(HashMap<ChunkLocation, Chunk> chunks) {
        for (Map.Entry<ChunkLocation, Chunk> set : chunks.entrySet()) {
            prepareChunk(set.getKey(), set.getValue());
        }
    }

    public void prepareChunk(ChunkLocation location, Chunk chunk) {
        if (Math.abs(location.getX()) < 2 && Math.abs(location.getZ()) < 2) {
            return;
        }
        int xOffset = location.getX() * 16;
        int zOffset = location.getZ() * 16;
        for (Map.Entry<Byte, ChunkNibble> set : chunk.getNibbles().entrySet()) {
            // only gives nibbles with at least 1 block inside (not air)
            for (byte x = 0; x < 16; x++) {
                for (byte y = 0; y < 16; y++) {
                    for (byte z = 0; z < 16; z++) {
                        Block block = set.getValue().getBlock(x, y, z);
                        if (block == Block.AIR) {
                            continue;
                        }
                        prepareBlock(new BlockPosition(xOffset + x,
                                        (short) (set.getKey() * 16 + y), zOffset + z),
                                block);
                    }
                }
            }
        }
    }

    public void prepareBlock(BlockPosition position, Block block) {
        for (FaceOrientation orientation : FaceOrientation.values()) {
            FacePosition facePosition = new FacePosition(position, orientation);
            if (!faces.containsKey(facePosition)) {
                synchronized (faces) {
                    faces.put(facePosition, "block/dirt");
                }
            }
        }
    }

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

    public void draw() {
        synchronized (faces) {
            double start = glfwGetTime();

            for (Map.Entry<FacePosition, String> entry : faces.entrySet()) {
                drawFullFace(entry.getKey(), entry.getValue());
            }

            double time = glfwGetTime() - start;
            if (time > 1) {
                Log.warn(String.format("frame took: %s s amount of faces drawn: %d", time, faces.size()));
            }
        }
    }
}

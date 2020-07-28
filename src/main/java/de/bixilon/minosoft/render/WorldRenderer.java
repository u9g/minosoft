/*
 * Codename Minosoft
 * Copyright (C) 2020 Lukas Eisenhauer
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

import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;
import de.bixilon.minosoft.game.datatypes.world.*;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.BlockModelLoader;
import de.bixilon.minosoft.render.fullFace.FaceOrientation;
import de.bixilon.minosoft.render.fullFace.FullFacePosition;
import de.bixilon.minosoft.render.fullFace.RenderConstants;
import de.bixilon.minosoft.render.texture.TextureLoader;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

import static de.bixilon.minosoft.render.fullFace.RenderConstants.UV;
import static de.bixilon.minosoft.render.fullFace.RenderConstants.faceDir;
import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final TextureLoader textureLoader;
    private final HashMap<FullFacePosition, Pair<Float, Float>> faces;
    private final int faceCount = 0;
    private BlockModelLoader modelLoader;

    public WorldRenderer() {
        textureLoader = new TextureLoader(MainWindow.getOpenGLWindow().getWindow());
        faces = new HashMap<FullFacePosition, Pair<Float, Float>>();
    }

    public void init() {
        modelLoader = new BlockModelLoader();
        Log.info("finished loading textures");
    }

    public void prepareChunkBulk(HashMap<ChunkLocation, Chunk> chunks) {
        for (Map.Entry<ChunkLocation, Chunk> set : chunks.entrySet()) {
            prepareChunk(set.getKey(), set.getValue());
        }
    }

    public void prepareChunk(ChunkLocation location, Chunk chunk) {
        int xOffset = location.getX() * 16;
        int zOffset = location.getZ() * 16;
        for (Map.Entry<Byte, ChunkNibble> set : chunk.getNibbles().entrySet()) {
            for (Map.Entry<ChunkNibbleLocation, Block> blockEntry : set.getValue().getBlocks().entrySet()) {
                prepareBlock(new BlockPosition(blockEntry.getKey().getX() + xOffset,
                                (short) (blockEntry.getKey().getY() + set.getKey() * 16),
                                blockEntry.getKey().getZ() + zOffset),
                        blockEntry.getValue());
            }
        }
    }

    public void prepareBlock(BlockPosition position, Block block) {
        if (block == Blocks.nullBlock)
            return;

        for (FaceOrientation orientation : FaceOrientation.values()) {
            BlockPosition neighbourPos = position.add(faceDir[orientation.getId()]);

            if (neighbourPos.getY() >= 0) {
                Block neighbourBlock = MainWindow.getConnection().getPlayer().getWorld().getBlock(neighbourPos);
                if (!(neighbourBlock == Blocks.nullBlock || neighbourBlock == null)) //!modelLoader.isFull(neighbourBlock))
                    // if there is a block next to the current block, don't draw the face
                    continue;
                //TODO: fix buggy behavior, not always working correctly, probably a problem in the World or BlockPosition class
            }
            /*
            FullFacePosition facePosition = new FullFacePosition(position, orientation);
            Pair<Float, Float> texture = modelLoader.getBlockDescription(block).getTexture(orientation);
            if (!faces.containsKey(facePosition)) {
                synchronized (faces) {
                    faces.put(facePosition, texture);
                }
            }
             */
        }
    }

    public void draw() {
        glPushMatrix();
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
        glBindTexture(GL_TEXTURE_2D, textureLoader.getTextureID());
        glBegin(GL_QUADS);
        synchronized (faces) {
            for (Map.Entry<FullFacePosition, Pair<Float, Float>> entry : faces.entrySet()) {
                float[][] vertPositions = RenderConstants.FACE_VERTEX[entry.getKey().getFaceOrientation().getId()];

                for (int vert = 0; vert < 4; vert++) {
                    float u = 0;
                    switch (UV[vert][0]) {
                        case 0:
                            u = entry.getValue().getKey();
                            break;
                        case 1:
                            u = entry.getValue().getValue();
                            break;
                    }
                    float x = vertPositions[vert][0] + entry.getKey().getBlockPosition().getX();
                    float y = vertPositions[vert][1] + entry.getKey().getBlockPosition().getY();
                    float z = vertPositions[vert][2] + entry.getKey().getBlockPosition().getZ();

                    glTexCoord2f(u, UV[vert][1]);
                    glVertex3f(x, y, z);
                }
            }
        }
        glEnd();
        glPopMatrix();
    }

    public TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public BlockModelLoader getModelLoader() {
        return modelLoader;
    }
}

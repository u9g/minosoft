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
import de.bixilon.minosoft.render.blockModels.Face;
import de.bixilon.minosoft.render.fullFace.FaceOrientation;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static de.bixilon.minosoft.render.fullFace.RenderConstants.faceDir;
import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final TextureLoader textureLoader;
    private final HashMap<BlockPosition, HashSet<Face>> faces;
    private final int faceCount = 0;
    private BlockModelLoader modelLoader;

    public WorldRenderer() {
        textureLoader = new TextureLoader(MainWindow.getOpenGLWindow().getWindow());
        faces = new HashMap<>();
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
        if (block.equals(Blocks.nullBlock))
            faces.put(position, null);
        HashMap<FaceOrientation, Boolean> adjacentBlocks = new HashMap<>();

        for (FaceOrientation orientation : FaceOrientation.values()) {
            BlockPosition neighbourPos = position.add(faceDir[orientation.getId()]);

            if (neighbourPos.getY() >= 0) {
                Block neighbourBlock = MainWindow.getConnection().getPlayer().getWorld().getBlock(neighbourPos);
                boolean isNeighbourFull = modelLoader.isFull(neighbourBlock);
                adjacentBlocks.put(orientation, isNeighbourFull);
            } else {
                adjacentBlocks.put(orientation, false);
            }
        }
        synchronized (faces) {
            faces.put(position, modelLoader.prepare(block, adjacentBlocks));
        }
    }

    public void draw() {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, textureLoader.getTextureID());
        glBegin(GL_QUADS);
        synchronized (faces) {
            for (Map.Entry<BlockPosition, HashSet<Face>> entry : faces.entrySet()) {
                for (Face face : entry.getValue()) {
                    face.draw(entry.getKey());
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

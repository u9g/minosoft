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
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static de.bixilon.minosoft.render.blockModels.Face.RenderConstants.faceDir;
import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final HashMap<BlockPosition, HashSet<Face>> faces;
    private AssetsLoader assetsLoader;

    private LinkedBlockingQueue<Pair<ChunkLocation, Chunk>> queuedChunks;

    public WorldRenderer() {
        faces = new HashMap<>();
    }

    public void init() {
        queuedChunks = new LinkedBlockingQueue<>();
        Thread chunkLoadThread = new Thread(() -> {
            while (GameWindow.getConnection() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (true) {
                try {
                    Pair<ChunkLocation, Chunk> current = queuedChunks.take();
                    prepareChunk(current.getKey(), current.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        chunkLoadThread.setName(String.format("%d/ChunkLoading", 0)); // TODO: connection ID
        chunkLoadThread.start();
        assetsLoader = new AssetsLoader();
    }

    public void queueChunkBulk(HashMap<ChunkLocation, Chunk> chunks) {
        for (Map.Entry<ChunkLocation, Chunk> set : chunks.entrySet()) {
            queueChunk(set.getKey(), set.getValue());
        }
    }

    public void queueChunk(ChunkLocation location, Chunk chunk) {
        queuedChunks.add(new Pair<>(location, chunk));
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
        if (block.equals(Blocks.nullBlock)) {
            faces.put(position, null);
        }
        HashMap<FaceOrientation, Boolean> adjacentBlocks = new HashMap<>();

        for (FaceOrientation orientation : FaceOrientation.values()) {
            BlockPosition neighbourPos = position.add(faceDir[orientation.getId()]);

            if (neighbourPos.getY() >= 0 && neighbourPos.getY() <= 255) {
                Block neighbourBlock = GameWindow.getConnection().getPlayer().getWorld().getBlock(neighbourPos);
                boolean isNeighbourFull = assetsLoader.getBlockModelLoader().isFull(neighbourBlock);
                adjacentBlocks.put(orientation, isNeighbourFull);
            } else {
                adjacentBlocks.put(orientation, false);
            }
        }
        synchronized (faces) {
            faces.put(position, assetsLoader.getBlockModelLoader().prepare(block, adjacentBlocks));
        }
    }

    public void draw() {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, assetsLoader.getTextureLoader().getTextureID());
        glBegin(GL_QUADS);
        synchronized (faces) {
            for (Map.Entry<BlockPosition, HashSet<Face>> entry : faces.entrySet()) {
                for (Face face : entry.getValue()) {
                    face.draw(entry.getKey());
                }
            }
        }
        glEnd();
    }

    public AssetsLoader getAssetsLoader() {
        return assetsLoader;
    }
}

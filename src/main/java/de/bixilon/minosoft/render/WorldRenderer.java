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
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import javafx.util.Pair;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final ConcurrentHashMap<ChunkLocation, ConcurrentHashMap<Byte, ArrayFloatList>> faces;
    private AssetsLoader assetsLoader;

    private LinkedBlockingQueue<Pair<ChunkLocation, Chunk>> queuedChunks;

    public WorldRenderer() {
        faces = new ConcurrentHashMap<>();
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
        chunks.forEach(this::queueChunk);
    }

    public void queueChunk(ChunkLocation location, Chunk chunk) {
        queuedChunks.add(new Pair<>(location, chunk));
    }

    public void prepareChunk(ChunkLocation location, Chunk chunk) {
        // clear or create current chunk
        faces.put(location, new ConcurrentHashMap<>());
        chunk.getNibbles().forEach(((height, chunkNibble) -> {
            prepareChunkNibble(location, height, chunkNibble);
        }));
    }

    public void prepareChunkNibble(ChunkLocation chunkLocation, byte height, ChunkNibble nibble) {
        // clear or create current chunk nibble
        ArrayFloatList nibbleMap = new ArrayFloatList();
        faces.get(chunkLocation).put(height, nibbleMap);
        HashMap<ChunkNibbleLocation, Block> nibbleBlocks = nibble.getBlocks();
        nibbleBlocks.forEach((location, block) -> {
            HashSet<FaceOrientation> facesToDraw = new HashSet<>();
            if (block.equals(Blocks.nullBlock)) {
                return;
            }
            for (FaceOrientation orientation : FaceOrientation.values()) {
                if ((location.getX() == 0 && orientation == FaceOrientation.WEST) || (location.getX() == 15 && orientation == FaceOrientation.EAST)) {
                    facesToDraw.add(orientation);
                    continue;
                }
                if ((location.getY() == 0 && orientation == FaceOrientation.DOWN) || (location.getY() == 15 && orientation == FaceOrientation.UP)) {
                    facesToDraw.add(orientation);
                    continue;
                }
                if ((location.getZ() == 0 && orientation == FaceOrientation.NORTH) || (location.getZ() == 15 && orientation == FaceOrientation.SOUTH)) {
                    facesToDraw.add(orientation);
                    continue;
                }
                boolean isNeighbourFull = switch (orientation) {
                    case DOWN -> assetsLoader.getBlockModelLoader().isFull(nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY() - 1, location.getZ())));
                    case UP -> assetsLoader.getBlockModelLoader().isFull(nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY() + 1, location.getZ())));
                    case WEST -> assetsLoader.getBlockModelLoader().isFull(nibbleBlocks.get(new ChunkNibbleLocation(location.getX() - 1, location.getY(), location.getZ())));
                    case EAST -> assetsLoader.getBlockModelLoader().isFull(nibbleBlocks.get(new ChunkNibbleLocation(location.getX() + 1, location.getY(), location.getZ())));
                    case NORTH -> assetsLoader.getBlockModelLoader().isFull(nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY(), location.getZ() - 1)));
                    case SOUTH -> assetsLoader.getBlockModelLoader().isFull(nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY(), location.getZ() + 1)));
                };
                if (!isNeighbourFull) {
                    facesToDraw.add(orientation);
                }
            }
            if (facesToDraw.size() > 0) {
                faces.get(chunkLocation).put(height, assetsLoader.getBlockModelLoader().prepare(block, facesToDraw, new BlockPosition(chunkLocation, height, location)));
                if (!location.equals(new ChunkNibbleLocation(0,0,0))) {
                    Log.debug(".");
                }
            }
    });
}


    public void draw() {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, assetsLoader.getTextureLoader().getTextureID());
        glBegin(GL_QUADS);
        for (ConcurrentHashMap<Byte, ArrayFloatList> chunk : faces.values()) {
            for (ArrayFloatList nibble : chunk.values()) {
                float[] array = nibble.toArray();
                for (int i = 0; i < array.length; i+=5) {
                    glTexCoord2f(array[i], array[i+1]);
                    glVertex3f(array[i+2], array[i+3], array[i+4]);
                }
            }
        }
        glEnd();
    }

    public AssetsLoader getAssetsLoader() {
        return assetsLoader;
    }
}

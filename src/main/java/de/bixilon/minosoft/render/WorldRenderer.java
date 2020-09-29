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
import de.bixilon.minosoft.game.datatypes.world.*;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final ConcurrentHashMap<ChunkLocation, ConcurrentHashMap<Byte, ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>>>> faces = new ConcurrentHashMap<>();
    private AssetsLoader assetsLoader;

    private LinkedBlockingQueue<Pair<ChunkLocation, Chunk>> queuedChunks;


    public void init() {
        queuedChunks = new LinkedBlockingQueue<>();
        assetsLoader = new AssetsLoader();
    }

    public void startChunkPreparation(Connection connection) {
        Thread chunkLoadThread = new Thread(() -> {
            while (true) {
                try {
                    Pair<ChunkLocation, Chunk> current = queuedChunks.take();
                    prepareChunk(current.getKey(), current.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        chunkLoadThread.setName(String.format("%d/ChunkLoading", connection.getConnectionId()));
        chunkLoadThread.start();
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
        chunk.getNibbles().forEach(((height, chunkNibble) -> prepareChunkNibble(location, height, chunkNibble)));
    }

    public void prepareChunkNibble(ChunkLocation chunkLocation, byte height, ChunkNibble nibble) {
        // clear or create current chunk nibble
        ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>> nibbleMap = new ConcurrentHashMap<>();
        faces.get(chunkLocation).put(height, nibbleMap);
        HashMap<ChunkNibbleLocation, Block> nibbleBlocks = nibble.getBlocks();
        nibbleBlocks.forEach((location, block) -> {
            HashSet<FaceOrientation> facesToDraw = new HashSet<>();

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
                //BlockPosition neighbourPos = location.add(faceDir[orientation.ordinal()]);
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
                nibbleMap.put(location, assetsLoader.getBlockModelLoader().prepare(block, facesToDraw));
            }

        });
    }


    public void draw() {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, assetsLoader.getTextureLoader().getTextureID());
        glBegin(GL_QUADS);
        faces.forEach((chunkLocation, nibbleMap) -> nibbleMap.forEach((height, faceMap) -> faceMap.forEach(((nibbleLocation, faces) -> faces.forEach((face -> face.draw(new BlockPosition(chunkLocation, height, nibbleLocation))))))));
        glEnd();
    }

    public AssetsLoader getAssetsLoader() {
        return assetsLoader;
    }
}

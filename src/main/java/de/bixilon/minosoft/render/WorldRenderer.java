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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final ConcurrentHashMap<ChunkLocation, ConcurrentHashMap<Byte, ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>>>> faces = new ConcurrentHashMap<>();
    private AssetsLoader assetsLoader;

    private LinkedBlockingQueue<Pair<ChunkLocation, Chunk>> queuedChunks;

    public int getCountOfFaces() {
        AtomicInteger count = new AtomicInteger();
        faces.forEach((chunkLocation, nibbleMap) -> nibbleMap.forEach((height, faceMap) -> faceMap.forEach(((nibbleLocation, faces) -> faces.forEach((face -> count.incrementAndGet()))))));
        return count.get();
    }

    public void init() {
        queuedChunks = new LinkedBlockingQueue<>();
        assetsLoader = new AssetsLoader();
    }

    public void startChunkPreparation(Connection connection) {
        Thread chunkLoadThread = new Thread(() -> {
            while (true) {
                try {
                    Pair<ChunkLocation, Chunk> current = queuedChunks.take();
                    prepareChunk(current.getKey(), current.getValue(), true);
                    //Log.verbose(String.format("Count of faces: %d", getCountOfFaces()));
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

    public void prepareChunk(ChunkLocation location, Chunk chunk, boolean checkEdges) {
        // clear or create current chunk
        ConcurrentHashMap<Byte, ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>>> chunkFaces = new ConcurrentHashMap<>();
        chunk.getNibbles().forEach(((height, chunkNibble) -> chunkFaces.put(height, getFacesForChunkNibble(location, height, chunkNibble))));
        faces.put(location, chunkFaces);
        if (!checkEdges) {
            return;
        }
        //ToDo

    }

    public ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>> getFacesForChunkNibble(ChunkLocation chunkLocation, byte sectionHeight, ChunkNibble nibble) {
        ConcurrentHashMap<ChunkLocation, Chunk> world = GameWindow.getConnection().getPlayer().getWorld().getAllChunks();
        // clear or create current chunk nibble
        ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>> nibbleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<ChunkNibbleLocation, Block> nibbleBlocks = nibble.getBlocks();
        for (Map.Entry<ChunkNibbleLocation, Block> entry : nibbleBlocks.entrySet()) {
            ChunkNibbleLocation location = entry.getKey();
            Block block = entry.getValue();
            HashSet<FaceOrientation> facesToDraw = new HashSet<>();

            for (FaceOrientation orientation : FaceOrientation.values()) {
                Block dependedBlock = switch (orientation) {
                    case DOWN -> {
                        if (location.getY() == 0) {
                            // need to check upper section (nibble)
                            if (sectionHeight == 0) {
                                // y = 0, there can't be any blocks below me
                                yield null;
                            }
                            // check if block over us is a full block
                            byte bottomSection = (byte) (sectionHeight - 1);
                            if (!world.get(chunkLocation).getNibbles().containsKey(bottomSection)) {
                                yield null;
                            }
                            yield world.get(chunkLocation).getNibbles().get(bottomSection).getBlock(location.getX(), 15, location.getZ());
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY() - 1, location.getZ()));
                    }
                    case UP -> {
                        if (location.getY() == 15) {
                            // need to check upper section (nibble)
                            if (sectionHeight == 15) {
                                // y = 255, there can't be any blocks above me
                                yield null;
                            }
                            // check if block over us is a full block
                            byte upperSection = (byte) (sectionHeight + 1);
                            if (!world.get(chunkLocation).getNibbles().containsKey(upperSection)) {
                                yield null;
                            }
                            yield world.get(chunkLocation).getNibbles().get(upperSection).getBlock(location.getX(), 0, location.getZ());
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY() + 1, location.getZ()));
                    }
                    case WEST -> {
                        if (location.getX() == 0) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX() - 1, chunkLocation.getZ()), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(15, location.getY(), location.getZ());
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX() - 1, location.getY(), location.getZ()));
                    }
                    case EAST -> {
                        if (location.getX() == 15) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX() + 1, chunkLocation.getZ()), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(0, location.getY(), location.getZ());
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX() + 1, location.getY(), location.getZ()));
                    }
                    case NORTH -> {
                        if (location.getZ() == 0) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX(), chunkLocation.getZ() - 1), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(location.getX(), location.getY(), 15);
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY(), location.getZ() - 1));
                    }
                    case SOUTH -> {
                        if (location.getZ() == 15) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX(), chunkLocation.getZ() + 1), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(location.getX(), location.getY(), 0);
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY(), location.getZ() + 1));
                    }
                };
                if (dependedBlock == null || !assetsLoader.getBlockModelLoader().isFull(dependedBlock)) {
                    facesToDraw.add(orientation);
                }
            }
            if (facesToDraw.size() > 0) {
                nibbleMap.put(location, assetsLoader.getBlockModelLoader().prepare(block, facesToDraw));
            }

        }
        return nibbleMap;
    }

    public void prepareChunkNibble(ChunkLocation chunkLocation, byte sectionHeight, ChunkNibble nibble) {
        faces.get(chunkLocation).put(sectionHeight, getFacesForChunkNibble(chunkLocation, sectionHeight, nibble));
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

    private ChunkNibble getChunkNibbleOfWorld(ConcurrentHashMap<ChunkLocation, Chunk> world, ChunkLocation location, byte sectionHeight) {
        if (world.containsKey(location) && world.get(location).getNibbles().containsKey(sectionHeight)) {
            return world.get(location).getNibbles().get(sectionHeight);
        }
        return null;
    }
}

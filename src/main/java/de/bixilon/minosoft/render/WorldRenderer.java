/*
 * Codename Minosoft
 * Copyright (C) 2020 Lukas Eisenhauer, Moritz Zwerger
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
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;

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

    private LinkedBlockingQueue<Runnable> queuedMapData;

    public int getCountOfFaces() {
        AtomicInteger count = new AtomicInteger();
        faces.forEach((chunkLocation, nibbleMap) -> nibbleMap.forEach((height, faceMap) -> faceMap.forEach(((nibbleLocation, faces) -> faces.forEach((face -> count.incrementAndGet()))))));
        return count.get();
    }

    public void init() {
        queuedMapData = new LinkedBlockingQueue<>();
        assetsLoader = new AssetsLoader();
    }

    public void startChunkPreparation(Connection connection) {
        Thread chunkLoadThread = new Thread(() -> {
            while (true) {
                try {
                    queuedMapData.take().run();
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
        queuedMapData.add(() -> prepareChunk(location, chunk));
    }

    public void queueChunkNibble(ChunkLocation location, byte sectionHeight, ChunkNibble nibble) {
        queuedMapData.add(() -> prepareChunkNibble(location, sectionHeight, nibble));
    }

    public void queueBlock(BlockPosition position, Block block) {
        queuedMapData.add(() -> prepareBlock(position, block, false));
    }

    private void prepareChunk(ChunkLocation location, Chunk chunk) {
        // clear or create current chunk
        ConcurrentHashMap<Byte, ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>>> chunkFaces = new ConcurrentHashMap<>();
        chunk.getNibbles().forEach(((height, chunkNibble) -> chunkFaces.put(height, getFacesForChunkNibble(location, height, chunkNibble))));
        faces.put(location, chunkFaces);
    }

    private void prepareChunkNibble(ChunkLocation chunkLocation, byte sectionHeight, ChunkNibble nibble) {
        faces.get(chunkLocation).put(sectionHeight, getFacesForChunkNibble(chunkLocation, sectionHeight, nibble));
    }


    private ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>> getFacesForChunkNibble(ChunkLocation chunkLocation, byte sectionHeight, ChunkNibble nibble) {
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
                        if (location.getY() == RenderConstants.SECTIONS_MIN_Y) {
                            // need to check upper section (nibble)
                            if (sectionHeight == RenderConstants.SECTIONS_MIN_Y) {
                                // y = 0, there can't be any blocks below me
                                yield null;
                            }
                            // check if block over us is a full block
                            byte bottomSection = (byte) (sectionHeight - 1);
                            if (!world.get(chunkLocation).getNibbles().containsKey(bottomSection)) {
                                yield null;
                            }
                            yield world.get(chunkLocation).getNibbles().get(bottomSection).getBlock(location.getX(), RenderConstants.SECTIONS_MAX_Y, location.getZ());
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY() - 1, location.getZ()));
                    }
                    case UP -> {
                        if (location.getY() == RenderConstants.SECTIONS_MAX_Y) {
                            // need to check upper section (nibble)
                            if (sectionHeight == RenderConstants.SECTIONS_MAX_Y) {
                                // y = 255, there can't be any blocks above me
                                yield null;
                            }
                            // check if block over us is a full block
                            byte upperSection = (byte) (sectionHeight + 1);
                            if (!world.get(chunkLocation).getNibbles().containsKey(upperSection)) {
                                yield null;
                            }
                            yield world.get(chunkLocation).getNibbles().get(upperSection).getBlock(location.getX(), RenderConstants.SECTIONS_MIN_Y, location.getZ());
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY() + 1, location.getZ()));
                    }
                    case WEST -> {
                        if (location.getX() == RenderConstants.SECTIONS_MIN_X) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX() - 1, chunkLocation.getZ()), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(RenderConstants.SECTIONS_MAX_X, location.getY(), location.getZ());
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX() - 1, location.getY(), location.getZ()));
                    }
                    case EAST -> {
                        if (location.getX() == RenderConstants.SECTIONS_MIN_X) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX() + 1, chunkLocation.getZ()), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(RenderConstants.SECTIONS_MAX_X, location.getY(), location.getZ());
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX() + 1, location.getY(), location.getZ()));
                    }
                    case NORTH -> {
                        if (location.getZ() == RenderConstants.SECTIONS_MIN_Z) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX(), chunkLocation.getZ() - 1), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(location.getX(), location.getY(), RenderConstants.SECTIONS_MAX_Z);
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY(), location.getZ() - 1));
                    }
                    case SOUTH -> {
                        if (location.getZ() == RenderConstants.SECTIONS_MAX_Z) {
                            ChunkNibble otherChunkNibble = getChunkNibbleOfWorld(world, new ChunkLocation(chunkLocation.getX(), chunkLocation.getZ() + 1), sectionHeight);
                            if (otherChunkNibble != null) {
                                yield otherChunkNibble.getBlock(location.getX(), location.getY(), RenderConstants.SECTIONS_MIN_Z);
                            }
                        }
                        yield nibbleBlocks.get(new ChunkNibbleLocation(location.getX(), location.getY(), location.getZ() + 1));
                    }
                };
                if (dependedBlock == null || !assetsLoader.getBlockModelLoader().isFull(dependedBlock)) {
                    facesToDraw.add(orientation);
                }
            }
            if (!facesToDraw.isEmpty()) {
                nibbleMap.put(location, assetsLoader.getBlockModelLoader().prepare(block, facesToDraw));
            }

        }
        return nibbleMap;
    }


    private void prepareBlock(BlockPosition position, Block block, boolean trustEdges) {
        HashSet<FaceOrientation> facesToDraw = new HashSet<>();

        if (block != null && !block.equals(Blocks.nullBlock)) {
            for (FaceOrientation orientation : FaceOrientation.values()) {
                Block dependedBlock = switch (orientation) {
                    case DOWN -> {
                        if (position.getY() == RenderConstants.CHUNK_MIN_Y) {
                            facesToDraw.add(orientation);
                            yield null;
                        }
                        yield GameWindow.getConnection().getPlayer().getWorld().getBlock(new BlockPosition(position.getX(), position.getY() - 1, position.getZ()));
                    }
                    case UP -> {
                        if (position.getY() == RenderConstants.CHUNK_MAX_Y) {
                            facesToDraw.add(orientation);
                            yield null;
                        }
                        yield GameWindow.getConnection().getPlayer().getWorld().getBlock(new BlockPosition(position.getX(), position.getY() + 1, position.getZ()));
                    }
                    case NORTH -> GameWindow.getConnection().getPlayer().getWorld().getBlock(new BlockPosition(position.getX(), position.getY(), position.getZ() - 1));
                    case SOUTH -> GameWindow.getConnection().getPlayer().getWorld().getBlock(new BlockPosition(position.getX(), position.getY(), position.getZ() + 1));
                    case WEST -> GameWindow.getConnection().getPlayer().getWorld().getBlock(new BlockPosition(position.getX() - 1, position.getY(), position.getZ()));
                    case EAST -> GameWindow.getConnection().getPlayer().getWorld().getBlock(new BlockPosition(position.getX() + 1, position.getY(), position.getZ()));
                };
                if (dependedBlock == null || !assetsLoader.getBlockModelLoader().isFull(dependedBlock)) {
                    facesToDraw.add(orientation);
                }
            }
        }
        ConcurrentHashMap<ChunkNibbleLocation, HashSet<Face>> nibbleMap = faces.get(position.getChunkLocation()).get((byte) (position.getY() / RenderConstants.SECTION_HEIGHT));
        if (facesToDraw.size() == 0) {
            // remove all faces
            nibbleMap.remove(position.getInChunkLocation().getChunkNibbleLocation());
        } else {
            nibbleMap.put(position.getInChunkLocation().getChunkNibbleLocation(), assetsLoader.getBlockModelLoader().prepare(block, facesToDraw));
        }

        if (trustEdges) {
            return;
        }
        if (position.getY() != RenderConstants.CHUNK_MIN_Y) {
            // bottom
            prepareBlock(new BlockPosition(position.getX(), position.getY() - 1, position.getZ()), true);
        }
        if (position.getY() != RenderConstants.CHUNK_MAX_Y) {
            // bottom
            prepareBlock(new BlockPosition(position.getX(), position.getY() + 1, position.getZ()), true);
        }
        prepareBlock(new BlockPosition(position.getX() + 1, position.getY(), position.getZ()), true);
        prepareBlock(new BlockPosition(position.getX() - 1, position.getY(), position.getZ()), true);
        prepareBlock(new BlockPosition(position.getX(), position.getY(), position.getZ() + 1), true);
        prepareBlock(new BlockPosition(position.getX(), position.getY(), position.getZ() - 1), true);
    }

    private void prepareBlock(BlockPosition position, boolean trustEdges) {
        prepareBlock(position, GameWindow.getConnection().getPlayer().getWorld().getBlock(position), trustEdges);
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

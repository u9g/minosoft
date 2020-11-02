/*
 * Minosoft
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

import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.world.*;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.blockModels.BlockModelLoader;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final ConcurrentHashMap<ChunkLocation, ConcurrentHashMap<Byte, ArrayFloatList>> faces = new ConcurrentHashMap<>();

    private final LinkedBlockingQueue<Runnable> queuedMapData = new LinkedBlockingQueue<>();
    private final Connection connection;

    public WorldRenderer(Connection connection) {
        this.connection = connection;
        new Thread(() -> {
            while (true) {
                try {
                    queuedMapData.take().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, String.format("%d/ChunkLoading", connection.getConnectionId())).start();
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

    private void prepareBlock(BlockPosition position, Block block, boolean b) {
    }

    private void prepareChunk(ChunkLocation location, Chunk chunk) {
        // clear or create current chunk
        ConcurrentHashMap<Byte, ArrayFloatList> chunkFaces = new ConcurrentHashMap<>();
        chunk.getNibbles().forEach(((height, chunkNibble) -> chunkFaces.put(height, getFacesForChunkNibble(location, height, chunkNibble))));
        faces.put(location, chunkFaces);
    }

    private void prepareChunkNibble(ChunkLocation chunkLocation, byte sectionHeight, ChunkNibble nibble) {
        faces.get(chunkLocation).put(sectionHeight, getFacesForChunkNibble(chunkLocation, sectionHeight, nibble));
    }

    private ArrayFloatList getFacesForChunkNibble(ChunkLocation chunkLocation, byte sectionHeight, ChunkNibble nibble) {
        ConcurrentHashMap<ChunkLocation, Chunk> world = connection.getPlayer().getWorld().getAllChunks();
        // clear or create current chunk nibble
        ArrayFloatList nibbleMap = new ArrayFloatList();
        //faces.get(chunkLocation).put(sectionHeight, nibbleMap);
        ConcurrentHashMap<ChunkNibbleLocation, Block> nibbleBlocks = nibble.getBlocks();
        nibbleBlocks.forEach((location, block) -> {
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
                if (dependedBlock == null || !BlockModelLoader.getInstance().isFull(dependedBlock, FaceOrientation.inverse(orientation))) {
                    facesToDraw.add(orientation);
                }
            }
            if (!facesToDraw.isEmpty()) {
                nibbleMap.addAll(BlockModelLoader.getInstance().prepare(block, facesToDraw, new BlockPosition(chunkLocation, sectionHeight, location)));
            }
        });
        return nibbleMap;
    }


    public void draw() {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, BlockModelLoader.getInstance().getTextureLoader().getTextureID());
        glBegin(GL_QUADS);
        for (ConcurrentHashMap<Byte, ArrayFloatList> chunk : faces.values()) {
            for (ArrayFloatList nibble : chunk.values()) {
                float[] array = nibble.toArray();
                for (int i = 0; i < array.length; i += 5) {
                    glTexCoord2f(array[i], array[i + 1]);
                    glVertex3f(array[i + 2], array[i + 3], array[i + 4]);
                }
            }
        }
        glEnd();
    }

    private ChunkNibble getChunkNibbleOfWorld(ConcurrentHashMap<ChunkLocation, Chunk> world, ChunkLocation location, byte sectionHeight) {
        if (world.containsKey(location) && world.get(location).getNibbles().containsKey(sectionHeight)) {
            return world.get(location).getNibbles().get(sectionHeight);
        }
        return null;
    }

    public void clearFaces() {
        faces.clear();
    }
}

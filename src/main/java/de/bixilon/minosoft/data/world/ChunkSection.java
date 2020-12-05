/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.data.world;

import de.bixilon.minosoft.data.entities.block.BlockEntityMetaData;
import de.bixilon.minosoft.data.mappings.blocks.Block;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of 16x16x16 blocks
 */
public class ChunkSection {
    private final ConcurrentHashMap<InChunkSectionLocation, Block> blocks;
    private final ConcurrentHashMap<InChunkSectionLocation, BlockEntityMetaData> blockEntityMeta = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<InChunkSectionLocation, Byte> light;
    private final ConcurrentHashMap<InChunkSectionLocation, Byte> skyLight;

    public ChunkSection(ConcurrentHashMap<InChunkSectionLocation, Block> blocks) {
        this(blocks, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    public ChunkSection(ConcurrentHashMap<InChunkSectionLocation, Block> blocks, ConcurrentHashMap<InChunkSectionLocation, Byte> light, ConcurrentHashMap<InChunkSectionLocation, Byte> skyLight) {
        this.blocks = blocks;
        this.light = light;
        this.skyLight = skyLight;
    }

    public ChunkSection() {
        this(new ConcurrentHashMap<>());
    }

    public Block getBlock(int x, int y, int z) {
        return getBlock(new InChunkSectionLocation(x, y, z));
    }

    public Block getBlock(InChunkSectionLocation loc) {
        return blocks.get(loc);
    }

    public void setBlock(int x, int y, int z, Block block) {
        setBlock(new InChunkSectionLocation(x, y, z), block);
    }

    public void setBlock(InChunkSectionLocation location, Block block) {
        blockEntityMeta.remove(location);
        Block current = blocks.get(location);
        if (block == null) {
            blocks.remove(location);
            return;
        }
        if (current.equals(block)) {
            return;
        }
        blocks.put(location, block);
    }

    public void setBlockEntityData(InChunkSectionLocation position, BlockEntityMetaData data) {
        // ToDo check if block is really a block entity (command block, spawner, skull, flower pot)
        blockEntityMeta.put(position, data);
    }

    public ConcurrentHashMap<InChunkSectionLocation, Block> getBlocks() {
        return blocks;
    }

    public ConcurrentHashMap<InChunkSectionLocation, BlockEntityMetaData> getBlockEntityMeta() {
        return blockEntityMeta;
    }

    public ConcurrentHashMap<InChunkSectionLocation, Byte> getLight() {
        return light;
    }

    public ConcurrentHashMap<InChunkSectionLocation, Byte> getSkyLight() {
        return skyLight;
    }

    public BlockEntityMetaData getBlockEntityData(InChunkSectionLocation position) {
        return blockEntityMeta.get(position);
    }

    public void setBlockEntityData(ConcurrentHashMap<InChunkSectionLocation, BlockEntityMetaData> blockEntities) {
        blockEntities.forEach(blockEntityMeta::put);
    }
}

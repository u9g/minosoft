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

import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.Blocks;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of 16x16x16 blocks
 */
public class ChunkSection {
    final ConcurrentHashMap<InChunkSectionLocation, Block> blocks;

    public ChunkSection(ConcurrentHashMap<InChunkSectionLocation, Block> blocks) {
        this.blocks = blocks;
    }

    public ChunkSection() {
        this.blocks = new ConcurrentHashMap<>();
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
        if (block == null || block.equals(Blocks.nullBlock)) {
            blocks.remove(location);
            return;
        }
        blocks.put(location, block);
    }

    public ConcurrentHashMap<InChunkSectionLocation, Block> getBlocks() {
        return blocks;
    }
}

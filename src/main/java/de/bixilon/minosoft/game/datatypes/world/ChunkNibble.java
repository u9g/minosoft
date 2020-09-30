/*
 * Codename Minosoft
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

package de.bixilon.minosoft.game.datatypes.world;

import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of 16x16x16 blocks
 */
public class ChunkNibble {
    final ConcurrentHashMap<ChunkNibbleLocation, Block> blocks;

    public ChunkNibble(ConcurrentHashMap<ChunkNibbleLocation, Block> blocks) {
        this.blocks = blocks;
    }

    public ChunkNibble() {
        // empty
        this.blocks = new ConcurrentHashMap<>();
    }

    public Block getBlock(ChunkNibbleLocation loc) {
        if (!blocks.containsKey(loc)) {
            return null;
        }
        return blocks.get(loc);
    }

    public Block getBlock(int x, int y, int z) {
        return getBlock(new ChunkNibbleLocation(x, y, z));
    }

    public void setBlock(int x, int y, int z, Block block) {
        setBlock(new ChunkNibbleLocation(x, y, z), block);
    }

    public void setBlock(ChunkNibbleLocation location, Block block) {
        if (block == null || block.equals(Blocks.nullBlock)) {
            blocks.remove(location);
            return;
        }
        blocks.put(location, block);
    }

    public ConcurrentHashMap<ChunkNibbleLocation, Block> getBlocks() {
        return blocks;
    }
}

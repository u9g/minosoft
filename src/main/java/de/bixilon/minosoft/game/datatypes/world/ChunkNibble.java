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

import de.bixilon.minosoft.game.datatypes.blocks.Block;

import java.util.HashMap;

/**
 * Collection of 16x16x16 blocks
 */
public class ChunkNibble {
    final HashMap<ChunkNibbleLocation, Block> blocks;

    public ChunkNibble(HashMap<ChunkNibbleLocation, Block> blocks) {
        this.blocks = blocks;
    }

    public ChunkNibble() {
        // empty
        this.blocks = new HashMap<>();
    }

    public Block getBlock(ChunkNibbleLocation loc) {
        return blocks.get(loc);
    }

    public Block getBlock(int x, int y, int z) {
        return getBlock(new ChunkNibbleLocation(x, y, z));
    }

    public void setBlock(int x, int y, int z, Block block) {
        blocks.put(new ChunkNibbleLocation(x, y, z), block);
    }

    public void setBlock(ChunkNibbleLocation location, Block block) {
        blocks.put(location, block);
    }

    public HashMap<ChunkNibbleLocation, Block> getBlocks() {
        return blocks;
    }
}

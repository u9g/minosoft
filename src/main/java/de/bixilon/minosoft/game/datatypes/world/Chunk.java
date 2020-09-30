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
import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection of 16 chunks nibbles
 */
public class Chunk {
    final ConcurrentHashMap<Byte, ChunkNibble> nibbles;

    public Chunk(ConcurrentHashMap<Byte, ChunkNibble> chunks) {
        this.nibbles = chunks;
    }

    public Block getBlock(int x, int y, int z) {
        byte section = (byte) (y / RenderConstants.SECTION_HEIGHT);
        return nibbles.get(section).getBlock(x, y % RenderConstants.SECTION_HEIGHT, z);
    }

    public Block getBlock(InChunkLocation location) {
        return getBlock(location.getX(), location.getY(), location.getZ());
    }

    public void setBlock(int x, int y, int z, Block block) {
        byte section = (byte) (y / RenderConstants.SECTION_HEIGHT);
        createSectionIfNotExist(section);
        nibbles.get(section).setBlock(x, y % RenderConstants.SECTION_HEIGHT, z, block);
    }

    public void setBlock(InChunkLocation location, Block block) {
        byte section = (byte) (location.getY() / RenderConstants.SECTION_HEIGHT);
        createSectionIfNotExist(section);
        nibbles.get(section).setBlock(location.getChunkNibbleLocation(), block);
    }

    void createSectionIfNotExist(byte section) {
        if (nibbles.get(section) == null) {
            nibbles.put(section, new ChunkNibble());
        }
    }

    public void setBlocks(HashMap<InChunkLocation, Block> blocks) {
        for (Map.Entry<InChunkLocation, Block> set : blocks.entrySet()) {
            setBlock(set.getKey(), set.getValue());
        }
    }

    public ConcurrentHashMap<Byte, ChunkNibble> getNibbles() {
        return nibbles;
    }
}

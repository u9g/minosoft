/*
 * minosoft
 * Copyright (C) 2020 Lukas Eisenhauer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.data.mappings.blocks;

import de.bixilon.minosoft.data.world.Chunk;
import de.bixilon.minosoft.data.world.ChunkSection;
import de.bixilon.minosoft.data.world.InChunkSectionLocation;

import java.util.Map;

public final class LegacyBlockTransform {
    private static final Block GRASS_BLOCK = new Block("grass");
    private static final Block GRASS_BLOCK_SNOWY = new Block("grass", BlockProperties.GRASS_SNOWY_YES);
    private static final Block GRASS_BLOCK_NOT_SNOWY = new Block("grass", BlockProperties.GRASS_SNOWY_NO);
    private static final String SNOW_IDENTIFIER = "snow";
    private static final String SNOW_LAYER_IDENTIFIER = "snow_layer";

    public static Chunk transformChunk(Chunk chunk) {
        // TODO: call at block changes
        for (Map.Entry<Byte, ChunkSection> section : chunk.getSections().entrySet()) {
            for (Map.Entry<InChunkSectionLocation, Block> block : section.getValue().getBlocks().entrySet()) {
                Block blockAbove = chunk.getBlock(block.getKey().getChunkLocation(section.getKey()).add(0, 1, 0));
                Block newBlock = null;
                if (block.getValue().equals(GRASS_BLOCK)) {
                    if (blockAbove == null || (!blockAbove.getIdentifier().equals(SNOW_IDENTIFIER) & !blockAbove.getIdentifier().equals(SNOW_LAYER_IDENTIFIER))) {
                        newBlock = GRASS_BLOCK_NOT_SNOWY;
                    } else {
                        newBlock = GRASS_BLOCK_SNOWY;
                    }
                }
                if (newBlock != null) {
                    section.getValue().setBlock(block.getKey(), newBlock);
                }
            }
        }
        return chunk;
    }
}

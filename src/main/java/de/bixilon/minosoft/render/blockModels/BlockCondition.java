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

package de.bixilon.minosoft.render.blockModels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.BlockProperties;
import de.bixilon.minosoft.data.mappings.blocks.BlockRotations;

import java.util.HashSet;
import java.util.Map;

public class BlockCondition {
    public static final BlockCondition TRUE_CONDITION = new BlockCondition() {
        @Override
        public boolean contains(Block block) {
            return true;
        }
    };

    private HashSet<BlockProperties> properties;
    private BlockRotations rotation;

    public BlockCondition(JsonObject json) {
        properties = new HashSet<>();
        rotation = BlockRotations.NONE;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String value = entry.getValue().getAsString();
            if (BlockProperties.PROPERTIES_MAPPING.containsKey(entry.getKey())) {
                properties.add(BlockProperties.PROPERTIES_MAPPING.get(entry.getKey()).get(value));
                continue;
            }
            rotation = BlockRotations.ROTATION_MAPPING.get(value);
        }
    }

    public BlockCondition() {
    }

    public boolean contains(Block block) {
        if (rotation != BlockRotations.NONE && rotation != block.getRotation()) {
            return false;
        }
        return properties.equals(block.getProperties());
    }
}

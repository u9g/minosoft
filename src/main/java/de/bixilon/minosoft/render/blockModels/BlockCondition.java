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

package de.bixilon.minosoft.render.blockModels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.BlockProperties;
import de.bixilon.minosoft.data.mappings.blocks.BlockRotations;
import de.bixilon.minosoft.data.mappings.blocks.Blocks;

import java.util.HashSet;
import java.util.Map;

public class BlockCondition {
    public static final BlockCondition trueCondition = new BlockCondition() {
        @Override
        public boolean contains(Block block) {
            return true;
        }
    };

    HashSet<BlockProperties> properties;
    BlockRotations rotation;

    public BlockCondition(JsonObject json) {
        properties = new HashSet<>();
        rotation = BlockRotations.NONE;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String value = entry.getValue().getAsString();
            if (Blocks.getPropertiesMapping().containsKey(entry.getKey())) {
                properties.add(Blocks.getPropertiesMapping().get(entry.getKey()).get(value));
                continue;
            }
            rotation = Blocks.getRotationMapping().get(value);
        }
    }

    public BlockCondition() {
    }

    public boolean contains(Block block) {
        if (rotation != BlockRotations.NONE && rotation != block.getRotation()) {
            return false;
        }
        for (BlockProperties property : properties) {
            if (!block.getProperties().contains(property)) {
                return false;
            }
        }
        return true;
    }
}

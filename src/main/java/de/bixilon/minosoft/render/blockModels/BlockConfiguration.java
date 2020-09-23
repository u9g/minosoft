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

import com.google.gson.JsonObject;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotations;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;

import java.util.HashMap;
import java.util.HashSet;

public class BlockConfiguration {
    private BlockRotations rotation;
    private HashSet<BlockProperties> blockProperties;

    public BlockConfiguration(JsonObject json) {
        if (json.has("facing")) {
            rotation = Blocks.getRotationMapping().get(json.get("facing").getAsString());
            json.remove("facing");
        }
        if (json.has("rotation")) {
            rotation = Blocks.getRotationMapping().get(json.get("rotation").getAsString());
            json.remove("rotation");
        }
        if (json.has("axis")) {
            rotation = Blocks.getRotationMapping().get(json.get("axis").getAsString());
            json.remove("axis");
        }

        blockProperties = new HashSet<>();
        for (String propertyName : json.keySet()) {
            HashMap<String, BlockProperties> properties = Blocks.getPropertiesMapping().get(propertyName);
            if (properties == null) {
                throw new RuntimeException(String.format("Unknown block property: %s", propertyName));
            }
            BlockProperties property = properties.get(json.get(propertyName).getAsString());
            if (property == null) {
                throw new RuntimeException(String.format("Unknown block property: %s -> %s",
                        propertyName, json.get(propertyName).getAsString()));
            }
            blockProperties.add(property);
        }
    }

    public BlockConfiguration() {
    }

    public BlockRotations getRotation() {
        return rotation;
    }

    public HashSet<BlockProperties> getBlockProperties() {
        return blockProperties;
    }

    public boolean equals(BlockConfiguration blockConfiguration) {
        return rotation.equals(blockConfiguration.getRotation()) &&
                blockProperties.equals(blockConfiguration.getBlockProperties());
    }

    public boolean contains(Block block) {
        if (rotation != null && block.getRotation() != rotation) {
            return false;
        }
        for (BlockProperties property : blockProperties) {
            if (!block.getProperties().contains(property)) {
                return false;
            }
        }
        return true;
    }
}

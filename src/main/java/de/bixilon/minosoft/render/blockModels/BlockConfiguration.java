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

import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotation;

import java.util.HashSet;

public class BlockConfiguration {
    BlockRotation rotation;
    HashSet<BlockProperties> blockProperties;


    public BlockConfiguration(String config) {
        blockProperties = new HashSet<>();
        for (String configuration : config.split(",")) {
            switch (configuration) {
                case "orientation:up":
                    rotation = BlockRotation.UP;
                    break;
                case "orientation:down":
                    rotation = BlockRotation.DOWN;
                    break;
            }
        }
    }

    public BlockRotation getRotation() {
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
        if (block.getRotation().equals(BlockRotation.NONE)) {
            return false;
        }
        if (!block.getRotation().equals(rotation)) {
            return false;
        }
        if (blockProperties.size() == 0 && block.getProperties().size() == 0) {
            return true;
        }
        for (BlockProperties property : blockProperties) {
            if (!block.getProperties().contains(property)) {
                return false;
            }
        }
        return true;
    }
}

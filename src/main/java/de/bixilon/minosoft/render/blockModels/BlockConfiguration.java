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
    HashSet<BlockRotation> rotations;
    HashSet<BlockProperties> blockProperties;

    public BlockConfiguration(String config) {
        rotations = new HashSet<>();
        blockProperties = new HashSet<>();
        for (String configuration : config.split(",")) {
            switch (configuration) {
                case "orientation:vertical":
                    rotations.add(BlockRotation.UP);
                    rotations.add(BlockRotation.DOWN);
                    break;
                case "orientation:up":
                    rotations.add(BlockRotation.UP);
                    break;
                case "orientation:down":
                    rotations.add(BlockRotation.DOWN);
                    break;
            }
        }
    }

    public HashSet<BlockRotation> getRotations() {
        return rotations;
    }

    public HashSet<BlockProperties> getBlockProperties() {
        return blockProperties;
    }

    public boolean equals(BlockConfiguration blockConfiguration) {
        return rotations.equals(blockConfiguration.getRotations()) &&
                blockProperties.equals(blockConfiguration.getBlockProperties());
    }

    public boolean contains(Block block) {
        if (!rotations.contains(block.getRotation()) && block.getRotation() != BlockRotation.NONE) {
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

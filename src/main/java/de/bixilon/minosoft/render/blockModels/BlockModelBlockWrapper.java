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

package de.bixilon.minosoft.render.blockModels;

import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.BlockRotations;

public class BlockModelBlockWrapper {
    Block block;

    public BlockModelBlockWrapper(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public int hashCode() {
        return block.getMod().hashCode() * block.getIdentifier().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        BlockModelBlockWrapper their = (BlockModelBlockWrapper) obj;
        if (block.equals(their.getBlock())) {
            return true;
        }
        if (!(block.getMod().equals(their.getBlock().getMod()) && block.getIdentifier().equals(their.getBlock().getIdentifier()))) {
            return false;
        }
        if (block.getRotation() == BlockRotations.NONE || their.getBlock().getRotation() == BlockRotations.NONE || block.getProperties().size() == 0 || their.getBlock().getProperties().size() == 0) {
            return true;
        }
        return block.getProperties().equals(their.getBlock().getProperties()) && block.getMod().equals(their.getBlock().getMod());
    }
}

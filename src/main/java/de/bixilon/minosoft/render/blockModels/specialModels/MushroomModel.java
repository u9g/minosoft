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

package de.bixilon.minosoft.render.blockModels.specialModels;

import com.google.gson.JsonObject;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.BlockProperties;
import de.bixilon.minosoft.data.mappings.blocks.BlockRotations;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashSet;

public class MushroomModel implements BlockModelInterface {
    private final HashSet<SubBlock> subBlocks;

    public MushroomModel(JsonObject block, String mod) {
        subBlocks = BlockModelInterface.load(mod, block.get("block").getAsString());
    }

    public ArrayFloatList prepare(Block block, HashSet<FaceOrientation> facesToDraw, BlockPosition position) {
        ArrayFloatList result = new ArrayFloatList();
        if (block.getProperties().contains(BlockProperties.DOWN)) {
            result.addAll(BlockModelInterface.prepareState(subBlocks, BlockRotations.DOWN, facesToDraw, position));
        }
        if (block.getProperties().contains(BlockProperties.UP)) {
            result.addAll(BlockModelInterface.prepareState(subBlocks, BlockRotations.UP, facesToDraw, position));
        }
        if (block.getProperties().contains(BlockProperties.EAST)) {
            result.addAll(BlockModelInterface.prepareState(subBlocks, BlockRotations.EAST, facesToDraw, position));
        }
        if (block.getProperties().contains(BlockProperties.WEST)) {
            result.addAll(BlockModelInterface.prepareState(subBlocks, BlockRotations.WEST, facesToDraw, position));
        }
        if (block.getProperties().contains(BlockProperties.NORTH)) {
            result.addAll(BlockModelInterface.prepareState(subBlocks, BlockRotations.NORTH, facesToDraw, position));
        }
        if (block.getProperties().contains(BlockProperties.SOUTH)) {
            result.addAll(BlockModelInterface.prepareState(subBlocks, BlockRotations.SOUTH, facesToDraw, position));
        }
        return result;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public HashSet<String> getAllTextures() {
        return BlockModelInterface.getTextures(subBlocks);
    }

    @Override
    public void applyTextures(String mod, TextureLoader loader) {
        BlockModelInterface.applyConfigurationTextures(subBlocks, mod, loader);
    }
}

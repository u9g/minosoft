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

package de.bixilon.minosoft.render.blockModels.specialModels;

import com.google.gson.JsonObject;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotations;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashSet;


public class DoorModel implements BlockModelInterface {
    private final HashSet<SubBlock> bottom;
    private final HashSet<SubBlock> bottom_hinge;

    private final HashSet<SubBlock> top;
    private final HashSet<SubBlock> top_hinge;

    public DoorModel(JsonObject block, String mod) {
        bottom = BlockModelInterface.load(mod, block.get("bottom").getAsString());
        bottom_hinge = BlockModelInterface.load(mod, block.get("bottom_hinge").getAsString());

        top = BlockModelInterface.load(mod, block.get("top").getAsString());
        top_hinge = BlockModelInterface.load(mod, block.get("top_hinge").getAsString());
    }

    private static HashSet<Face> prepareHinge(HashSet<SubBlock> bottom, HashSet<SubBlock> top, Block block, HashSet<FaceOrientation> adjacentBlocks) {
        if (block.getProperties().contains(BlockProperties.OPEN)) {
            return prepareHalf(bottom, top, block, adjacentBlocks, rotationAdjust.inverse().get(block.getRotation()));
        }
        return prepareHalf(bottom, top, block, adjacentBlocks, block.getRotation());
    }

    private static HashSet<Face> prepareHalf(HashSet<SubBlock> bottom, HashSet<SubBlock> top, Block block, HashSet<FaceOrientation> adjacentBlocks, BlockRotations rotation) {
        if (block.getProperties().contains(BlockProperties.HALF_LOWER)) {
            return BlockModel.prepareBlockState(bottom, adjacentBlocks, new Block("", "", rotation));
        } else if (block.getProperties().contains(BlockProperties.HALF_UPPER)) {
            return BlockModel.prepareBlockState(top, adjacentBlocks, new Block("", "", rotation));
        }
        Log.warn("now");
        return null;
    }

    public HashSet<Face> prepare(Block block, HashSet<FaceOrientation> facesToDraw) {
        if (block.getProperties().contains(BlockProperties.HINGE_LEFT)) {
            return prepareHinge(bottom, top, block, facesToDraw);
        }
        return prepareHinge(bottom_hinge, top_hinge, block, facesToDraw);
    }

    public boolean isFull() {
        return false;
    }

    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        result.addAll(BlockModelInterface.getTextures(bottom));
        result.addAll(BlockModelInterface.getTextures(bottom_hinge));
        result.addAll(BlockModelInterface.getTextures(top));
        result.addAll(BlockModelInterface.getTextures(top_hinge));
        return result;
    }

    public void applyTextures(String mod, TextureLoader loader) {
        BlockModelInterface.applyConfigurationTextures(bottom, mod, loader);
        BlockModelInterface.applyConfigurationTextures(bottom_hinge, mod, loader);
        BlockModelInterface.applyConfigurationTextures(top, mod, loader);
        BlockModelInterface.applyConfigurationTextures(top_hinge, mod, loader);
    }
}

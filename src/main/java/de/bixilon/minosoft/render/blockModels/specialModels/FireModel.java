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
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotations;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashSet;

public class FireModel implements BlockModelInterface {
    private final HashSet<SubBlock> floor;
    private final HashSet<SubBlock> side;
    private final HashSet<SubBlock> up;

    public FireModel(JsonObject block, String mod) {
        floor = BlockModelInterface.load(mod, block.get("floor").getAsString());
        side = BlockModelInterface.load(mod, block.get("side").getAsString());
        up = BlockModelInterface.load(mod, block.get("up").getAsString());
    }

    public ArrayFloatList prepare(Block block, HashSet<FaceOrientation> facesToDraw, BlockPosition position) {
        HashSet<BlockProperties> properties = block.getProperties();

        ArrayFloatList result = new ArrayFloatList();
        if (properties.contains(BlockProperties.EAST)) {
            result.addAll(BlockModelInterface.prepareState(side, BlockRotations.EAST, facesToDraw, position));
        }
        if (properties.contains(BlockProperties.WEST)) {
            result.addAll(BlockModelInterface.prepareState(side, BlockRotations.WEST, facesToDraw, position));
        }
        if (properties.contains(BlockProperties.NORTH)) {
            result.addAll(BlockModelInterface.prepareState(side, BlockRotations.NORTH, facesToDraw, position));
        }
        if (properties.contains(BlockProperties.SOUTH)) {
            result.addAll(BlockModelInterface.prepareState(side, BlockRotations.SOUTH, facesToDraw, position));
        }
        if (properties.contains(BlockProperties.UP)) {
            result.addAll(BlockModelInterface.prepareState(up, BlockRotations.UP, facesToDraw, position));
        }
        if (result.size() == 0) {
            result.addAll(BlockModelInterface.prepareState(floor, BlockRotations.NONE, facesToDraw, position));
        }
        return result;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        result.addAll(BlockModelInterface.getTextures(floor));
        result.addAll(BlockModelInterface.getTextures(side));
        result.addAll(BlockModelInterface.getTextures(up));
        return result;
    }

    @Override
    public void applyTextures(String mod, TextureLoader loader) {
        BlockModelInterface.applyConfigurationTextures(floor, mod, loader);
        BlockModelInterface.applyConfigurationTextures(side, mod, loader);
        BlockModelInterface.applyConfigurationTextures(up, mod, loader);
    }
}

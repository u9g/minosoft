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
import de.bixilon.minosoft.render.blockModels.BlockModel;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashMap;
import java.util.HashSet;

public class FireModel extends BlockModel {
    HashSet<SubBlock> floor;
    HashSet<SubBlock> side;
    HashSet<SubBlock> up;


    public FireModel(JsonObject block, String mod) {
        floor = super.load(mod, block.get("floor").getAsString());
        side = super.load(mod, block.get("side").getAsString());
        up = super.load(mod, block.get("up").getAsString());
    }

    @Override
    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        HashSet<BlockProperties> properties = block.getProperties();

        HashSet<Face> result = new HashSet<>();
        if (properties.contains(BlockProperties.EAST)) {
            result.addAll(prepareState(side, BlockRotations.EAST));
        }
        if (properties.contains(BlockProperties.WEST)) {
            result.addAll(prepareState(side, BlockRotations.WEST));
        }
        if (properties.contains(BlockProperties.NORTH)) {
            result.addAll(prepareState(side, BlockRotations.NORTH));
        }
        if (properties.contains(BlockProperties.SOUTH)) {
            result.addAll(prepareState(side, BlockRotations.SOUTH));
        }
        if (properties.contains(BlockProperties.UP)) {
            result.addAll(prepareState(up, BlockRotations.UP));
        }
        if (result.size() == 0) {
            result.addAll(prepareState(floor, BlockRotations.NONE));
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
        result.addAll(getTextures(floor));
        result.addAll(getTextures(side));
        result.addAll(getTextures(up));
        return result;
    }

    @Override
    public void applyTextures(String mod, TextureLoader loader) {
        applyConfigurationTextures(floor, mod, loader);
        applyConfigurationTextures(side, mod, loader);
        applyConfigurationTextures(up, mod, loader);
    }
}

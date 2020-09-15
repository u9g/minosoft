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
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotation;
import de.bixilon.minosoft.render.blockModels.BlockModel;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashMap;
import java.util.HashSet;

public class StairsModel extends BlockModel {
    HashSet<SubBlock> straight;
    HashSet<SubBlock> inner;
    HashSet<SubBlock> outer;

    public StairsModel(JsonObject block, String mod) {
        straight = super.load(mod, block.get("straight").getAsString());
        inner = super.load(mod, block.get("inner").getAsString());
        outer = super.load(mod, block.get("outer").getAsString());
    }

    @Override
    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        HashSet<BlockProperties> properties = block.getProperties();

        for (BlockProperties property : properties) {
            if (property.name().contains("INNER")) {
                return prepareCorner(outer, property, block.getRotation());
            } else if (property.name().contains("OUTER")) {
                return prepareCorner(inner, property, block.getRotation());
            }
        }
        return prepareState(straight, rotationAdjust.get(block.getRotation()));
    }

    public static HashSet<Face> prepareCorner(HashSet<SubBlock> subBlocks, BlockProperties property,
                                              BlockRotation rotation) {
        if (property.name().contains("LEFT")) {
            return prepareState(subBlocks, rotation);
        }
        return prepareState(subBlocks, rotationAdjust.get(rotation));
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        result.addAll(getTextures(straight));
        result.addAll(getTextures(inner));
        result.addAll(getTextures(outer));
        return result;
    }

    @Override
    public void applyTextures(String mod, TextureLoader loader) {
        applyConfigurationTextures(straight, mod, loader);
        applyConfigurationTextures(inner, mod, loader);
        applyConfigurationTextures(outer, mod, loader);
    }
}

/*
 * Minosoft
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ConditionalModel implements BlockModelInterface {
    HashMap<BlockCondition, HashSet<SubBlock>> conditionMap;

    public ConditionalModel(HashMap<String, HashSet<SubBlock>> blockModels, JsonArray elements) {
        conditionMap = new HashMap<>();
        for (JsonElement element : elements) {
            JsonObject block = element.getAsJsonObject();
            BlockCondition condition;
            if (block.has("properties")) {
                condition = new BlockCondition(block.get("properties").getAsJsonObject());
            } else {
                condition = BlockCondition.trueCondition;
            }
            HashSet<SubBlock> model = blockModels.get(block.get("model").getAsString());
            conditionMap.put(condition, model);
        }
    }

    @Override
    public ArrayFloatList prepare(HashSet<FaceOrientation> facesToDraw, BlockPosition position, Block block) {
        ArrayFloatList result = new ArrayFloatList();
        for (Map.Entry<BlockCondition, HashSet<SubBlock>> entry : conditionMap.entrySet()) {
            if (entry.getKey().contains(block)) {
                for (SubBlock subBlock : entry.getValue()) {
                    result.addAll(subBlock.getFaces(facesToDraw, position));
                }
            }
        }
        return result;
    }

    @Override
    public boolean full(Block block, FaceOrientation orientation) {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }
}

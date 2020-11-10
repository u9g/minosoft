/*
 * minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.render.blockModels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;

public interface BlockModelInterface {
    static HashSet<SubBlock> load(JsonObject block, JsonObject allModels, HashMap<String, String> variables) {
        HashSet<SubBlock> result = new HashSet<>();
        if (block.has("textures")) {
            // read the textures into a variable hashmap
            JsonObject textures = block.getAsJsonObject("textures");
            for (String texture : textures.keySet()) {
                if (texture.contains("#") && variables.containsKey(texture)) {
                    variables.put("#" + texture, variables.get(texture));
                } else {
                    variables.put("#" + texture, textures.get(texture).getAsString());
                }
            }
        }
        if (block.has("elements")) {
            for (JsonElement subBlockJson : block.get("elements").getAsJsonArray()) {
                result.add(new SubBlock(subBlockJson.getAsJsonObject(), variables));
            }
        } else if (block.has("parent") && !block.get("parent").getAsString().equals("block/block")) {
            String parent = block.get("parent").getAsString();
            if (parent.equals("block/block")) {
                return result;
            }
            String parentIdentifier = parent.substring(parent.lastIndexOf("/") + 1);
            result.addAll(load(allModels.get(parentIdentifier).getAsJsonObject(), allModels, variables));
        }
        return result;
    }

    static HashSet<SubBlock> load(JsonObject json, JsonObject allModels) {
        return load(json, allModels, new HashMap<>());
    }

    static void rotateModel(HashSet<SubBlock> subBlocks, Axis axis, double rotation) {
        for (SubBlock subBlock : subBlocks) {
            subBlock.rotate(axis, rotation);
        }
    }

    boolean full(Block block, FaceOrientation orientation);

    boolean isFull();

    ArrayFloatList prepare(HashSet<FaceOrientation> facesToDraw, BlockPosition position, Block block);
}

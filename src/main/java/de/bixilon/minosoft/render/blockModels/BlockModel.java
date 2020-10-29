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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BlockModel {
    private ArrayList<SubBlock> subBlocks;
    private boolean[] full; // minor memory improvement over a Map

    public BlockModel(JsonObject block, JsonObject allModels) {
        subBlocks = load(block, allModels);
    }

    public BlockModel(BlockModel blockModel, JsonObject json) {
        if (blockModel != null) {
            subBlocks = blockModel.getSubBlocks();
        } else {
            subBlocks = new ArrayList<>();
        }
        if (json.has("x")) {
            rotate(Axis.X, json.get("x").getAsInt());
        }
        if (json.has("y")) {
            rotate(Axis.Y, json.get("y").getAsInt());
        }
        if (json.has("z")) {
            rotate(Axis.Z, json.get("z").getAsInt());
        }
        full = createFullValues();
    }

    private void rotate(Axis axis, int rotation) {
        for (SubBlock subBlock : subBlocks) {
            subBlock.rotate(axis, rotation);
        }
    }

    public BlockModel() {
    }

    static ArrayList<SubBlock> load(JsonObject json, JsonObject allModels, HashMap<String, String> variables) {
        ArrayList<SubBlock> result = new ArrayList<>();
        if (json.has("textures")) {
            // read the textures into a variable hashmap
            JsonObject textures = json.getAsJsonObject("textures");
            for (String texture : textures.keySet()) {
                if (texture.contains("#") && variables.containsKey(texture)) {
                    variables.put("#" + texture, variables.get(texture));
                } else {
                    variables.put("#" + texture, textures.get(texture).getAsString());
                }
            }
        }
        if (json.has("elements")) {
            for (JsonElement subBlockJson : json.get("elements").getAsJsonArray()) {
                result.add(new SubBlock(subBlockJson.getAsJsonObject(), variables));
            }
        } else if (json.has("parent") && !json.get("parent").getAsString().equals("block/block")) {
            String parent = json.get("parent").getAsString();
            if (parent.equals("block/block")) {
                return result;
            }
            String parentIdentifier = parent.substring(parent.lastIndexOf("/") + 1);
            result.addAll(load(allModels.get(parentIdentifier).getAsJsonObject(), allModels, variables));
        }
        return result;
    }

    static ArrayList<SubBlock> load(JsonObject json, JsonObject allModels) {
        return load(json, allModels, new HashMap<>());
    }

    private boolean[] createFullValues() {
        boolean[] result = new boolean[6];
        outer:
        for (FaceOrientation orientation : FaceOrientation.values()) {
            for (SubBlock subBlock : subBlocks) {
                if (subBlock.getFull()[orientation.getId()]) {
                    result[orientation.getId()] = true;
                    continue outer;
                }
            }
        }
        return result;
    }

    public boolean isFull(FaceOrientation orientation) {
        return full[orientation.getId()];
    }

    public ArrayFloatList prepare(HashSet<FaceOrientation> facesToDraw, BlockPosition position, Block block) {
        ArrayFloatList result = new ArrayFloatList();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getFaces(facesToDraw, position));
        }
        return result;
    }

    public boolean isFull() {
        return true;
    }

    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getTextures());
        }
        return result;
    }

    public void applyTextures(String mod, TextureLoader loader) {
        for (SubBlock subBlock : subBlocks) {
            subBlock.applyTextures(mod, loader);
        }
    }

    public ArrayList<SubBlock> getSubBlocks() {
        return subBlocks;
    }

    public boolean[] getFull() {
        return full;
    }
}

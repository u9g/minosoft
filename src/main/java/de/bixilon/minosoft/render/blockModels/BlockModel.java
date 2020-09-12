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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotation;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockModel {
    HashMap<BlockConfiguration, HashSet<SubBlock>> blockConfigurationStates;
    boolean isFull;

    public BlockModel(JsonObject block, String mod) {
        blockConfigurationStates = new HashMap<>();

        if (block.has("blockModel")) {
            blockConfigurationStates.put(new BlockConfigurationTrue(),
                    load(mod, block.get("blockModel").getAsString()));
        } else if (block.has("states")) {
            for (JsonElement element : block.get("states").getAsJsonArray()) {
                JsonObject state = element.getAsJsonObject();
                BlockConfiguration configuration = new BlockConfiguration(state.get("properties").getAsJsonObject());
                blockConfigurationStates.put(configuration,
                        load(mod, state.get("blockModel").getAsString()));
            }
        }
        // TODO
        isFull = true;
    }

    public BlockModel() {
    }

    public static HashSet<SubBlock> load(String mod, String identifier, HashMap<String, String> variables) {
        String path = Config.homeDir + "assets/" + mod + "/models/block/" + identifier + ".json";
        JsonObject json = null;
        try {
            json = readJsonFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (json == null) {
            Log.warn("File not found: " + path);
            return null;
        }
        HashSet<SubBlock> result = new HashSet<>();
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
            String parentIdentifier = parent.substring(parent.lastIndexOf("/") + 1);
            result.addAll(load(mod, parentIdentifier, variables));
        }
        return result;
    }

    public static HashSet<Face> prepareBlockState(HashSet<SubBlock> subBlocks,
                                                  HashMap<FaceOrientation, Boolean> adjacentBlocks, Block block) {
        HashSet<Face> result = new HashSet<>();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getFaces(block, adjacentBlocks));
        }
        return result;
    }

    public boolean isFull() {
        return isFull;
    }

    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        for (Map.Entry<BlockConfiguration, HashSet<SubBlock>> entry : blockConfigurationStates.entrySet()) {
            if (entry.getKey().contains(block)) {
                return prepareBlockState(entry.getValue(), adjacentBlocks, block);
            }
        }
        Log.warn("no matching blockConfiguration found! Block: " + block.toString());
        return new HashSet<>();
    }

    public HashSet<SubBlock> load(String mod, String identifier) {
        return load(mod, identifier, new HashMap<>());
    }

    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        for (HashSet<SubBlock> subBlocks : blockConfigurationStates.values()) {
            for (SubBlock subBlock : subBlocks) {
                result.addAll(subBlock.getTextures());
            }
        }
        return result;
    }

    public void applyTextures(String mod, TextureLoader loader) {
        for (HashSet<SubBlock> subBlocks : blockConfigurationStates.values()) {
            applyConfigurationTextures(subBlocks, mod, loader);
        }
    }

    public static void applyConfigurationTextures(HashSet<SubBlock> subBlocks, String mod, TextureLoader loader) {
        for (SubBlock subBlock : subBlocks) {
            subBlock.applyTextures(mod, loader);
        }
    }

    public static HashSet<Face> prepareState(HashSet<SubBlock> subBlocks, BlockRotation rotation) {
        HashSet<Face> result = new HashSet<>();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getFacesSimple(new Block("", "", rotation)));
        }
        return result;
    }

    public HashSet<String> getTextures(HashSet<SubBlock> subBlocks) {
        HashSet<String> result = new HashSet<>();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getTextures());
        }
        return result;
    }
}

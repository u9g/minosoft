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
import de.bixilon.minosoft.render.fullFace.FaceOrientation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockDescription {
    HashSet<SubBlock> defaultState;
    HashMap<BlockConfiguration, HashSet<SubBlock>> blockConfigurationStates;
    boolean isFull;

    public BlockDescription(JsonElement child, String identifier, String mod) {
        if (child.getAsString().equals("invisible")) {
            return;
        } else if (child.getAsString().equals("regular")) {
            defaultState = load(mod, identifier, new HashMap<>());
        } else {
            JsonObject childJson = child.getAsJsonObject();
            for (String state : childJson.keySet()) {
                if (state.equals("else")) {
                    defaultState = load(mod, childJson.get("else").getAsString(), new HashMap<>());
                }
                blockConfigurationStates.put(new BlockConfiguration(state),
                        load(mod, childJson.get(state).getAsString()));
            }
        }
        for (SubBlock subBlock : defaultState) {
            if (subBlock.isFull()) {
                isFull = true;
            }
        }
    }

    public static HashSet<SubBlock> load(String mod, String identifier, HashMap<String, String> variables) {
        String path = Config.homeDir + "assets/" + mod + "/models/block/" + identifier + ".json";
        JsonObject json = null;
        try {
            json = readJsonFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashSet<SubBlock> result = new HashSet<>();
        try {
            // read the textures into a variable hashmap
            JsonObject textures = json.getAsJsonObject("textures");
            for (String texture : textures.keySet()) {
                if (texture.contains("#") && variables.containsKey(texture)) {
                    variables.put("#" + texture, variables.get(texture));
                } else {
                    variables.put("#" + texture, textures.get(texture).getAsString());
                }
            }
        } catch (Exception ignored) {
        }
        if (json.has("elements")) {
            for (JsonElement subBlockJson : json.get("elements").getAsJsonArray()) {
                result.add(new SubBlock(subBlockJson.getAsJsonObject(), variables));
            }
        } else if (json.has("parent") && !json.get("parent").getAsString().equals("block/block")) {
            String parent = json.get("parent").getAsString();
            String parentIdentifier = parent.substring(parent.lastIndexOf("/") + 1);
            result.addAll(load(mod, parentIdentifier, variables));
        } else {
            throw new IllegalArgumentException("json does not have a parent nor subblocks");
        }
        return result;
    }

    private HashSet<SubBlock> load(String mod, String identifier) {
        return load(mod, identifier, new HashMap<>());
    }

    public boolean isFull() {
        return isFull;
    }

    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        HashSet<Face> result = new HashSet<>();
        for (SubBlock subBlock : defaultState) {
            result.addAll(subBlock.getFaces(adjacentBlocks));
        }
        return result;
    }
}

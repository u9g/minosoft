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

package de.bixilon.minosoft.render.blockModels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.render.fullFace.FaceOrientation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockDescription {
    HashSet<SubBlock> subBlocks;
    boolean isFull;

    public BlockDescription(JsonObject json, HashMap<String, String> variables) {
        subBlocks = new HashSet<>();
        if (!json.has("textures")) {
            //throw new IllegalArgumentException("could not find 'textures' in json");
        }
        try {
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
                subBlocks.add(new SubBlock(subBlockJson.getAsJsonObject(), variables));
            }
        } else if (json.has("parent") && !json.get("parent").getAsString().equals("block/block")) {
            String parent = json.get("parent").getAsString();
            String path = Config.homeDir + "assets/minecraft/models/" + parent + ".json";
            try {
                subBlocks.addAll(new BlockDescription(readJsonFromFile(path), variables).subBlocks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("json does not have a parent nor subblocks");
        }

        for (SubBlock subBlock : subBlocks) {
            if (subBlock.isFull()) {
                isFull = true;
            }
        }
    }

    public BlockDescription(JsonObject json) {
        this(json, new HashMap<>());
    }

    public boolean isFull() {
        return isFull;
    }

    public HashSet<Face> prepare(HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        HashSet<Face> result = new HashSet<>();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getFaces(adjacentBlocks));
        }
        return result;
    }
}

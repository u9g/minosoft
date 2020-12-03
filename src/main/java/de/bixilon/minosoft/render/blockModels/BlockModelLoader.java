/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger, Lukas Eisenhauer
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.protocol.protocol.ProtocolDefinition;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;
import de.bixilon.minosoft.util.Pair;
import de.bixilon.minosoft.util.Util;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockModelLoader {
    /**
     * @param data json file which describes all block models
     * @return blockModels, textureID
     */
    public static Pair<HashMap<String, BlockModelInterface>, Integer> load(String modName, JsonObject data) {
        HashMap<String, float[]> tints = new HashMap<>();
        if (modName.equals(ProtocolDefinition.DEFAULT_MOD)) {
            try {
                tints = readTints(Util.readJsonAsset("mapping/tints.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, HashSet<SubBlock>> blockModels = loadModels(data);

        TextureLoader textureLoader = new TextureLoader(getTextures(blockModels), tints);

        applyTextures(blockModels, textureLoader);
        HashMap<String, BlockModelInterface> modelMap = loadBlocks(data, blockModels);
        return new Pair<>(modelMap, textureLoader.getTextureID());
    }

    private static HashMap<String, BlockModelInterface> loadBlocks(JsonObject data, HashMap<String, HashSet<SubBlock>> blockModels) {
        HashMap<String, BlockModelInterface> modelMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> blockEntry : data.get("blockStates").getAsJsonObject().entrySet()) {
            if (blockEntry.getKey().equals("grass_block")) {
                int x = 0;
            }
            JsonObject block = blockEntry.getValue().getAsJsonObject();
            if (block.has("states")) {
                JsonArray states = block.get("states").getAsJsonArray();
                modelMap.put(blockEntry.getKey(), new BlockModel(blockModels, states));
            } else if (block.has("conditional")) {
                modelMap.put(blockEntry.getKey(), new ConditionalModel(blockModels, block.get("conditional").getAsJsonArray()));
            }
        }
        return modelMap;
    }

    private static HashMap<String, HashSet<SubBlock>> loadModels(JsonObject mod) {
        HashMap<String, HashSet<SubBlock>> modMap = new HashMap<>();
        JsonObject allModels = mod.get("blockModels").getAsJsonObject();
        for (Map.Entry<String, JsonElement> block : allModels.entrySet()) {
            modMap.put(block.getKey(), BlockModelInterface.load(block.getValue().getAsJsonObject(), allModels));
        }
        return modMap;
    }

    public static HashSet<String> getTextures(HashMap<String, HashSet<SubBlock>> blockModels) {
        HashSet<String> result = new HashSet<>();
        for (HashSet<SubBlock> subBlocks : blockModels.values()) {
            for (SubBlock subBlock : subBlocks) {
                result.addAll(subBlock.getTextures());
            }
        }
        return result;
    }

    public static void applyTextures(HashMap<String, HashSet<SubBlock>> blockModels, TextureLoader textureLoader) {
        for (HashSet<SubBlock> block : blockModels.values()) {
            for (SubBlock subBlock : block) {
                subBlock.applyTextures(textureLoader);
            }
        }
    }

    private static HashMap<String, float[]> readTints(JsonObject textures) {
        HashMap<String, float[]> result = new HashMap<>();
        for (String textureName : textures.keySet()) {
            ArrayFloatList colorValues = new ArrayFloatList();
            for (JsonElement colorValue : textures.get(textureName).getAsJsonArray()) {
                colorValues.add(colorValue.getAsFloat());
            }
            float[] color = colorValues.toArray();
            result.put(textureName, color);
        }
        return result;
    }
}

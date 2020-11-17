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
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.Blocks;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;
import de.bixilon.minosoft.util.Util;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockModelLoader {
    private final TextureLoader textureLoader;
    private final HashMap<String, HashMap<String, BlockModelInterface>> modelMap;

    public BlockModelLoader(JsonObject data) {
        modelMap = new HashMap<>();
        HashSet<JsonObject> mods = new HashSet<>();
        mods.add(data);
        HashMap<String, float[]> tints = null;
        try {
            tints = readTints(Util.readJsonAsset("mapping/tints.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, HashMap<String, HashSet<SubBlock>>> blockModels = new HashMap<>();
        for (JsonObject mod : mods) {
            String modName = mod.get("mod").getAsString();
            if (!modelMap.containsKey(mod.get("mod").getAsString())) {
                modelMap.put(modName, new HashMap<>());
            }
            blockModels.put(modName, loadModels(mod));
        }
        textureLoader = new TextureLoader(getTextures(blockModels), tints);
        applyTextures(blockModels);
        for (JsonObject mod : mods) {
            loadBlocks(mod, blockModels.get(mod.get("mod").getAsString()));
        }
    }

    private void loadBlocks(JsonObject mod, HashMap<String, HashSet<SubBlock>> blockModels) {
        for (Map.Entry<String, JsonElement> blockEntry : mod.get("blockStates").getAsJsonObject().entrySet()) {
            JsonObject block = blockEntry.getValue().getAsJsonObject();
            if (block.has("states")) {
                JsonArray states = block.get("states").getAsJsonArray();
                modelMap.get(mod.get("mod").getAsString()).put(blockEntry.getKey(), new BlockModel(blockModels, states));
            } else if (block.has("conditional")) {
                modelMap.get(mod.get("mod").getAsString()).put(blockEntry.getKey(), new ConditionalModel(blockModels, block.get("conditional").getAsJsonArray()));
            }
        }
    }

    private HashMap<String, HashSet<SubBlock>> loadModels(JsonObject mod) {
        HashMap<String, HashSet<SubBlock>> modMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> block : mod.getAsJsonObject("blockModels").entrySet()) {
            modMap.put(block.getKey(), BlockModelInterface.load(block.getValue().getAsJsonObject(), mod.getAsJsonObject("blockModels")));
        }
        return modMap;
    }

    public HashMap<String, HashSet<String>> getTextures(HashMap<String, HashMap<String, HashSet<SubBlock>>> blockModels) {
        HashMap<String, HashSet<String>> textures = new HashMap<>();
        for (Map.Entry<String, HashMap<String, HashSet<SubBlock>>> mod : blockModels.entrySet()) {
            HashSet<String> modTextures = new HashSet<>();
            for (HashSet<SubBlock> subBlocks : mod.getValue().values()) {
                for (SubBlock subBlock : subBlocks) {
                    modTextures.addAll(subBlock.getTextures());
                }
            }
            textures.put(mod.getKey(), modTextures);
        }
        return textures;
    }

    public void applyTextures(HashMap<String, HashMap<String, HashSet<SubBlock>>> blockModels) {
        for (Map.Entry<String, HashMap<String, HashSet<SubBlock>>> mod : blockModels.entrySet()) {
            for (Map.Entry<String, HashSet<SubBlock>> block : mod.getValue().entrySet()) {
                for (SubBlock subBlock : block.getValue()) {
                    subBlock.applyTextures(mod.getKey(), textureLoader);
                }
            }
        }
    }

    private HashMap<String, float[]> readTints(JsonObject json) {
        HashMap<String, float[]> result = new HashMap<>();
        if (json.has("tinted_textures")) {
            JsonObject textures = json.get("tinted_textures").getAsJsonObject();
            for (String textureName : textures.keySet()) {
                ArrayFloatList colorValues = new ArrayFloatList();
                for (JsonElement colorValue : textures.get(textureName).getAsJsonArray()) {
                    colorValues.add(colorValue.getAsFloat());
                }
                float[] color = colorValues.toArray();
                result.put(textureName, color);
            }
        }
        return result;
    }

    public BlockModelInterface getBlockModel(Block block) {
        BlockModelInterface model = modelMap.get(block.getMod()).get(block.getIdentifier());
        if (model == null) {
            throw new RuntimeException(String.format("Block model for could not be found: %s", block));
        }
        return model;
    }

    public boolean isFull(Block block, FaceOrientation orientation) {
        if (block == null || block.equals(Blocks.nullBlock)) {
            return false;
        }
        return getBlockModel(block).full(block, orientation);
    }

    public boolean isFull(Block block) {
        return block != null && !block.equals(Blocks.nullBlock);
    }

    public ArrayFloatList prepare(Block block, HashSet<FaceOrientation> facesToDraw, BlockPosition position) {
        return getBlockModel(block).prepare(facesToDraw, position, block);
    }

    public TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public void clear() {
        modelMap.clear();
    }
}

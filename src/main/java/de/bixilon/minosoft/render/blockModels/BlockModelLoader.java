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
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.Blocks;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.texture.TextureLoader;
import de.bixilon.minosoft.util.Util;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class BlockModelLoader {
    HashMap<BlockModelBlockWrapper, BlockModel> blockMap;
    TextureLoader loader;

    public BlockModelLoader() {
        blockMap = new HashMap<>();
        HashMap<String, JsonObject> mods = new HashMap<>();
        HashMap<String, HashMap<String, float[]>> tints = new HashMap<>();
        try {
            //TODO: modding
            mods.put("minecraft", Util.readJsonAsset("mapping/blockModels.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, HashMap<String, BlockModel>> blockModels = new HashMap<>();
        for (Map.Entry<String, JsonObject> mod : mods.entrySet()) {
            blockModels.put(mod.getKey(), loadModels(mod));
            tints.put(mod.getKey(), readTints(mod.getValue()));
        }
        loader = new TextureLoader(getTextures(blockModels), tints);
        applyTextures(blockModels);
        for (Map.Entry<String, JsonObject> mod : mods.entrySet()) {
            loadBlocks(mod, blockModels.get(mod.getKey()));
        }
    }

    private void loadBlocks(Map.Entry<String, JsonObject> mod, HashMap<String, BlockModel> blockModels) {
        for (Map.Entry<String, JsonElement> blockEntry : mod.getValue().get("blockStates").getAsJsonObject().entrySet()) {
            JsonObject block = blockEntry.getValue().getAsJsonObject();
            if (block.has("variants")) {
                JsonObject variants = block.get("variants").getAsJsonObject();
                for (Map.Entry<String, JsonElement> variant : variants.entrySet()) {
                    if (variant.getValue().isJsonArray()) {
                        ArrayList<BlockModel> models = new ArrayList<>();
                        for (JsonElement model : variant.getValue().getAsJsonArray()) {
                            models.add(new BlockModel(blockModels.get(blockEntry.getKey()), model.getAsJsonObject()));
                        }
                        blockMap.put(new BlockModelBlockWrapper(new Block(mod.getKey(), blockEntry.getKey(), variant.getKey())), new BlockModel(models));
                    } else {
                        String fullModel = variant.getValue().getAsJsonObject().get("model").getAsString();
                        String modelName = fullModel.substring(fullModel.indexOf("/") + 1);
                        BlockModel model = blockModels.get(modelName);
                        blockMap.put(new BlockModelBlockWrapper(new Block(mod.getKey(), blockEntry.getKey(), variant.getKey())), new BlockModel(model, variant.getValue().getAsJsonObject()));
                    }
                }
            }
        }
    }

    private HashMap<String, BlockModel> loadModels(Map.Entry<String, JsonObject> mod) {
        HashMap<String, BlockModel> modMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> block : mod.getValue().get("blockModels").getAsJsonObject().entrySet()) {
            modMap.put(block.getKey(), new BlockModel(block.getValue().getAsJsonObject(), mod.getValue().get("blockModels").getAsJsonObject()));
        }
        return modMap;
    }

    public HashMap<String, HashSet<String>> getTextures(HashMap<String, HashMap<String, BlockModel>> blockModels) {
        HashMap<String, HashSet<String>> textures = new HashMap<>();
        for (Map.Entry<String, HashMap<String, BlockModel>> mod : blockModels.entrySet()) {
            HashSet<String> modTextures = new HashSet<>();
            for (BlockModel blockModel : mod.getValue().values()) {
                modTextures.addAll(blockModel.getAllTextures());
            }
            textures.put(mod.getKey(), modTextures);
        }
        return textures;
    }

    public void applyTextures(HashMap<String, HashMap<String, BlockModel>> blockModels) {
        for (Map.Entry<String, HashMap<String, BlockModel>> mod : blockModels.entrySet()) {
            for (Map.Entry<String, BlockModel> block : mod.getValue().entrySet()) {
                block.getValue().applyTextures(mod.getKey(), loader);
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

    public BlockModel getBlockModel(Block block) {
        BlockModel model = blockMap.get(new BlockModelBlockWrapper(block));
        if (model == null) {
            throw new RuntimeException("block " + block + " could not be found");
        }
        return blockMap.get(new BlockModelBlockWrapper(block));
    }

    public boolean isFull(Block block, FaceOrientation orientation) {
        if (block == null || block.equals(Blocks.nullBlock)) {
            return false;
        }
        return getBlockModel(block).isFull(orientation);
    }

    public boolean isFull(Block block) {
        return block != null && !block.equals(Blocks.nullBlock);
    }

    public ArrayFloatList prepare(Block block, HashSet<FaceOrientation> facesToDraw, BlockPosition position) {
        return getBlockModel(block).prepare(facesToDraw, position);
    }

    public TextureLoader getTextureLoader() {
        return loader;
    }
}
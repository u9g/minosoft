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
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.specialModels.*;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static de.bixilon.minosoft.util.Util.readJsonAsset;

public class BlockModelLoader {
    private final HashMap<String, HashMap<String, BlockModelInterface>> blockDescriptionMap;
    private final HashMap<String, HashSet<String>> textures;
    HashMap<String, HashMap<String, float[]>> tints;

    public BlockModelLoader() {
        blockDescriptionMap = new HashMap<>();
        tints = new HashMap<>();
        textures = new HashMap<>();
        try {
            JsonObject json = readJsonAsset("mapping/blockModels.json");
            String mod = "minecraft";
            tints.put(mod, readTints(json));
            textures.put(mod, loadModels(json.get("blocks").getAsJsonObject(), mod));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, HashMap<String, float[]>> getTints() {
        return tints;
    }

    public HashMap<String, HashSet<String>> getTextures() {
        return textures;
    }

    public void applyTextures(TextureLoader loader) {
        for (Map.Entry<String, HashMap<String, BlockModelInterface>> mod : blockDescriptionMap.entrySet()) {
            for (Map.Entry<String, BlockModelInterface> block : mod.getValue().entrySet()) {
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

    private HashSet<String> loadModels(JsonObject blockList, String mod) {
        HashSet<String> result = new HashSet<>();
        blockDescriptionMap.put(mod, new HashMap<>());
        for (String identifier : blockList.keySet()) {
            JsonObject block = blockList.get(identifier).getAsJsonObject();
            result.addAll(loadModel(mod, identifier, block));
        }
        return result;
    }

    private HashSet<String> loadModel(String mod, String identifier, JsonObject block) {
        HashSet<String> result = new HashSet<>();
        try {
            String type = "";

            if (block.has("type")) {
                type = block.get("type").getAsString();
            }
            BlockModelInterface model = switch (type) {
                case "fire" -> new FireModel(block, mod);
                case "stairs" -> new StairsModel(block, mod);
                case "wire" -> new WireModel(block, mod);
                case "crop" -> new CropModel(block, mod);
                case "door" -> new DoorModel(block, mod);
                case "fence" -> new FenceModel(block, mod);
                case "mushroom" -> new MushroomModel(block, mod);
                default -> new BlockModel(block, mod);
            };
            result.addAll(model.getAllTextures());
            HashMap<String, BlockModelInterface> modMap = blockDescriptionMap.get(mod);
            modMap.put(identifier, model);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(mod + ":" + identifier);
            System.exit(-1);
        }
        return result;
    }

    public BlockModelInterface getBlockDescription(Block block) {
        HashMap<String, BlockModelInterface> modList = blockDescriptionMap.get(block.getMod());
        if (modList == null || !modList.containsKey(block.getIdentifier())) {
            throw new IllegalArgumentException(String.format("No block %s:%s found", block.getMod(), block.getIdentifier()));
        }
        return modList.get(block.getIdentifier());
    }

    public boolean isFull(Block block) {
        if (block == Blocks.nullBlock || block == null) {
            return false;
        }
        BlockModelInterface description = getBlockDescription(block);
        if (description == null) {
            return false;
        }
        return description.isFull();
    }

    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        BlockModelInterface description = getBlockDescription(block);
        if (description == null) {
            return new HashSet<>();
        }
        return description.prepare(block, adjacentBlocks);
    }
}
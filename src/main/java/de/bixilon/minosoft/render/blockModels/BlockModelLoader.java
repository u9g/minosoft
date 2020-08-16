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
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.Face.FaceOrientation;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockModelLoader {
    private final HashMap<String, HashMap<String, BlockDescription>> blockDescriptionMap;
    TextureLoader textureLoader;

    public BlockModelLoader() {
        blockDescriptionMap = new HashMap<>();
        HashMap<String, HashMap<String, float[]>> tints = new HashMap<>();
        HashMap<String, HashSet<String>> textures = new HashMap<>();
        HashMap<String, HashSet<String>> ignoredTextures = new HashMap<>();
        try {
            String folderPath = Config.homeDir + "assets/mapping/blockModels/";
            for (File file : new File(folderPath).listFiles()) {
                JsonObject json = readJsonFromFile(file.getAbsolutePath());
                String mod = file.getName().substring(0, file.getName().lastIndexOf('.'));
                tints.put(mod, readTints(json));
                ignoredTextures.put(mod, readIgnored(json));
                textures.put(mod, loadModels(json.get("blocks").getAsJsonObject(), mod));
            }
            textureLoader = new TextureLoader(textures, tints, ignoredTextures);
            applyTextures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.info("finished loading all blocks");
    }

    private HashSet<String> readIgnored(JsonObject json) {
        if (!json.has("ignored_textures")) {
            return new HashSet<>();
        }
        HashSet<String> result = new HashSet<>();
        for (JsonElement texture : json.get("ignored_textures").getAsJsonArray()) {
            result.add(texture.getAsString());
        }
        return result;
    }

    private void applyTextures() {
        for (Map.Entry<String, HashMap<String, BlockDescription>> mod : blockDescriptionMap.entrySet()) {
            for (Map.Entry<String, BlockDescription> block : mod.getValue().entrySet()) {
                block.getValue().applyTextures(mod.getKey(), textureLoader);
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
            JsonElement child = blockList.get(identifier);
            result.addAll(loadModel(mod, identifier, child));
        }
        return result;
    }

    private HashSet<String> loadModel(String mod, String identifier, JsonElement child) {
        HashSet<String> result = new HashSet<>();
        try {
            HashMap<String, BlockDescription> modList = blockDescriptionMap.get(mod);
            BlockDescription description = new BlockDescription(child, identifier, mod);
            result.addAll(description.getAllTextures());
            modList.put(identifier, description);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(mod + ":" + identifier);
            System.exit(-1);
        }
        return result;
    }

    public BlockDescription getBlockDescription(Block block) {
        if (!blockDescriptionMap.containsKey(block.getMod())) {
            System.out.println(String.format("No mod %s found", block.getMod()));
            //System.exit(-1);
        }
        HashMap<String, BlockDescription> modList = blockDescriptionMap.get(block.getMod());
        if (!modList.containsKey(block.getIdentifier())) {
            System.out.println(String.format("No block %s:%s found", block.getMod(), block.getIdentifier()));
            //System.exit(-1);
        }
        return modList.get(block.getIdentifier());
    }

    public boolean isFull(Block block) {
        if (block == Blocks.nullBlock || block == null) {
            return false;
        }
        BlockDescription description = getBlockDescription(block);
        if (description == null) {
            return false;
        }
        return description.isFull();
    }

    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        BlockDescription description = getBlockDescription(block);
        if (description == null) {
            return new HashSet<>();
        }
        return description.prepare(block, adjacentBlocks);
    }

    public TextureLoader getTextureLoader() {
        return textureLoader;
    }
}
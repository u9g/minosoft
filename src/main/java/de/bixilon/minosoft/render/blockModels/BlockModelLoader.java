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
import de.bixilon.minosoft.render.fullFace.FaceOrientation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockModelLoader {
    public BlockModelLoader() {
        blockDescriptionMap = new HashMap<>();
        try {
            String folderPath = Config.homeDir + "assets/mapping/blockModels/";
            for (File file : new File(folderPath).listFiles()) {
                JsonObject blockList = readJsonFromFile(file.getAbsolutePath());
                String mod = file.getName().substring(0, file.getName().lastIndexOf('.'));
                loadModels(blockList, mod);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        Log.info("finished loading all block descriptions");
    }

    final HashMap<String, HashMap<String, BlockDescription>> blockDescriptionMap;

    private void loadModels(JsonObject blockList, String mod) {
        blockDescriptionMap.put(mod, new HashMap<>());
        for (String identifier : blockList.keySet()) {
            JsonElement child = blockList.get(identifier);
            loadModel(mod, identifier, child);
        }
    }

    private void loadModel(String mod, String identifier, JsonElement child) {
        try {
            HashMap<String, BlockDescription> modList = blockDescriptionMap.get(mod);
            BlockDescription description = new BlockDescription(child, identifier, mod);
            modList.put(identifier, description);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(mod + ":" + identifier);
            System.exit(-1);
        }

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
}
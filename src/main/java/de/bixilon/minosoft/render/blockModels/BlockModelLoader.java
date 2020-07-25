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

import com.google.gson.JsonObject;
import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;

import java.io.IOException;
import java.util.HashMap;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockModelLoader {
    final HashMap<String, DrawDescription> drawDescriptionMap;

    public BlockModelLoader() {
        drawDescriptionMap = new HashMap<>();
        try {
            loadModels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModels() throws IOException {
        for (Block block : Blocks.getBlockList()) {
            String mod = block.getMod();
            String identifier = block.getIdentifier();
            if (handleProperties(block)) {
                return;
            }
            if (identifier.contains("pane")) {
                // TODO: handle glass panes
                continue;
            }
            if (identifier.equals("large_fern")) {
                loadModel(mod, identifier + "_bottom");
                loadModel(mod, identifier + "_top");
                continue;
            }
            if (drawDescriptionMap.containsKey((mod + ":" + identifier))) {
                // a description for that block already exists, checking because Blocks.getBlockList()
                // returns all blocks with all possible combinations
                continue;
            }
            loadModel(mod, identifier);
        }
    }

    private boolean handleProperties(Block block) {
        return !block.getProperties().contains(BlockProperties.NONE) && block.getProperties().size() != 0;
    }

    private void loadModel(String mod, String identifier) throws IOException {
        String path = Config.homeDir + "assets/" + mod + "/models/block/" + identifier + ".json";
        JsonObject object = readJsonFromFile(path);
        DrawDescription description = new DrawDescription(object);
        drawDescriptionMap.put(mod + ":" + identifier, description);
    }

    public DrawDescription getDrawDescription(Block block) {
        if (!drawDescriptionMap.containsKey(block)) {
            throw new IllegalArgumentException(String.format("No description for block %s found", block));
        }
        return drawDescriptionMap.get(block.getMod() + ":" + block.getIdentifier());
    }

    public boolean isFull(Block block) {
        if (block == Blocks.nullBlock || block == null) {
            return false;
        }
        return getDrawDescription(block).isFull();
    }
}

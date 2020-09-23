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

package de.bixilon.minosoft.render.blockModels.specialModels;

import com.google.gson.JsonObject;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashMap;
import java.util.HashSet;

import static de.bixilon.minosoft.render.blockModels.specialModels.BlockModel.*;

public class CropModel implements BlockModelInterface {
    private final HashMap<String, HashSet<SubBlock>> modelMap;

    public CropModel(JsonObject block, String mod) {
        int stages = block.get("stages").getAsInt();
        modelMap = new HashMap<>();
        for (int i = 0; i < stages; i++) {
            modelMap.put(String.format("%s%d", "AGE_", i),
                    BlockModelInterface.load(mod,
                            String.format("%s%d", block.get("base_name").getAsString(), i)));
        }
    }

    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        for (BlockProperties property : block.getProperties()) {
            if (modelMap.containsKey(property.name())) {
                return prepareBlockState(modelMap.get(property.name()), adjacentBlocks, block);
            }
        }
        Log.warn("failed to prepare block: " + block.toString());
        return new HashSet<>();
    }

    public boolean isFull() {
        return false;
    }

    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        for (HashSet<SubBlock> subBlocks : modelMap.values()) {
            for (SubBlock subBlock : subBlocks) {
                result.addAll(subBlock.getTextures());
            }
        }
        return result;
    }

    public void applyTextures(String mod, TextureLoader loader) {
        for (HashSet<SubBlock> subBlocks : modelMap.values()) {
            BlockModelInterface.applyConfigurationTextures(subBlocks, mod, loader);
        }
    }
}

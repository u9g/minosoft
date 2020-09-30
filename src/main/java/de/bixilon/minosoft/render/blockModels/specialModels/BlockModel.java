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

package de.bixilon.minosoft.render.blockModels.specialModels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.BlockConfiguration;
import de.bixilon.minosoft.render.blockModels.BlockConfigurationTrue;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockModel implements BlockModelInterface {
    private final HashMap<BlockConfiguration, HashSet<SubBlock>> blockConfigurationStates;
    private final boolean isFull;

    public BlockModel(JsonObject block, String mod) {
        blockConfigurationStates = new HashMap<>();

        if (block.has("blockModel")) {
            blockConfigurationStates.put(new BlockConfigurationTrue(),
                    BlockModelInterface.load(mod, block.get("blockModel").getAsString()));
        } else if (block.has("states")) {
            for (JsonElement element : block.get("states").getAsJsonArray()) {
                JsonObject state = element.getAsJsonObject();
                BlockConfiguration configuration = new BlockConfiguration(state.get("properties").getAsJsonObject());
                blockConfigurationStates.put(configuration,
                        BlockModelInterface.load(mod, state.get("blockModel").getAsString()));
            }
        }
        // TODO
        isFull = true;
    }

    public static ArrayFloatList prepareBlockState(HashSet<SubBlock> subBlocks, HashSet<FaceOrientation> facesToDraw, Block block, BlockPosition position) {
        ArrayFloatList result = new ArrayFloatList();
        for (SubBlock subBlock : subBlocks) {
            result.addAll(subBlock.getFaces(block, facesToDraw, position));
        }
        return result;
    }

    public boolean isFull() {
        return isFull;
    }

    public ArrayFloatList prepare(Block block, HashSet<FaceOrientation> facesToDraw,
                                 BlockPosition position) {
        for (Map.Entry<BlockConfiguration, HashSet<SubBlock>> entry : blockConfigurationStates.entrySet()) {
            if (entry.getKey().contains(block)) {
                return prepareBlockState(entry.getValue(), facesToDraw, block, position);
            }
        }
        Log.warn("no matching blockConfiguration found! Block: " + block.toString());
        return new ArrayFloatList();
    }

    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        for (HashSet<SubBlock> subBlocks : blockConfigurationStates.values()) {
            for (SubBlock subBlock : subBlocks) {
                result.addAll(subBlock.getTextures());
            }
        }
        return result;
    }

    public void applyTextures(String mod, TextureLoader loader) {
        for (HashSet<SubBlock> subBlocks : blockConfigurationStates.values()) {
            BlockModelInterface.applyConfigurationTextures(subBlocks, mod, loader);
        }
    }
}

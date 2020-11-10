/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
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
import de.bixilon.minosoft.data.mappings.blocks.BlockProperties;
import de.bixilon.minosoft.data.mappings.blocks.BlockRotations;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockModel implements BlockModelInterface {
    private final HashMap<HashSet<String>, HashSet<SubBlock>> stateMap;
    private final HashMap<HashSet<String>, boolean[]> full;             // minor memory improvement over a Map FaceOrientation -> boolean and also performance should be boosted

    public BlockModel(HashMap<String, HashSet<SubBlock>> blockModels, JsonArray states) {
        stateMap = new HashMap<>();
        full = new HashMap<>();
        for (JsonElement element : states) {
            JsonObject state = element.getAsJsonObject();
            HashSet<SubBlock> model = blockModels.get(state.get("model").getAsString()).stream().map(SubBlock::new).collect(Collectors.toCollection(HashSet::new));
            HashSet<String> properties = new HashSet<>();
            for (Map.Entry<String, JsonElement> property : state.getAsJsonObject("properties").entrySet()) {
                if (BlockProperties.propertiesMapping.containsKey(property.getKey())) {
                    properties.add(BlockProperties.propertiesMapping.get(property.getKey()).get(property.getValue().getAsString()).name());
                } else if (BlockRotations.rotationMapping.containsKey(property.getValue().getAsString())) {
                    properties.add(BlockRotations.rotationMapping.get(property.getValue().getAsString()).name());
                }
            }
            for (Axis axis : Axis.values()) {
                String lowercase = axis.name().toLowerCase();
                if (state.has(lowercase)) {
                    BlockModelInterface.rotateModel(model, axis, state.get(lowercase).getAsInt());
                }
            }
            stateMap.put(properties, model);
            full.put(properties, createFullValues(model));
        }
    }

    private static boolean[] createFullValues(HashSet<SubBlock> subBlocks) {
        boolean[] result = new boolean[6];
        outer:
        for (FaceOrientation orientation : FaceOrientation.values()) {
            for (SubBlock subBlock : subBlocks) {
                if (subBlock.getFull()[orientation.getId()]) {
                    result[orientation.getId()] = true;
                    continue outer;
                }
            }
        }
        return result;
    }

    private static HashSet<String> getProperties(Block block) {
        HashSet<String> result = new HashSet<>();
        if (block.getRotation() != BlockRotations.NONE) {
            result.add(block.getRotation().name());
        }
        for (BlockProperties property : block.getProperties()) {
            result.add(property.name());
        }
        return result;
    }

    public boolean full(Block block, FaceOrientation orientation) {
        HashSet<String> properties = getProperties(block);
        for (Map.Entry<HashSet<String>, boolean[]> entry : full.entrySet()) {
            if (properties.containsAll(entry.getKey())) {
                return entry.getValue()[orientation.getId()];
            }
        }
        properties.add(BlockRotations.NONE.name());
        for (Map.Entry<HashSet<String>, boolean[]> entry : full.entrySet()) {
            if (properties.containsAll(entry.getKey())) {
                return entry.getValue()[orientation.getId()];
            }
        }
        Log.warn(String.format("could not find a corresponding block model for block %s", block.toString()));
        return false;
    }

    public ArrayFloatList prepare(HashSet<FaceOrientation> facesToDraw, BlockPosition position, Block block) {
        HashSet<String> properties = getProperties(block);
        for (Map.Entry<HashSet<String>, HashSet<SubBlock>> entry : stateMap.entrySet()) {
            if (properties.containsAll(entry.getKey())) {
                ArrayFloatList result = new ArrayFloatList();
                for (SubBlock subBlock : entry.getValue()) {
                    result.addAll(subBlock.getFaces(facesToDraw, position));
                }
                return result;
            }
        }
        properties.add(BlockRotations.NONE.name());
        for (Map.Entry<HashSet<String>, HashSet<SubBlock>> entry : stateMap.entrySet()) {
            if (properties.containsAll(entry.getKey())) {
                ArrayFloatList result = new ArrayFloatList();
                for (SubBlock subBlock : entry.getValue()) {
                    result.addAll(subBlock.getFaces(facesToDraw, position));
                }
                return result;
            }
        }
        Log.warn(String.format("could not find a corresponding block model for block %s", block.toString()));
        return new ArrayFloatList();
    }

    public boolean isFull() {
        return true;
    }
}

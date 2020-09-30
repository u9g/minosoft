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
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotations;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlock;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashSet;

public class StairsModel implements BlockModelInterface {
    private final HashSet<SubBlock> straight;
    private final HashSet<SubBlock> inner;
    private final HashSet<SubBlock> outer;

    public StairsModel(JsonObject block, String mod) {
        straight = BlockModelInterface.load(mod, block.get("straight").getAsString());
        inner = BlockModelInterface.load(mod, block.get("inner").getAsString());
        outer = BlockModelInterface.load(mod, block.get("outer").getAsString());
    }

    public static HashSet<Face> prepareCorner(HashSet<SubBlock> subBlocks, BlockProperties property, BlockRotations rotation) {
        if (property.name().contains("LEFT")) {
            return BlockModelInterface.prepareState(subBlocks, rotation);
        }
        return BlockModelInterface.prepareState(subBlocks, rotationAdjust.get(rotation));
    }

    @Override
    public HashSet<Face> prepare(Block block, HashSet<FaceOrientation> facesToDraw) {
        HashSet<BlockProperties> properties = block.getProperties();

        for (BlockProperties property : properties) {
            if (property.name().contains("INNER")) {
                return prepareCorner(outer, property, block.getRotation());
            } else if (property.name().contains("OUTER")) {
                return prepareCorner(inner, property, block.getRotation());
            }
        }
        return BlockModelInterface.prepareState(straight, rotationAdjust.get(block.getRotation()));
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public HashSet<String> getAllTextures() {
        HashSet<String> result = new HashSet<>();
        result.addAll(BlockModelInterface.getTextures(straight));
        result.addAll(BlockModelInterface.getTextures(inner));
        result.addAll(BlockModelInterface.getTextures(outer));
        return result;
    }

    @Override
    public void applyTextures(String mod, TextureLoader loader) {
        BlockModelInterface.applyConfigurationTextures(straight, mod, loader);
        BlockModelInterface.applyConfigurationTextures(inner, mod, loader);
        BlockModelInterface.applyConfigurationTextures(outer, mod, loader);
    }
}

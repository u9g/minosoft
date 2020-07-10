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

import de.bixilon.minosoft.game.datatypes.blocks.Blocks;

import java.util.HashMap;
import java.util.Map;

import static de.bixilon.minosoft.game.datatypes.blocks.Blocks.byId;

public class BlockName {
    private static final Map<String, Blocks> nameToBlock = new HashMap<String, Blocks>() {
        {
            //put("air", byId(0));
            put("bedrock", byId(7));
            put("grass_block", byId(2));
            put("dirt", byId(3));
        }
    };

    private static final Map<Blocks, String> blockToName = new HashMap<Blocks, String>() {
        {
            //put("air", byId(0));
            put(byId(7), "bedrock");
            put(byId(2), "grass_block");
            put(byId(3), "dirt");
        }
    };

    public static Blocks getBlockByName(String name) {
        return nameToBlock.get(name);
    }

    public static String getNameByBlock(Blocks block) {
        return blockToName.get(block);
    }
}

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

package de.bixilon.minosoft.game.datatypes.world.palette;

import com.google.common.collect.HashBiMap;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;
import de.bixilon.minosoft.protocol.protocol.InByteBuffer;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class IndirectPalette implements Palette {
    ProtocolVersion version;
    HashBiMap<Integer, Integer> map = HashBiMap.create();
    byte bitsPerBlock;

    public IndirectPalette(byte bitsPerBlock) {
        this.bitsPerBlock = bitsPerBlock;
    }

    @Override
    public Block byId(int id) {
        Block block;
        if (map.containsKey(id)) {
            block = Blocks.getBlock(map.get(id), version);
        } else {
            block = Blocks.getBlock(id, version);
        }
        return block;
    }

    @Override
    public byte getBitsPerBlock() {
        return bitsPerBlock;
    }

    @Override
    public void read(InByteBuffer buffer) {
        this.version = buffer.getVersion();
        switch (version) {
            case VERSION_1_9_4:
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
            case VERSION_1_14_4: {
                int paletteLength = buffer.readVarInt();
                for (int i = 0; i < paletteLength; i++) {
                    map.put(i, buffer.readVarInt());
                }
                break;
            }
        }
    }
}

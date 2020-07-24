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
package de.bixilon.minosoft.game.datatypes.entities.meta;

import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;
import de.bixilon.minosoft.util.BitByte;

import java.util.HashMap;

public class GuardianMetaData extends MobMetaData {

    public GuardianMetaData(HashMap<Integer, MetaDataSet> sets, ProtocolVersion version) {
        super(sets, version);
    }

    public boolean isElderly() {
        switch (version) {
            case VERSION_1_8:
                return BitByte.isBitMask((byte) sets.get(16).getData(), 0x02);
            case VERSION_1_9_4:
                return BitByte.isBitMask((byte) sets.get(11).getData(), 0x04);
            case VERSION_1_10:
                return BitByte.isBitMask((byte) sets.get(12).getData(), 0x04);
        }
        return false;
    }

    public boolean isRetractingSpikes() {
        switch (version) {
            case VERSION_1_8:
                return BitByte.isBitSet((byte) sets.get(16).getData(), 0x04);
            case VERSION_1_9_4:
                return BitByte.isBitSet((byte) sets.get(11).getData(), 0x02);
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return BitByte.isBitSet((byte) sets.get(12).getData(), 0x02);
        }
        return false;
    }


    public int getTargetEntityId() {
        switch (version) {
            case VERSION_1_8:
                return (int) sets.get(17).getData();
            case VERSION_1_9_4:
                return (int) sets.get(12).getData();
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return (int) sets.get(13).getData();
        }
        return 0;
    }

}

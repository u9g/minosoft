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
package de.bixilon.minosoft.game.datatypes.objectLoader.entities.meta;

import de.bixilon.minosoft.game.datatypes.objectLoader.entities.VillagerData;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

import java.util.HashMap;

public class ZombieMetaData extends MonsterMetaData {

    public ZombieMetaData(HashMap<Integer, MetaDataSet> sets, ProtocolVersion version) {
        super(sets, version);
    }


    public boolean isChild() {
        switch (version) {
            case VERSION_1_7_10:
            case VERSION_1_8:
                return (byte) sets.get(12).getData() == 0x01;
            case VERSION_1_9_4:
                return (boolean) sets.get(11).getData();
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return (boolean) sets.get(12).getData();
            case VERSION_1_14_4:
                return (boolean) sets.get(14).getData();
        }
        return false;
    }

    public VillagerData.VillagerProfessions getProfession() {
        switch (version) {
            case VERSION_1_7_10:
            case VERSION_1_8:
            case VERSION_1_10:
                byte set = (byte) sets.get(13).getData();
                if (version == ProtocolVersion.VERSION_1_10 && set == 6) {
                    return VillagerData.VillagerProfessions.HUSK;
                }
                return VillagerData.VillagerProfessions.byId(set - 1, version);
            case VERSION_1_9_4:
                return VillagerData.VillagerProfessions.byId((int) sets.get(12).getData() - 1, version);
        }
        return VillagerData.VillagerProfessions.NONE;
    }

    public boolean isConverting() {
        switch (version) {
            case VERSION_1_7_10:
            case VERSION_1_8:
                return (byte) sets.get(14).getData() == 0x01;
            case VERSION_1_9_4:
                return (boolean) sets.get(13).getData();
            case VERSION_1_10:
                return (boolean) sets.get(14).getData();
        }
        return false;
    }

    public boolean areHandsHeldUp() {
        switch (version) {
            case VERSION_1_9_4:
                return (boolean) sets.get(14).getData();
            case VERSION_1_10:
                return (boolean) sets.get(15).getData();
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return (boolean) sets.get(16).getData();
        }
        return false;
    }

    public boolean isBecomingADrowned() {
        switch (version) {
            case VERSION_1_13_2:
                return ((boolean) sets.get(15).getData());
            case VERSION_1_14_4:
                return (boolean) sets.get(16).getData();
        }
        return false;
    }
}

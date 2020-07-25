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

package de.bixilon.minosoft.game.datatypes.objectLoader.entities;

import de.bixilon.minosoft.game.datatypes.MapSet;
import de.bixilon.minosoft.game.datatypes.VersionValueMap;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class VillagerData {
    final VillagerTypes type;
    final VillagerProfessions profession;
    final VillagerLevels level;

    public VillagerData(int type, int profession, int level, ProtocolVersion version) {
        this.type = VillagerTypes.byId(type);
        this.profession = VillagerProfessions.byId(profession, version);
        this.level = VillagerLevels.byId(level);
    }

    public VillagerData(VillagerTypes type, VillagerProfessions profession, VillagerLevels level) {
        this.type = type;
        this.profession = profession;
        this.level = level;
    }

    public VillagerTypes getType() {
        return type;
    }

    public VillagerProfessions getProfession() {
        return profession;
    }

    public VillagerLevels getLevel() {
        return level;
    }

    public enum VillagerProfessions {
        NONE(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 0)}),
        ARMORER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 1)}),
        BUTCHER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 4), new MapSet<>(ProtocolVersion.VERSION_1_14_4, 2)}),
        CARTOGRAPHER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 3)}),
        CLERIC(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 4)}),
        FARMER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 0), new MapSet<>(ProtocolVersion.VERSION_1_14_4, 5)}),
        FISHERMAN(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 6)}),
        FLETCHER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 7)}),
        LEATHER_WORKER(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 8)}),
        LIBRARIAN(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 1), new MapSet<>(ProtocolVersion.VERSION_1_14_4, 9)}),
        MASON(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 10)}),
        NITWIT(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 5), new MapSet<>(ProtocolVersion.VERSION_1_14_4, 11)}),
        SHEPHERD(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 12)}),
        TOOL_SMITH(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 13)}),
        WEAPON_SMITH(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_14_4, 14)}),
        PRIEST(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 2), new MapSet<>(ProtocolVersion.VERSION_1_14_4, -1)}),
        BLACKSMITH(new MapSet[]{new MapSet<>(ProtocolVersion.VERSION_1_7_10, 3), new MapSet<>(ProtocolVersion.VERSION_1_14_4, -1)}),

        HUSK(new MapSet[]{}); // used earlier (ZombieVillagerMeta)

        final VersionValueMap<Integer> valueMap;

        VillagerProfessions(MapSet<ProtocolVersion, Integer>[] values) {
            valueMap = new VersionValueMap<>(values, true);
        }

        public static VillagerProfessions byId(int id, ProtocolVersion version) {
            for (VillagerProfessions profession : values()) {
                if (profession.getId(version) == id) {
                    return profession;
                }
            }
            return null;
        }

        public int getId(ProtocolVersion version) {
            return valueMap.get(version);
        }
    }

    public enum VillagerTypes {
        DESSERT(0),
        JUNGLE(1),
        PLAINS(2),
        SAVANNA(3),
        SNOW(4),
        SWAMP(5),
        TAIGA(6);

        final int id;

        VillagerTypes(int id) {
            this.id = id;
        }

        public static VillagerTypes byId(int id) {
            for (VillagerTypes type : values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public enum VillagerLevels {
        NOVICE(0),
        APPRENTICE(1),
        JOURNEYMAN(2),
        EXPERT(4),
        MASTER(5);

        final int id;

        VillagerLevels(int id) {
            this.id = id;
        }

        public static VillagerLevels byId(int id) {
            for (VillagerLevels level : values()) {
                if (level.getId() == id) {
                    return level;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}

/*
 * Minosoft
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

package de.bixilon.minosoft.data.entities;

import de.bixilon.minosoft.data.MapSet;
import de.bixilon.minosoft.data.VersionValueMap;

public class VillagerData {
    final VillagerTypes type;
    final VillagerProfessions profession;
    final VillagerLevels level;

    public VillagerData(int type, int profession, int level, int versionId) {
        this.type = VillagerTypes.byId(type);
        this.profession = VillagerProfessions.byId(profession, versionId);
        this.level = VillagerLevels.byId(level);
    }

    public VillagerData(VillagerTypes type, VillagerProfessions profession, VillagerLevels level) {
        this.type = type;
        this.profession = profession;
        this.level = level;
    }

    @Override
    public int hashCode() {
        return type.hashCode() * profession.hashCode() * level.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (hashCode() != obj.hashCode()) {
            return false;
        }
        VillagerData their = (VillagerData) obj;
        return getType() == their.getType() && getProfession() == their.getProfession() && getLevel() == their.getLevel();
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
        NONE(new MapSet[]{new MapSet<>(451, 0)}),
        ARMORER(new MapSet[]{new MapSet<>(451, 1)}),
        BUTCHER(new MapSet[]{new MapSet<>(0, 4), new MapSet<>(451, 2)}),
        CARTOGRAPHER(new MapSet[]{new MapSet<>(451, 3)}),
        CLERIC(new MapSet[]{new MapSet<>(451, 4)}),
        FARMER(new MapSet[]{new MapSet<>(0, 0), new MapSet<>(451, 5)}),
        FISHERMAN(new MapSet[]{new MapSet<>(451, 6)}),
        FLETCHER(new MapSet[]{new MapSet<>(451, 7)}),
        LEATHER_WORKER(new MapSet[]{new MapSet<>(451, 8)}),
        LIBRARIAN(new MapSet[]{new MapSet<>(0, 1), new MapSet<>(451, 9)}),
        MASON(new MapSet[]{new MapSet<>(451, 10)}),
        NITWIT(new MapSet[]{new MapSet<>(0, 5), new MapSet<>(451, 11)}),
        SHEPHERD(new MapSet[]{new MapSet<>(451, 12)}),
        TOOL_SMITH(new MapSet[]{new MapSet<>(451, 13)}),
        WEAPON_SMITH(new MapSet[]{new MapSet<>(451, 14)}),
        PRIEST(new MapSet[]{new MapSet<>(0, 2), new MapSet<>(451, -1)}),
        BLACKSMITH(new MapSet[]{new MapSet<>(0, 3), new MapSet<>(451, -1)}),

        HUSK(new MapSet[]{new MapSet<>(204, 5), new MapSet<>(315, -100)}), //ToDo
        ZOMBIE(new MapSet[]{new MapSet<>(204, -1), new MapSet<>(315, -100)}); // used earlier (ZombieVillagerMeta)

        final VersionValueMap<Integer> valueMap;

        VillagerProfessions(MapSet<Integer, Integer>[] values) {
            valueMap = new VersionValueMap<>(values, true);
        }

        public static VillagerProfessions byId(int id, int versionId) {
            for (VillagerProfessions profession : values()) {
                if (profession.getId(versionId) == id) {
                    return profession;
                }
            }
            return null;
        }

        public int getId(int versionId) {
            return valueMap.get(versionId);
        }
    }

    public enum VillagerTypes {
        DESSERT,
        JUNGLE,
        PLAINS,
        SAVANNA,
        SNOW,
        SWAMP,
        TAIGA;

        public static VillagerTypes byId(int id) {
            return values()[id];
        }
    }

    public enum VillagerLevels {
        NOVICE,
        APPRENTICE,
        JOURNEYMAN,
        EXPERT,
        MASTER;

        public static VillagerLevels byId(int id) {
            return values()[id];
        }
    }
}

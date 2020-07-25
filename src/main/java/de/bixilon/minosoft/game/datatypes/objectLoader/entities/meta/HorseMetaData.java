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

import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

import java.util.HashMap;

public class HorseMetaData extends AbstractHorseMetaData {

    public HorseMetaData(HashMap<Integer, MetaDataSet> sets, ProtocolVersion version) {
        super(sets, version);
    }

    public HorseColor getColor() {
        switch (version) {
            case VERSION_1_7_10:
            case VERSION_1_8:
                return HorseColor.byId((int) sets.get(20).getData() & 0xFF);
            case VERSION_1_9_4:
                return HorseColor.byId((int) sets.get(14).getData() & 0xFF);
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return HorseColor.byId((int) sets.get(15).getData() & 0xFF);
        }
        return HorseColor.WHITE;
    }

    public HorseDots getDots() {
        switch (version) {
            case VERSION_1_7_10:
            case VERSION_1_8:
                return HorseDots.byId((int) sets.get(20).getData() & 0xFF00);
            case VERSION_1_9_4:
                return HorseDots.byId((int) sets.get(14).getData() & 0xFF00);
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return HorseDots.byId((int) sets.get(15).getData() & 0xFF00);
        }
        return HorseDots.NONE;
    }

    public HorseArmor getArmor() {
        switch (version) {
            case VERSION_1_7_10:
            case VERSION_1_8:
                return HorseArmor.byId((int) sets.get(21).getData());
            case VERSION_1_9_4:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
                return HorseArmor.byId((int) sets.get(16).getData());
            case VERSION_1_10:
                return HorseArmor.byId((int) sets.get(17).getData());
        }
        return HorseArmor.NO_ARMOR;
    }

    public enum HorseType {
        HORSE(0),
        DONKEY(1),
        MULE(2),
        ZOMBIE(3),
        SKELETON(4);

        final int id;

        HorseType(int id) {
            this.id = id;
        }

        public static HorseType byId(int id) {
            for (HorseType h : values()) {
                if (h.getId() == id) {
                    return h;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public enum HorseArmor {
        NO_ARMOR(0),
        IRON_ARMOR(1),
        GOLD_ARMOR(2),
        DIAMOND_ARMOR(3);

        final int id;

        HorseArmor(int id) {
            this.id = id;
        }

        public static HorseArmor byId(int id) {
            for (HorseArmor a : values()) {
                if (a.getId() == id) {
                    return a;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public enum HorseColor {
        WHITE(0),
        CREAMY(1),
        CHESTNUT(2),
        BROWN(3),
        BLACK(4),
        GRAY(5),
        DARK_BROWN(6);

        final int id;

        HorseColor(int id) {
            this.id = id;
        }

        public static HorseColor byId(int id) {
            for (HorseColor c : values()) {
                if (c.getId() == id) {
                    return c;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public enum HorseDots {
        NONE(0),
        WHITE(1),
        WHITEFIELD(2),
        WHITE_DOTS(3),
        BLACK_DOTS(4);

        final int id;

        HorseDots(int id) {
            this.id = id;
        }

        public static HorseDots byId(int id) {
            for (HorseDots d : values()) {
                if (d.getId() == id) {
                    return d;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}

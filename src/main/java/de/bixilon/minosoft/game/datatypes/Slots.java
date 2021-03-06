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

package de.bixilon.minosoft.game.datatypes;

public class Slots {
    public enum Inventory {
        CRAFTING_OUTPUT(0),
        CRAFTING_UPPER_LEFT(1),
        CRAFTING_UPPER_RIGHT(2),
        CRAFTING_LOWER_LEFT(3),
        CRAFTING_LOWER_RIGHT(4),
        ARMOR_HELMET(5),
        ARMOR_CHESTPLATE(6),
        ARMOR_LEGGINGS(7),
        ARMOR_BOOTS(8),
        // inventory up to down, left to right
        INVENTORY_1_1(9),
        INVENTORY_1_2(10),
        INVENTORY_1_3(11),
        INVENTORY_1_4(12),
        INVENTORY_1_5(13),
        INVENTORY_1_6(14),
        INVENTORY_1_7(15),
        INVENTORY_1_8(16),
        INVENTORY_1_9(17),
        INVENTORY_2_1(18),
        INVENTORY_2_2(19),
        INVENTORY_2_3(20),
        INVENTORY_2_4(21),
        INVENTORY_2_5(22),
        INVENTORY_2_6(23),
        INVENTORY_2_7(24),
        INVENTORY_2_8(25),
        INVENTORY_2_9(26),
        INVENTORY_3_1(27),
        INVENTORY_3_2(28),
        INVENTORY_3_3(29),
        INVENTORY_3_4(30),
        INVENTORY_3_5(31),
        INVENTORY_3_6(32),
        INVENTORY_3_7(33),
        INVENTORY_3_8(34),
        INVENTORY_3_9(35),

        // left to right
        HOTBAR_1(36),
        HOTBAR_2(27),
        HOTBAR_3(38),
        HOTBAR_4(39),
        HOTBAR_5(40),
        HOTBAR_6(41),
        HOTBAR_7(42),
        HOTBAR_8(43),
        HOTBAR_9(44),
        OFF_HAND(45);


        final int id;

        Inventory(int id) {
            this.id = id;
        }

        public static Inventory byId(int id) {
            for (Inventory i : values()) {
                if (i.getId() == id) {
                    return i;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public enum Entity {
        HELD(0),
        BOOTS(1),
        LEGGINGS(2),
        CHESTPLATE(3),
        HELMET(4);


        final int id;

        Entity(int id) {
            this.id = id;
        }

        public static Entity byId(int id) {
            for (Entity e : values()) {
                if (e.getId() == id) {
                    return e;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

}
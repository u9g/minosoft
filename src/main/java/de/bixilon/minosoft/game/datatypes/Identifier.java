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

public class Identifier {
    final String legacy;
    String mod = "minecraft"; // by default minecraft
    String water;

    public Identifier(String mod, String legacy, String water) { // water for water update name (post 1.13.x)
        this.mod = mod;
        this.legacy = legacy;
        this.water = water;
    }

    public Identifier(String legacy, String water) {
        this.legacy = legacy;
        this.water = water;
    }

    public Identifier(String name) {
        // legacy and water are the same
        this.legacy = name;
    }

    public String getMod() {
        return mod;
    }

    public String getLegacy() {
        return legacy;
    }

    public String getWaterUpdateName() {
        return ((water == null) ? legacy : water);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        Identifier that = (Identifier) obj;
        return that.getLegacy().equals(getLegacy()) || that.getWaterUpdateName().equals(getWaterUpdateName());
    }
}

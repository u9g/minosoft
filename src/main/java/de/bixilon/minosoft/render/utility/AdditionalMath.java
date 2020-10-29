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

package de.bixilon.minosoft.render.utility;


public class AdditionalMath {
    public static int[] valuesBetween(int x, int y) {
        int[] result = new int[Math.abs(x - y) + 1];
        if (x > y) {
            for (int z = y; z <= x; z++) {
                result[x - z] = z;
            }
        } else if (y > x) {
            for (int z = x; z <= y; z++) {
                result[y - z] = z;
            }
        } else {
            result[0] = x;
        }

        return result;
    }

    public static int betterRound(double x) {
        if (x >= 0) {
            return (int) x;
        }
        return (int) x - 1;
    }
}

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

package de.bixilon.minosoft.render.fullFace;

import com.google.gson.JsonArray;

public class InFaceUV {
    public int u1, v1, u2, v2;

    public InFaceUV(JsonArray json) {
        u1 = json.get(0).getAsInt();
        v1 = json.get(1).getAsInt();
        u2 = json.get(2).getAsInt();
        v2 = json.get(3).getAsInt();
    }

    public InFaceUV() {
        u1 = v1 = 0;
        u2 = v2 = 16;
    }
}

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

package de.bixilon.minosoft.render.blockModels.subBlocks;

import com.google.gson.JsonObject;
import de.bixilon.minosoft.render.Face.Axis;

public class SubBlockRotation {
    SubBlockPosition origin;
    Axis direction;
    float angle;

    public SubBlockRotation(JsonObject rotation) {
        origin = new SubBlockPosition(rotation.get("origin").getAsJsonArray());
        String axis = rotation.get("axis").getAsString();
        switch (axis) {
            case "x":
                direction = Axis.X;
                break;
            case "y":
                direction = Axis.Y;
                break;
            case "z":
                direction = Axis.Z;
        }
        angle = rotation.get("angle").getAsFloat();
    }


}

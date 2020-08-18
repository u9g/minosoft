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
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import javafx.util.Pair;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

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

    private static Pair<Float, Float> rotate(float x, float y, float angle) {
        return new Pair<Float, Float>(
                x * (float) cos(angle) + y * (float) sin(angle),
                -x * (float) sin(angle) + y * (float) cos(angle)
        );
    }

    public SubBlockPosition apply(SubBlockPosition position) {
        SubBlockPosition transformed = SubBlockPosition.subtract(position, origin);
        Pair<Float, Float> rotated;
        switch (direction) {
            case X:
                rotated = rotate(transformed.y, transformed.z, angle);
                transformed.y = rotated.getKey();
                transformed.z = rotated.getValue();
                break;
            case Y:
                rotated = rotate(transformed.x, transformed.z, angle);
                transformed.x = rotated.getKey();
                transformed.z = rotated.getValue();
                break;
            case Z:
                rotated = rotate(transformed.x, transformed.y, angle);
                transformed.x = rotated.getKey();
                transformed.y = rotated.getValue();
                break;
        }
        return SubBlockPosition.add(transformed, origin);
    }
}

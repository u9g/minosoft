/*
 * Codename Minosoft
 * Copyright (C) 2020 Lukas Eisenhauer
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
    private final SubBlockPosition origin;
    private Axis direction;
    private final float angle;

    public SubBlockRotation(SubBlockPosition origin, Axis direction, float angle) {
        this.origin = origin;
        this.direction = direction;
        this.angle = angle;
    }

    public SubBlockRotation(JsonObject rotation) {
        origin = new SubBlockPosition(rotation.get("origin").getAsJsonArray());
        String axis = rotation.get("axis").getAsString();
        switch (axis) {
            case "x" -> direction = Axis.X;
            case "y" -> direction = Axis.Y;
            case "z" -> direction = Axis.Z;
        }
        angle = rotation.get("angle").getAsFloat();
    }

    public static Pair<Float, Float> rotate(float x, float y, float angle) {
        float angleRad = (float) Math.toRadians(angle);
        float newX = x * (float) cos(angleRad) + y * (float) sin(angleRad);
        float newY = -x * (float) sin(angleRad) + y * (float) cos(angleRad);

        return new Pair<>(
                newX,
                newY
        );
    }

    public SubBlockPosition apply(SubBlockPosition position) {
        SubBlockPosition transformed = SubBlockPosition.subtract(position, origin);
        Pair<Float, Float> rotated;
        switch (direction) {
            case X -> {
                rotated = rotate(transformed.y, transformed.z, angle);
                transformed.y = rotated.getKey();
                transformed.z = rotated.getValue();
            }
            case Y -> {
                rotated = rotate(transformed.x, transformed.z, angle);
                transformed.x = rotated.getKey();
                transformed.z = rotated.getValue();
            }
            case Z -> {
                rotated = rotate(transformed.x, transformed.y, angle);
                transformed.x = rotated.getKey();
                transformed.y = rotated.getValue();
            }
        }
        return SubBlockPosition.add(transformed, origin);
    }
}

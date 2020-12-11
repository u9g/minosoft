/*
 * Minosoft
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
import de.bixilon.minosoft.render.blockModels.face.Axis;
import de.bixilon.minosoft.render.utility.Vec3;
import javafx.util.Pair;

public class SubBlockRotation {
    private final Vec3 origin;
    private final Axis axis;
    private final double sin;
    private final double cos;

    public SubBlockRotation(SubBlockPosition origin, Axis axis, double angle) {
        this.origin = origin.getVector();
        this.axis = axis;
        double angleRad = Math.toRadians(angle);
        sin = Math.sin(angleRad);
        cos = Math.cos(angleRad);
    }

    public SubBlockRotation(JsonObject rotation) {
        origin = new SubBlockPosition(rotation.get("origin").getAsJsonArray()).getVector();
        String direction = rotation.get("axis").getAsString();
        axis = switch (direction) {
            case "x" -> Axis.X;
            case "y" -> Axis.Y;
            case "z" -> Axis.Z;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
        double angleRad = Math.toRadians(rotation.get("angle").getAsDouble());
        sin = Math.sin(angleRad);
        cos = Math.cos(angleRad);
    }

    private Pair<Double, Double> rotate(double x, double y) {
        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;
        return new Pair<>(newX, newY);
    }


    public SubBlockPosition apply(SubBlockPosition position) {
        Vec3 transformedPosition = Vec3.add(position.getVector(), Vec3.mul(origin, -1));
        return switch (axis) {
            case X:
                Pair<Double, Double> rotateX = rotate(transformedPosition.y, transformedPosition.z); transformedPosition.y = rotateX.getKey(); transformedPosition.z = rotateX.getValue(); yield new SubBlockPosition(Vec3.add(transformedPosition, origin));
            case Y:
                Pair<Double, Double> rotateY = rotate(transformedPosition.x, transformedPosition.z); transformedPosition.x = rotateY.getKey(); transformedPosition.z = rotateY.getValue(); yield new SubBlockPosition(Vec3.add(transformedPosition, origin));
            case Z:
                Pair<Double, Double> rotateZ = rotate(transformedPosition.x, transformedPosition.y); transformedPosition.x = rotateZ.getKey(); transformedPosition.y = rotateZ.getValue(); yield new SubBlockPosition(Vec3.add(transformedPosition, origin));
        };
    }
}

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
import de.bixilon.minosoft.render.utility.Vec3;


public class SubBlockRotation {
    private final Vec3 origin;
    private final double angle;
    private final Vec3 direction;

    public SubBlockRotation(SubBlockPosition origin, Axis direction, float angle) {
        this.origin = origin.getVector();
        this.direction = switch (direction) {
            case X -> new Vec3(1, 0, 0);
            case Y -> new Vec3(0, 1, 0);
            case Z -> new Vec3(0, 0, 1);
        };
        this.angle = angle;
    }

    public SubBlockRotation(JsonObject rotation) {
        origin = new SubBlockPosition(rotation.get("origin").getAsJsonArray()).getVector();
        String axis = rotation.get("axis").getAsString();
        direction = switch (axis) {
            case "x" -> new Vec3(1, 0, 0);
            case "y" -> new Vec3(0, 1, 0);
            case "z" -> new Vec3(0, 0, 1);
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        };
        angle = Math.toRadians(rotation.get("angle").getAsFloat());
    }

    public SubBlockPosition apply(SubBlockPosition position) {
        Vec3 transformed = Vec3.add(position.getVector(), Vec3.mul(origin, -1));

        Vec3 result = Vec3.mul(transformed, Math.cos(angle));
        result.add(Vec3.mul(Vec3.cross(direction, transformed), Math.sin(angle)));
        //result.add(Vec3.mul(direction, direction, transformed, ));
        return new SubBlockPosition(Vec3.add(transformed, origin));
    }

    /*
    public static Pair<Float, Float> rotate(float x, float y, float angle) {
        float angleRad = (float) Math.toRadians(angle);
        float newX = x * (float) StrictMath.cos(angleRad) + y * (float) StrictMath.sin(angleRad);
        float newY = - x * (float) StrictMath.sin(angleRad) + y * (float) StrictMath.cos(angleRad);
        return new Pair<>(newX, newY);
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
     */
}

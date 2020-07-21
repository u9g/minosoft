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

package de.bixilon.minosoft.render.utility;

import de.bixilon.minosoft.game.datatypes.entities.Location;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Vec3 {
    public float x, y, z;

    public Vec3() {
        x = y = z = 0;
    }

    public Vec3(float x_, float y_, float z_) {
        x = x_;
        y = y_;
        z = z_;
    }

    public Vec3(Location location) {
        x = (float) location.getX();
        y = (float) location.getY();
        z = (float) location.getZ();
    }

    public static Vec3 add(Vec3 v1, Vec3 v2) {
        return new Vec3(
                v1.x + v2.x,
                v1.y + v2.y,
                v1.z + v2.z
        );
    }

    public static Vec3 mul(Vec3 v, float n) {
        return new Vec3(
                v.x * n,
                v.y * n,
                v.z * n
        );
    }

    public static Vec3 mul(Vec3 v1, Vec3 v2) {
        return new Vec3(
                v1.x * v2.x,
                v1.y * v2.y,
                v1.z * v2.z
        );
    }

    public static Vec3 normalize(Vec3 v) {
        float l = v.len();
        Vec3 out = v;
        out.x /= l;
        out.y /= l;
        out.z /= l;
        return out;
    }

    public static Vec3 cross(Vec3 v1, Vec3 v2) {
        return new Vec3(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x
        );
    }

    public void add(Vec3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void add(float x_, float y_, float z_) {
        x += x_;
        y += y_;
        z += z_;
    }

    public float len() {
        return (float) sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
    }

    public void normalize() {
        float l = len();
        x /= l;
        y /= l;
        z /= l;
    }

    public Vec3 copy() {
        return new Vec3(x, y, z);
    }

    public void zero() {
        x = y = z = 0f;
    }

    public int getXNormalized() {
        if (x == 0f) {
            return 0;
        } else if (x > 0) {
            return 1;
        }
        return -1;
    }
}

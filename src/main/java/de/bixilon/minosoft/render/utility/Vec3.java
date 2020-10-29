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

import de.bixilon.minosoft.data.entities.Location;


public class Vec3 {
    public double x, y, z;

    public Vec3() {
        x = y = z = 0;
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Location location) {
        x = (float) location.getX();
        y = (float) location.getY();
        z = (float) location.getZ();
    }

    public static Vec3 add(Vec3... vectors) {
        Vec3 result = new Vec3(0, 0, 0);
        for (Vec3 vector : vectors) {
            result.x += vector.x;
            result.y += vector.y;
            result.z += vector.z;
        }
        return result;
    }

    public static Vec3 mul(Vec3 v, double n) {
        return new Vec3(v.x * n, v.y * n, v.z * n);
    }

    public static Vec3 mul(Vec3... vectors) {
        Vec3 result = new Vec3(0, 0, 0);
        for (Vec3 vector : vectors) {
            result.x *= vector.x;
            result.y *= vector.y;
            result.z *= vector.z;
        }
        return result;
    }

    public static Vec3 normalize(Vec3 v) {
        float l = v.len();
        v.x /= l;
        v.y /= l;
        v.z /= l;
        return v;
    }

    public static Vec3 cross(Vec3 v1, Vec3 v2) {
        return new Vec3(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    public void add(Vec3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public Vec3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public float len() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
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
}

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

package de.bixilon.minosoft.render.blockModels.subBlocks;

import com.google.gson.JsonArray;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import de.bixilon.minosoft.render.utility.Vec3;
import org.apache.commons.collections.primitives.ArrayFloatList;

import static de.bixilon.minosoft.render.blockModels.Face.RenderConstants.BLOCK_RESOLUTION;

public class SubBlockPosition {
    public static final float e = 0.01f;
    private static final SubBlockPosition middlePos = new SubBlockPosition(BLOCK_RESOLUTION / 2f, BLOCK_RESOLUTION / 2f, BLOCK_RESOLUTION / 2f);
    private final Vec3 vector;

    public SubBlockPosition(JsonArray json) {
        float x = json.get(0).getAsFloat();
        float y = json.get(1).getAsFloat();
        float z = json.get(2).getAsFloat();
        vector = new Vec3(x, y, z);
    }

    public SubBlockPosition(double x, double y, double z) {
        vector = new Vec3(x, y, z);
    }

    public SubBlockPosition(Vec3 vector) {
        this.vector = vector;
    }

    public SubBlockPosition(SubBlockPosition position) {
        vector = new Vec3(position.vector);
    }

    public SubBlockPosition rotated(Axis axis, int rotation) {
        return new SubBlockRotation(middlePos, axis, rotation).apply(this);
    }

    public ArrayFloatList getFloats(BlockPosition position) {
        ArrayFloatList result = new ArrayFloatList();
        result.add((float) (vector.x / BLOCK_RESOLUTION + position.getX()));
        result.add((float) (vector.y / BLOCK_RESOLUTION + position.getY()));
        result.add((float) (vector.z / BLOCK_RESOLUTION + position.getZ()));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubBlockPosition position = (SubBlockPosition) o;
        return Math.abs(position.getVector().x - vector.x) < e && Math.abs(position.getVector().y - vector.y) < e && Math.abs(position.getVector().z - vector.z) < e;
    }

    public Vec3 getVector() {
        return vector;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

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

import com.google.gson.JsonArray;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import org.apache.commons.collections.primitives.ArrayFloatList;

import static de.bixilon.minosoft.render.blockModels.Face.RenderConstants.BLOCK_RESOLUTION;

public class SubBlockPosition {
    private static final SubBlockPosition middlePos = new SubBlockPosition(8, 8, 8);
    public float x;
    public float y;
    public float z;


    public SubBlockPosition(JsonArray json) {
        x = json.get(0).getAsFloat();
        y = json.get(1).getAsFloat();
        z = json.get(2).getAsFloat();
    }

    public SubBlockPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SubBlockPosition add(SubBlockPosition pos1, SubBlockPosition pos2) {
        return new SubBlockPosition(pos1.x + pos2.x, pos1.y + pos2.y, pos1.z + pos2.z);
    }

    public static SubBlockPosition subtract(SubBlockPosition pos1, SubBlockPosition pos2) {
        return new SubBlockPosition(pos1.x - pos2.x, pos1.y - pos2.y, pos1.z - pos2.z);
    }

    public ArrayFloatList getFloats(BlockPosition position) {
        ArrayFloatList result = new ArrayFloatList();
        result.add(x / BLOCK_RESOLUTION + position.getX());
        result.add(y / BLOCK_RESOLUTION + position.getY());
        result.add(z / BLOCK_RESOLUTION + position.getZ());
        return result;
    }

    public SubBlockPosition rotated(Axis axis, int rotation) {
        return new SubBlockRotation(middlePos, axis, rotation).apply(this);
    }
}

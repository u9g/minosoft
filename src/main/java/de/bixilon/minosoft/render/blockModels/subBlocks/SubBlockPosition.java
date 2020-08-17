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

import com.google.gson.JsonArray;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;

import static de.bixilon.minosoft.render.Face.RenderConstants.blockRes;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class SubBlockPosition {
    float x, y, z;

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
        return new SubBlockPosition(
                pos1.x + pos2.x,
                pos1.y + pos2.y,
                pos1.z + pos2.z);
    }

    public static SubBlockPosition subtract(SubBlockPosition pos1, SubBlockPosition pos2) {
        return new SubBlockPosition(
                pos1.x - pos2.x,
                pos1.y - pos2.y,
                pos1.z - pos2.z);
    }

    public void draw(BlockPosition pos) {
        glVertex3f(
                pos.getX() + x / blockRes,
                pos.getY() + y / blockRes,
                pos.getZ() + z / blockRes);
    }
}

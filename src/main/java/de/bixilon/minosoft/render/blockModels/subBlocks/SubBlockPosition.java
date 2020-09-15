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
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotation;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.blockModels.Face.Axis;

import static de.bixilon.minosoft.render.blockModels.Face.RenderConstants.blockRes;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class SubBlockPosition {
    float x, y, z;

    public static final SubBlockPosition middlePos = new SubBlockPosition(8, 8, 8);

    public static final SubBlockRotation westRotator = new SubBlockRotation(middlePos, Axis.Y, 90);
    public static final SubBlockRotation eastRotator = new SubBlockRotation(middlePos, Axis.Y, 270);
    public static final SubBlockRotation southRotator = new SubBlockRotation(middlePos, Axis.Y, 180);

    public static final SubBlockRotation xAxisRotator = new SubBlockRotation(middlePos, Axis.Z, 90);
    public static final SubBlockRotation zAxisRotator = new SubBlockRotation(middlePos, Axis.X, 90);

    public static final SubBlockRotation downRotator = new SubBlockRotation(middlePos, Axis.X, 90);
    public static final SubBlockRotation downAltRotator = new SubBlockRotation(middlePos, Axis.X, 180);
    public static final SubBlockRotation upRotator = new SubBlockRotation(middlePos, Axis.X, -90);


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

    public SubBlockPosition rotated(Block block) {
        if (block.getRotation() == null) {
            return this;
        }
        switch (block.getRotation()) {
            case EAST:
                return eastRotator.apply(this);
            case WEST:
                return westRotator.apply(this);
            case SOUTH:
                return southRotator.apply(this);
            case UP:
                if (block.getIdentifier().equals("dispenser") || block.getIdentifier().equals("dropper")) {
                    return this;
                }
                return upRotator.apply(this);
            case DOWN:
                if (block.getIdentifier().equals("dispenser") || block.getIdentifier().equals("dropper")) {
                    return downAltRotator.apply(this);
                }
                return downRotator.apply(this);
            case AXIS_X:
                return xAxisRotator.apply(this);
            case AXIS_Z:
                return zAxisRotator.apply(this);
        }
        return this;
    }
}

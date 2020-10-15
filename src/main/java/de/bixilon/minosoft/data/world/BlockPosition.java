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

package de.bixilon.minosoft.data.world;

import java.util.Objects;

import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;
import de.bixilon.minosoft.render.utility.Vec3;

public class BlockPosition {
    final int x;
    final int y;
    final int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(Vec3 testPosition) {
        x = (int) testPosition.x;
        y = (short) testPosition.y;
        z = (int) testPosition.z;
    }

    public BlockPosition(ChunkLocation chunkLocation, Byte height, ChunkNibbleLocation nibbleLocation) {
        this.x = chunkLocation.getX() * RenderConstants.SECTION_WIDTH + nibbleLocation.getX();
        this.y = height * RenderConstants.SECTION_HEIGHT + nibbleLocation.getY();
        this.z = chunkLocation.getZ() * RenderConstants.SECTION_WIDTH + nibbleLocation.getZ();
    }

    public ChunkLocation getChunkLocation() {
        return new ChunkLocation(getX() / 16, getZ() / 16);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        BlockPosition pos = (BlockPosition) obj;
        return pos.getX() == getX() && pos.getY() == getY() && pos.getZ() == getZ();
    }

    public ChunkLocation getChunkLocation() {
        int x = getX() / RenderConstants.SECTION_WIDTH;
        int z = getZ() / RenderConstants.SECTION_WIDTH;
        //ToDo
        if (getX() < 0) {
            x--;
        }
        if (getZ() < 0) {
            z--;
        }
        return new ChunkLocation(x, z);
    }

    @Override
    public String toString() {
        return String.format("%d %d %d", getX(), getY(), getZ());
    }

    public InChunkLocation getInChunkLocation() {
        int x = getX() % RenderConstants.SECTION_WIDTH;
        if (x < 0) {
            x += RenderConstants.SECTION_WIDTH;
        }
        int z = getZ() % RenderConstants.SECTION_WIDTH;
        if (z < 0) {
            z += RenderConstants.SECTION_WIDTH;
        }
        return new InChunkLocation(x, getY(), z);
    }

    public BlockPosition add(int[] ints) {
        return new BlockPosition(
                x + ints[0],
                (short) (y + ints[1]),
                z + ints[2]
        );
    }
}

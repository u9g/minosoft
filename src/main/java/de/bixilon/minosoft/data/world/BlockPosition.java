/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.data.world;

import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;
import de.bixilon.minosoft.render.utility.Vec3;

public record BlockPosition(int x, int y, int z) {
    public BlockPosition(Vec3 vec3) {
        this((int) vec3.x, (int) vec3.y, (int) vec3.z);
    }

    public BlockPosition(ChunkLocation chunkLocation, Byte height, InChunkSectionLocation sectionLocation) {
        this((chunkLocation.getX() * RenderConstants.SECTION_WIDTH + sectionLocation.getX()), (height * RenderConstants.SECTION_WIDTH + sectionLocation.getY()), (chunkLocation.getZ() * RenderConstants.SECTION_WIDTH + sectionLocation.getZ()));
    }

    public ChunkLocation getChunkLocation() {
        return new ChunkLocation(x / 16, z / 16);
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

    public InChunkLocation getInChunkLocation() {
        int x = this.x % RenderConstants.SECTION_WIDTH;
        if (x < 0) {
            x += RenderConstants.SECTION_WIDTH;
        }
        int z = this.z % RenderConstants.SECTION_WIDTH;
        if (z < 0) {
            z += RenderConstants.SECTION_WIDTH;
        }
        return new InChunkLocation(x, this.y, z);
    }

    @Override
    public String toString() {
        return String.format("%d %d %d", x, y, z);
    }

    public BlockPosition add(int[] ints) {
        return new BlockPosition(x + ints[0], (short) (y + ints[1]), z + ints[2]);
    }
}

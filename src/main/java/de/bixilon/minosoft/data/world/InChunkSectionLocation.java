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

/**
 * Chunk X, Y and Z location (max 16x16x16)
 */
public record InChunkSectionLocation(int x, int y, int z) {
    @Deprecated
    public int getX() {
        return x;
    }

    @Deprecated
    public int getY() {
        return y;
    }

    @Deprecated
    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return String.format("%d %d %d", x, y, z);
    }

    public InChunkLocation getChunkLocation(byte sectionHeight) {
        return new InChunkLocation(x, (y + sectionHeight * RenderConstants.SECTION_HEIGHT), z);
    }
}

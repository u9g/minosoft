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

package de.bixilon.minosoft.render.blockModels.Face;

public class RenderConstants {
    public static final int TEXTURE_PACK_RESOLUTION = 16;
    public static final int BLOCK_RESOLUTION = 16;

    public static final byte SECTION_HEIGHT = 16;
    public static final byte SECTION_WIDTH = 16;
    public static final byte SECTIONS_PER_CHUNK = 16;

    public static final byte SECTIONS_MIN_X = 0;
    public static final byte SECTIONS_MIN_Y = 0;
    public static final byte SECTIONS_MIN_Z = 0;
    public static final byte SECTIONS_MAX_X = SECTION_WIDTH - 1;
    public static final byte SECTIONS_MAX_Y = SECTION_HEIGHT - 1;
    public static final byte SECTIONS_MAX_Z = SECTION_WIDTH - 1;

    public static final byte CHUNK_MIN_Y = 0;
    public static final int CHUNK_MAX_Y = SECTION_HEIGHT * SECTIONS_PER_CHUNK - 1;
}

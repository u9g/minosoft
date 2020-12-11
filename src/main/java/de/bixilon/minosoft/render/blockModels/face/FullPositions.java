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

package de.bixilon.minosoft.render.blockModels.face;

import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlockPosition;

import java.util.Arrays;
import java.util.HashSet;

import static de.bixilon.minosoft.render.blockModels.face.RenderConstants.BLOCK_RESOLUTION;

public class FullPositions {
    public static final SubBlockPosition POSITION_1 = new SubBlockPosition(0, 0, 0);
    public static final SubBlockPosition POSITION_2 = new SubBlockPosition(BLOCK_RESOLUTION, 0, 0);
    public static final SubBlockPosition POSITION_3 = new SubBlockPosition(0, 0, BLOCK_RESOLUTION);
    public static final SubBlockPosition POSITION_4 = new SubBlockPosition(BLOCK_RESOLUTION, 0, BLOCK_RESOLUTION);

    public static final SubBlockPosition POSITION_5 = new SubBlockPosition(0, BLOCK_RESOLUTION, 0);
    public static final SubBlockPosition POSITION_6 = new SubBlockPosition(BLOCK_RESOLUTION, BLOCK_RESOLUTION, 0);
    public static final SubBlockPosition POSITION_7 = new SubBlockPosition(0, BLOCK_RESOLUTION, BLOCK_RESOLUTION);
    public static final SubBlockPosition POSITION_8 = new SubBlockPosition(BLOCK_RESOLUTION, BLOCK_RESOLUTION, BLOCK_RESOLUTION);

    public static final HashSet<SubBlockPosition> EAST_POSITIONS = new HashSet<>(Arrays.asList(POSITION_1, POSITION_3, POSITION_5, POSITION_7));
    public static final HashSet<SubBlockPosition> WEST_POSITIONS = new HashSet<>(Arrays.asList(POSITION_2, POSITION_4, POSITION_6, POSITION_8));
    public static final HashSet<SubBlockPosition> DOWN_POSITIONS = new HashSet<>(Arrays.asList(POSITION_1, POSITION_2, POSITION_3, POSITION_4));
    public static final HashSet<SubBlockPosition> UP_POSITIONS = new HashSet<>(Arrays.asList(POSITION_5, POSITION_6, POSITION_7, POSITION_8));
    public static final HashSet<SubBlockPosition> SOUTH_POSITIONS = new HashSet<>(Arrays.asList(POSITION_1, POSITION_2, POSITION_5, POSITION_6));
    public static final HashSet<SubBlockPosition> NORTH_POSITIONS = new HashSet<>(Arrays.asList(POSITION_3, POSITION_4, POSITION_7, POSITION_8));
}

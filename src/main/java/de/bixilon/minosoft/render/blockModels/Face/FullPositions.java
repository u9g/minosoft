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

package de.bixilon.minosoft.render.blockModels.Face;

import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlockPosition;

import java.util.Arrays;
import java.util.HashSet;

import static de.bixilon.minosoft.render.blockModels.Face.RenderConstants.BLOCK_RESOLUTION;

public class FullPositions {
    public static final SubBlockPosition position1 = new SubBlockPosition(0, 0, 0);
    public static final SubBlockPosition position2 = new SubBlockPosition(BLOCK_RESOLUTION, 0, 0);
    public static final SubBlockPosition position3 = new SubBlockPosition(0, 0, BLOCK_RESOLUTION);
    public static final SubBlockPosition position4 = new SubBlockPosition(BLOCK_RESOLUTION, 0, BLOCK_RESOLUTION);

    public static final SubBlockPosition position5 = new SubBlockPosition(0, BLOCK_RESOLUTION, 0);
    public static final SubBlockPosition position6 = new SubBlockPosition(BLOCK_RESOLUTION, BLOCK_RESOLUTION, 0);
    public static final SubBlockPosition position7 = new SubBlockPosition(0, BLOCK_RESOLUTION, BLOCK_RESOLUTION);
    public static final SubBlockPosition position8 = new SubBlockPosition(BLOCK_RESOLUTION, BLOCK_RESOLUTION, BLOCK_RESOLUTION);

    public static final HashSet<SubBlockPosition> EAST_POSITIONS = new HashSet<>(Arrays.asList(position1, position3, position5, position7));
    public static final HashSet<SubBlockPosition> WEST_POSITIONS = new HashSet<>(Arrays.asList(position2, position4, position6, position8));
    public static final HashSet<SubBlockPosition> DOWN_POSITIONS = new HashSet<>(Arrays.asList(position1, position2, position3, position4));
    public static final HashSet<SubBlockPosition> UP_POSITIONS = new HashSet<>(Arrays.asList(position5, position6, position7, position8));
    public static final HashSet<SubBlockPosition> SOUTH_POSITIONS = new HashSet<>(Arrays.asList(position1, position2, position5, position6));
    public static final HashSet<SubBlockPosition> NORTH_POSITIONS = new HashSet<>(Arrays.asList(position3, position4, position7, position8));
}

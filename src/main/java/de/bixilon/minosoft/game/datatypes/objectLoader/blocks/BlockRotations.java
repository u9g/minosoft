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

package de.bixilon.minosoft.game.datatypes.objectLoader.blocks;

public enum BlockRotations {
    NONE,
    SOUTH,
    SOUTH_SOUTH_WEST,
    SOUTH_WEST,
    WEST_SOUTH_WEST,
    WEST,
    WEST_NORTH_WEST,
    NORTH_WEST,
    NORTH_NORTH_WEST,
    NORTH,
    NORTH_NORTH_EAST,
    NORTH_EAST,
    EAST_NORTH_EAST,
    EAST,
    EAST_SOUTH_EAST,
    SOUTH_EAST,
    SOUTH_SOUTH_EAST,

    NORTH_SOUTH,
    EAST_WEST,
    ASCENDING_EAST,
    ASCENDING_WEST,
    ASCENDING_NORTH,
    ASCENDING_SOUTH,

    UP,
    DOWN,

    // log, portal
    AXIS_X,
    AXIS_Y,
    AXIS_Z,

    // 1.16+

    DOWN_EAST,
    DOWN_WEST,
    DOWN_NORTH,
    DOWN_SOUTH,
    UP_EAST,
    EAST_UP,
    UP_WEST,
    WEST_UP,
    UP_NORTH,
    NORTH_UP,
    UP_SOUTH,
    SOUTH_UP,
}

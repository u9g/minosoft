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

package de.bixilon.minosoft.render.blockModels.subBlocks;

// a 3d object with 8 corners, 6 faces and 12 edges (cube, but can be deformed)

import de.bixilon.minosoft.render.blockModels.face.Axis;
import de.bixilon.minosoft.render.blockModels.face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.face.FullPositions;

import java.util.Arrays;
import java.util.HashSet;


public class Cuboid {
    public static final int[][] FACE_POSITION_MAP_TEMPLATE = new int[][]{{7, 5, 1, 3}, {4, 6, 2, 0}, {4, 5, 7, 6}, {2, 3, 1, 0}, {6, 7, 3, 2}, {5, 4, 0, 1}};

    private final SubBlockPosition[] positions;

    public Cuboid(SubBlockPosition from, SubBlockPosition to, SubBlockRotation rotation) {
        positions = new SubBlockPosition[]{
                from,
                new SubBlockPosition(to.getVector().x, from.getVector().y, from.getVector().z),
                new SubBlockPosition(from.getVector().x, from.getVector().y, to.getVector().z),
                new SubBlockPosition(to.getVector().x, from.getVector().y, to.getVector().z),

                new SubBlockPosition(from.getVector().x, to.getVector().y, from.getVector().z),
                new SubBlockPosition(to.getVector().x, to.getVector().y, from.getVector().z),
                new SubBlockPosition(from.getVector().x, to.getVector().y, to.getVector().z),
                to
        };

        if (rotation == null) {
            return;
        }
        for (int i = 0; i < positions.length; i++) {
            positions[i] = rotation.apply(positions[i]);
        }
    }

    public Cuboid(Cuboid cuboid) {
        this.positions = cuboid.positions.clone();
    }

    public SubBlockPosition[] getFacePositions(FaceOrientation orientation) {
        int[] positionIds = FACE_POSITION_MAP_TEMPLATE[orientation.getId()];
        SubBlockPosition[] result = new SubBlockPosition[positionIds.length];
        for (int i = 0; i < positionIds.length; i++) {
            result[i] = positions[positionIds[i]];
        }
        return result;
    }

    public boolean isFull(FaceOrientation orientation) {
        HashSet<SubBlockPosition> positions = new HashSet<>(Arrays.asList(this.positions));
        return switch (orientation) {
            case EAST -> (positions.containsAll(FullPositions.EAST_POSITIONS));
            case WEST -> (positions.containsAll(FullPositions.WEST_POSITIONS));
            case DOWN -> (positions.containsAll(FullPositions.DOWN_POSITIONS));
            case UP -> (positions.containsAll(FullPositions.UP_POSITIONS));
            case SOUTH -> (positions.containsAll(FullPositions.SOUTH_POSITIONS));
            case NORTH -> (positions.containsAll(FullPositions.NORTH_POSITIONS));
        };
    }

    public void rotate(Axis axis, double rotation) {
        for (int i = 0; i < positions.length; i++) {
            positions[i] = positions[i].rotated(axis, rotation);
        }
    }
}

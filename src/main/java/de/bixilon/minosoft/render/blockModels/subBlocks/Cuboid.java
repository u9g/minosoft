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

// some 3d object with 8 corners, 6 faces and 12 edges (example: cube, but can be deformed)

import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotation;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;

import java.util.HashMap;
import java.util.Map;

public class Cuboid {
    public static final Map<FaceOrientation, int[]> facePositionMapTemplate = Map.of(
            FaceOrientation.EAST, new int[]{7, 5, 1, 3},
            FaceOrientation.WEST, new int[]{4, 6, 2, 0},
            FaceOrientation.UP, new int[]{4, 5, 7, 6},
            FaceOrientation.DOWN, new int[]{2, 3, 1, 0},
            FaceOrientation.SOUTH, new int[]{6, 7, 3, 2},
            FaceOrientation.NORTH, new int[]{5, 4, 0, 1}
    );

    HashMap<FaceOrientation, SubBlockPosition[]> facePositionMap;

    public Cuboid(SubBlockPosition from, SubBlockPosition to, SubBlockRotation rotation) {
        SubBlockPosition[] positions = new SubBlockPosition[8];
        positions[0] = from;
        positions[1] = new SubBlockPosition(to.x, from.y, from.z);
        positions[2] = new SubBlockPosition(from.x, from.y, to.z);
        positions[3] = new SubBlockPosition(to.x, from.y, to.z);

        positions[4] = new SubBlockPosition(from.x, to.y, from.z);
        positions[5] = new SubBlockPosition(to.x, to.y, from.z);
        positions[6] = new SubBlockPosition(from.x, to.y, to.z);
        positions[7] = to;

        if (rotation != null) {
            for (int i = 0; i < positions.length; i++) {
                positions[i] = rotation.apply(positions[i]);
            }
        }
        facePositionMap = new HashMap<>();
        for (Map.Entry<FaceOrientation, int[]> entry : facePositionMapTemplate.entrySet()) {
            SubBlockPosition[] facePositions = new SubBlockPosition[4];
            for (int i = 0; i < 4; i++) {
                facePositions[i] = positions[entry.getValue()[i]];
            }
            facePositionMap.put(entry.getKey(), facePositions);
        }
    }

    public SubBlockPosition[] getFacePositions(FaceOrientation orientation, Block block) {
        SubBlockPosition[] positions = facePositionMap.get(orientation);
        if (block.getRotation() == BlockRotation.NONE || block.getRotation() == BlockRotation.NORTH) {
            return positions;
        }
        SubBlockPosition[] result = new SubBlockPosition[positions.length];
        for (int i = 0; i < positions.length; i++) {
            result[i] = positions[i].rotated(block);
        }
        return result;
    }
}

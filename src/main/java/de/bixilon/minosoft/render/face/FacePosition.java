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

package de.bixilon.minosoft.render.face;

import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import javafx.util.Pair;
import org.apache.commons.collections.primitives.ArrayFloatList;

import static de.bixilon.minosoft.render.face.RenderConstants.UV;

public class FacePosition {
    private final BlockPosition blockPosition;
    private final FaceOrientation faceOrientation;

    public FacePosition(BlockPosition blockPosition, FaceOrientation faceOrientation) {
        this.blockPosition = blockPosition;
        this.faceOrientation = faceOrientation;
    }

    public BlockPosition getBlockPosition() {
        return blockPosition;
    }

    public FaceOrientation getFaceOrientation() {
        return faceOrientation;
    }

    @Override
    public int hashCode() {
        return blockPosition.hashCode() * faceOrientation.hashCode();
    }

    public void addVertices(ArrayFloatList vertPos, ArrayFloatList textPos, Pair<Float, Float> texture) {
        float[][] vertPositions = RenderConstants.FACE_VERTEX[faceOrientation.getId()];
        for (int vert = 0; vert < 4; vert++) {
            vertPos.add(vertPositions[vert][0] + this.getBlockPosition().getX());
            vertPos.add(vertPositions[vert][1] + this.getBlockPosition().getY());
            vertPos.add(vertPositions[vert][2] + this.getBlockPosition().getZ());

            float u;
            switch (UV[vert][0]) {
                case 0:
                    u = texture.getKey();
                    break;
                case 1:
                    u = texture.getValue();
                    break;
                default:
                    u = 0;
            }

            textPos.add(u);
            textPos.add(UV[vert][1]);
        }
    }
}

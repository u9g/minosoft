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

package de.bixilon.minosoft.render.blockModels;

import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.render.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.subBlocks.Cuboid;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlockPosition;
import de.bixilon.minosoft.render.texture.InFaceUV;
import javafx.util.Pair;

public class Face {
    SubBlockPosition[] positions;
    InFaceUV uv;

    public Face(FaceOrientation orientation, Pair<Float, Float> texture, InFaceUV uv, Cuboid cuboid) {
        positions = cuboid.getFacePositions(orientation);
        this.uv = uv;
        this.uv.prepare(texture);
    }

    public void draw(BlockPosition pos) {
        for (int i = 0; i < 4; i++) {
            uv.draw(i);
            positions[i].draw(pos);
        }
    }
}

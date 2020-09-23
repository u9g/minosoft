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

import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlockPosition;
import de.bixilon.minosoft.render.texture.InFaceUV;

public class Face {
    private final SubBlockPosition[] positions;
    private int rotation;
    private InFaceUV uv;

    public Face(float texture, InFaceUV uv, SubBlockPosition[] facePositions) {
        this(texture, uv, facePositions, 0);
    }

    public Face(float texture, InFaceUV uv, SubBlockPosition[] facePositions, Integer rotation) {
        positions = facePositions;
        this.uv = uv;
        this.uv.prepare(texture);
        this.rotation = rotation;
    }

    public Face() {
        positions = new SubBlockPosition[]{};
    }

    public void draw(BlockPosition pos) {
        for (int i = 0; i < positions.length; i++) {
            uv.draw(i + rotation);
            positions[i].draw(pos);
        }
    }
}

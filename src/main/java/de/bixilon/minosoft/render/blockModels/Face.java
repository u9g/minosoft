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
import de.bixilon.minosoft.render.MainWindow;
import de.bixilon.minosoft.render.fullFace.FaceOrientation;
import de.bixilon.minosoft.render.fullFace.InFaceUV;
import javafx.util.Pair;

import static de.bixilon.minosoft.render.fullFace.RenderConstants.blockRes;
import static de.bixilon.minosoft.render.fullFace.RenderConstants.texturePackRes;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class Face {
    FaceOrientation orientation;
    Pair<Float, Float> texture;
    InFaceUV uv;
    SubBlock subBlock;

    public Face(FaceOrientation orientation, Pair<Float, Float> texture, InFaceUV uv, SubBlock subBlock) {
        this.orientation = orientation;
        this.texture = texture;
        this.uv = uv;
        this.subBlock = subBlock;
    }

    public void draw(BlockPosition pos) {
        float x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
        float step = MainWindow.getRenderer().getTextureLoader().getStep();
        float u1 = texture.getKey() + (float) uv.u1 / (float) blockRes * step;
        float u2 = texture.getValue() + (float) uv.u1 / (float) blockRes * step;
        float v1 = (float) uv.v1 / (float) texturePackRes;
        float v2 = (float) uv.v2 / (float) texturePackRes;

        switch (orientation) {
            case EAST:
                x1 = x2 = subBlock.pos2.x;
                y1 = subBlock.pos1.y;
                y2 = subBlock.pos2.y;
                z1 = subBlock.pos1.z;
                z2 = subBlock.pos2.z;
                break;
            case WEST:
                x1 = x2 = subBlock.pos1.x;
                y1 = subBlock.pos1.y;
                y2 = subBlock.pos2.y;
                z1 = subBlock.pos1.z;
                z2 = subBlock.pos2.z;
                break;
            case UP:
                y1 = y2 = subBlock.pos2.y;
                x1 = subBlock.pos1.x;
                x2 = subBlock.pos2.x;
                z1 = subBlock.pos1.z;
                z2 = subBlock.pos2.z;
                break;
            case DOWN:
                y1 = y2 = subBlock.pos1.y;
                x1 = subBlock.pos1.x;
                x2 = subBlock.pos2.x;
                z1 = subBlock.pos1.z;
                z2 = subBlock.pos2.z;
                break;
            case SOUTH:
                z1 = z2 = subBlock.pos2.z;
                x1 = subBlock.pos1.x;
                x2 = subBlock.pos2.x;
                y1 = subBlock.pos1.y;
                y2 = subBlock.pos2.y;
                break;
            case NORTH:
                z1 = z2 = subBlock.pos1.z;
                x1 = subBlock.pos1.x;
                x2 = subBlock.pos2.x;
                y1 = subBlock.pos1.y;
                y2 = subBlock.pos2.y;
                break;
        }
        switch (orientation) {
            case EAST:
            case WEST:
                glTexCoord2f(u1, v1);
                glVertex3f(x1, y1, z1);

                glTexCoord2f(u2, v1);
                glVertex3f(x1, y2, z1);

                glTexCoord2f(u2, v2);
                glVertex3f(x1, y2, z2);

                glTexCoord2f(u2, v2);
                glVertex3f(x1, y2, z2);
                break;
            case UP:
            case DOWN:
                glTexCoord2f(u1, v1);
                glVertex3f(x1, y1, z1);

                glTexCoord2f(u2, v1);
                glVertex3f(x2, y1, z1);

                glTexCoord2f(u2, v2);
                glVertex3f(x2, y1, z2);

                glTexCoord2f(u2, v2);
                glVertex3f(x1, y1, z2);
                break;
            case NORTH:
            case SOUTH:
                glTexCoord2f(u1, v1);
                glVertex3f(x1, y1, z1);

                glTexCoord2f(u2, v1);
                glVertex3f(x2, y1, z1);

                glTexCoord2f(u2, v2);
                glVertex3f(x2, y2, z1);

                glTexCoord2f(u2, v2);
                glVertex3f(x1, y2, z1);
                break;
        }
    }
}

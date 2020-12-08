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

package de.bixilon.minosoft.render.texture;

import com.google.gson.JsonArray;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;
import de.bixilon.minosoft.render.blockModels.subBlocks.SubBlockPosition;
import org.apache.commons.collections.primitives.ArrayFloatList;

public class InFaceUV {
    private final int u1;
    private final int v1;
    private final int u2;
    private final int v2;

    private float realU1 = -1, realV1, realU2, realV2;

    public InFaceUV(JsonArray json, SubBlockPosition from, SubBlockPosition to, FaceOrientation orientation) {
        this(from, to, orientation);
    }

    public InFaceUV(SubBlockPosition from, SubBlockPosition to, FaceOrientation orientation) {
        switch (orientation) {
            case EAST, WEST -> {
                u1 = (int) from.getVector().getZ();
                v1 = (int) (RenderConstants.TEXTURE_PACK_RESOLUTION - from.getVector().getY());
                u2 = (int) to.getVector().getZ();
                v2 = (int) (RenderConstants.TEXTURE_PACK_RESOLUTION - to.getVector().getY());
            }
            case UP, DOWN -> {
                u1 = (int) from.getVector().getX();
                v1 = (int) (RenderConstants.TEXTURE_PACK_RESOLUTION - from.getVector().getZ());
                u2 = (int) to.getVector().getX();
                v2 = (int) (RenderConstants.TEXTURE_PACK_RESOLUTION - to.getVector().getZ());
            }
            case SOUTH, NORTH -> {
                u1 = (int) from.getVector().getX();
                v1 = (int) (RenderConstants.TEXTURE_PACK_RESOLUTION - from.getVector().getY());
                u2 = (int) to.getVector().getX();
                v2 = (int) (RenderConstants.TEXTURE_PACK_RESOLUTION - to.getVector().getY());
            }
            default -> throw new RuntimeException();
        }
    }

    public void prepare(float texture, TextureLoader textureLoader) {
        realU1 = texture + textureLoader.getStep() * u1 / RenderConstants.TEXTURE_PACK_RESOLUTION;
        realU2 = texture + textureLoader.getStep() * u2 / RenderConstants.TEXTURE_PACK_RESOLUTION;
        realV1 = (float) v2 / RenderConstants.TEXTURE_PACK_RESOLUTION;
        realV2 = (float) v1 / RenderConstants.TEXTURE_PACK_RESOLUTION;
    }

    public ArrayFloatList getFloats(int i) {
        if (i > 3) {
            i -= 4;
        }
        ArrayFloatList result = new ArrayFloatList();
        switch (i) {
            case 0 -> {
                result.add(realU1);
                result.add(realV1);
                return result;
            }
            case 1 -> {
                result.add(realU2);
                result.add(realV1);
                return result;
            }
            case 2 -> {
                result.add(realU2);
                result.add(realV2);
                return result;
            }
            case 3 -> {
                result.add(realU1);
                result.add(realV2);
                return result;
            }
        }
        return result;
    }

    public boolean exists() {
        return realU1 >= 0;
    }
}

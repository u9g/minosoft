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

package de.bixilon.minosoft.render.texture;

import com.google.gson.JsonArray;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.blockModels.Face.RenderConstants;
import org.apache.commons.collections.primitives.ArrayFloatList;

public class InFaceUV {
    public final int u1, v1, u2, v2;

    public float realU1, realV1, realU2, realV2;

    public InFaceUV(JsonArray json) {
        u1 = json.get(0).getAsInt();
        v1 = json.get(1).getAsInt();
        u2 = json.get(2).getAsInt();
        v2 = json.get(3).getAsInt();
    }

    public InFaceUV() {
        u1 = v1 = 0;
        u2 = v2 = 16;
    }

    public void prepare(float texture) {
        realU1 = texture + u1 * GameWindow.getRenderer().getBlockModelLoader().getTextureLoader().getStep() / RenderConstants.TEXTURE_PACK_RESOLUTION;
        realU2 = texture + u2 * GameWindow.getRenderer().getBlockModelLoader().getTextureLoader().getStep() / RenderConstants.TEXTURE_PACK_RESOLUTION;
        realV1 = (float) v1 / RenderConstants.TEXTURE_PACK_RESOLUTION;
        realV2 = (float) v2 / RenderConstants.TEXTURE_PACK_RESOLUTION;
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
}

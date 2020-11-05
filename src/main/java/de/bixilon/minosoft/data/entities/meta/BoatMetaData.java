/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.data.entities.meta;

public class BoatMetaData extends EntityMetaData {

    public BoatMetaData(MetaDataHashMap sets, int versionId) {
        super(sets, versionId);
    }

    public int getTimeSinceHit() {
        final int defaultValue = 0;
        if (versionId < 57) {
            return sets.getInt(17, defaultValue);
        }
        return sets.getInt(super.getLastDataIndex() + 1, defaultValue);
    }

    public int getForwardDirection() {
        final int defaultValue = 1;
        if (versionId < 57) {
            return sets.getInt(18, defaultValue);
        }
        return sets.getInt(super.getLastDataIndex() + 2, defaultValue);
    }

    public float getDamageTaken() {
        final float defaultValue = 0.0F;
        if (versionId < 57) {
            return sets.getFloat(19, defaultValue);
        }
        return sets.getFloat(super.getLastDataIndex() + 3, defaultValue);
    }

    public BoatMaterials getMaterial() {
        final int defaultValue = BoatMaterials.OAK.ordinal();
        if (versionId < 57) {
            return BoatMaterials.byId(defaultValue);
        }
        return BoatMaterials.byId(sets.getInt(super.getLastDataIndex() + 4, defaultValue));
    }

    public boolean isRightPaddleTurning() {
        final boolean defaultValue = false;
        if (versionId < 110) { //ToDo
            return defaultValue;
        }
        return sets.getBoolean(super.getLastDataIndex() + 5, defaultValue);
    }

    public boolean isLeftPaddleTurning() {
        final boolean defaultValue = false;
        if (versionId < 110) { //ToDo
            return defaultValue;
        }
        return sets.getBoolean(super.getLastDataIndex() + 6, defaultValue);
    }

    public int getSplashTimer() {
        final int defaultValue = 0;
        if (versionId < 401) { // ToDo
            return defaultValue;
        }
        return sets.getInt(super.getLastDataIndex() + 7, defaultValue);
    }

    @Override
    protected int getLastDataIndex() {
        if (versionId < 110) { //ToDo
            return super.getLastDataIndex() + 4;
        }
        if (versionId < 401) { // ToDo
            return super.getLastDataIndex() + 5;
        }
        return super.getLastDataIndex() + 6;
    }

    public enum BoatMaterials {
        OAK,
        SPRUCE,
        BIRCH,
        JUNGLE,
        ACACIA,
        DARK_OAK;

        public static BoatMaterials byId(int id) {
            return values()[id];
        }
    }
}

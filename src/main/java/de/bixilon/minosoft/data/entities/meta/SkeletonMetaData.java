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

public class SkeletonMetaData extends MonsterMetaData {

    public SkeletonMetaData(MetaDataHashMap sets, int versionId) {
        super(sets, versionId);
    }

    public SkeletonTypes getSkeletonType() {
        final int defaultValue = SkeletonTypes.NORMAL.ordinal();
        if (versionId < 57) {
            return SkeletonTypes.byId(sets.getInt(13, defaultValue));
        }
        if (versionId <= 204) { //ToDo
            return SkeletonTypes.byId(sets.getInt(super.getLastDataIndex() + 1, defaultValue));
        }
        return SkeletonTypes.byId(defaultValue);
    }

    public boolean isSwingingArms() {
        final boolean defaultValue = false;
        if (versionId < 110) { //ToDo
            return defaultValue;
        }
        if (versionId <= 204) { //ToDo
            return sets.getBoolean(super.getLastDataIndex() + 2, defaultValue);
        }
        if (versionId <= 401) { // ToDo
            return sets.getBoolean(super.getLastDataIndex() + 1, defaultValue);
        }
        return defaultValue;
    }

    @Override
    protected int getLastDataIndex() {
        if (versionId == 110 || versionId == 204) { //ToDo
            return super.getLastDataIndex() + 2;
        }
        return super.getLastDataIndex() + 1;
    }

    public enum SkeletonTypes {
        NORMAL,
        WITHER,
        STRAY;

        public static SkeletonTypes byId(int id) {
            return values()[id];
        }
    }
}

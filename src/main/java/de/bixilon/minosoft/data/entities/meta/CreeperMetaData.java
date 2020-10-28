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
package de.bixilon.minosoft.data.entities.meta;

public class CreeperMetaData extends MonsterMetaData {

    public CreeperMetaData(MetaDataHashMap sets, int versionId) {
        super(sets, versionId);
    }

    public int getState() {
        final int defaultValue = -1;
        if (versionId < 57) {
            return sets.getInt(16, defaultValue);
        }
        return sets.getInt(super.getLastDataIndex() + 1, defaultValue);
    }

    public boolean isCharged() {
        final boolean defaultValue = false;
        if (versionId < 57) {
            return sets.getBoolean(17, defaultValue);
        }
        return sets.getBoolean(super.getLastDataIndex() + 2, defaultValue);
    }

    public boolean isIgnited() {
        final boolean defaultValue = false;
        if (versionId < 57) {
            return defaultValue;
        }
        return sets.getBoolean(super.getLastDataIndex() + 3, defaultValue);
    }

    @Override
    protected int getLastDataIndex() {
        return super.getLastDataIndex() + 3;
    }
}

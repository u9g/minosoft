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

package de.bixilon.minosoft.game.datatypes.entities.mob;

import de.bixilon.minosoft.game.datatypes.entities.*;
import de.bixilon.minosoft.game.datatypes.entities.meta.EntityMetaData;
import de.bixilon.minosoft.game.datatypes.entities.meta.SnowGolemMetaData;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

import java.util.HashMap;

public class SnowGolem extends Mob implements MobInterface {
    SnowGolemMetaData metaData;

    public SnowGolem(int entityId, Location location, short yaw, short pitch, Velocity velocity, HashMap<Integer, EntityMetaData.MetaDataSet> sets, ProtocolVersion version) {
        super(entityId, location, yaw, pitch, velocity);
        this.metaData = new SnowGolemMetaData(sets, version);
    }

    @Override
    public Entities getEntityType() {
        return Entities.SNOW_GOLEM;
    }

    @Override
    public SnowGolemMetaData getMetaData() {
        return metaData;
    }

    @Override
    public void setMetaData(EntityMetaData metaData) {
        this.metaData = (SnowGolemMetaData) metaData;
    }

    @Override
    public float getWidth() {
        return 0.7F;
    }

    @Override
    public float getHeight() {
        return 1.9F;
    }

    @Override
    public int getMaxHealth() {
        return 4;
    }

    @Override
    public Class<? extends EntityMetaData> getMetaDataClass() {
        return SnowGolemMetaData.class;
    }
}

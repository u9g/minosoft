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

package de.bixilon.minosoft.data.entities.objects;

import de.bixilon.minosoft.data.entities.EntityObject;
import de.bixilon.minosoft.data.entities.Location;
import de.bixilon.minosoft.data.entities.ObjectInterface;
import de.bixilon.minosoft.data.entities.meta.EntityMetaData;

import java.util.UUID;

public class LightningBolt extends EntityObject implements ObjectInterface {
    EntityMetaData metaData;

    public LightningBolt(int entityId, UUID uuid, Location location, short yaw, short pitch, short headYaw, EntityMetaData.MetaDataHashMap sets, int versionId) {
        super(entityId, uuid, location, yaw, pitch, headYaw);
        this.metaData = new EntityMetaData(sets, versionId);
    }

    public LightningBolt(int entityId, Location location, int versionId) {
        super(entityId, null, location, 0, 0);
    }

    @Override
    public EntityMetaData getMetaData() {
        return metaData;
    }

    @Override
    public void setMetaData(EntityMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    @Override
    public float getHeight() {
        return 0.0F;
    }
}

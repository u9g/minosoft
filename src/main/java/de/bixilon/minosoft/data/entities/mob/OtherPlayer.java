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

package de.bixilon.minosoft.data.entities.mob;

import de.bixilon.minosoft.data.PlayerPropertyData;
import de.bixilon.minosoft.data.entities.Location;
import de.bixilon.minosoft.data.entities.Mob;
import de.bixilon.minosoft.data.entities.MobInterface;
import de.bixilon.minosoft.data.entities.Poses;
import de.bixilon.minosoft.data.entities.meta.EntityMetaData;
import de.bixilon.minosoft.data.entities.meta.HumanMetaData;

import java.util.UUID;

public class OtherPlayer extends Mob implements MobInterface {
    final String name;
    final PlayerPropertyData[] properties;
    final short currentItem;
    final Poses status = Poses.STANDING;
    HumanMetaData metaData;

    public OtherPlayer(int entityId, String name, UUID uuid, PlayerPropertyData[] properties, Location location, int yaw, int pitch, int headYaw, short currentItem, HumanMetaData metaData) {
        super(entityId, uuid, location, yaw, pitch, headYaw);
        this.name = name;
        this.properties = properties;
        this.currentItem = currentItem;
        this.metaData = metaData;
    }

    @Override
    public EntityMetaData getMetaData() {
        return metaData;
    }

    @Override
    public void setMetaData(EntityMetaData data) {
        this.metaData = (HumanMetaData) data;
    }

    @Override
    public float getWidth() {
        if (status == Poses.SLEEPING) {
            return 0.2F;
        }
        return 0.6F;
    }

    @Override
    public float getHeight() {
        return switch (status) {
            default -> 1.8F;
            case SNEAKING -> 1.5F;
            case FLYING, SWIMMING -> 0.6F;
            case SLEEPING -> 0.2F;
        };
    }

    @Override
    public int getMaxHealth() {
        return (int) (40 + metaData.getAdditionalHearts());
    }

    public String getName() {
        return name;
    }

    public PlayerPropertyData[] getProperties() {
        return properties;
    }

    public short getCurrentItem() {
        return currentItem;
    }

    public Poses getStatus() {
        return status;
    }

    @Override
    public Class<? extends EntityMetaData> getMetaDataClass() {
        return HumanMetaData.class;
    }
}

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

package de.bixilon.minosoft.data.entities.entities.projectile;

import de.bixilon.minosoft.data.entities.EntityMetaDataFields;
import de.bixilon.minosoft.data.entities.EntityRotation;
import de.bixilon.minosoft.data.entities.Location;
import de.bixilon.minosoft.protocol.network.Connection;

import java.util.UUID;

public abstract class AbstractArrow extends Projectile {
    public AbstractArrow(Connection connection, int entityId, UUID uuid, Location location, EntityRotation rotation) {
        super(connection, entityId, uuid, location, rotation);
    }

    private boolean getAbstractArrowFlag(int bitMask) {
        return metaData.getSets().getBitMask(EntityMetaDataFields.ABSTRACT_ARROW_FLAGS, bitMask);
    }

    public boolean isCritical() {
        return getAbstractArrowFlag(0x01);
    }

    public boolean isNoClip() {
        return getAbstractArrowFlag(0x02);
    }

    public byte getPiercingLevel() {
        return metaData.getSets().getByte(EntityMetaDataFields.ABSTRACT_ARROW_PIERCE_LEVEL);
    }

    public UUID getOwnerUUID() {
        return metaData.getSets().getUUID(EntityMetaDataFields.ABSTRACT_ARROW_OWNER_UUID);
    }
}

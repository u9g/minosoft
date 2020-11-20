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

package de.bixilon.minosoft.data.entities.entities.boss.enderdragon;

import de.bixilon.minosoft.data.entities.EntityMetaDataFields;
import de.bixilon.minosoft.data.entities.EntityRotation;
import de.bixilon.minosoft.data.entities.Location;
import de.bixilon.minosoft.data.entities.entities.Entity;
import de.bixilon.minosoft.data.entities.entities.EntityMetaDataFunction;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.protocol.network.Connection;

import javax.annotation.Nullable;
import java.util.UUID;

public class EndCrystal extends Entity {

    public EndCrystal(Connection connection, int entityId, UUID uuid, Location location, EntityRotation rotation) {
        super(connection, entityId, uuid, location, rotation);
    }

    @EntityMetaDataFunction(identifier = "beamTarget")
    @Nullable
    public BlockPosition getBeamTarget() {
        return getMetaData().getSets().getPosition(EntityMetaDataFields.END_CRYSTAL_BEAM_TARGET);
    }

    @EntityMetaDataFunction(identifier = "showBottom")
    public boolean shotBottom() {
        return getMetaData().getSets().getBoolean(EntityMetaDataFields.END_CRYSTAL_SHOW_BOTTOM);
    }
}


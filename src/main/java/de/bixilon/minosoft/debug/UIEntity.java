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

package de.bixilon.minosoft.debug;

import de.bixilon.minosoft.game.datatypes.entities.Entity;
import de.bixilon.minosoft.game.datatypes.entities.EntityObject;
import de.bixilon.minosoft.game.datatypes.entities.Mob;

import java.util.HashMap;

public class UIEntity {
    static HashMap<Integer, UIEntity> entityHashMap;
    final int id;
    final Entity entity;

    public UIEntity(int id, Entity entity) {
        if (entityHashMap == null) {
            entityHashMap = new HashMap<>();
        }
        this.id = id;
        this.entity = entity;
        entityHashMap.put(id, this);
    }

    public static UIEntity getByID(int id) {
        return entityHashMap.get(id);
    }

    public int getId() {
        return id;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        if (entity instanceof Mob) {
            return String.format("%d (%s)", id, ((Mob) entity).getEntityType().name());
        }
        return String.format("%d (%s)", id, ((EntityObject) entity).getEntityType().name());
    }
}

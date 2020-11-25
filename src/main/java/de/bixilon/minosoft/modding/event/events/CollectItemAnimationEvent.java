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

package de.bixilon.minosoft.modding.event.events;

import de.bixilon.minosoft.data.entities.entities.Entity;
import de.bixilon.minosoft.data.entities.entities.item.ItemEntity;
import de.bixilon.minosoft.modding.event.events.annotations.MinimumProtocolVersion;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.protocol.packets.clientbound.play.PacketCollectItem;

public class CollectItemAnimationEvent extends CancelableEvent {
    private final ItemEntity item;
    private final Entity collector;
    private final int count;

    public CollectItemAnimationEvent(Connection connection, ItemEntity item, Entity collector, int count) {
        super(connection);
        this.item = item;
        this.collector = collector;
        this.count = count;
    }

    public CollectItemAnimationEvent(Connection connection, PacketCollectItem pkg) {
        super(connection);
        this.item = (ItemEntity) connection.getPlayer().getWorld().getEntity(pkg.getItemEntityId());
        this.collector = connection.getPlayer().getWorld().getEntity(pkg.getCollectorEntityId());
        this.count = pkg.getCount();
    }

    public ItemEntity getItem() {
        return item;
    }

    public Entity getCollector() {
        return collector;
    }

    @MinimumProtocolVersion(versionId = 301)
    public int getCount() {
        return count;
    }
}

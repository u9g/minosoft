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

package de.bixilon.minosoft.game.datatypes.entities.objects;

import de.bixilon.minosoft.game.datatypes.entities.EntityObject;
import de.bixilon.minosoft.game.datatypes.entities.Location;
import de.bixilon.minosoft.game.datatypes.entities.ObjectInterface;
import de.bixilon.minosoft.game.datatypes.entities.Objects;
import de.bixilon.minosoft.game.datatypes.entities.meta.EntityMetaData;
import de.bixilon.minosoft.game.datatypes.entities.meta.ItemFrameMetaData;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class ItemFrame extends EntityObject implements ObjectInterface {
    ItemFrameMetaData metaData;
    FrameDirection direction;

    public ItemFrame(int id, Location location, int yaw, int pitch, int additionalInt, ProtocolVersion v) {
        super(id, location, yaw, pitch, null);
        // objects do not spawn with metadata... reading additional info from the following int
        // tnt does not have any additional info
        direction = FrameDirection.byId(additionalInt);
    }

    @Override
    public Objects getEntityType() {
        return Objects.ITEM_FRAME;
    }

    @Override
    public ItemFrameMetaData getMetaData() {
        return metaData;
    }

    @Override
    public void setMetaData(EntityMetaData metaData) {
        this.metaData = (ItemFrameMetaData) metaData;
    }

    @Override
    public float getWidth() {
        return 1.0F; //ToDo
    }

    @Override
    public float getHeight() {
        return 1.0F; //ToDo
    }

    @Override
    public Class<? extends EntityMetaData> getMetaDataClass() {
        return ItemFrameMetaData.class;
    }

    public FrameDirection getDirection() { // orientation
        return direction;
    }

    public enum FrameDirection {
        SOUTH(0),
        WEST(1),
        NORTH(2),
        EAST(3);

        final int id;

        FrameDirection(int id) {
            this.id = id;
        }

        public static FrameDirection byId(int id) {
            for (FrameDirection d : values()) {
                if (d.getId() == id) {
                    return d;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

}

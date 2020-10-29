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

package de.bixilon.minosoft.protocol.packets.clientbound.play;

import de.bixilon.minosoft.data.mappings.statistics.Statistic;
import de.bixilon.minosoft.data.mappings.statistics.StatisticCategories;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ClientboundPacket;
import de.bixilon.minosoft.protocol.protocol.InByteBuffer;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;

import java.util.HashMap;

public class PacketStatistics implements ClientboundPacket {
    final HashMap<Statistic, Integer> statistics = new HashMap<>();

    @Override
    public boolean read(InByteBuffer buffer) {
        int length = buffer.readVarInt();
        for (int i = 0; i < length; i++) {
            if (buffer.getVersionId() < 346) { // ToDo
                statistics.put(buffer.getConnection().getMapping().getStatisticByIdentifier(buffer.readString()), buffer.readVarInt());
            } else {
                StatisticCategories category = StatisticCategories.byId(buffer.readVarInt());
                statistics.put(buffer.getConnection().getMapping().getStatisticById(buffer.readVarInt()), buffer.readVarInt());
            }
        }
        return true;
    }

    @Override
    public void handle(PacketHandler h) {
        h.handle(this);
    }

    @Override
    public void log() {
        Log.protocol(String.format("Received player statistics (count=%d)", statistics.size()));
    }

    public HashMap<Statistic, Integer> getStatistics() {
        return statistics;
    }
}

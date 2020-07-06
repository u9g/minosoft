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

package de.bixilon.minosoft.protocol.packets.serverbound.status;

import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class PacketStatusPing implements ServerboundPacket {
    final Long id;

    public PacketStatusPing(Long id) {
        this.id = id;
    }

    public PacketStatusPing(int id) {
        this.id = (long) id;
    }

    @Override
    public OutPacketBuffer write(ProtocolVersion version) {
        log();
        // no version checking, is the same in all versions (1.7.x - 1.15.2)
        OutPacketBuffer buffer = new OutPacketBuffer(version, version.getPacketCommand(Packets.Serverbound.STATUS_PING));
        buffer.writeLong(id);
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Sending ping packet (%s)", id));
    }
}

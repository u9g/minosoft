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

package de.bixilon.minosoft.protocol.packets.serverbound.play;

import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class PacketSelectTrade implements ServerboundPacket {

    final int id;

    public PacketSelectTrade(int id) {
        this.id = id;
    }


    @Override
    public OutPacketBuffer write(ProtocolVersion version) {
        OutPacketBuffer buffer = new OutPacketBuffer(version, version.getPacketCommand(Packets.Serverbound.PLAY_SELECT_TRADE));
        switch (version) {
            case VERSION_1_13_2:
            case VERSION_1_14_4:
                buffer.writeVarInt(id);
                break;
        }
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Sending select trade packet (id=%d)", id));
    }
}

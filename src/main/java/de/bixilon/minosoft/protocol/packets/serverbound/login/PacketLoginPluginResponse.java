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

package de.bixilon.minosoft.protocol.packets.serverbound.login;

import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class PacketLoginPluginResponse implements ServerboundPacket {
    final int messageId;
    final boolean successful;
    byte[] data;

    public PacketLoginPluginResponse(int messageId, boolean successful) {
        this.messageId = messageId;
        this.successful = successful;
    }

    public PacketLoginPluginResponse(int messageId, byte[] data) {
        this.messageId = messageId;
        this.successful = true;
        this.data = data;
    }

    @Override
    public OutPacketBuffer write(ProtocolVersion version) {
        OutPacketBuffer buffer = new OutPacketBuffer(version, version.getPacketCommand(Packets.Serverbound.LOGIN_PLUGIN_RESPONSE));
        switch (version) {
            case VERSION_1_13_2:
            case VERSION_1_14_4:
                buffer.writeVarInt(messageId);
                buffer.writeBoolean(successful);
                if (successful) {
                    buffer.writeBytes(data);
                }
        }
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Sending login plugin response (messageId=%d, successful=%s)", messageId, successful));
    }
}

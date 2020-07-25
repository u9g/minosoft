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

public class PacketPlayerPositionSending implements ServerboundPacket {
    final double x;
    final double feetY;
    final double headY;
    final double z;
    final boolean onGround;

    public PacketPlayerPositionSending(double x, double feetY, double headY, double z, boolean onGround) {
        this.x = x;
        this.feetY = feetY;
        this.headY = headY;
        this.z = z;
        this.onGround = onGround;
    }

    public PacketPlayerPositionSending(double x, double feetY, double z, boolean onGround) {
        this.x = x;
        this.feetY = feetY;
        this.headY = feetY - 1.62F;
        this.z = z;
        this.onGround = onGround;
    }


    @Override
    public OutPacketBuffer write(ProtocolVersion version) {
        OutPacketBuffer buffer = new OutPacketBuffer(version, version.getPacketCommand(Packets.Serverbound.PLAY_PLAYER_POSITION));
        switch (version) {
            case VERSION_1_7_10:
                buffer.writeDouble(x);
                buffer.writeDouble(feetY);
                buffer.writeDouble(headY);
                buffer.writeDouble(z);
                buffer.writeBoolean(onGround);
                break;
            case VERSION_1_8:
            case VERSION_1_9_4:
            case VERSION_1_10:
            case VERSION_1_11_2:
            case VERSION_1_12_2:
            case VERSION_1_13_2:
            case VERSION_1_14_4:
                buffer.writeDouble(x);
                buffer.writeDouble(feetY);
                buffer.writeDouble(z);
                buffer.writeBoolean(onGround);
                break;
        }
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Sending player position: %s %s %s", x, headY, z));
    }
}

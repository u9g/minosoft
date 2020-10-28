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
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;

public class PacketPlayerPositionAndRotationSending implements ServerboundPacket {
    final double x;
    final double feetY;
    final double headY;
    final double z;
    final float yaw;
    final float pitch;
    final boolean onGround;

    public PacketPlayerPositionAndRotationSending(double x, double feetY, double headY, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.feetY = feetY;
        this.headY = headY;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PacketPlayerPositionAndRotationSending(double x, double feetY, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.feetY = feetY;
        this.headY = feetY - 1.62F;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public OutPacketBuffer write(Connection connection) {
        OutPacketBuffer buffer = new OutPacketBuffer(connection, Packets.Serverbound.PLAY_PLAYER_POSITION_AND_ROTATION);
        buffer.writeDouble(x);
        buffer.writeDouble(feetY);
        if (buffer.getVersionId() < 10) {
            buffer.writeDouble(headY);
        }
        buffer.writeDouble(z);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        buffer.writeBoolean(onGround);
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Sending player position and rotation: %s %s %s (yaw=%s, pitch=%s)", x, headY, z, yaw, pitch));
    }
}

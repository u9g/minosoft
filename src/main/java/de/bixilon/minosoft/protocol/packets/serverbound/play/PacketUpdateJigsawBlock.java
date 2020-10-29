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

package de.bixilon.minosoft.protocol.packets.serverbound.play;

import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;

public class PacketUpdateJigsawBlock implements ServerboundPacket {
    final BlockPosition position;
    final String targetPool;
    final String finalState;
    String attachmentType;
    String name;
    String target;
    String jointType;

    public PacketUpdateJigsawBlock(BlockPosition position, String attachmentType, String targetPool, String finalState) {
        this.position = position;
        this.attachmentType = attachmentType;
        this.targetPool = targetPool;
        this.finalState = finalState;
    }

    public PacketUpdateJigsawBlock(BlockPosition position, String name, String target, String targetPool, String finalState, String jointType) {
        this.position = position;
        this.name = name;
        this.target = target;
        this.targetPool = targetPool;
        this.finalState = finalState;
        this.jointType = jointType;
    }

    @Override
    public OutPacketBuffer write(Connection connection) {
        OutPacketBuffer buffer = new OutPacketBuffer(connection, Packets.Serverbound.PLAY_UPDATE_JIGSAW_BLOCK);
        buffer.writePosition(position);
        if (buffer.getVersionId() < 708) {
            buffer.writeString(attachmentType);
            buffer.writeString(targetPool);
            buffer.writeString(finalState);
        } else {
            buffer.writeString(name);
            buffer.writeString(target);
            buffer.writeString(targetPool);
            buffer.writeString(finalState);
            buffer.writeString(jointType);
        }
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Updating jigsaw block (position=%s, attachmentType=%s, targetPool=%s, finalState=%s)", position, attachmentType, targetPool, finalState));
    }
}

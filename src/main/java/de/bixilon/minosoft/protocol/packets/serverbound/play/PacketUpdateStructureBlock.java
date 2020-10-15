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

import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;

public class PacketUpdateStructureBlock implements ServerboundPacket {
    final BlockPosition position;
    final StructureBlockActions action;
    final StructureBlockModes mode;
    final String name;

    final byte offsetX;
    final byte offsetY;
    final byte offsetZ;

    final byte sizeX;
    final byte sizeY;
    final byte sizeZ;

    final StructureBlockMirrors mirror;
    final StructureBlockRotations rotation;
    final String metaData;
    final float integrity;
    final long seed;
    final byte flags;

    public PacketUpdateStructureBlock(BlockPosition position, StructureBlockActions action, StructureBlockModes mode, String name, byte offsetX, byte offsetY, byte offsetZ, byte sizeX, byte sizeY, byte sizeZ, StructureBlockMirrors mirror, StructureBlockRotations rotation, String metaData, float integrity, long seed, byte flags) {
        this.position = position;
        this.action = action;
        this.mode = mode;
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.mirror = mirror;
        this.rotation = rotation;
        this.metaData = metaData;
        this.integrity = integrity;
        this.seed = seed;
        this.flags = flags;
    }

    @Override
    public OutPacketBuffer write(Connection connection) {
        OutPacketBuffer buffer = new OutPacketBuffer(connection, Packets.Serverbound.PLAY_UPDATE_STRUCTURE_BLOCK);
        buffer.writePosition(position);
        buffer.writeVarInt(action.ordinal());
        buffer.writeVarInt(mode.ordinal());
        buffer.writeString(name);
        buffer.writeByte(offsetX);
        buffer.writeByte(offsetY);
        buffer.writeByte(offsetZ);
        buffer.writeByte(sizeX);
        buffer.writeByte(sizeY);
        buffer.writeByte(sizeZ);
        buffer.writeVarInt(mirror.ordinal());
        buffer.writeVarInt(rotation.ordinal());
        buffer.writeString(metaData);
        buffer.writeFloat(integrity);
        buffer.writeVarLong(seed);
        buffer.writeByte(flags);
        return buffer;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Sending update structure block packet (position=%s, action=%s, mode=%s, name=\"%s\", offsetX=%d, offsetY=%d, offsetZ=%d, sizeX=%d, sizeY=%d, sizeZ=%d, mirror=%s, rotation=%s, metaData=\"%s\", integrity=%s, seed=%s, flags=%s)", position, action, mode, name, offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, mirror, rotation, metaData, integrity, seed, flags));
    }

    public enum StructureBlockActions {
        UPDATE,
        SAVE,
        LOAD,
        DETECT_SIZE;

        public static StructureBlockActions byId(int id) {
            return values()[id];
        }
    }

    public enum StructureBlockModes {
        SAVE,
        LOAD,
        CORNER,
        DATA;

        public static StructureBlockModes byId(int id) {
            return values()[id];
        }
    }

    public enum StructureBlockMirrors {
        NONE,
        LEFT_RIGHT,
        FRONT_BACK;

        public static StructureBlockMirrors byId(int id) {
            return values()[id];
        }
    }

    public enum StructureBlockRotations {
        NONE,
        CLOCKWISE_90,
        CLOCKWISE_180,
        COUNTERCLOCKWISE_90;

        public static StructureBlockRotations byId(int id) {
            return values()[id];
        }
    }

}

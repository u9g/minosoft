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

package de.bixilon.minosoft.protocol.protocol;

import de.bixilon.minosoft.game.datatypes.Direction;
import de.bixilon.minosoft.game.datatypes.Slot;
import de.bixilon.minosoft.game.datatypes.TextComponent;
import de.bixilon.minosoft.game.datatypes.entities.Pose;
import de.bixilon.minosoft.game.datatypes.particle.BlockParticle;
import de.bixilon.minosoft.game.datatypes.particle.OtherParticles;
import de.bixilon.minosoft.game.datatypes.particle.Particle;
import de.bixilon.minosoft.game.datatypes.particle.Particles;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.nbt.tag.CompoundTag;
import de.bixilon.minosoft.nbt.tag.TagTypes;
import de.bixilon.minosoft.util.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class InByteBuffer {
    private final byte[] bytes;
    private int pos;

    public InByteBuffer(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte readByte() {
        byte ret;
        ret = bytes[pos];
        pos = pos + 1;
        return ret;
    }

    public byte[] readBytes(int count) {
        byte[] ret = new byte[count];
        System.arraycopy(bytes, pos, ret, 0, count);
        pos = pos + count;
        return ret;
    }


    private byte[] readBytes(int pos, int count) {
        byte[] ret = new byte[count];
        System.arraycopy(bytes, pos, ret, 0, count);
        return ret;
    }

    public boolean readBoolean() {
        boolean ret;
        ret = readByte() == 1;
        return ret;
    }

    public short readShort() {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.put(readBytes(Short.BYTES));
        return buffer.getShort(0);
    }

    public short[] readShorts(int num) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * num);
        buffer.put(readBytes(Short.BYTES * num));
        short[] ret = new short[num];
        for (int i = 0; i < num; i++) {
            ret[i] = buffer.getShort(i);
        }
        return ret;
    }

    public int readInteger() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(readBytes(Integer.BYTES));
        return buffer.getInt(0);
    }

    public Long readLong() {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(readBytes(Long.BYTES));
        return buffer.getLong(0);
    }

    public Float readFloat() {
        ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.put(readBytes(Float.BYTES));
        return buffer.getFloat(0);
    }

    public Double readDouble() {
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.put(readBytes(Double.BYTES));
        return buffer.getDouble(0);
    }

    public String readString() {
        int length = readVarInt();
        if (length > ProtocolDefinition.STRING_MAX_LEN) {
            return null;
        }
        return new String(readBytes(length), StandardCharsets.UTF_8);
    }

    public String readString(int length) {
        return new String(readBytes(length));
    }


    public UUID readUUID() {
        ByteBuffer buffer = ByteBuffer.allocate(16); // UUID.BYTES
        buffer.put(readBytes(16));
        return new UUID(buffer.getLong(0), buffer.getLong(1));
    }

    public int readVarInt() {
        // thanks https://wiki.vg/Protocol#VarInt_and_VarLong
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public long readVarLong() {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public double readFixedPointNumberInteger() {
        return readInteger() / 32.0D;
    }

    public double readFixedPointNumberByte() {
        return readByte() / 32.0D;
    }

    public JSONObject readJson() {
        return new JSONObject(readString());
    }

    public BlockPosition readBlockPosition() {
        Long raw = readLong();
        return new BlockPosition(Long.valueOf(raw >> 38).intValue(), Long.valueOf(raw & 0xFFF).shortValue(), Long.valueOf(raw << 26 >> 38).intValue());
    }

    @Override
    public String toString() {
        return "dataLen: " + bytes.length + "; pos: " + pos;
    }

    public TextComponent readTextComponent() {
        return new TextComponent(readString());
    }

    public int getPosition() {
        return this.pos;
    }

    public int getLength() {
        return bytes.length;
    }

    public int getBytesLeft() {
        return bytes.length - pos;
    }


    public Direction readDirection() {
        return Direction.byId(readVarInt());
    }

    public Pose readPose() {
        return Pose.byId(readVarInt());
    }

    public Particle readParticle() {
        Particles type = Particles.byType(readVarInt());
        try {
            if (type.getClazz() == OtherParticles.class) {
                return type.getClazz().getConstructor(Particles.class).newInstance(type);
            } else if (type.getClazz() == BlockParticle.class) {
                return type.getClazz().getConstructor(int.class).newInstance(readVarInt());
            }
            //ToDo
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPosition(int pos) {
        this.pos = pos;
    }

    public CompoundTag readNBT() {

        if (readByte() != TagTypes.COMPOUND.getId()) { // will be a Compound Tag
            // maybe compressed
            setPosition(getPosition() - 1);
            short length = readShort();
            if (length == -1) {
                // no nbt data here...
                return null;
            }
            try {
                return new CompoundTag(new InByteBuffer(Util.decompressGzip(readBytes(length))));
            } catch (IOException e) {
                // oh no
                e.printStackTrace();
                throw new IllegalArgumentException("Bad nbt");
            }
            // try again
        }
        setPosition(getPosition() - 1);
        return new CompoundTag(this);
    }

    public Slot readSlot(ProtocolVersion v) {
        switch (v) {
            case VERSION_1_7_10:
                short id = readShort();
                if (id != -1) {
                    return new Slot(id, readByte(), readShort(), readNBT());
                }
                return null;
                /*

        if (readBoolean()) {
            return new Slot(readVarInt(), readByte(), readNBT());
        }
        //else no data
        return null;
                 */
        }
        return null;
    }

    public String getBase64(int pos, int length) {
        return new String(Base64.getEncoder().encode(readBytes(pos, length)));
    }

    public String getBase64() {
        return getBase64(getPosition(), getBytesLeft());
    }
}

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

package de.bixilon.minosoft.protocol.packets.clientbound.play;

import de.bixilon.minosoft.game.datatypes.entities.Location;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ClientboundPacket;
import de.bixilon.minosoft.protocol.protocol.InPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;


public class PacketSoundEffect implements ClientboundPacket {
    static final float pitchCalc = 100.0F / 63.0F;
    Location location;
    int sound;
    int category;
    float volume;
    float pitch;

    @Override
    public boolean read(InPacketBuffer buffer) {
        switch (buffer.getVersion()) {
            case VERSION_1_9_4:
                sound = buffer.readVarInt();
                category = buffer.readVarInt(); //ToDo: category
                location = new Location(buffer.readFixedPointNumberInteger() * 4, buffer.readFixedPointNumberInteger() * 4, buffer.readFixedPointNumberInteger() * 4);
                volume = buffer.readFloat();
                pitch = (buffer.readByte() * pitchCalc) / 100F;
                return true;
            case VERSION_1_10:
                sound = buffer.readVarInt();
                category = buffer.readVarInt(); //ToDo: category
                location = new Location(buffer.readFixedPointNumberInteger() * 4, buffer.readFixedPointNumberInteger() * 4, buffer.readFixedPointNumberInteger() * 4);
                volume = buffer.readFloat();
                pitch = buffer.readFloat();
                return true;
        }

        return false;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Play sound effect %d with volume=%s and pitch=%s at %s", sound, volume, pitch, location.toString()));
    }

    @Override
    public void handle(PacketHandler h) {
        h.handle(this);
    }

    public Location getLocation() {
        return location;
    }

    /**
     * @return Pitch in Percent
     */
    public float getPitch() {
        return pitch;
    }

    public int getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }
}

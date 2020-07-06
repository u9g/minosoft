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

import de.bixilon.minosoft.game.datatypes.Identifier;
import de.bixilon.minosoft.game.datatypes.particle.Particles;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ClientboundPacket;
import de.bixilon.minosoft.protocol.protocol.InPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;

import java.util.Random;

public class PacketParticle implements ClientboundPacket {
    Particles particle;
    boolean longDistance = false;
    float x;
    float y;
    float z;
    float particleData;
    int count;
    int[] data;

    @Override
    public boolean read(InPacketBuffer buffer) {
        Random random = new Random();
        switch (buffer.getVersion()) {
            case VERSION_1_7_10:
                particle = Particles.byIdentifier(new Identifier(buffer.readString()));
                x = buffer.readFloat();
                y = buffer.readFloat();
                z = buffer.readFloat();

                // offset
                x += buffer.readFloat() * random.nextGaussian();
                y += buffer.readFloat() * random.nextGaussian();
                z += buffer.readFloat() * random.nextGaussian();

                particleData = buffer.readFloat();
                count = buffer.readInt();
                return true;
            case VERSION_1_8:
            case VERSION_1_9_4:
            case VERSION_1_10:
                particle = Particles.byType(buffer.readInt());
                longDistance = buffer.readBoolean();
                x = buffer.readFloat();
                y = buffer.readFloat();
                z = buffer.readFloat();

                // offset
                x += buffer.readFloat() * random.nextGaussian();
                y += buffer.readFloat() * random.nextGaussian();
                z += buffer.readFloat() * random.nextGaussian();

                particleData = buffer.readFloat();
                count = buffer.readInt();
                return true;
        }

        return false;
    }

    @Override
    public void log() {
        Log.protocol(String.format("Received particle spawn at %s %s %s (particle=%s, data=%s, count=%d", x, y, z, particle.name(), particleData, count));
    }

    @Override
    public void handle(PacketHandler h) {
        h.handle(this);
    }

    public Particles getParticle() {
        return particle;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getParticleData() {
        return particleData;
    }

    public int getCount() {
        return count;
    }
}

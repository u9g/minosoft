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

import de.bixilon.minosoft.protocol.packets.clientbound.login.PacketEncryptionKeyRequest;
import de.bixilon.minosoft.protocol.packets.clientbound.login.PacketLoginDisconnect;
import de.bixilon.minosoft.protocol.packets.clientbound.login.PacketLoginSuccess;
import de.bixilon.minosoft.protocol.packets.clientbound.play.*;
import de.bixilon.minosoft.protocol.packets.clientbound.status.PacketStatusPong;
import de.bixilon.minosoft.protocol.packets.clientbound.status.PacketStatusResponse;

public class PacketHandler {

    public void handle(PacketStatusResponse pkg) {
    }

    public void handle(PacketStatusPong pkg) {
    }

    public void handle(PacketEncryptionKeyRequest pkg) {
    }

    public void handle(PacketLoginSuccess pkg) {
    }

    public void handle(PacketJoinGame pkg) {
    }

    public void handle(PacketLoginDisconnect pkg) {
    }

    public void handle(PacketPlayerInfo pkg) {
    }

    public void handle(PacketTimeUpdate pkg) {
    }

    public void handle(PacketKeepAlive pkg) {
    }

    public void handle(PacketChunkBulk pkg) {
    }

    public void handle(PacketUpdateHealth pkg) {
    }

    public void handle(PacketPluginMessageReceiving pkg) {
    }

    public void handle(PacketSpawnLocation pkg) {
    }

    public void handle(PacketChatMessage pkg) {
    }

    public void handle(PacketDisconnect pkg) {
    }

    public void handle(PacketHeldItemChangeReceiving pkg) {
    }

    public void handle(PacketSetExperience pkg) {
    }

    public void handle(PacketChangeGameState pkg) {
    }

    public void handle(PacketSpawnMob pkg) {
    }

    public void handle(PacketEntityPositionAndRotation pkg) {
    }

    public void handle(PacketEntityPosition pkg) {
    }

    public void handle(PacketEntityRotation pkg) {
    }

    public void handle(PacketDestroyEntity pkg) {
    }

    public void handle(PacketEntityVelocity pkg) {
    }

    public void handle(PacketSpawnPlayer pkg) {
    }

    public void handle(PacketEntityTeleport pkg) {
    }

    public void handle(PacketEntityHeadRotation pkg) {
    }

    public void handle(PacketWindowItems pkg) {
    }

    public void handle(PacketEntityMetadata pkg) {
    }

    public void handle(PacketEntityEquipment pkg) {
    }

    public void handle(PacketBlockChange pkg) {
    }

    public void handle(PacketMultiBlockChange pkg) {
    }

    public void handle(PacketRespawn pkg) {
    }

    public void handle(PacketOpenSignEditor pkg) {
    }

    public void handle(PacketSpawnObject pkg) {
    }

    public void handle(PacketSpawnExperienceOrb pkg) {
    }

    public void handle(PacketSpawnWeatherEntity pkg) {
    }

    public void handle(PacketChunkData pkg) {
    }
}

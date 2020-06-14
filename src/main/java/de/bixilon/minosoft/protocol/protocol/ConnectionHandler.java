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

import de.bixilon.minosoft.Minosoft;
import de.bixilon.minosoft.config.GameConfiguration;
import de.bixilon.minosoft.game.datatypes.GameMode;
import de.bixilon.minosoft.game.datatypes.entities.meta.HumanMetaData;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.protocol.packets.clientbound.login.PacketEncryptionKeyRequest;
import de.bixilon.minosoft.protocol.packets.clientbound.login.PacketLoginDisconnect;
import de.bixilon.minosoft.protocol.packets.clientbound.play.*;
import de.bixilon.minosoft.protocol.packets.clientbound.status.PacketStatusPong;
import de.bixilon.minosoft.protocol.packets.clientbound.status.PacketStatusResponse;
import de.bixilon.minosoft.protocol.packets.serverbound.login.PacketEncryptionResponse;
import de.bixilon.minosoft.protocol.packets.serverbound.play.PacketKeepAliveResponse;
import de.bixilon.minosoft.protocol.packets.serverbound.play.PacketPluginMessageSending;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.PublicKey;

public class ConnectionHandler extends PacketHandler {
    final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handle(PacketStatusResponse pkg) {
        Log.info(String.format("Status response received: %s/%s online. MotD: '%s'", pkg.getResponse().getPlayerOnline(), pkg.getResponse().getMaxPlayers(), pkg.getResponse().getMotd()));
    }

    @Override
    public void handle(PacketStatusPong pkg) {
        Log.debug("Pong: " + pkg.getID());
        if (connection.isOnlyPing()) {
            // pong arrived, closing connection
            connection.disconnect();
        }
    }

    @Override
    public void handle(PacketEncryptionKeyRequest pkg) {
        SecretKey secretKey = CryptManager.createNewSharedKey();
        PublicKey publicKey = CryptManager.decodePublicKey(pkg.getPublicKey());
        String serverHash = new BigInteger(CryptManager.getServerHash(pkg.getServerId(), publicKey, secretKey)).toString(16);
        connection.getPlayer().getAccount().join(serverHash);
        connection.sendPacket(new PacketEncryptionResponse(secretKey, pkg.getVerifyToken(), publicKey));

    }

    @Override
    public void handle(PacketJoinGame pkg) {
        connection.getPlayer().setGameMode(pkg.getGameMode());
        connection.getPlayer().setEntityId(pkg.getEntityId());
        connection.getPlayer().getWorld().setHardcore(pkg.isHardcore());
        connection.getPlayer().getWorld().setDimension(pkg.getDimension());
    }

    @Override
    public void handle(PacketLoginDisconnect pkg) {
        Log.info(String.format("Disconnecting from server(%s)", pkg.getReason().getColoredMessage()));
        connection.setConnectionState(ConnectionState.DISCONNECTING);
    }

    @Override
    public void handle(PacketKeepAlive pkg) {
        connection.sendPacket(new PacketKeepAliveResponse(pkg.getId()));
    }

    @Override
    public void handle(PacketChunkBulk pkg) {
        connection.getPlayer().getWorld().setChunks(pkg.getChunkMap());
    }

    @Override
    public void handle(PacketUpdateHealth pkg) {
        connection.getPlayer().setFood(pkg.getFood());
        connection.getPlayer().setHealth(pkg.getHealth());
        connection.getPlayer().setSaturation(pkg.getSaturation());
    }

    @Override
    public void handle(PacketPluginMessageReceiving pkg) {
        if (pkg.getChannel().equals("MC|Brand")) {
            // server brand received
            Log.info(String.format("Server is running %s on version %s", new String(pkg.getData()), connection.getVersion().getName()));

            // send back own brand
            connection.sendPacket(new PacketPluginMessageSending("MC|Brand", (Minosoft.getConfig().getBoolean(GameConfiguration.NETWORK_FAKE_CLIENT_BRAND) ? "vanilla" : "Minosoft")));
        }
    }

    @Override
    public void handle(PacketSpawnLocation pkg) {
        connection.getPlayer().setSpawnLocation(pkg.getSpawnLocation());
    }

    @Override
    public void handle(PacketDisconnect pkg) {
        // got kicked
        connection.setConnectionState(ConnectionState.DISCONNECTING);
    }

    @Override
    public void handle(PacketHeldItemChangeReceiving pkg) {
        connection.getPlayer().setSelectedSlot(pkg.getSlot());
    }

    @Override
    public void handle(PacketSetExperience pkg) {
        connection.getPlayer().setLevel(pkg.getLevel());
        connection.getPlayer().setTotalExperience(pkg.getTotal());
    }

    @Override
    public void handle(PacketChangeGameState pkg) {
        switch (pkg.getReason()) {
            case START_RAIN:
                connection.getPlayer().getWorld().setRaining(true);
                break;
            case END_RAIN:
                connection.getPlayer().getWorld().setRaining(false);
                break;
            case CHANGE_GAMEMODE:
                connection.getPlayer().setGameMode(GameMode.byId(pkg.getValue().intValue()));
                break;
            //ToDo: handle all updates
        }
    }

    @Override
    public void handle(PacketSpawnMob pkg) {
        connection.getPlayer().getWorld().addEntity(pkg.getMob());
    }

    @Override
    public void handle(PacketEntityPositionAndRotation pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setLocation(pkg.getRelativeLocation());
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setYaw(pkg.getYaw());
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setPitch(pkg.getPitch());
    }

    @Override
    public void handle(PacketEntityPosition pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setLocation(pkg.getRelativeLocation());
    }

    @Override
    public void handle(PacketEntityRotation pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setYaw(pkg.getYaw());
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setPitch(pkg.getPitch());
    }

    @Override
    public void handle(PacketDestroyEntity pkg) {
        for (int entityId : pkg.getEntityIds()) {
            connection.getPlayer().getWorld().removeEntity(entityId);
        }
    }

    @Override
    public void handle(PacketEntityVelocity pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setVelocity(pkg.getVelocity());

    }

    @Override
    public void handle(PacketSpawnPlayer pkg) {
        connection.getPlayer().getWorld().addEntity(pkg.getPlayer());
    }

    @Override
    public void handle(PacketEntityTeleport pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setLocation(pkg.getRelativeLocation());
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setYaw(pkg.getYaw());
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setPitch(pkg.getPitch());
    }

    @Override
    public void handle(PacketEntityHeadRotation pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setHeadYaw(pkg.getHeadYaw());
    }

    @Override
    public void handle(PacketWindowItems pkg) {
        switch (pkg.getWindowId()) {
            case 0: //Inventory
                connection.getPlayer().setInventory(pkg.getData());
                break;
        }
    }

    @Override
    public void handle(PacketEntityMetadata pkg) {
        if (pkg.getEntityId() == connection.getPlayer().getEntityId()) {
            // our own meta data...set it
            connection.getPlayer().setMetaData((HumanMetaData) pkg.getEntityData(HumanMetaData.class));
        } else {
            connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setMetaData(pkg.getEntityData(connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).getMetaDataClass()));
        }
    }

    @Override
    public void handle(PacketEntityEquipment pkg) {
        connection.getPlayer().getWorld().getEntity(pkg.getEntityId()).setEquipment(pkg.getSlot(), pkg.getData());
    }

    @Override
    public void handle(PacketBlockChange pkg) {
        connection.getPlayer().getWorld().setBlock(pkg.getPosition(), pkg.getBlock());
    }

    @Override
    public void handle(PacketMultiBlockChange pkg) {
        connection.getPlayer().getWorld().getChunk(pkg.getLocation()).setBlocks(pkg.getBlocks());
    }

    @Override
    public void handle(PacketRespawn pkg) {
        connection.getPlayer().getWorld().setDimension(pkg.getDimension());
        connection.getPlayer().setSpawnConfirmed(false);
        connection.getPlayer().setGameMode(pkg.getGameMode());
    }

    @Override
    public void handle(PacketOpenSignEditor pkg) {
        //ToDo
    }

    @Override
    public void handle(PacketSpawnObject pkg) {
        connection.getPlayer().getWorld().addEntity(pkg.getObject());
    }

    @Override
    public void handle(PacketSpawnExperienceOrb pkg) {
        connection.getPlayer().getWorld().addEntity(pkg.getOrb());
    }

    @Override
    public void handle(PacketSpawnWeatherEntity pkg) {
        //ToDo
    }

    @Override
    public void handle(PacketChunkData pkg) {
        connection.getPlayer().getWorld().setChunk(pkg.getLocation(), pkg.getChunk());
    }
}

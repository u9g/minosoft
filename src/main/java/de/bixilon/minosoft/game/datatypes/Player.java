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

package de.bixilon.minosoft.game.datatypes;

import de.bixilon.minosoft.game.datatypes.inventory.Inventory;
import de.bixilon.minosoft.game.datatypes.inventory.InventoryProperties;
import de.bixilon.minosoft.game.datatypes.inventory.InventorySlots;
import de.bixilon.minosoft.game.datatypes.inventory.Slot;
import de.bixilon.minosoft.game.datatypes.objectLoader.entities.mob.OtherPlayer;
import de.bixilon.minosoft.game.datatypes.player.PlayerInfo;
import de.bixilon.minosoft.game.datatypes.scoreboard.ScoreboardManager;
import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.game.datatypes.world.World;
import de.bixilon.minosoft.mojang.api.MojangAccount;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

import java.util.HashMap;
import java.util.UUID;

import static de.bixilon.minosoft.protocol.protocol.ProtocolDefinition.PLAYER_INVENTORY_ID;

public class Player {
    final MojangAccount acc;
    final ScoreboardManager scoreboardManager = new ScoreboardManager();
    public HashMap<UUID, PlayerInfo> playerInfos = new HashMap<>();
    float health;
    int food;
    float saturation;
    BlockPosition spawnLocation;
    GameMode gameMode;
    World world = new World("world");
    byte selectedSlot;
    int level;
    int totalExperience;
    OtherPlayer player;
    HashMap<Integer, Inventory> inventories = new HashMap<>();
    boolean spawnConfirmed = false;

    TextComponent tabHeader;
    TextComponent tabFooter;

    public Player(MojangAccount acc) {
        this.acc = acc;
        // create our own inventory without any properties
        inventories.put(PLAYER_INVENTORY_ID, new Inventory(null));
    }

    public String getPlayerName() {
        return acc.getPlayerName();
    }

    public UUID getPlayerUUID() {
        return acc.getUUID();
    }

    public MojangAccount getAccount() {
        return this.acc;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public BlockPosition getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(BlockPosition spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public World getWorld() {
        return world;
    }

    public byte getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(byte selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(int totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Inventory getPlayerInventory() {
        return getInventory(PLAYER_INVENTORY_ID);
    }

    public void setPlayerInventory(Slot[] data) {
        setInventory(PLAYER_INVENTORY_ID, data);
    }

    public Inventory getInventory(int id) {
        return inventories.get(id);
    }

    public void setInventory(int windowId, Slot[] data) {
        for (int i = 0; i < data.length; i++) {
            setSlot(windowId, i, data[i]);
        }
    }

    public Slot getSlot(int windowId, InventorySlots.InventoryInterface slot, ProtocolVersion version) {
        return getSlot(windowId, slot.getId(version));
    }

    public Slot getSlot(int windowId, int slot) {
        return inventories.get(windowId).getSlot(slot);
    }

    public void setSlot(int windowId, InventorySlots.InventoryInterface slot, ProtocolVersion version, Slot data) {
        setSlot(windowId, slot.getId(version), data);
    }

    public void setSlot(int windowId, int slot, Slot data) {
        inventories.get(windowId).setSlot(slot, data);
    }

    public void createInventory(InventoryProperties properties) {
        inventories.put(properties.getWindowId(), new Inventory(properties));
    }

    public void deleteInventory(int windowId) {
        inventories.remove(windowId);
    }

    public boolean isSpawnConfirmed() {
        return spawnConfirmed;
    }

    public void setSpawnConfirmed(boolean spawnConfirmed) {
        this.spawnConfirmed = spawnConfirmed;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public HashMap<UUID, PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

    public PlayerInfo getPlayerInfo(String name) {
        // only legacy
        for (PlayerInfo info : playerInfos.values()) {
            if (info.getName().equals(name)) {
                return info;
            }
        }
        return null;
    }

    public TextComponent getTabHeader() {
        return tabHeader;
    }

    public void setTabHeader(TextComponent tabHeader) {
        this.tabHeader = tabHeader;
    }

    public TextComponent getTabFooter() {
        return tabFooter;
    }

    public void setTabFooter(TextComponent tabFooter) {
        this.tabFooter = tabFooter;
    }

    public OtherPlayer getPlayer() {
        return player;
    }

    public void setPlayer(OtherPlayer player) {
        this.player = player;
    }
}

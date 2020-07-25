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

package de.bixilon.minosoft.protocol.protocol.versions;


import de.bixilon.minosoft.protocol.protocol.Packets;
import de.bixilon.minosoft.protocol.protocol.Protocol;

public class Protocol_1_13_2 extends Protocol {

    public Protocol_1_13_2() {
        super();
        registerPacket(Packets.Clientbound.LOGIN_SET_COMPRESSION, 0x03);
        registerPacket(Packets.Clientbound.LOGIN_PLUGIN_REQUEST, 0x04);

        registerPacket(Packets.Clientbound.PLAY_SPAWN_OBJECT, 0x00);
        registerPacket(Packets.Clientbound.PLAY_SPAWN_EXPERIENCE_ORB, 0x01);
        registerPacket(Packets.Clientbound.PLAY_SPAWN_WEATHER_ENTITY, 0x02);
        registerPacket(Packets.Clientbound.PLAY_SPAWN_MOB, 0x03);
        registerPacket(Packets.Clientbound.PLAY_SPAWN_PAINTING, 0x04);
        registerPacket(Packets.Clientbound.PLAY_SPAWN_PLAYER, 0x05);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_ANIMATION, 0x06);
        registerPacket(Packets.Clientbound.PLAY_STATISTICS, 0x07);
        registerPacket(Packets.Clientbound.PLAY_BLOCK_BREAK_ANIMATION, 0x08);
        registerPacket(Packets.Clientbound.PLAY_BLOCK_ENTITY_DATA, 0x09);
        registerPacket(Packets.Clientbound.PLAY_BLOCK_ACTION, 0x0A);
        registerPacket(Packets.Clientbound.PLAY_BLOCK_CHANGE, 0x0B);
        registerPacket(Packets.Clientbound.PLAY_BOSS_BAR, 0x0C);
        registerPacket(Packets.Clientbound.PLAY_SERVER_DIFFICULTY, 0x0D);
        registerPacket(Packets.Clientbound.PLAY_CHAT_MESSAGE, 0x0E);
        registerPacket(Packets.Clientbound.PLAY_MULTIBLOCK_CHANGE, 0x0F);
        registerPacket(Packets.Clientbound.PLAY_TAB_COMPLETE, 0x10);
        registerPacket(Packets.Clientbound.PLAY_DECLARE_COMMANDS, 0x11);
        registerPacket(Packets.Clientbound.PLAY_WINDOW_CONFIRMATION, 0x12);
        registerPacket(Packets.Clientbound.PLAY_CLOSE_WINDOW, 0x13);
        registerPacket(Packets.Clientbound.PLAY_OPEN_WINDOW, 0x14);
        registerPacket(Packets.Clientbound.PLAY_WINDOW_ITEMS, 0x15);
        registerPacket(Packets.Clientbound.PLAY_WINDOW_PROPERTY, 0x16);
        registerPacket(Packets.Clientbound.PLAY_SET_SLOT, 0x17);
        registerPacket(Packets.Clientbound.PLAY_SET_COOLDOWN, 0x18);
        registerPacket(Packets.Clientbound.PLAY_PLUGIN_MESSAGE, 0x19);
        registerPacket(Packets.Clientbound.PLAY_NAMED_SOUND_EFFECT, 0x1A);
        registerPacket(Packets.Clientbound.PLAY_DISCONNECT, 0x1B);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_STATUS, 0x1C);
        registerPacket(Packets.Clientbound.PLAY_NBT_QUERY_RESPONSE, 0x1D);
        registerPacket(Packets.Clientbound.PLAY_EXPLOSION, 0x1E);
        registerPacket(Packets.Clientbound.PLAY_UNLOAD_CHUNK, 0x1F);
        registerPacket(Packets.Clientbound.PLAY_CHANGE_GAME_STATE, 0x20);
        registerPacket(Packets.Clientbound.PLAY_KEEP_ALIVE, 0x21);
        registerPacket(Packets.Clientbound.PLAY_CHUNK_DATA, 0x22);
        registerPacket(Packets.Clientbound.PLAY_EFFECT, 0x23);
        registerPacket(Packets.Clientbound.PLAY_PARTICLE, 0x24);

        registerPacket(Packets.Clientbound.PLAY_JOIN_GAME, 0x25);
        registerPacket(Packets.Clientbound.PLAY_MAP_DATA, 0x26);
        //registerPacket(Packets.Clientbound.PLAY_ENTITY, 0x27);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_MOVEMENT, 0x28);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_MOVEMENT_AND_ROTATION, 0x29);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_ROTATION, 0x2A);
        registerPacket(Packets.Clientbound.PLAY_VEHICLE_MOVEMENT, 0x2B);
        registerPacket(Packets.Clientbound.PLAY_OPEN_SIGN_EDITOR, 0x2C);
        registerPacket(Packets.Clientbound.PLAY_CRAFT_RECIPE_RESPONSE, 0x2D);
        registerPacket(Packets.Clientbound.PLAY_PLAYER_ABILITIES, 0x2E);
        registerPacket(Packets.Clientbound.PLAY_COMBAT_EVENT, 0x2F);
        registerPacket(Packets.Clientbound.PLAY_PLAYER_INFO, 0x30);
        registerPacket(Packets.Clientbound.PLAY_FACE_PLAYER, 0x31);
        registerPacket(Packets.Clientbound.PLAY_PLAYER_POSITION_AND_ROTATION, 0x32);
        registerPacket(Packets.Clientbound.PLAY_USE_BED, 0x33);
        registerPacket(Packets.Clientbound.PLAY_UNLOCK_RECIPES, 0x34);
        registerPacket(Packets.Clientbound.PLAY_DESTROY_ENTITIES, 0x35);
        registerPacket(Packets.Clientbound.PLAY_REMOVE_ENTITY_EFFECT, 0x36);
        registerPacket(Packets.Clientbound.PLAY_RESOURCE_PACK_SEND, 0x37);
        registerPacket(Packets.Clientbound.PLAY_RESPAWN, 0x38);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_HEAD_ROTATION, 0x39);
        registerPacket(Packets.Clientbound.PLAY_SELECT_ADVANCEMENT_TAB, 0x3A);
        registerPacket(Packets.Clientbound.PLAY_WORLD_BORDER, 0x3B);
        registerPacket(Packets.Clientbound.PLAY_CAMERA, 0x3C);
        registerPacket(Packets.Clientbound.PLAY_HELD_ITEM_CHANGE, 0x3D);
        registerPacket(Packets.Clientbound.PLAY_DISPLAY_SCOREBOARD, 0x3E);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_METADATA, 0x3F);
        registerPacket(Packets.Clientbound.PLAY_ATTACH_ENTITY, 0x40);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_VELOCITY, 0x41);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_EQUIPMENT, 0x42);
        registerPacket(Packets.Clientbound.PLAY_SET_EXPERIENCE, 0x43);
        registerPacket(Packets.Clientbound.PLAY_UPDATE_HEALTH, 0x44);
        registerPacket(Packets.Clientbound.PLAY_SCOREBOARD_OBJECTIVE, 0x45);
        registerPacket(Packets.Clientbound.PLAY_SET_PASSENGERS, 0x46);
        registerPacket(Packets.Clientbound.PLAY_TEAMS, 0x47);
        registerPacket(Packets.Clientbound.PLAY_UPDATE_SCORE, 0x48);
        registerPacket(Packets.Clientbound.PLAY_SPAWN_POSITION, 0x49);
        registerPacket(Packets.Clientbound.PLAY_TIME_UPDATE, 0x4A);
        registerPacket(Packets.Clientbound.PLAY_TITLE, 0x4B);
        registerPacket(Packets.Clientbound.PLAY_STOP_SOUND, 0x4C);
        registerPacket(Packets.Clientbound.PLAY_SOUND_EFFECT, 0x4D);
        registerPacket(Packets.Clientbound.PLAY_LIST_HEADER_AND_FOOTER, 0x4E);
        registerPacket(Packets.Clientbound.PLAY_COLLECT_ITEM, 0x4F);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_TELEPORT, 0x50);
        registerPacket(Packets.Clientbound.PLAY_ADVANCEMENTS, 0x51);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_PROPERTIES, 0x52);
        registerPacket(Packets.Clientbound.PLAY_ENTITY_EFFECT, 0x53);
        registerPacket(Packets.Clientbound.PLAY_DECLARE_RECIPES, 0x54);
        registerPacket(Packets.Clientbound.PLAY_TAGS, 0x55);


        registerPacket(Packets.Serverbound.LOGIN_PLUGIN_RESPONSE, 0x02);

        registerPacket(Packets.Serverbound.PLAY_TELEPORT_CONFIRM, 0x0);
        registerPacket(Packets.Serverbound.PLAY_QUERY_BLOCK_NBT, 0x01);
        registerPacket(Packets.Serverbound.PLAY_CHAT_MESSAGE, 0x02);
        registerPacket(Packets.Serverbound.PLAY_CLIENT_STATUS, 0x03);
        registerPacket(Packets.Serverbound.PLAY_CLIENT_SETTINGS, 0x04);
        registerPacket(Packets.Serverbound.PLAY_TAB_COMPLETE, 0x05);
        registerPacket(Packets.Serverbound.PLAY_WINDOW_CONFIRMATION, 0x06);
        registerPacket(Packets.Serverbound.PLAY_CLICK_WINDOW_BUTTON, 0x07);
        registerPacket(Packets.Serverbound.PLAY_CLICK_WINDOW, 0x08);
        registerPacket(Packets.Serverbound.PLAY_CLOSE_WINDOW, 0x09);
        registerPacket(Packets.Serverbound.PLAY_PLUGIN_MESSAGE, 0x0A);
        registerPacket(Packets.Serverbound.PLAY_EDIT_BOOK, 0x0B);
        registerPacket(Packets.Serverbound.PLAY_ENTITY_NBT_REQUEST, 0x0C);
        registerPacket(Packets.Serverbound.PLAY_INTERACT_ENTITY, 0x0D);
        registerPacket(Packets.Serverbound.PLAY_KEEP_ALIVE, 0x0E);
        registerPacket(Packets.Serverbound.PLAY_PLAYER_POSITION, 0x10);
        registerPacket(Packets.Serverbound.PLAY_PLAYER_POSITION_AND_ROTATION, 0x11);
        registerPacket(Packets.Serverbound.PLAY_PLAYER_ROTATION, 0x12);
        registerPacket(Packets.Serverbound.PLAY_VEHICLE_MOVE, 0x13);
        registerPacket(Packets.Serverbound.PLAY_STEER_BOAT, 0x14);
        registerPacket(Packets.Serverbound.PLAY_PICK_ITEM, 0x15);
        registerPacket(Packets.Serverbound.PLAY_CRAFT_RECIPE_REQUEST, 0x16);
        registerPacket(Packets.Serverbound.PLAY_PLAYER_ABILITIES, 0x17);
        registerPacket(Packets.Serverbound.PLAY_PLAYER_DIGGING, 0x18);
        registerPacket(Packets.Serverbound.PLAY_ENTITY_ACTION, 0x19);
        registerPacket(Packets.Serverbound.PLAY_STEER_VEHICLE, 0x1A);
        registerPacket(Packets.Serverbound.PLAY_RECIPE_BOOK_DATA, 0x1B);
        registerPacket(Packets.Serverbound.PLAY_NAME_ITEM, 0x1C);
        registerPacket(Packets.Serverbound.PLAY_RESOURCE_PACK_STATUS, 0x1D);
        registerPacket(Packets.Serverbound.PLAY_ADVANCEMENT_TAB, 0x1E);
        registerPacket(Packets.Serverbound.PLAY_SELECT_TRADE, 0x1F);
        registerPacket(Packets.Serverbound.PLAY_SET_BEACON_EFFECT, 0x20);
        registerPacket(Packets.Serverbound.PLAY_HELD_ITEM_CHANGE, 0x21);
        registerPacket(Packets.Serverbound.PLAY_UPDATE_COMMAND_BLOCK, 0x22);
        registerPacket(Packets.Serverbound.PLAY_UPDATE_COMMAND_BLOCK_MINECART, 0x23);
        registerPacket(Packets.Serverbound.PLAY_CREATIVE_INVENTORY_ACTION, 0x24);
        registerPacket(Packets.Serverbound.PLAY_UPDATE_STRUCTURE_BLOCK, 0x25);
        registerPacket(Packets.Serverbound.PLAY_UPDATE_SIGN, 0x26);
        registerPacket(Packets.Serverbound.PLAY_ANIMATION, 0x27);
        registerPacket(Packets.Serverbound.PLAY_SPECTATE, 0x28);
        registerPacket(Packets.Serverbound.PLAY_PLAYER_BLOCK_PLACEMENT, 0x29);
        registerPacket(Packets.Serverbound.PLAY_USE_ITEM, 0x2A);
    }

    public int getProtocolVersionNumber() {
        return 404;
    }

    @Override
    public String getVersionString() {
        return "1.13.2";
    }

    @Override
    public String getReleaseName() {
        return "Aquatic";
    }
}

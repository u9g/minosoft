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
import de.bixilon.minosoft.protocol.packets.ServerboundPacket;
import de.bixilon.minosoft.protocol.protocol.OutPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.Packets;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;

public class PacketCraftingBookData implements ServerboundPacket {
    final BookDataStatus action;

    final int recipeId;

    final boolean craftingBookOpen;
    final boolean craftingFilter;

    public PacketCraftingBookData(BookDataStatus action, int recipeId) {
        this.action = action;
        this.recipeId = recipeId;
        craftingBookOpen = false;
        craftingFilter = false;
    }

    public PacketCraftingBookData(BookDataStatus action, boolean craftingBookOpen, boolean craftingFilter) {
        this.action = action;
        this.recipeId = 0;
        this.craftingBookOpen = craftingBookOpen;
        this.craftingFilter = craftingFilter;
    }


    @Override
    public OutPacketBuffer write(ProtocolVersion version) {
        OutPacketBuffer buffer = new OutPacketBuffer(version, version.getPacketCommand(Packets.Serverbound.PLAY_RECIPE_BOOK_DATA));
        switch (version) {
            case VERSION_1_12_2:
                buffer.writeVarInt(action.getId());
                switch (action) {
                    case DISPLAY_RECIPE:
                        buffer.writeVarInt(recipeId);
                        break;
                    case CRAFTING_BOOK_STATUS:
                        buffer.writeBoolean(craftingBookOpen);
                        buffer.writeBoolean(craftingFilter);
                        break;
                }
        }
        return buffer;
    }

    @Override
    public void log() {
        switch (action) {
            case DISPLAY_RECIPE:
                Log.protocol(String.format("Sending crafting book status (action=%s, recipeId=%d)", action.name(), recipeId));
                break;
            case CRAFTING_BOOK_STATUS:
                Log.protocol(String.format("Sending crafting book status (action=%s, craftingBookOpen=%s, craftingFilter=%s)", action.name(), craftingBookOpen, craftingFilter));
                break;
        }
    }

    public enum BookDataStatus {
        DISPLAY_RECIPE(0),
        CRAFTING_BOOK_STATUS(1);

        final int id;


        BookDataStatus(int id) {
            this.id = id;
        }

        public static BookDataStatus byId(int id) {
            for (BookDataStatus action : values()) {
                if (action.getId() == id) {
                    return action;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}

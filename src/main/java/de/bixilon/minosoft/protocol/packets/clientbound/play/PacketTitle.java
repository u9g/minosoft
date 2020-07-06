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

import de.bixilon.minosoft.game.datatypes.TextComponent;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.packets.ClientboundPacket;
import de.bixilon.minosoft.protocol.protocol.InPacketBuffer;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;

public class PacketTitle implements ClientboundPacket {
    TitleAction action;

    //fields depend on action
    TextComponent text;
    TextComponent subText;
    int fadeInTime;
    int stayTime;
    int fadeOutTime;


    @Override
    public boolean read(InPacketBuffer buffer) {
        switch (buffer.getVersion()) {
            case VERSION_1_8:
            case VERSION_1_9_4:
            case VERSION_1_10:
                action = TitleAction.byId(buffer.readVarInt());
                switch (action) {
                    case SET_TITLE:
                        text = buffer.readTextComponent();
                        break;
                    case SET_SUBTITLE:
                        subText = buffer.readTextComponent();
                        break;
                    case SET_TIMES_AND_DISPLAY:
                        fadeInTime = buffer.readInt();
                        stayTime = buffer.readInt();
                        fadeOutTime = buffer.readInt();
                        break;
                }
                return true;
        }

        return false;
    }

    @Override
    public void log() {
        switch (action) {
            case SET_TITLE:
                Log.protocol(String.format("Received title (action=%s, text=%s)", action.name(), text.getColoredMessage()));
                break;
            case SET_SUBTITLE:
                Log.protocol(String.format("Received title (action=%s, subText=%s)", action.name(), subText.getColoredMessage()));
                break;
            case SET_TIMES_AND_DISPLAY:
                Log.protocol(String.format("Received title (action=%s, fadeInTime=%d, stayTime=%d, fadeOutTime=%d)", action.name(), fadeInTime, stayTime, fadeOutTime));
                break;
            case HIDE:
            case RESET:
                Log.protocol(String.format("Received title (action=%s)", action.name()));
                break;
        }
    }

    @Override
    public void handle(PacketHandler h) {
        h.handle(this);
    }

    public int getFadeInTime() {
        return fadeInTime;
    }

    public int getFadeOutTime() {
        return fadeOutTime;
    }

    public int getStayTime() {
        return stayTime;
    }

    public TextComponent getSubText() {
        return subText;
    }

    public TextComponent getText() {
        return text;
    }

    public TitleAction getAction() {
        return action;
    }

    public enum TitleAction {
        SET_TITLE(0),
        SET_SUBTITLE(1),
        SET_TIMES_AND_DISPLAY(2),
        HIDE(3),
        RESET(4);

        final int id;

        TitleAction(int id) {
            this.id = id;
        }

        public static TitleAction byId(int id) {
            for (TitleAction a : values()) {
                if (a.getId() == id) {
                    return a;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}

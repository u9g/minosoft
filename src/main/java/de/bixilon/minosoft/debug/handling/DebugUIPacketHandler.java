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

package de.bixilon.minosoft.debug.handling;

import de.bixilon.minosoft.debug.DebugWindow;
import de.bixilon.minosoft.protocol.packets.clientbound.play.PacketChatMessage;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;

public class DebugUIPacketHandler extends PacketHandler {
    DebugWindow window;

    public DebugUIPacketHandler(DebugWindow window) {
        this.window = window;
    }

    @Override
    public void handle(PacketChatMessage pkg) {
        window.getUIHandler().printText(pkg.getTextComponent());
    }

}

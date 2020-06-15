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

package de.bixilon.minosoft.debug;

import de.bixilon.minosoft.debug.gui.DebugMainWindow;
import de.bixilon.minosoft.debug.handling.DebugUIHandler;
import de.bixilon.minosoft.debug.handling.DebugUIPacketHandler;
import de.bixilon.minosoft.protocol.network.Connection;

public class DebugWindow {
    final Connection connection;
    Thread uiThread;
    DebugUIHandler handler;

    public DebugWindow(Connection connection) {
        this.connection = connection;
        handler = new DebugUIHandler();
        connection.addHandler(new DebugUIPacketHandler(this));
    }

    public Connection getConnection() {
        return connection;
    }

    public void run() {
        // start gui
        uiThread = new Thread(() -> {
            DebugMainWindow.setHandler(handler);
            DebugMainWindow.setConnection(connection);
            DebugMainWindow.initAndShow();
        });
        uiThread.start();
    }

    public DebugUIHandler getUIHandler() {
        return handler;
    }

    public void disconnect() {
        DebugMainWindow.disconnect();
    }
}

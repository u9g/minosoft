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
import de.bixilon.minosoft.protocol.packets.clientbound.play.*;
import de.bixilon.minosoft.protocol.protocol.PacketHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class DebugUIPacketHandler extends PacketHandler {
    DebugWindow window;

    public DebugUIPacketHandler(DebugWindow window) {
        this.window = window;
    }

    @Override
    public void handle(PacketChatMessage pkg) {
        window.getUIHandler().printText(pkg.getTextComponent());
    }

    @Override
    public void handle(PacketSpawnMob pkg) {
        window.getUIHandler().addEntity(pkg.getMob());
    }

    @Override
    public void handle(PacketSpawnObject pkg) {
        window.getUIHandler().addEntity(pkg.getObject());
    }

    @Override
    public void handle(PacketDestroyEntity pkg) {
        window.getUIHandler().removeEntities(pkg.getEntityIds());
    }

    @Override
    public void handle(PacketDisconnect pkg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Disconnected!");
            alert.setHeaderText("You have been disconnected from the server");
            alert.setContentText(pkg.getReason().getRawMessage());
            alert.show();
        });
    }

}

/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.modding.event.events;

import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.protocol.packets.clientbound.play.PacketChangeGameState;

/**
 * Fired when the player should spectate an entity
 */
public class ChangeGameStateEvent extends CancelableEvent {
    private final PacketChangeGameState.Reason reason;
    private final float value;

    public ChangeGameStateEvent(Connection connection, PacketChangeGameState.Reason reason, float value) {
        super(connection);
        this.reason = reason;
        this.value = value;
    }

    public ChangeGameStateEvent(Connection connection, PacketChangeGameState pkg) {
        super(connection);
        this.reason = pkg.getReason();
        this.value = pkg.getValue();
    }

    public PacketChangeGameState.Reason getReason() {
        return reason;
    }

    public float getValue() {
        return value;
    }
}

/*
 * Codename Minosoft
 * Copyright (C) 2020 Lukas Eisenhauer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.movement.PlayerController;

public class RenderProperties {
    private final PlayerController controller;
    private final WorldRenderer renderer;

    public RenderProperties(Connection connection) {
        this.controller = new PlayerController(connection);
        this.renderer = new WorldRenderer(connection);
    }

    public PlayerController getController() {
        return controller;
    }

    public WorldRenderer getRenderer() {
        return renderer;
    }
}

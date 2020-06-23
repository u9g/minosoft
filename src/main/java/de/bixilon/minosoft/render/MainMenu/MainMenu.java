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

package de.bixilon.minosoft.render.MainMenu;

import de.bixilon.minosoft.render.MainMenu.button.Button;
import de.bixilon.minosoft.render.MainMenu.button.ButtonAction;
import de.bixilon.minosoft.render.MainMenu.button.PlayButtonAction;
import de.bixilon.minosoft.render.MainWindow;

public class MainMenu {
    Button play;
    long window;

    public MainMenu(int width, int height) {
        this.window = MainWindow.getOpenGLWindow().getWindow();
        ButtonAction action = new PlayButtonAction();
        play = new Button(width / 3, height / 3, width / 3, height / 3, "PLAY",
                0.9f, 0f, 0f, MainWindow.getOpenGLWindow().getWindow(), action);
    }

    public void draw(float mouseX, float mouseY) {
        play.loop(mouseX, mouseY);
    }
}
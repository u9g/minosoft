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

import de.bixilon.minosoft.debug.gui.DebugMainWindow;
import de.bixilon.minosoft.game.datatypes.TextComponent;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

public class DebugUIHandler {
    List<TextComponent> toPrint = new ArrayList<>();

    public void printText(TextComponent component) {
        if (DebugMainWindow.isInitialized()) {
            TextFlow chat = ((TextFlow) DebugMainWindow.getStage().getScene().lookup("#chat"));
            Platform.runLater(
                    () -> chat.getChildren().add(new Text(component.getRawMessage() + "\n"))
            );
        } else {
            toPrint.add(component);
        }
    }

    public void printTextLeft() {
        TextFlow chat = ((TextFlow) DebugMainWindow.getStage().getScene().lookup("#chat"));
        if (toPrint != null) {
            // append here
            for (TextComponent toDoComponent : toPrint) {
                chat.getChildren().add(new Text(toDoComponent.getRawMessage() + "\n"));
            }
            toPrint = null;
        }

    }

    public void initializedCallback() {
        printTextLeft();
    }
}

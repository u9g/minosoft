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

package de.bixilon.minosoft.debug.gui;

import de.bixilon.minosoft.debug.handling.DebugUIHandler;
import de.bixilon.minosoft.protocol.network.Connection;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DebugMainWindow extends Application {
    static DebugUIHandler handler;
    static Stage stage;
    static boolean initialized = false;
    private static Connection connection;

    public static void initAndShow() {
        launch();
    }

    public static void setHandler(DebugUIHandler handler) {
        DebugMainWindow.handler = handler;
    }

    public static Stage getStage() {
        return stage;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setConnection(Connection connection) {
        DebugMainWindow.connection = connection;
    }

    @Override
    public void start(Stage stage) throws IOException {

        VBox root = FXMLLoader.load(getClass().getResource("/layout/debug/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Minosoft DebugUI");
        stage.show();

        // autoscroll for chat
        ((TextArea) scene.lookup("#chat")).textProperty().addListener((ChangeListener<Object>) (observableValue, o, t1) -> ((TextArea) scene.lookup("#chat")).setScrollTop(Double.MAX_VALUE));
        // listen for enter in text field
        TextField chatToSend = ((TextField) scene.lookup("#chatToSend"));
        chatToSend.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // send chat message
                connection.sendChatMessage(chatToSend.getText());
                chatToSend.setText("");
            }
        });

        DebugMainWindow.stage = stage;
        initialized = true;
        handler.initializedCallback();

        // bring to front
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
    }
}

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
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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

    private static void showCloseConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Disconnect?");
        alert.setHeaderText("Do you want to disconnect from the server?");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().clear();

        alert.getButtonTypes().addAll(yes, no, cancel);

        Optional<ButtonType> option = alert.showAndWait();
        if (option.isEmpty()) {
            // no selection
            return;
        }
        if (option.get() == no) {
            stage.close();
        } else if (option.get() == yes) {
            connection.disconnect();
            stage.close();
        }
        // else return
    }

    @Override
    public void start(Stage stage) throws IOException {

        VBox root = FXMLLoader.load(getClass().getResource("/layout/debug/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Minosoft DebugUI");
        stage.getIcons().add(new Image(DebugMainWindow.class.getResourceAsStream("/icons/windowIcon.png")));

        // listen for enter in text field
        TextField chatToSend = ((TextField) scene.lookup("#chatToSend"));
        chatToSend.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // send chat message
                connection.sendChatMessage(chatToSend.getText());
                chatToSend.setText("");
            }
        });

        Text statusBarServerAddress = (Text) scene.lookup("#statusBarServerAddress");
        statusBarServerAddress.setText(String.format(statusBarServerAddress.getText(), connection.getHost(), connection.getPort()));


        stage.setOnCloseRequest(event -> {
            event.consume();
            showCloseConfirmation();
        });


        Platform.setImplicitExit(false);
        stage.show();
        DebugMainWindow.stage = stage;
        initialized = true;
        handler.initializedCallback();

        // bring to front
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
    }
}

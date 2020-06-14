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
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DebugMainWindow extends Application {
    static DebugUIHandler handler;
    static Stage stage;
    static boolean initialized = false;

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

    @Override
    public void start(Stage stage) throws IOException {

        VBox root = FXMLLoader.load(getClass().getResource("/layout/debug/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Minosoft DebugUI");
        stage.show();

        // autoscroll for chat
        ((TextArea) scene.lookup("#chat")).textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
                ((TextArea) scene.lookup("#chat")).setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom

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

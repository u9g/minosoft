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

package de.bixilon.minosoft.gui.main;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import de.bixilon.minosoft.data.locale.LocaleManager;
import de.bixilon.minosoft.data.locale.Strings;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.util.CountUpAndDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class StartProgressWindow extends Application {
    public final static CountDownLatch toolkitLatch = new CountDownLatch(2); // 2 if not started, 1 if started, 2 if loaded
    public static JFXAlert<Boolean> progressDialog;
    private static JFXProgressBar progressBar;
    private static Label progressLabel;
    private static boolean exit = false;

    public static void show(CountUpAndDownLatch progress) throws InterruptedException {
        if (exit) {
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            if (progress.getCount() == 0) {
                latch.countDown();
                return;
            }
            Platform.runLater(() -> {
                progressDialog = new JFXAlert<>();
                GUITools.initializePane(progressDialog.getDialogPane());
                progressDialog.setTitle(LocaleManager.translate(Strings.MINOSOFT_STILL_STARTING_TITLE));

                JFXDialogLayout layout = new JFXDialogLayout();
                layout.setHeading(new Label(LocaleManager.translate(Strings.MINOSOFT_STILL_STARTING_HEADER)));


                progressBar = new JFXProgressBar();
                progressBar.setPrefHeight(50);

                progressLabel = new Label();

                GridPane gridPane = new GridPane();
                gridPane.setHgap(20);
                gridPane.add(progressBar, 0, 0);
                gridPane.add(progressLabel, 1, 0);


                layout.setBody(gridPane);
                progressDialog.setContent(layout);

                Stage stage = (Stage) progressDialog.getDialogPane().getScene().getWindow();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setOnCloseRequest((request) -> System.exit(0));
                if (exit) {
                    latch.countDown();
                    return;
                }
                progressDialog.show();
                stage.toFront();
            });
            latch.countDown();
            while (progress.getCount() > 0) {
                try {
                    progress.waitForChange();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    progressBar.setProgress(1.0F - ((float) progress.getCount() / progress.getTotal()));
                    progressLabel.setText(String.format("%d / %d", (progress.getTotal() - progress.getCount()), progress.getTotal()));
                });
            }
            hideDialog();
        }).start();
        latch.await();
    }

    public static void start() throws InterruptedException {
        Log.debug("Initializing JavaFX Toolkit...");
        toolkitLatch.countDown();
        new Thread(Application::launch).start();
        toolkitLatch.await();
        Log.debug("Initialized JavaFX Toolkit!");
    }

    public static void hideDialog() {
        exit = true;
        if (progressDialog == null) {
            return;
        }
        Platform.runLater(() -> {
            progressDialog.setResult(Boolean.TRUE);
            progressDialog.hide();
        });
    }

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(false);
        toolkitLatch.countDown();
    }
}

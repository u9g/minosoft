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

package de.bixilon.minosoft;

import com.google.common.collect.HashBiMap;
import de.bixilon.minosoft.config.Configuration;
import de.bixilon.minosoft.config.ConfigurationPaths;
import de.bixilon.minosoft.config.StaticConfiguration;
import de.bixilon.minosoft.data.assets.AssetsManager;
import de.bixilon.minosoft.data.locale.LocaleManager;
import de.bixilon.minosoft.data.locale.minecraft.MinecraftLocaleManager;
import de.bixilon.minosoft.data.mappings.versions.Versions;
import de.bixilon.minosoft.gui.main.*;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.logging.LogLevels;
import de.bixilon.minosoft.modding.event.EventManager;
import de.bixilon.minosoft.modding.loading.ModLoader;
import de.bixilon.minosoft.modding.loading.Priorities;
import de.bixilon.minosoft.protocol.protocol.LANServerListener;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.util.CountUpAndDownLatch;
import de.bixilon.minosoft.util.Util;
import de.bixilon.minosoft.util.mojang.api.MojangAccount;
import de.bixilon.minosoft.util.task.AsyncTaskWorker;
import de.bixilon.minosoft.util.task.Task;
import de.bixilon.minosoft.util.task.TaskImportance;
import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public final class Minosoft {
    public static final HashSet<EventManager> eventManagers = new HashSet<>();
    private static final CountUpAndDownLatch startStatusLatch = new CountUpAndDownLatch(1);
    public static HashBiMap<String, MojangAccount> accountList;
    public static MojangAccount selectedAccount;
    public static ArrayList<Server> serverList;
    public static Configuration config;

    public static void main(String[] args) {
        Log.info("Starting...");
        AsyncTaskWorker taskWorker = new AsyncTaskWorker("StartUp");

        taskWorker.setFatalError((exception) -> {
            Log.fatal("Critical error occurred while preparing. Exit");
            try {
                if (StartProgressWindow.toolkitLatch.getCount() == 2) {
                    StartProgressWindow.start();
                }
                StartProgressWindow.toolkitLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            // hide all other gui parts
            StartProgressWindow.hideDialog();
            Launcher.exit();
            Platform.runLater(() -> {
                Dialog<Boolean> dialog = new Dialog<>();
                // Do not translate this, translations might fail to load...
                dialog.setTitle("Critical Error");
                dialog.setHeaderText("An error occurred while starting Minosoft");
                TextArea text = new TextArea(exception.getClass().getCanonicalName() + ": " + exception.getLocalizedMessage());
                text.setEditable(false);
                text.setWrapText(true);
                dialog.getDialogPane().setContent(text);

                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(GUITools.logo);
                stage.setAlwaysOnTop(true);
                stage.toFront();
                stage.setOnCloseRequest(dialogEvent -> {
                    dialog.setResult(Boolean.TRUE);
                    dialog.close();
                    System.exit(1);
                });
                dialog.showAndWait();
                System.exit(1);
            });
        });
        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            Log.info("Reading config file...");
            try {
                config = new Configuration();
            } catch (IOException e) {
                Log.fatal("Failed to load config file!");
                e.printStackTrace();
                return;
            }
            Log.info(String.format("Loaded config file (version=%s)", config.getInt(ConfigurationPaths.IntegerPaths.GENERAL_CONFIG_VERSION)));
            // set log level from config
            Log.setLevel(LogLevels.valueOf(config.getString(ConfigurationPaths.StringPaths.GENERAL_LOG_LEVEL)));
            Log.info(String.format("Logging info with level: %s", Log.getLevel()));

            serverList = config.getServers();
            progress.countDown();
        }, "Configuration", String.format("Load config file (%s)", StaticConfiguration.CONFIG_FILENAME), Priorities.HIGHEST, TaskImportance.REQUIRED));

        taskWorker.addTask(new Task((progress) -> StartProgressWindow.start(), "JavaFX Toolkit", "Initialize JavaFX", Priorities.HIGHEST));

        taskWorker.addTask(new Task((progress) -> StartProgressWindow.show(startStatusLatch), "Progress Window", "Display progress window", Priorities.HIGH, TaskImportance.OPTIONAL, "JavaFX Toolkit", "Configuration"));

        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            LocaleManager.load(config.getString(ConfigurationPaths.StringPaths.GENERAL_LANGUAGE));
            progress.countDown();
        }, "Minosoft Language", "Load minosoft language files", Priorities.HIGH, TaskImportance.REQUIRED, "Configuration"));

        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            Log.info("Loading versions.json...");
            long mappingStartLoadingTime = System.currentTimeMillis();
            Versions.loadAvailableVersions(Util.readJsonAsset("mapping/versions.json"));
            Log.info(String.format("Loaded %d versions in %dms", Versions.getVersionIdMap().size(), (System.currentTimeMillis() - mappingStartLoadingTime)));
            progress.countDown();
        }, "Version mappings", "Load available minecraft versions inclusive mappings", Priorities.NORMAL, TaskImportance.REQUIRED, "Configuration"));

        taskWorker.addTask(new Task(progress -> {
            Log.debug("Refreshing account token...");
            checkClientToken();
            accountList = config.getMojangAccounts();
            selectAccount(accountList.get(config.getString(ConfigurationPaths.StringPaths.ACCOUNT_SELECTED)));
        }, "Token refresh", "Refresh selected account token", Priorities.LOW, TaskImportance.OPTIONAL, "Configuration"));

        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            ModLoader.loadMods(progress);
            progress.countDown();
        }, "ModLoading", "Load all minosoft mods", Priorities.NORMAL, TaskImportance.REQUIRED, "Configuration"));

        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            AssetsManager.downloadAllAssets(progress);
            progress.countDown();
        }, "Assets", "Download and verify all minecraft assets", Priorities.HIGH, TaskImportance.REQUIRED, "Configuration"));

        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            MinecraftLocaleManager.load(config.getString(ConfigurationPaths.StringPaths.GENERAL_LANGUAGE));
            progress.countDown();
        }, "Mojang language", "Load minecraft language files", Priorities.HIGH, TaskImportance.REQUIRED, "Assets"));

        taskWorker.addTask(new Task(progress -> {
            progress.countUp();
            GameWindow.prepare();
            progress.countDown();

        }, "Game Window", "", Priorities.NORMAL, TaskImportance.REQUIRED, "Assets", "Progress Window"));

        taskWorker.addTask(new Task(progress -> {
            if (!config.getBoolean(ConfigurationPaths.BooleanPaths.NETWORK_SHOW_LAN_SERVERS)) {
                return;
            }
            progress.countUp();
            LANServerListener.listen();
            progress.countDown();
        }, "LAN Server Listener", "Listener for LAN Servers", Priorities.LOWEST, TaskImportance.OPTIONAL, "Configuration"));

        taskWorker.work(startStatusLatch);
        try {
            startStatusLatch.waitUntilZero();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Launcher.start();
    }

    public static void checkClientToken() {
        if (config.getString(ConfigurationPaths.StringPaths.CLIENT_TOKEN).isBlank()) {
            config.putString(ConfigurationPaths.StringPaths.CLIENT_TOKEN, UUID.randomUUID().toString());
            config.saveToFile();
        }
    }

    public static void selectAccount(MojangAccount account) {
        if (account == null) {
            selectedAccount = null;
            config.putString(ConfigurationPaths.StringPaths.ACCOUNT_SELECTED, "");
            config.saveToFile();
            return;
        }
        MojangAccount.RefreshStates refreshState = account.refreshToken();
        if (refreshState == MojangAccount.RefreshStates.ERROR) {
            accountList.remove(account.getUserId());
            account.delete();
            AccountListCell.listView.getItems().remove(account);
            selectedAccount = null;
            return;
        }
        config.putString(ConfigurationPaths.StringPaths.ACCOUNT_SELECTED, account.getUserId());
        selectedAccount = account;
        MainWindow.selectAccount();
        account.saveToConfig();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static ArrayList<Server> getServerList() {
        return serverList;
    }

    public static HashBiMap<String, MojangAccount> getAccountList() {
        return accountList;
    }

    public static MojangAccount getSelectedAccount() {
        return selectedAccount;
    }

    /**
     * Waits until all critical components are started
     */
    public static void waitForStartup() {
        try {
            startStatusLatch.waitUntilZero();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static CountUpAndDownLatch getStartStatusLatch() {
        return startStatusLatch;
    }
}

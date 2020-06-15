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

package de.bixilon.minosoft;

import de.bixilon.minosoft.config.Configuration;
import de.bixilon.minosoft.config.GameConfiguration;
import de.bixilon.minosoft.game.datatypes.Player;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.logging.LogLevel;
import de.bixilon.minosoft.mojang.api.MojangAccount;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.util.OSUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Minosoft {
    static Configuration config;
    static List<MojangAccount> accountList;

    public static void main(String[] args) {
        // int log thread
        Log.initThread();

        Log.info("Starting...");
        setConfigFolder();
        Log.info("Reading config file...");
        try {
            config = new Configuration(Config.configFileName);
        } catch (IOException e) {
            Log.fatal("Failed to load config file!");
            e.printStackTrace();
            return;
        }
        Log.info(String.format("Loaded config file (version=%s)", config.getInteger(GameConfiguration.CONFIG_VERSION)));
        // set log level from config
        Log.setLevel(LogLevel.byName(config.getString(GameConfiguration.GENERAL_LOG_LEVEL)));

        checkClientToken();

        accountList = config.getMojangAccounts();
        if (accountList.size() == 0) {
            /*
            MojangAccount account = MojangAuthentication.login("email", "password");
            account.saveToConfig();
             */
            throw new RuntimeException("No accounts in config file!");
        }
        MojangAccount account = accountList.get(0);
        if (!account.refreshToken()) {
            // could not login
            System.exit(1);
        }
        account.saveToConfig();

        Connection connection = new Connection(config.getString("debug.host"), config.getInteger("debug.port"));
        connection.setPlayer(new Player(account));
        connection.connect();
    }

    /**
     * Sets Config.homeDir to the correct folder per OS
     */
    public static void setConfigFolder() {
        String path = System.getProperty("user.home");
        if (!path.endsWith(File.separator)) {
            path += "/";
        }
        switch (OSUtil.getOS()) {
            case LINUX:
                path += ".local/share/minosoft/";
                break;
            case WINDOWS:
                path += "AppData/Roaming/Minosoft/";
                break;
            //ToDo: Mac, Other
        }
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            // failed creating folder
            throw new RuntimeException(String.format("Could not create home folder (%s)!", path));
        }
        Config.homeDir = path;
    }

    public static Configuration getConfig() {
        return config;
    }

    public static void checkClientToken() {
        if (config.getString(GameConfiguration.CLIENT_TOKEN) == null || config.getString(GameConfiguration.CLIENT_TOKEN).equals("randomGenerated")) {
            config.putString(GameConfiguration.CLIENT_TOKEN, UUID.randomUUID().toString());
            config.saveToFile(Config.configFileName);
        }
    }
}

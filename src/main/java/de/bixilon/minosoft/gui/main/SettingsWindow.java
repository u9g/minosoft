/*
 * Minosoft
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

package de.bixilon.minosoft.gui.main;

import de.bixilon.minosoft.Minosoft;
import de.bixilon.minosoft.config.ConfigurationPaths;
import de.bixilon.minosoft.data.locale.LocaleManager;
import de.bixilon.minosoft.data.locale.Strings;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.logging.LogLevels;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindow implements Initializable {
    public GridPane tabGeneral;
    public ComboBox<LogLevels> generalLogLevel;
    public Tab general;
    public Tab download;
    public Label generalLogLevelLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generalLogLevel.setItems(GUITools.logLevels);
        generalLogLevel.getSelectionModel().select(Log.getLevel());
        generalLogLevel.setOnAction((actionEvent -> {
            LogLevels newLevel = generalLogLevel.getValue();
            if (Log.getLevel() == newLevel) {
                return;
            }
            Log.setLevel(newLevel);
            Minosoft.getConfig().putString(ConfigurationPaths.StringPaths.GENERAL_LOG_LEVEL, newLevel.name());
            Minosoft.getConfig().saveToFile();
        }));

        general.setText(LocaleManager.translate(Strings.SETTINGS_GENERAL));
        generalLogLevelLabel.setText(LocaleManager.translate(Strings.SETTINGS_GENERAL_LOG_LEVEL));
        download.setText(LocaleManager.translate(Strings.SETTINGS_DOWNLOAD));
    }
}

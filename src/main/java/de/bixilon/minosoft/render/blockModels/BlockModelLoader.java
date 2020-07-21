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

package de.bixilon.minosoft.render.blockModels;

import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.game.datatypes.blocks.Blocks;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static de.bixilon.minosoft.render.blockModels.BlockName.getBlockByName;

public class BlockModelLoader {
    private final Map<Blocks, DrawDescription> drawDescriptionMap;

    public BlockModelLoader() {
        drawDescriptionMap = new HashMap<>();
        try {
            loadModels(Config.homeDir + "assets/minecraft/models/block");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModels(String path) throws IOException {
        File[] files = new File(path).listFiles();

        for (File file : files) {
            String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            String fileContent = new String(Files.readAllBytes(Paths.get(file.getPath())));
            JSONObject object = new JSONObject(fileContent);
            DrawDescription drawDescription = new DrawDescription(object);
            drawDescriptionMap.put(getBlockByName(fileName), drawDescription);
        }
    }

    public DrawDescription getDrawDescription(Blocks block) {
        if (!drawDescriptionMap.containsKey(block))
            throw new IllegalArgumentException(String.format("no description for block %s found", block));
        return drawDescriptionMap.get(block);
    }

    public boolean isFull(Blocks block) {
        if (block == Blocks.AIR || block == null) {
            return false;
        }
        return drawDescriptionMap.get(block).isFull();
    }
}

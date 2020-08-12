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

import com.google.gson.JsonObject;
import de.bixilon.minosoft.render.MainWindow;
import de.bixilon.minosoft.render.fullFace.FaceOrientation;
import javafx.util.Pair;

import java.util.*;

public class DrawDescription {
    private final FaceOrientation[] sideOrientations = new FaceOrientation[]{
            FaceOrientation.EAST,
            FaceOrientation.WEST,
            FaceOrientation.SOUTH,
            FaceOrientation.NORTH
    };
    private final FaceOrientation[] endOrientations = new FaceOrientation[]{
            FaceOrientation.UP,
            FaceOrientation.DOWN
    };


    Map<FaceOrientation, Pair<Float, Float>> faces;
    boolean full = false; // is the block a completely filled block?

    public DrawDescription(JsonObject json, String mod) {
        if (!(json.has("parent") && json.has("textures"))) return;

        faces = new HashMap<>();

        JsonObject textures = json.getAsJsonObject("textures");

        for (String texture : textures.keySet()) {
            String textureUse = textures.getAsJsonObject(texture).toString();

            Pair<Float, Float> texturePair;

            try {
                texturePair = MainWindow.getRenderer().getModelLoader().getTextureLoader().getTexture(mod, texture);
            } catch (Exception e) {
                continue;
            }

            List<FaceOrientation> faceOrientations = new ArrayList<>();

            switch (textureUse) {
                case "all":
                    faceOrientations.addAll(Arrays.asList(FaceOrientation.values()));
                case "side":
                    faceOrientations.addAll(Arrays.asList(sideOrientations));
                case "end":
                    faceOrientations.addAll(Arrays.asList(endOrientations));
                case "top":
                    faceOrientations.add(FaceOrientation.UP);
                case "bottom":
                    faceOrientations.add(FaceOrientation.DOWN);
            }

            for (FaceOrientation faceOrientation : faceOrientations) {
                faces.put(faceOrientation, texturePair);
            }
        }
        full = true;
    }

    public boolean isFull() {
        return full;
    }

    public Pair<Float, Float> getTexture(FaceOrientation orientation) {
        if (!faces.containsKey(orientation))
            throw new IllegalArgumentException("face " + orientation + " not covered by: " + this);

        return faces.get(orientation);
    }

    @Override
    public String toString() {
        return String.format("%s full: %s", faces.toString(), full);
    }
}
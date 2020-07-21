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

import de.bixilon.minosoft.render.MainWindow;
import de.bixilon.minosoft.render.face.FaceOrientation;
import javafx.util.Pair;
import org.json.JSONObject;

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

    public DrawDescription(JSONObject json) {
        if (!(json.has("parent") && json.has("textures"))) return;

        faces = new HashMap<>();

        JSONObject textures = json.getJSONObject("textures");

        for (Iterator<String> textureIterator = textures.keys(); textureIterator.hasNext(); ) {
            String textureUse = textureIterator.next();

            String textureName = textures.getString(textureUse);
            Pair<Float, Float> texture;

            try {
                texture = MainWindow.getRenderer().getTextureLoader().getTexture(textureName);
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
                faces.put(faceOrientation, texture);
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
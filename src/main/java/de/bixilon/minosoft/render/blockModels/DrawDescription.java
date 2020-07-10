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

import java.util.HashMap;
import java.util.Map;

public class DrawDescription {
    Map<FaceOrientation, Pair<Float, Float>> faces;
    boolean full; // is the block a completely filled block

    public DrawDescription(JSONObject json) {
        if (!(json.has("parent") && json.has("textures"))) return;

        faces = new HashMap<>();

        JSONObject textures = (JSONObject) json.get("textures");
        if (json.get("parent").equals("block/cube_all")) {
            // we have a full block and all sides are the same
            Pair<Float, Float> texture = MainWindow.getRenderer().getTextureLoader().getTexture(
                    (String) textures.get("all"));

            for (FaceOrientation orientation : FaceOrientation.values()) {
                faces.put(orientation, texture);
            }
        } else if (json.get("parent").equals("block/cube_column")) {
            Pair<Float, Float> sideTexture = MainWindow.getRenderer().getTextureLoader().getTexture(
                    (String) textures.get("side"));
            faces.put(FaceOrientation.EAST, sideTexture);
            faces.put(FaceOrientation.WEST, sideTexture);
            faces.put(FaceOrientation.SOUTH, sideTexture);
            faces.put(FaceOrientation.NORTH, sideTexture);

            Pair<Float, Float> endTexture = MainWindow.getRenderer().getTextureLoader().getTexture(
                    (String) textures.get("end"));
            faces.put(FaceOrientation.UP, endTexture);
            faces.put(FaceOrientation.DOWN, endTexture);

        } else if (json.get("parent").equals("block/block")) {
            // top and bottom faces are different
            if (textures.has("side")) {
                Pair<Float, Float> sideTexture = MainWindow.getRenderer().getTextureLoader().getTexture(
                        (String) textures.get("side"));
                faces.put(FaceOrientation.EAST, sideTexture);
                faces.put(FaceOrientation.WEST, sideTexture);
                faces.put(FaceOrientation.SOUTH, sideTexture);
                faces.put(FaceOrientation.NORTH, sideTexture);
            }
            if (textures.has("top")) {
                Pair<Float, Float> topTexture = MainWindow.getRenderer().getTextureLoader().getTexture(
                        (String) textures.get("top"));
                faces.put(FaceOrientation.UP, topTexture);
            }
            if (textures.has("bottom")) {
                Pair<Float, Float> bottomTexture = MainWindow.getRenderer().getTextureLoader().getTexture(
                        (String) textures.get("bottom"));
                faces.put(FaceOrientation.DOWN, bottomTexture);
            }

            if (textures.has("end")) {
                Pair<Float, Float> endTexture = MainWindow.getRenderer().getTextureLoader().getTexture(
                        (String) textures.get("end"));
                faces.put(FaceOrientation.UP, endTexture);
                faces.put(FaceOrientation.DOWN, endTexture);
            }
        }
        full = json.get("parent").equals("block/block") |
                json.get("parent").equals("block/cube_all");
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
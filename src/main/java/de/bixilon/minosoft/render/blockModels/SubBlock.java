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
import de.bixilon.minosoft.render.fullFace.FaceOrientation;
import de.bixilon.minosoft.render.fullFace.InFaceUV;
import de.bixilon.minosoft.render.texture.TextureLoader;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SubBlock {
    SubBlockPosition pos1; // the most negative Point of the SubBlock
    SubBlockPosition pos2; // the most positive Point of the SubBlock

    HashMap<FaceOrientation, Pair<Float, Float>> textureCoordinates;
    HashMap<FaceOrientation, String> textures;
    HashMap<FaceOrientation, Boolean> cullFaceTextures;

    HashMap<FaceOrientation, InFaceUV> uv;
    private final boolean isFull;

    public SubBlock(JsonObject json, HashMap<String, String> variables) {
        uv = new HashMap<>();
        textures = new HashMap<>();
        textureCoordinates = new HashMap<>();
        cullFaceTextures = new HashMap<>();

        pos1 = new SubBlockPosition(json.getAsJsonArray("from"));
        pos2 = new SubBlockPosition(json.getAsJsonArray("to"));
        JsonObject faces = json.getAsJsonObject("faces");
        for (FaceOrientation orientation : FaceOrientation.values()) {
            if (faces.has(orientation.name().toLowerCase())) {
                applyTexture(faces.getAsJsonObject(orientation.name().toLowerCase()),
                        orientation, variables);
            }
        }
        isFull = (pos1.x == 0 && pos1.y == 0 && pos1.z == 0) && (pos2.x == 16 && pos2.y == 16 && pos2.z == 16);
    }

    private static String getRealTextureName(String textureName, HashMap<String, String> variables) {
        // read the variables and find the real texture name
        if (textureName.contains("#")) {
            if (variables.containsKey(textureName)) {
                String newName = variables.get(textureName);
                if (newName.contains("#")) {
                    if (newName.equals(textureName)) {
                        throw new IllegalArgumentException("self relation: " + textureName);
                    }
                    return getRealTextureName(newName, variables);
                }
                return newName;
            } else {
                throw new IllegalArgumentException("could not find variable " + textureName);
            }
        } else {
            return textureName;
        }
    }

    public void applyTextures(String mod, TextureLoader loader) {
        for (Map.Entry<FaceOrientation, String> entry : textures.entrySet()) {
            Pair<Float, Float> texture = loader.getTexture(mod, entry.getValue());
            if (texture == null) {
                continue;
            }
            textureCoordinates.put(entry.getKey(), texture);
        }
        // clean up
        textures.clear();
    }

    private void applyTexture(JsonObject faceJson, FaceOrientation orientation, HashMap<String, String> variables) {
        try {
            uv.put(orientation, new InFaceUV(faceJson.getAsJsonArray("uv")));
        } catch (Exception e) {
            uv.put(orientation, new InFaceUV());
        }
        String textureName = getRealTextureName(faceJson.get("texture").getAsString(), variables);
        textures.put(orientation, textureName);
        cullFaceTextures.put(orientation, faceJson.has("cullface"));
    }

    public HashSet<Face> getFaces(HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        HashSet<Face> result = new HashSet<>();
        for (FaceOrientation orientation : FaceOrientation.values()) {
            if (!textureCoordinates.containsKey(orientation)) {
                continue;
            }
            if (!(adjacentBlocks.get(orientation) && cullFaceTextures.get(orientation))) {
                result.add(new Face(orientation, textureCoordinates.get(orientation),
                        uv.get(orientation), this));
            }
        }
        return result;
    }

    public boolean isFull() {
        return isFull;
    }

    public HashSet<String> getTextures() {
        HashSet<String> result = new HashSet<>();
        for (Map.Entry<FaceOrientation, String> texture : textures.entrySet()) {
            result.add(texture.getValue());
        }
        return result;
    }
}

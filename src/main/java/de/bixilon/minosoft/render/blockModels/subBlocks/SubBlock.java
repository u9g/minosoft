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

package de.bixilon.minosoft.render.blockModels.subBlocks;

import com.google.gson.JsonObject;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockRotation;
import de.bixilon.minosoft.render.blockModels.Face.Face;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.texture.InFaceUV;
import de.bixilon.minosoft.render.texture.TextureLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SubBlock {
    SubBlockRotation rotation;

    HashMap<FaceOrientation, Float> textureCoordinates;
    HashMap<FaceOrientation, String> textures;
    HashMap<FaceOrientation, Boolean> cullFaceTextures;

    HashMap<FaceOrientation, InFaceUV> uv;

    Cuboid cuboid;

    private final boolean isFull;

    public SubBlock(JsonObject json, HashMap<String, String> variables) {
        uv = new HashMap<>();
        textures = new HashMap<>();
        textureCoordinates = new HashMap<FaceOrientation, Float>();
        cullFaceTextures = new HashMap<>();

        SubBlockPosition from = new SubBlockPosition(json.getAsJsonArray("from"));
        SubBlockPosition to = new SubBlockPosition(json.getAsJsonArray("to"));
        if (json.has("rotation")) {
            rotation = new SubBlockRotation(json.get("rotation").getAsJsonObject());
        }
        cuboid = new Cuboid(from, to, rotation);

        JsonObject faces = json.getAsJsonObject("faces");
        for (FaceOrientation orientation : FaceOrientation.values()) {
            if (faces.has(orientation.name().toLowerCase())) {
                applyTexture(faces.getAsJsonObject(orientation.name().toLowerCase()),
                        orientation, variables);
            }
        }
        isFull = (from.x == 0 && from.y == 0 && from.z == 0) && (to.x == 16 && to.y == 16 && to.z == 16) && rotation == null;
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
                throw new IllegalArgumentException("could not resolve variable " + textureName);
            }
        } else {
            return textureName;
        }
    }

    public void applyTextures(String mod, TextureLoader loader) {
        for (Map.Entry<FaceOrientation, String> entry : textures.entrySet()) {
            float texture = loader.getTexture(mod, entry.getValue());
            if (texture == -1) {
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

    public HashSet<Face> getFaces(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        HashSet<Face> result = new HashSet<>();
        for (FaceOrientation orientation : FaceOrientation.values()) {
            if (!textureCoordinates.containsKey(orientation)) {
                continue;
            }
            result.add(prepareFace(orientation, block.getRotation(), adjacentBlocks));
        }
        return result;
    }

    private Face prepareFace(FaceOrientation faceDirection, BlockRotation rotation,
                             HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        if (adjacentBlocks.get(faceDirection) && !cullFaceTextures.get(faceDirection)) {
            return new Face();
        }
        return new Face(textureCoordinates.get(faceDirection), uv.get(faceDirection),
                cuboid.getFacePositions(faceDirection, rotation));
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

    public HashSet<Face> getFacesSimple(BlockRotation rotation) {
        HashSet<Face> result = new HashSet<>();
        for (FaceOrientation orientation : FaceOrientation.values()) {
            result.add(prepareFace(orientation, rotation, new HashMap<>()));
        }
        return result;
    }
}

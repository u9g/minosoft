/*
 * Minosoft
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
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.render.blockModels.Face.Axis;
import de.bixilon.minosoft.render.blockModels.Face.FaceOrientation;
import de.bixilon.minosoft.render.texture.InFaceUV;
import de.bixilon.minosoft.render.texture.TextureLoader;
import org.apache.commons.collections.primitives.ArrayFloatList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SubBlock {
    private final HashMap<FaceOrientation, Float> textureCoordinates;
    private HashMap<FaceOrientation, String> textures;
    private final HashMap<FaceOrientation, Integer> textureRotations;
    private final boolean[] full;
    private final HashMap<FaceOrientation, InFaceUV> uv;
    private final Cuboid cuboid;
    private SubBlockRotation rotation;

    public SubBlock(JsonObject json, HashMap<String, String> variables) {
        uv = new HashMap<>();
        textures = new HashMap<>();
        textureRotations = new HashMap<>();
        textureCoordinates = new HashMap<>();

        SubBlockPosition from = new SubBlockPosition(json.getAsJsonArray("from"));
        SubBlockPosition to = new SubBlockPosition(json.getAsJsonArray("to"));
        if (json.has("rotation")) {
            rotation = new SubBlockRotation(json.get("rotation").getAsJsonObject());
        }
        cuboid = new Cuboid(from, to, rotation);

        JsonObject faces = json.getAsJsonObject("faces");
        for (FaceOrientation orientation : FaceOrientation.values()) {
            if (faces.has(orientation.name().toLowerCase())) {
                putTexture(faces.getAsJsonObject(orientation.name().toLowerCase()), orientation, variables);
            }
        }
        full = createFull();
    }

    public SubBlock(SubBlock subBlock) {
        textureCoordinates = subBlock.textureCoordinates;
        textureRotations = subBlock.textureRotations;
        uv = subBlock.uv;
        cuboid = new Cuboid(subBlock.cuboid);
        full = createFull();
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
                return "";
            }
        } else {
            return textureName;
        }
    }

    private boolean[] createFull() {
        return new boolean[]{cuboid.isFull(FaceOrientation.EAST), cuboid.isFull(FaceOrientation.WEST), cuboid.isFull(FaceOrientation.UP), cuboid.isFull(FaceOrientation.DOWN), cuboid.isFull(FaceOrientation.NORTH), cuboid.isFull(FaceOrientation.SOUTH)};
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

    private void putTexture(JsonObject faceJson, FaceOrientation orientation, HashMap<String, String> variables) {
        if (faceJson.has("uv")) {
            uv.put(orientation, new InFaceUV(faceJson.getAsJsonArray("uv")));
        } else {
            uv.put(orientation, new InFaceUV());
        }
        if (faceJson.has("rotation")) {
            int rotation = (360 - faceJson.get("rotation").getAsInt()) / 90;
            textureRotations.put(orientation, rotation);
        } else {
            textureRotations.put(orientation, 0);
        }
        String textureName = getRealTextureName(faceJson.get("texture").getAsString(), variables);
        textures.put(orientation, textureName);
    }

    public ArrayFloatList getFaces(HashSet<FaceOrientation> facesToDraw, BlockPosition position) {
        ArrayFloatList result = new ArrayFloatList();
        for (FaceOrientation orientation : FaceOrientation.values()) {
            if (full[orientation.getId()] && !facesToDraw.contains(orientation)) {
                continue;
            }
            ArrayFloatList face = prepareFace(orientation, position);
            if (face == null) {
                continue;
            }
            result.addAll(face);
        }
        return result;
    }

    private ArrayFloatList prepareFace(FaceOrientation faceDirection, BlockPosition position) {
        if (!textureCoordinates.containsKey(faceDirection)) {
            return null;
        }
        ArrayFloatList result = new ArrayFloatList();
        SubBlockPosition[] positions = cuboid.getFacePositions(faceDirection);
        InFaceUV inFaceUV = uv.get(faceDirection);
        inFaceUV.prepare(textureCoordinates.get(faceDirection));
        int rotation = textureRotations.get(faceDirection);
        for (int i = 0; i < positions.length; i++) {
            result.addAll(inFaceUV.getFloats(i + rotation));
            result.addAll(positions[i].getFloats(position));
        }
        return result;
    }

    public HashSet<String> getTextures() {
        HashSet<String> result = new HashSet<>();
        for (Map.Entry<FaceOrientation, String> texture : textures.entrySet()) {
            result.add(texture.getValue());
        }
        return result;
    }

    public void rotate(Axis axis, double rotation) {
        cuboid.rotate(axis, rotation);
    }

    public boolean[] getFull() {
        return full;
    }
}

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

package de.bixilon.minosoft.data.mappings.blocks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Map;

public class Block {
    final String mod;
    final String identifier;
    final BlockRotations rotation;
    final HashSet<BlockProperties> properties;

    public Block(String mod, String identifier, HashSet<BlockProperties> properties, BlockRotations rotation) {
        this.mod = mod;
        this.identifier = identifier;
        this.properties = properties;
        this.rotation = rotation;
    }

    public Block(String mod, String identifier, HashSet<BlockProperties> properties) {
        this.mod = mod;
        this.identifier = identifier;
        this.properties = properties;
        this.rotation = BlockRotations.NONE;
    }

    public Block(String mod, String identifier, BlockRotations rotation) {
        this.mod = mod;
        this.identifier = identifier;
        this.properties = new HashSet<>();
        this.rotation = rotation;
    }

    public Block(String mod, String identifier) {
        this.mod = mod;
        this.identifier = identifier;
        this.properties = new HashSet<>();
        this.rotation = BlockRotations.NONE;
    }

    public Block(String mod, String identifier, JsonObject properties) {
        this.mod = mod;
        this.identifier = identifier;
        this.properties = new HashSet<>();
        BlockRotations rotation = BlockRotations.NONE;
        for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
            String key = property.getKey();
            String value = property.getValue().getAsString();
            if (BlockProperties.PROPERTIES_MAPPING.containsKey(key)) {
                this.properties.add(BlockProperties.PROPERTIES_MAPPING.get(key).get(value));
            } else if (BlockRotations.ROTATION_MAPPING.containsKey(key)) {
                rotation = BlockRotations.ROTATION_MAPPING.get(value);
            }
        }
        this.rotation = rotation;
    }

    public String getMod() {
        return mod;
    }

    public String getIdentifier() {
        return identifier;
    }

    public BlockRotations getRotation() {
        return rotation;
    }

    public HashSet<BlockProperties> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        if (rotation != BlockRotations.NONE) {
            out.append(" (");
            out.append("rotation=");
            out.append(getRotation());
        }
        if (!properties.isEmpty()) {
            if (out.length() > 0) {
                out.append(", ");
            } else {
                out.append(" (");
            }
            out.append("properties=");
            out.append(properties);
        }
        if (out.length() > 0) {
            out.append(")");
        }
        return String.format("%s:%s%s", getMod(), getIdentifier(), out);
    }

    @Override
    public int hashCode() {
        int ret = mod.hashCode() * identifier.hashCode() * rotation.hashCode();
        if (!properties.isEmpty()) {
            ret *= properties.hashCode();
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (hashCode() != obj.hashCode()) {
            return false;
        }
        Block their = (Block) obj;
        return getIdentifier().equals(their.getIdentifier()) && getRotation() == their.getRotation() && getProperties().equals(their.getProperties()) && getMod().equals(their.getMod());
    }
}

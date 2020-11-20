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

package de.bixilon.minosoft.data.entities.entities.animal;

import de.bixilon.minosoft.data.entities.EntityMetaDataFields;
import de.bixilon.minosoft.data.entities.EntityRotation;
import de.bixilon.minosoft.data.entities.Location;
import de.bixilon.minosoft.data.entities.entities.EntityMetaDataFunction;
import de.bixilon.minosoft.data.entities.entities.TamableAnimal;
import de.bixilon.minosoft.data.text.ChatColors;
import de.bixilon.minosoft.data.text.RGBColor;
import de.bixilon.minosoft.protocol.network.Connection;

import java.util.UUID;

public class Cat extends TamableAnimal {

    public Cat(Connection connection, int entityId, UUID uuid, Location location, EntityRotation rotation) {
        super(connection, entityId, uuid, location, rotation);
    }

    @EntityMetaDataFunction(identifier = "variant")
    public CatVariants getVariant() {
        return CatVariants.values()[metaData.getSets().getInt(EntityMetaDataFields.CAT_VARIANT)];
    }

    @EntityMetaDataFunction(identifier = "lying")
    public boolean isLying() {
        return metaData.getSets().getBoolean(EntityMetaDataFields.CAT_IS_LYING);
    }

    @EntityMetaDataFunction(identifier = "relaxed")
    public boolean isRelaxed() {
        return metaData.getSets().getBoolean(EntityMetaDataFields.CAT_IS_RELAXED);
    }

    @EntityMetaDataFunction(identifier = "collarColor")
    public RGBColor getCollarColor() {
        return ChatColors.getColorById(metaData.getSets().getInt(EntityMetaDataFields.CAT_GET_COLLAR_COLOR));
    }

    public enum CatVariants {
        TABBY,
        BLACK,
        RED,
        SIAMESE,
        BRITISH_SHORT_HAIR,
        CALICO,
        PERSIAN,
        RAGDOLL,
        ALL_BLACK
    }
}

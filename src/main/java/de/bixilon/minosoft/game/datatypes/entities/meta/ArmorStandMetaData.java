/*
 * Codename Minosoft
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
package de.bixilon.minosoft.game.datatypes.entities.meta;

import de.bixilon.minosoft.game.datatypes.EntityRotation;
import de.bixilon.minosoft.protocol.protocol.ProtocolVersion;
import de.bixilon.minosoft.util.BitByte;

import java.util.HashMap;

public class ArmorStandMetaData extends MobMetaData {

    public ArmorStandMetaData(HashMap<Integer, MetaDataSet> sets, ProtocolVersion version) {
        super(sets, version);
    }


    public boolean isSmall() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return BitByte.isBitMask((byte) sets.get(10).getData(), 0x01);
            case VERSION_1_10:
                return BitByte.isBitMask((byte) sets.get(11).getData(), 0x01);
        }
        return false;
    }

    public boolean hasGravity() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return BitByte.isBitMask((byte) sets.get(10).getData(), 0x02);
            case VERSION_1_10:
                return BitByte.isBitMask((byte) sets.get(11).getData(), 0x02);
        }
        return false;
    }

    public boolean hasArms() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return BitByte.isBitMask((byte) sets.get(10).getData(), 0x04);
            case VERSION_1_10:
                return BitByte.isBitMask((byte) sets.get(11).getData(), 0x04);
        }
        return false;
    }

    public boolean removeBasePlate() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return BitByte.isBitMask((byte) sets.get(10).getData(), 0x08);
            case VERSION_1_10:
                return BitByte.isBitMask((byte) sets.get(11).getData(), 0x08);
        }
        return false;
    }

    public boolean hasMarker() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return BitByte.isBitMask((byte) sets.get(10).getData(), 0x10);
            case VERSION_1_10:
                return BitByte.isBitMask((byte) sets.get(11).getData(), 0x10);
        }
        return false;
    }

    public EntityRotation getHeadRotation() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return (EntityRotation) sets.get(11).getData();
            case VERSION_1_10:
                return (EntityRotation) sets.get(12).getData();
        }
        return null;
    }

    public EntityRotation getBodyRotation() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return (EntityRotation) sets.get(12).getData();
            case VERSION_1_10:
                return (EntityRotation) sets.get(13).getData();
        }
        return null;
    }

    public EntityRotation getLeftArmRotation() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return (EntityRotation) sets.get(13).getData();
            case VERSION_1_10:
                return (EntityRotation) sets.get(14).getData();
        }
        return null;
    }

    public EntityRotation getRightArmRotation() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return (EntityRotation) sets.get(14).getData();
            case VERSION_1_10:
                return (EntityRotation) sets.get(15).getData();
        }
        return null;
    }

    public EntityRotation getLeftLegRotation() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return (EntityRotation) sets.get(15).getData();
            case VERSION_1_10:
                return (EntityRotation) sets.get(16).getData();
        }
        return null;
    }

    public EntityRotation getRightLegRotation() {
        switch (version) {
            case VERSION_1_8:
            case VERSION_1_9_4:
                return (EntityRotation) sets.get(16).getData();
            case VERSION_1_10:
                return (EntityRotation) sets.get(17).getData();
        }
        return null;
    }


}

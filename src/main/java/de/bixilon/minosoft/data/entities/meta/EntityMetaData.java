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
package de.bixilon.minosoft.data.entities.meta;

import de.bixilon.minosoft.data.*;
import de.bixilon.minosoft.data.entities.Poses;
import de.bixilon.minosoft.data.entities.VillagerData;
import de.bixilon.minosoft.data.inventory.Slot;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.particle.data.ParticleData;
import de.bixilon.minosoft.data.text.ChatComponent;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.protocol.protocol.InByteBuffer;
import de.bixilon.minosoft.util.BitByte;
import de.bixilon.minosoft.util.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class EntityMetaData {

    final MetaDataHashMap sets;
    final int versionId;

    /*
    1.7.10: https://wiki.vg/index.php?title=Entity_metadata&oldid=5991
    1.8: https://wiki.vg/index.php?title=Entity_metadata&oldid=6611
    1.9.4: https://wiki.vg/index.php?title=Entity_metadata&oldid=7955
    1.10: https://wiki.vg/index.php?title=Entity_metadata&oldid=8241
    1.11: https://wiki.vg/index.php?title=Entity_metadata&oldid=8413
    1.12: https://wiki.vg/index.php?title=Entity_metadata&oldid=13919
    1.13: https://wiki.vg/index.php?title=Entity_metadata&oldid=14800
    1.14.4: https://wiki.vg/index.php?title=Entity_metadata&oldid=15239
    1.15: https://wiki.vg/index.php?title=Entity_metadata&oldid=15885
     */

    public EntityMetaData(MetaDataHashMap sets, int versionId) {
        this.sets = sets;
        this.versionId = versionId;
    }

    public static Object getData(EntityMetaDataValueTypes type, InByteBuffer buffer) {
        return switch (type) {
            case BYTE -> buffer.readByte();
            case VAR_INT -> buffer.readVarInt();
            case SHORT -> buffer.readShort();
            case INT -> buffer.readInt();
            case FLOAT -> buffer.readFloat();
            case STRING -> buffer.readString();
            case CHAT -> buffer.readTextComponent();
            case BOOLEAN -> buffer.readBoolean();
            case VECTOR -> new Vector(buffer.readInt(), buffer.readInt(), buffer.readInt());
            case SLOT -> buffer.readSlot();
            case ROTATION -> new EntityRotation(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            case POSITION -> buffer.readPosition();
            case OPT_CHAT -> {
                if (buffer.readBoolean()) {
                    yield buffer.readTextComponent();
                }
                yield null;
            }
            case OPT_POSITION -> {
                if (buffer.readBoolean()) {
                    yield buffer.readPosition();
                }
                yield null;
            }
            case DIRECTION -> buffer.readDirection();
            case OPT_UUID -> {
                if (buffer.readBoolean()) {
                    yield buffer.readUUID();
                }
                yield null;
            }
            case NBT -> buffer.readNBT();
            case PARTICLE -> buffer.readParticle();
            case POSE -> buffer.readPose();
            case BLOCK_ID -> buffer.getConnection().getMapping().getBlockById(buffer.readVarInt());
            case OPT_VAR_INT -> buffer.readVarInt() - 1;
            case VILLAGER_DATA -> new VillagerData(VillagerData.VillagerTypes.byId(buffer.readVarInt()), VillagerData.VillagerProfessions.byId(buffer.readVarInt(), buffer.getVersionId()), VillagerData.VillagerLevels.byId(buffer.readVarInt()));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public MetaDataHashMap getSets() {
        return sets;
    }

    public boolean onFire() {
        return sets.getBitMask(0, 0x01, false);
    }

    public boolean isSprinting() {
        return sets.getBitMask(0, 0x08, false);
    }

    public boolean isDrinking() {
        return isEating();
    }

    public boolean isEating() {
        if (versionId > 335) { //ToDo
            return false;
        }
        return sets.getBitMask(0, 0x10, false);
    }

    public boolean isBlocking() {
        return isEating();
    }

    public boolean isInvisible() {
        return sets.getBitMask(0, 0x20, false);
    }

    public boolean isGlowing() {
        return sets.getBitMask(0, 0x40, false);
    }

    public boolean isFlyingWithElytra() {
        return sets.getBitMask(0, 0x80, false);
    }

    @Nullable
    public ChatComponent getNameTag() {
        if (versionId <= 110) { //ToDo
            return null;
        }
        if (versionId <= 335) { //ToDo
            return ChatComponent.fromString(sets.getString(2, null));
        }
        return sets.getTextComponent(2, null);
    }

    public boolean isCustomNameVisible() {
        if (versionId <= 110) { //ToDo
            return false;
        }
        return sets.getBoolean(3, false);
    }

    public boolean isSilent() {
        if (versionId <= 110) { //ToDo
            return false;
        }
        return sets.getBoolean(4, false);

    }

    public boolean hasGravity() {
        if (versionId <= 204) { //ToDo
            return true;
        }
        return !sets.getBoolean(5, false);
    }

    public Poses getPose() {
        if (versionId < 461) {
            if (isSneaking()) {
                return Poses.SNEAKING;
            } else if (isSwimming()) {
                return Poses.SWIMMING;
            } else {
                return Poses.STANDING;
            }
        }
        return sets.getPose(6, Poses.STANDING);
    }

    private boolean isSneaking() {
        return sets.getBitMask(0, 0x02, false);
    }

    private boolean isSwimming() {
        if (versionId < 358) {
            return false;
        }
        return sets.getBitMask(0, 0x10, false);
    }

    protected int getLastDataIndex() {
        if (versionId < 57) {
            throw new IllegalArgumentException("EntityMetaData::getLastDataIndex does not work below 1.9!");
        }
        if (versionId == 110) { //ToDo
            return 4;
        }
        if (versionId <= 461) {
            return 5;
        }
        return 6;
    }

    public enum EntityMetaDataValueTypes {
        BYTE(0),
        SHORT(new MapSet[]{new MapSet<>(0, 1), new MapSet<>(57, 1000)}), // got removed in 1.9
        INT(new MapSet[]{new MapSet<>(0, 2), new MapSet<>(57, 1001)}),
        VAR_INT(new MapSet[]{new MapSet<>(57, 1)}),
        FLOAT(new MapSet[]{new MapSet<>(0, 3), new MapSet<>(57, 2)}),
        STRING(new MapSet[]{new MapSet<>(0, 4), new MapSet<>(57, 3)}),
        CHAT(new MapSet[]{new MapSet<>(57, 4)}),
        OPT_CHAT(new MapSet[]{new MapSet<>(346, 5)}), // ToDo: when where the 1.13 changes? in 346?
        SLOT(new MapSet[]{new MapSet<>(0, 5), new MapSet<>(346, 6)}),
        BOOLEAN(new MapSet[]{new MapSet<>(57, 6), new MapSet<>(346, 7)}),
        VECTOR(new MapSet[]{new MapSet<>(0, 6), new MapSet<>(57, 1002)}),
        ROTATION(new MapSet[]{new MapSet<>(44, 7), new MapSet<>(346, 8)}),
        POSITION(new MapSet[]{new MapSet<>(57, 8), new MapSet<>(346, 9)}),
        OPT_POSITION(new MapSet[]{new MapSet<>(57, 9), new MapSet<>(346, 10)}),
        DIRECTION(new MapSet[]{new MapSet<>(57, 10), new MapSet<>(346, 11)}),
        OPT_UUID(new MapSet[]{new MapSet<>(57, 11), new MapSet<>(346, 12)}),
        BLOCK_ID(new MapSet[]{new MapSet<>(67, 12), new MapSet<>(210, -1)}), // ToDo: test: 1.10 blockId replacement
        OPT_BLOCK_ID(new MapSet[]{new MapSet<>(210, 12), new MapSet<>(346, 13)}),
        NBT(new MapSet[]{new MapSet<>(318, 13), new MapSet<>(346, 14)}),
        PARTICLE(new MapSet[]{new MapSet<>(346, 15)}),
        VILLAGER_DATA(new MapSet[]{new MapSet<>(451, 16)}),
        OPT_VAR_INT(new MapSet[]{new MapSet<>(459, 17)}),
        POSE(new MapSet[]{new MapSet<>(461, 18)});

        final VersionValueMap<Integer> valueMap;

        EntityMetaDataValueTypes(MapSet<Integer, Integer>[] values) {
            valueMap = new VersionValueMap<>(values, true);
        }

        EntityMetaDataValueTypes(int id) {
            valueMap = new VersionValueMap<>(id);
        }

        public static EntityMetaDataValueTypes byId(int id, int versionId) {
            for (EntityMetaDataValueTypes types : values()) {
                if (types.getId(versionId) == id) {
                    return types;
                }
            }
            return null;
        }

        public int getId(Integer versionId) {
            Integer ret = valueMap.get(versionId);
            if (ret == null) {
                return -2;
            }
            return ret;
        }
    }

    public static class MetaDataHashMap extends HashMap<Integer, Object> {
        public Poses getPose(int index, Poses defaultValue) {
            return (Poses) get(index, defaultValue);
        }

        public byte getByte(int index, int defaultValue) {
            return (byte) get(index, defaultValue);
        }

        public VillagerData getVillagerData(int index, VillagerData defaultValue) {
            return (VillagerData) get(index, defaultValue);
        }

        public ParticleData getParticle(int index, ParticleData defaultValue) {
            return (ParticleData) get(index, defaultValue);
        }

        public CompoundTag getNBT(int index, CompoundTag defaultValue) {
            return (CompoundTag) get(index, defaultValue);
        }

        public Block getBlock(int index, Block defaultValue) {
            return (Block) get(index, defaultValue);
        }

        public UUID getUUID(int index, UUID defaultValue) {
            return (UUID) get(index, defaultValue);
        }

        public Directions getDirection(int index, Directions defaultValue) {
            return (Directions) get(index, defaultValue);
        }

        public BlockPosition getPosition(int index, BlockPosition defaultValue) {
            return (BlockPosition) get(index, defaultValue);
        }

        public EntityRotation getRotation(int index, EntityRotation defaultValue) {
            return (EntityRotation) get(index, defaultValue);
        }

        public Vector getVector(int index, Vector defaultValue) {
            return (Vector) get(index, defaultValue);
        }

        public boolean getBoolean(int index, boolean defaultValue) {
            Object ret = get(index, defaultValue);
            if (ret instanceof Byte) {
                return (byte) ret == 0x01;
            }
            return (boolean) get(index, defaultValue);
        }

        public boolean getBitMask(int index, int bitMask, boolean defaultValue) {
            return BitByte.isBitMask(getByte(index, (defaultValue ? 1 : 0)), bitMask);
        }

        public Object get(int index, Object defaultValue) {
            if (containsKey(index)) {
                return super.get(index);
            }
            return defaultValue;
        }

        public Slot getSlot(int index, Slot defaultValue) {
            return (Slot) get(index, defaultValue);
        }

        public ChatComponent getTextComponent(int index, ChatComponent defaultValue) {
            return (ChatComponent) get(index, defaultValue);
        }

        public String getString(int index, String defaultValue) {
            return (String) get(index, defaultValue);
        }

        public float getFloat(int index, float defaultValue) {
            return (float) get(index, defaultValue);
        }

        public int getInt(int index, int defaultValue) {
            return (int) get(index, defaultValue);
        }

        public short getShort(int index, int defaultValue) {
            return (short) get(index, defaultValue);
        }
    }

}

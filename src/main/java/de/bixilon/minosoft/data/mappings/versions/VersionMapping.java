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

package de.bixilon.minosoft.data.mappings.versions;

import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.bixilon.minosoft.config.StaticConfiguration;
import de.bixilon.minosoft.data.EntityClassMappings;
import de.bixilon.minosoft.data.Mappings;
import de.bixilon.minosoft.data.entities.EntityInformation;
import de.bixilon.minosoft.data.entities.EntityMetaDataFields;
import de.bixilon.minosoft.data.entities.entities.Entity;
import de.bixilon.minosoft.data.mappings.*;
import de.bixilon.minosoft.data.mappings.blocks.Block;
import de.bixilon.minosoft.data.mappings.blocks.Blocks;
import de.bixilon.minosoft.data.mappings.particle.Particle;
import de.bixilon.minosoft.data.mappings.statistics.Statistic;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.protocol.ProtocolDefinition;
import de.bixilon.minosoft.render.blockModels.BlockModelInterface;
import de.bixilon.minosoft.render.blockModels.BlockModelLoader;
import de.bixilon.minosoft.render.blockModels.face.FaceOrientation;
import de.bixilon.minosoft.util.Pair;
import org.apache.commons.collections.primitives.ArrayFloatList;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;

public class VersionMapping {
    private final HashSet<Mappings> loaded = new HashSet<>();
    private final HashBiMap<Class<? extends Entity>, EntityInformation> entityInformationMap = HashBiMap.create();
    private final HashMap<EntityMetaDataFields, Integer> entityMetaIndexMap = new HashMap<>();
    private final HashMap<String, Pair<String, Integer>> entityMetaIndexOffsetParentMapping = new HashMap<>();
    private final HashBiMap<Integer, Class<? extends Entity>> entityIdClassMap = HashBiMap.create();
    private Version version;
    private VersionMapping parentMapping;
    private HashBiMap<String, Motive> motiveIdentifierMap = HashBiMap.create();
    private HashBiMap<String, Particle> particleIdentifierMap = HashBiMap.create();
    private HashBiMap<String, Statistic> statisticIdentifierMap = HashBiMap.create();
    private HashMap<String, HashBiMap<String, Dimension>> dimensionIdentifierMap = new HashMap<>();
    private HashBiMap<Integer, Item> itemMap = HashBiMap.create();
    private HashBiMap<Integer, Motive> motiveIdMap = HashBiMap.create();
    private HashBiMap<Integer, MobEffect> mobEffectMap = HashBiMap.create();
    private HashBiMap<Integer, Dimension> dimensionMap = HashBiMap.create();
    private HashBiMap<Integer, Block> blockMap = HashBiMap.create();
    private HashBiMap<Integer, BlockId> blockIdMap = HashBiMap.create();
    private HashBiMap<Integer, Enchantment> enchantmentMap = HashBiMap.create();
    private HashBiMap<Integer, Particle> particleIdMap = HashBiMap.create();
    private HashBiMap<Integer, Statistic> statisticIdMap = HashBiMap.create();
    private HashMap<String, HashMap<String, BlockModelInterface>> modelMap = new HashMap<>();
    private Integer blockTextureId; // OpenGL texture id for all block texture

    public VersionMapping(Version version) {
        this.version = version;
        this.parentMapping = null;
    }

    public VersionMapping(Version version, VersionMapping parentMapping) {
        this.version = version;
        this.parentMapping = parentMapping;
    }

    public Motive getMotiveByIdentifier(String identifier) {
        if (parentMapping != null) {
            Motive motive = parentMapping.getMotiveByIdentifier(identifier);
            if (motive != null) {
                return motive;
            }
        }
        return motiveIdentifierMap.get(identifier);
    }

    public Statistic getStatisticByIdentifier(String identifier) {
        if (parentMapping != null) {
            Statistic statistic = parentMapping.getStatisticByIdentifier(identifier);
            if (statistic != null) {
                return statistic;
            }
        }
        return statisticIdentifierMap.get(identifier);
    }

    public Particle getParticleByIdentifier(String identifier) {
        if (parentMapping != null) {
            Particle particle = parentMapping.getParticleByIdentifier(identifier);
            if (particle != null) {
                return particle;
            }
        }
        return particleIdentifierMap.get(identifier);
    }

    public Item getItemById(int versionId) {
        if (!version.isFlattened()) {
            return getItemByLegacy(versionId >>> 16, versionId & 0xFFFF);
        }
        return getItemByIdIgnoreFlattened(versionId);
    }

    private Item getItemByIdIgnoreFlattened(int versionId) {
        if (parentMapping != null) {
            Item item = parentMapping.getItemById(versionId);
            if (item != null) {
                return item;
            }
        }
        return itemMap.get(versionId);
    }

    public Integer getItemId(Item item) {
        if (parentMapping != null) {
            Integer itemId = parentMapping.getItemId(item);
            if (item != null) {
                return itemId;
            }
        }
        return itemMap.inverse().get(item);
    }

    public Motive getMotiveById(int versionId) {
        if (parentMapping != null) {
            Motive motive = parentMapping.getMotiveById(versionId);
            if (motive != null) {
                return motive;
            }
        }
        return motiveIdMap.get(versionId);
    }

    public MobEffect getMobEffectById(int versionId) {
        if (parentMapping != null) {
            MobEffect mobEffect = parentMapping.getMobEffectById(versionId);
            if (mobEffect != null) {
                return mobEffect;
            }
        }
        return mobEffectMap.get(versionId);
    }

    public Dimension getDimensionById(int versionId) {
        if (parentMapping != null) {
            Dimension dimension = parentMapping.getDimensionById(versionId);
            if (dimension != null) {
                return dimension;
            }
        }
        return dimensionMap.get(versionId);
    }

    @Nullable
    public Block getBlockById(int versionId) {
        if (versionId == ProtocolDefinition.NULL_BLOCK_ID) {
            return null;
        }
        if (parentMapping != null) {
            Block block = parentMapping.getBlockById(versionId);
            if (block != null) {
                return block;
            }
        }
        return blockMap.get(versionId);
    }

    public BlockId getBlockIdById(int versionId) {
        if (parentMapping != null) {
            BlockId blockId = parentMapping.getBlockIdById(versionId);
            if (blockId != null) {
                return blockId;
            }
        }
        return blockIdMap.get(versionId);
    }

    public Enchantment getEnchantmentById(int versionId) {
        if (parentMapping != null) {
            Enchantment enchantment = parentMapping.getEnchantmentById(versionId);
            if (enchantment != null) {
                return enchantment;
            }
        }
        return enchantmentMap.get(versionId);
    }

    public Particle getParticleById(int versionId) {
        if (parentMapping != null) {
            Particle particle = parentMapping.getParticleById(versionId);
            if (particle != null) {
                return particle;
            }
        }
        return particleIdMap.get(versionId);
    }

    public Statistic getStatisticById(int versionId) {
        if (parentMapping != null) {
            Statistic statistic = parentMapping.getStatisticById(versionId);
            if (statistic != null) {
                return statistic;
            }
        }
        return statisticIdMap.get(versionId);
    }

    public Integer getIdByEnchantment(Enchantment enchantment) {
        if (parentMapping != null) {
            Integer enchantmentId = parentMapping.getIdByEnchantment(enchantment);
            if (enchantmentId != null) {
                return enchantmentId;
            }
        }
        return enchantmentMap.inverse().get(enchantment);
    }

    public EntityInformation getEntityInformation(Class<? extends Entity> clazz) {
        if (parentMapping != null) {
            EntityInformation information = parentMapping.getEntityInformation(clazz);
            if (information != null) {
                return information;
            }
        }
        return entityInformationMap.get(clazz);
    }

    public Integer getEntityMetaDataIndex(EntityMetaDataFields field) {
        if (parentMapping != null) {
            Integer metaDataIndex = parentMapping.getEntityMetaDataIndex(field);
            if (metaDataIndex != null) {
                return metaDataIndex;
            }
        }
        return entityMetaIndexMap.get(field);
    }

    public Class<? extends Entity> getEntityClassById(int id) {
        if (parentMapping != null) {
            Class<? extends Entity> clazz = parentMapping.getEntityClassById(id);
            if (clazz != null) {
                return clazz;
            }
        }
        return entityIdClassMap.get(id);
    }

    public Dimension getDimensionByIdentifier(String identifier) {
        if (parentMapping != null) {
            Dimension dimension = parentMapping.getDimensionByIdentifier(identifier);
            if (dimension != null) {
                return dimension;
            }
        }
        String[] split = identifier.split(":", 2);
        if (dimensionIdentifierMap.containsKey(split[0]) && dimensionIdentifierMap.get(split[0]).containsKey(split[1])) {
            return dimensionIdentifierMap.get(split[0]).get(split[1]);
        }
        return null;
    }

    public void setDimensions(HashMap<String, HashBiMap<String, Dimension>> dimensions) {
        dimensionIdentifierMap = dimensions;
    }

    public Item getItemByLegacy(int itemId, int metaData) {
        int versionItemId = itemId << 16;
        if (metaData > 0 && metaData < Short.MAX_VALUE) {
            versionItemId |= metaData;
        }
        Item item = getItemByIdIgnoreFlattened(versionItemId);
        if (item == null) {
            // ignore meta data ?
            return getItemByIdIgnoreFlattened(itemId << 16);
        }
        return item;
    }

    public BlockModelInterface getBlockModel(Block block) {
        if (parentMapping != null) {
            BlockModelInterface blockModelInterface = parentMapping.getBlockModel(block);
            if (blockModelInterface != null) {
                return blockModelInterface;
            }
        }
        BlockModelInterface model = modelMap.get(block.getMod()).get(block.getIdentifier());
        if (StaticConfiguration.DEBUG_MODE && model == null) {
            Log.warn(String.format("The block model for the following block could not be found: %s", block));
            return BlockModelInterface.EMPTY;
        }
        return model;
    }

    public boolean isBlockFull(Block block, FaceOrientation orientation) {
        if (block == null) {
            return false;
        }
        return getBlockModel(block).full(block, orientation);
    }

    public boolean isBlockFull(Block block) {
        return block != null;
    }

    public ArrayFloatList prepareBlock(Block block, HashSet<FaceOrientation> facesToDraw, BlockPosition position) {
        return getBlockModel(block).prepare(facesToDraw, position, block);
    }

    public Integer getBlockTextureId() {
        if (parentMapping != null) {
            Integer blockTextureId = parentMapping.getBlockTextureId();
            if (blockTextureId != null) {
                return blockTextureId;
            }
        }
        return blockTextureId;
    }


    public void load(Mappings type, String mod, @Nullable JsonObject data, Version version) {
        switch (type) {
            case REGISTRIES -> {
                if (!version.isFlattened() && version.getVersionId() != ProtocolDefinition.PRE_FLATTENING_VERSION_ID) {
                    // clone all values
                    itemMap = Versions.PRE_FLATTENING_MAPPING.itemMap;
                    enchantmentMap = Versions.PRE_FLATTENING_MAPPING.enchantmentMap;
                    statisticIdMap = Versions.PRE_FLATTENING_MAPPING.statisticIdMap;
                    statisticIdentifierMap = Versions.PRE_FLATTENING_MAPPING.statisticIdentifierMap;
                    blockIdMap = Versions.PRE_FLATTENING_MAPPING.blockIdMap;
                    motiveIdMap = Versions.PRE_FLATTENING_MAPPING.motiveIdMap;
                    motiveIdentifierMap = Versions.PRE_FLATTENING_MAPPING.motiveIdentifierMap;
                    particleIdMap = Versions.PRE_FLATTENING_MAPPING.particleIdMap;
                    particleIdentifierMap = Versions.PRE_FLATTENING_MAPPING.particleIdentifierMap;
                    mobEffectMap = Versions.PRE_FLATTENING_MAPPING.mobEffectMap;
                    dimensionMap = Versions.PRE_FLATTENING_MAPPING.dimensionMap;
                    break;
                }

                if (data == null) {
                    break;
                }

                JsonObject itemJson = data.getAsJsonObject("item").getAsJsonObject("entries");
                for (String identifier : itemJson.keySet()) {
                    Item item = new Item(mod, identifier);
                    JsonObject identifierJSON = itemJson.getAsJsonObject(identifier);
                    int itemId = identifierJSON.get("id").getAsInt();
                    if (version.getVersionId() < ProtocolDefinition.FLATTING_VERSION_ID) {
                        itemId <<= 16;
                        if (identifierJSON.has("meta")) {
                            // old format (with metadata)
                            itemId |= identifierJSON.get("meta").getAsInt();
                        }
                    }
                    itemMap.put(itemId, item);
                }
                JsonObject enchantmentJson = data.getAsJsonObject("enchantment").getAsJsonObject("entries");
                for (String identifier : enchantmentJson.keySet()) {
                    Enchantment enchantment = new Enchantment(mod, identifier);
                    enchantmentMap.put(enchantmentJson.getAsJsonObject(identifier).get("id").getAsInt(), enchantment);
                }
                JsonObject statisticJson = data.getAsJsonObject("custom_stat").getAsJsonObject("entries");
                for (String identifier : statisticJson.keySet()) {
                    Statistic statistic = new Statistic(mod, identifier);
                    if (statisticJson.getAsJsonObject(identifier).has("id")) {
                        statisticIdMap.put(statisticJson.getAsJsonObject(identifier).get("id").getAsInt(), statistic);
                    }
                    statisticIdentifierMap.put(identifier, statistic);
                }
                JsonObject blockIdJson = data.getAsJsonObject("block").getAsJsonObject("entries");
                for (String identifier : blockIdJson.keySet()) {
                    BlockId blockId = new BlockId(mod, identifier);
                    blockIdMap.put(blockIdJson.getAsJsonObject(identifier).get("id").getAsInt(), blockId);
                }
                JsonObject motiveJson = data.getAsJsonObject("motive").getAsJsonObject("entries");
                for (String identifier : motiveJson.keySet()) {
                    Motive motive = new Motive(mod, identifier);
                    if (motiveJson.getAsJsonObject(identifier).has("id")) {
                        motiveIdMap.put(motiveJson.getAsJsonObject(identifier).get("id").getAsInt(), motive);
                    }
                    motiveIdentifierMap.put(identifier, motive);
                }
                JsonObject particleJson = data.getAsJsonObject("particle_type").getAsJsonObject("entries");
                for (String identifier : particleJson.keySet()) {
                    Particle particle = new Particle(mod, identifier);
                    if (particleJson.getAsJsonObject(identifier).has("id")) {
                        particleIdMap.put(particleJson.getAsJsonObject(identifier).get("id").getAsInt(), particle);
                    }
                    particleIdentifierMap.put(identifier, particle);
                }
                JsonObject mobEffectJson = data.getAsJsonObject("mob_effect").getAsJsonObject("entries");
                for (String identifier : mobEffectJson.keySet()) {
                    MobEffect mobEffect = new MobEffect(mod, identifier);
                    mobEffectMap.put(mobEffectJson.getAsJsonObject(identifier).get("id").getAsInt(), mobEffect);
                }
                if (data.has("dimension_type")) {
                    dimensionMap = HashBiMap.create();
                    JsonObject dimensionJson = data.getAsJsonObject("dimension_type").getAsJsonObject("entries");
                    for (String identifier : dimensionJson.keySet()) {
                        Dimension dimension = new Dimension(mod, identifier, dimensionJson.getAsJsonObject(identifier).get("has_skylight").getAsBoolean());
                        dimensionMap.put(dimensionJson.getAsJsonObject(identifier).get("id").getAsInt(), dimension);
                    }
                }
            }
            case BLOCKS -> {
                if (!version.isFlattened() && version.getVersionId() != ProtocolDefinition.PRE_FLATTENING_VERSION_ID) {
                    // clone all values
                    blockMap = Versions.PRE_FLATTENING_MAPPING.blockMap;
                    break;
                }

                if (data == null) {
                    break;
                }
                blockMap = Blocks.load(mod, data, !version.isFlattened());
            }
            case ENTITIES -> {
                if (data == null) {
                    break;
                }
                for (String identifier : data.keySet()) {
                    if (entityMetaIndexOffsetParentMapping.containsKey(identifier)) {
                        continue;
                    }
                    loadEntityMapping(mod, identifier, data);
                }
            }
            case BLOCK_MODELS -> {
                if (!version.isFlattened() && version.getVersionId() != ProtocolDefinition.PRE_FLATTENING_VERSION_ID) {
                    // clone all values
                    modelMap = Versions.PRE_FLATTENING_MAPPING.modelMap;
                    blockTextureId = Versions.PRE_FLATTENING_MAPPING.blockTextureId;
                    break;
                }

                if (data == null) {
                    break;
                }
                Pair<HashMap<String, BlockModelInterface>, Integer> pair = BlockModelLoader.load(mod, data);
                modelMap.put(mod, pair.key);
                blockTextureId = pair.value; // TODO
            }
        }
        loaded.add(type);
    }

    private void loadEntityMapping(String mod, String identifier, JsonObject fullModData) {
        JsonObject data = fullModData.getAsJsonObject(identifier);
        Class<? extends Entity> clazz = EntityClassMappings.getByIdentifier(mod, identifier);
        EntityInformation information = EntityInformation.deserialize(mod, identifier, data);
        if (information != null) {
            // not abstract, has id and attributes
            entityInformationMap.put(clazz, information);

            if (data.has("id")) {
                entityIdClassMap.put(data.get("id").getAsInt(), clazz);
            }
        }
        String parent = null;
        int metaDataIndexOffset = 0;
        if (data.has("extends")) {
            parent = data.get("extends").getAsString();

            // check if parent has been loaded
            Pair<String, Integer> metaParent = entityMetaIndexOffsetParentMapping.get(parent);
            if (metaParent == null) {
                loadEntityMapping(mod, parent, fullModData);
            }

            metaDataIndexOffset += entityMetaIndexOffsetParentMapping.get(parent).value;
        }
        // meta data index
        if (data.has("data")) {
            JsonElement metaDataJson = data.get("data");
            if (metaDataJson instanceof JsonArray metaDataJsonArray) {
                for (JsonElement jsonElement : metaDataJsonArray) {
                    String field = jsonElement.getAsString();
                    entityMetaIndexMap.put(EntityMetaDataFields.valueOf(field), metaDataIndexOffset++);
                }
            } else if (metaDataJson instanceof JsonObject metaDataJsonObject) {
                for (String key : metaDataJsonObject.keySet()) {
                    entityMetaIndexMap.put(EntityMetaDataFields.valueOf(key), metaDataJsonObject.get(key).getAsInt());
                    metaDataIndexOffset++;
                }
            } else {
                throw new RuntimeException("entities.json is invalid");
            }
        }
        entityMetaIndexOffsetParentMapping.put(identifier, new Pair<>(parent, metaDataIndexOffset));
    }

    public void unload() {
        motiveIdentifierMap.clear();
        particleIdentifierMap.clear();
        statisticIdentifierMap.clear();
        itemMap.clear();
        motiveIdMap.clear();
        mobEffectMap.clear();
        dimensionMap.clear();
        blockMap.clear();
        blockIdMap.clear();
        enchantmentMap.clear();
        particleIdMap.clear();
        statisticIdMap.clear();
        entityInformationMap.clear();
        entityMetaIndexMap.clear();
        entityMetaIndexOffsetParentMapping.clear();
        entityIdClassMap.clear();
    }

    public boolean isFullyLoaded() {
        if (loaded.size() == Mappings.values().length) {
            return true;
        }
        for (Mappings mapping : Mappings.values()) {
            if (!loaded.contains(mapping)) {
                return false;
            }
        }
        return true;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public VersionMapping getParentMapping() {
        return parentMapping;
    }

    public void setParentMapping(VersionMapping parentMapping) {
        this.parentMapping = parentMapping;
    }

    public HashSet<Mappings> getAvailableFeatures() {
        return loaded;
    }
}

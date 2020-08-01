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
import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Block;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.BlockProperties;
import de.bixilon.minosoft.game.datatypes.objectLoader.blocks.Blocks;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.render.fullFace.FaceOrientation;

import java.io.IOException;
import java.util.*;

import static de.bixilon.minosoft.util.Util.readJsonFromFile;

public class BlockModelLoader {
    // a list of blocks not drawn by the world renderer
    public static final List<String> ignoredBlocks = new ArrayList<>(Arrays.asList(
            "air", "cave_air", "void_air", "moving_piston", "shrub", "structure_void", "water", "lava",
            //TODO
            "chest", "trapped_chest", "oak_fence"
    ));

    public BlockModelLoader() {
        blockDescriptionMap = new HashMap<>();
        loadModels();
    }

    final HashMap<String, HashMap<String, BlockDescription>> blockDescriptionMap;

    private void loadModels() {
        for (Block block : Blocks.getBlockList()) {
            String mod = block.getMod();
            String identifier = block.getIdentifier();

            if (ignoredBlocks.contains(identifier)) {
                continue;
            }

            if (!blockDescriptionMap.containsKey(mod)) {
                blockDescriptionMap.put(mod, new HashMap<>());
            }

            if (blockDescriptionMap.containsKey(mod + ":" + identifier)) {
                continue;
            }

            if (identifier.equals("silver_glazed_terracotta")) {
                loadModel(mod, "light_gray_glazed_terracotta");
                continue;
            }
            if (identifier.equals("flower_upper_block")) {
                // WHAT EVEN IS THIS BLOCK!?!?!
                continue;
            }
            if (identifier.equals("bubble_column")) {
                // handled with client side "particles"
                continue;
            }
            if (identifier.equals("barrier")) {
                // TODO: display barriers if setting is enabled
                continue;
            }
            if (identifier.equals("end_portal") || identifier.equals("end_gateway")) {
                // TODO: display end portals (the portal itself, not the frames
                // probably with a shader
                continue;
            }
            if (identifier.equals("structure_void")) {
                // is not displayed
                continue;
            }
            if (identifier.contains("infested")) {
                // same block model as the not infested blocks
                continue;
            }
            if (identifier.equals("conduit")) {
                // shown as entity
                continue;
            }
            if (identifier.equals("piston_extension")) {
                // TODO: handle pistons
                continue;
            }
            if (identifier.contains("skull") || identifier.contains("head")) {
                // TODO: handle skulls
                continue;
            }
            if (identifier.equals("water")) {
                // TODO: handle water
                continue;
            }
            if (identifier.equals("lava")) {
                // TODO: handle lava
                continue;
            }
            if (identifier.contains("chest")) {
                // TODO: handle chests (trapped or not)
                continue;
            }
            if (identifier.contains("banner")) {
                // TODO: handle banners
                continue;
            }
            if (identifier.contains("shulker_box")) {
                // TODO: handle shulker_boxes
                continue;
            }
            if (identifier.contains("sign")) {
                // TODO: handle signs
                continue;
            }
            if (identifier.equals("fire")) {
                // TODO: handle fire
                continue;
            }
            if (identifier.contains("tripwire_hook")) {
                loadModel(mod, identifier);
                loadModel(mod, identifier + "_attached");
                loadModel(mod, identifier + "_attached_on");
                loadModel(mod, identifier + "_on");
                continue;
            }
            if (identifier.contains("brewing_stand")) {
                loadModel(mod, identifier);
                for (int i = 0; i < 3; i++) {
                    loadModel(mod, identifier + "_bottle" + i);
                    loadModel(mod, identifier + "_empty" + i);
                }
                continue;
            }
            if (identifier.contains("daylight_detector")) {
                loadModel(mod, identifier);
                loadModel(mod, identifier + "_inverted");
                continue;
            }
            if (identifier.contains("lever")) {
                loadModel(mod, identifier);
                loadModel(mod, identifier + "_on");
                continue;
            }
            if (identifier.contains("comparator")) {
                loadModel(mod, identifier);
                loadModel(mod, identifier + "_on");
                loadModel(mod, identifier + "_on_subtract");
                loadModel(mod, identifier + "_subtract");
                continue;
            }
            if (identifier.contains("trapdoor")) {
                loadModel(mod, identifier + "_bottom");
                loadModel(mod, identifier + "_open");
                loadModel(mod, identifier + "_top");
                continue;
            }
            if (identifier.contains("pane")) {
                loadModel(mod, identifier + "_noside");
                loadModel(mod, identifier + "_noside_alt");
                loadModel(mod, identifier + "_Post");
                loadModel(mod, identifier + "_side");
                loadModel(mod, identifier + "_side_alt");
                continue;
            }
            if (identifier.equals("iron_bars")) {
                loadModel(mod, identifier + "_cap");
                loadModel(mod, identifier + "_cap_alt");
                loadModel(mod, identifier + "_post");
                loadModel(mod, identifier + "_post_ends");
                loadModel(mod, identifier + "_side");
                loadModel(mod, identifier + "_side_alt");
                continue;
            }
            if (identifier.endsWith("bed") && !blockDescriptionMap.containsKey(mod + ":bed")) {
                // TODO: handle beds
                continue;
            }
            if (identifier.equals("vine")) {
                loadModel(mod, identifier + "_1");
                loadModel(mod, identifier + "_1u");
                loadModel(mod, identifier + "_2");
                loadModel(mod, identifier + "_2u");
                loadModel(mod, identifier + "_2_opposite");
                loadModel(mod, identifier + "_2u_opposite");
                loadModel(mod, identifier + "_3");
                loadModel(mod, identifier + "_3u");
                loadModel(mod, identifier + "_4");
                loadModel(mod, identifier + "_4u");
                loadModel(mod, identifier + "_u");
                continue;
            }
            if (identifier.equals("tripwire")) {
                loadModel(mod, identifier + "_attached_n");
                loadModel(mod, identifier + "_attached_ne");
                loadModel(mod, identifier + "_attached_ns");
                loadModel(mod, identifier + "_attached_nse");
                loadModel(mod, identifier + "_attached_nsew");
                continue;
            }
            if (identifier.equals("scaffolding")) {
                loadModel(mod, identifier + "_stable");
                loadModel(mod, identifier + "_unstable");
                continue;
            }
            if (identifier.equals("bell")) {
                loadModel(mod, identifier + "_between_walls");
                loadModel(mod, identifier + "_ceiling");
                loadModel(mod, identifier + "_floor");
                loadModel(mod, identifier + "_wall");
                continue;
            }
            if (identifier.equals("frosted_ice")) {
                loadModel(mod, identifier + "_0");
                loadModel(mod, identifier + "_1");
                loadModel(mod, identifier + "_2");
                loadModel(mod, identifier + "_3");
                continue;
            }
            if (identifier.equals("redstone_wire")) {
                loadModel(mod, "redstone_dust_dot");
                /*
                loadModel(mod, "redstone_dust_side");
                loadModel(mod, "redstone_dust_side_alt");
                loadModel(mod, "redstone_dust_side_alt0");
                loadModel(mod, "redstone_dust_side_alt1");
                loadModel(mod, "redstone_dust_side0");
                loadModel(mod, "redstone_dust_side1");
                loadModel(mod, "redstone_dust_up");
                 */ // throws error, can't find variable
                continue;
            }
            if (identifier.equals("brown_mushroom_stem")) {
                loadModel(mod, "brown_mushroom_block");
                continue;
            }
            if (identifier.equals("red_mushroom_stem")) {
                loadModel(mod, "red_mushroom_block");
                continue;
            }
            if (identifier.equals("snow")) {
                for (int height = 2; height < 16; height += 2) {
                    loadModel(mod, identifier + "_height" + height);
                }
                continue;
            }
            if (identifier.equals("bamboo")) {
                loadModel(mod, identifier + "_large_leaves");
                loadModel(mod, identifier + "_sapling");
                loadModel(mod, identifier + "_small_leaves");
                for (int variation = 1; variation < 5; variation++) {
                    for (int age = 0; age < 2; age++) {
                        loadModel(mod, identifier + variation + "_age" + age);
                    }
                }
                continue;
            }
            if (identifier.equals("wheat")) {
                for (int stage = 0; stage < 8; stage++) {
                    loadModel(mod, identifier + "_stage" + stage);
                }
                continue;
            }
            if (identifier.equals("potatoes") || identifier.equals("carrots") ||
                    identifier.equals("beetroots") || identifier.equals("sweet_berry_bush")) {
                for (int stage = 0; stage < 4; stage++) {
                    loadModel(mod, identifier + "_stage" + stage);
                }
                continue;
            }
            if (identifier.equals("nether_wart")) {
                for (int stage = 0; stage < 3; stage++) {
                    loadModel(mod, identifier + "_stage" + stage);
                }
                continue;
            }
            if (identifier.equals("waterlily")) {
                loadModel(mod, "lily_pad");
                continue;
            }
            if (identifier.equals("nether_brick")) {
                loadModel(mod, "nether_bricks");
                continue;
            }
            if (identifier.equals("quartz_ore")) {
                loadModel(mod, "nether_" + identifier);
                continue;
            }
            if (identifier.contains("end_bricks")) {
                loadModel(mod, "end_stone_bricks");
                continue;
            }
            if (identifier.equals("cocoa")) {
                for (int stage = 0; stage < 3; stage++) {
                    loadModel(mod, identifier + "_stage" + stage);
                }
                continue;
            }
            if (identifier.equals("melon_stem") || identifier.equals("pumpkin_stem")) {
                for (int stage = 0; stage < 8; stage++) {
                    loadModel(mod, identifier + "_stage" + stage);
                }
                continue;
            }
            if (identifier.equals("repeater")) {
                for (int ticks = 1; ticks < 5; ticks++) {
                    loadModel(mod, identifier + "_" + ticks + "tick");
                    loadModel(mod, identifier + "_" + ticks + "tick_locked");
                    loadModel(mod, identifier + "_" + ticks + "tick_on");
                    loadModel(mod, identifier + "_" + ticks + "tick_on_locked");
                }
                continue;
            }
            if (identifier.contains("door")) {
                loadModel(mod, identifier + "_bottom");
                loadModel(mod, identifier + "_bottom_hinge");
                loadModel(mod, identifier + "_top");
                loadModel(mod, identifier + "_top_hinge");
                continue;
            }
            if (identifier.endsWith("wall") || identifier.endsWith("fence")) {
                loadModel(mod, identifier + "_inventory");
                loadModel(mod, identifier + "_post");
                loadModel(mod, identifier + "_side");
                continue;
            }
            if (identifier.contains("large") || identifier.contains("tall") || identifier.equals("sunflower") ||
                    identifier.equals("rose_bush") || identifier.equals("lilac") || identifier.equals("peony")) {
                loadModel(mod, identifier + "_bottom");
                loadModel(mod, identifier + "_top");
                continue;
            }
            if (identifier.equals("nether_portal")) {
                loadModel(mod, identifier + "_ew");
                loadModel(mod, identifier + "_ns");
                continue;
            }
            if (identifier.equals("slime")) {
                loadModel(mod, identifier + "_block");
                continue;
            }
            loadModel(mod, identifier);
        }
        Log.info("finished loading all block descriptions");
    }

    private boolean handleProperties(Block block) {
        return !block.getProperties().contains(BlockProperties.NONE) && block.getProperties().size() != 0;
    }

    private void loadModel(String mod, String identifier) {
        if (blockDescriptionMap.containsKey(mod) && blockDescriptionMap.get(mod).containsKey(identifier)) {
            // a description for that block already exists. checking because Blocks.getBlockList()
            // returns all blocks with all possible combinations (rotation, etc.)
            return;
        }
        try {
            String path = Config.homeDir + "assets/" + mod + "/models/block/" + identifier + ".json";
            JsonObject json = readJsonFromFile(path);
            BlockDescription description = new BlockDescription(json);

            HashMap<String, BlockDescription> modList = blockDescriptionMap.get(mod);
            modList.put(identifier, description);
        } catch (IOException e) {
            Log.debug("could not find block model for block " + mod + ":" + identifier);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(mod + ":" + identifier);
            System.exit(-1);
        }

    }

    public BlockDescription getBlockDescription(Block block) {
        if (ignoredBlocks.contains(block.getIdentifier())) {
            return null;
        }
        if (!blockDescriptionMap.containsKey(block.getMod())) {
            System.out.println(String.format("No mod %s found", block.getMod()));
            //System.exit(-1);
        }
        HashMap<String, BlockDescription> modList = blockDescriptionMap.get(block.getMod());
        if (!modList.containsKey(block.getIdentifier())) {
            System.out.println(String.format("No block %s:%s found", block.getMod(), block.getIdentifier()));
            //System.exit(-1);
        }
        return modList.get(block.getIdentifier());
    }

    public boolean isFull(Block block) {
        if (block == Blocks.nullBlock || block == null) {
            return false;
        }
        BlockDescription description = getBlockDescription(block);
        if (description == null) {
            return false;
        }
        return description.isFull();
    }

    public HashSet<Face> prepare(Block block, HashMap<FaceOrientation, Boolean> adjacentBlocks) {
        BlockDescription description = getBlockDescription(block);
        if (description == null) {
            return new HashSet<>();
        }
        return description.prepare(adjacentBlocks);
    }
}
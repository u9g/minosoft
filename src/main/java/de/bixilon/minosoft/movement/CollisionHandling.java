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

package de.bixilon.minosoft.movement;

import de.bixilon.minosoft.game.datatypes.world.BlockPosition;
import de.bixilon.minosoft.game.datatypes.world.World;
import de.bixilon.minosoft.render.blockModels.BlockModelLoader;
import de.bixilon.minosoft.render.utility.Vec3;

import java.util.ArrayList;
import java.util.List;

import static de.bixilon.minosoft.render.utility.AdditionalMath.betterRound;
import static de.bixilon.minosoft.render.utility.AdditionalMath.valuesBetween;

public class CollisionHandling {
    public static void handleCollisions(World world, PlayerController controller, BlockModelLoader modelLoader, Vec3 deltaPos) {

        groundCollision(world, controller, modelLoader, deltaPos);
        if (!controller.isOnGround()) {
            // if we are stuck in a block, just stay there, otherwise, we would fall through the world
            //topCollision(world, controller, modelLoader);
        }
        xAxisCollision(world, controller, modelLoader, deltaPos);
        zAxisCollision(world, controller, modelLoader, deltaPos);
    }

    private static void zAxisCollision(World world, PlayerController controller, BlockModelLoader modelLoader, Vec3 deltaPos) {
        Vec3 playerPos = controller.getPlayerPos();
        int zVelocityDirection = deltaPos.getZNormalized();
        if (zVelocityDirection == 0) {
            return;
        }

        BlockPosition[] testPositions = getZAxisTestPositions(controller, playerPos);

        for (BlockPosition position : testPositions) {
            if (modelLoader.isFull(world.getBlock(position))) {
                if (zVelocityDirection == 1) {
                    playerPos.z = position.getZ() - controller.getPlayerWidth() * 0.5f;
                } else {
                    playerPos.z = position.getZ() + 1 + controller.getPlayerWidth() * 0.5f;
                }
                controller.playerVelocity.z = 0;
                return;
            }
        }
    }

    private static BlockPosition[] getZAxisTestPositions(PlayerController controller, Vec3 testPos) {
        List<BlockPosition> result = new ArrayList<>();
        float width = controller.getPlayerWidth();

        List<Integer> xPositions = new ArrayList<>();
        for (int zCoordinate : valuesBetween(betterRound(testPos.x + 0.5 * width), betterRound(testPos.x - 0.5 * width))) {
            xPositions.add(zCoordinate);
        }

        List<Integer> yPositions = new ArrayList<>();
        for (int yCoordinate : valuesBetween(betterRound(testPos.y + 1), betterRound(testPos.y + controller.getPlayerHeight()))) {
            yPositions.add(yCoordinate);
        }

        for (int xPos : xPositions) {
            for (int yPos : yPositions) {
                result.add(new BlockPosition(xPos, (short) yPos, betterRound(testPos.z)));
            }
        }

        return result.toArray(new BlockPosition[0]);
    }

    private static void xAxisCollision(World world, PlayerController controller, BlockModelLoader modelLoader, Vec3 deltaPos) {
        Vec3 playerPos = controller.getPlayerPos();
        int xVelocityDirection = deltaPos.getXNormalized();
        if (xVelocityDirection == 0) {
            return;
        }

        BlockPosition[] testPositions = getXAxisTestPositions(controller, playerPos, xVelocityDirection);

        for (BlockPosition position : testPositions) {
            if (modelLoader.isFull(world.getBlock(position))) {
                if (xVelocityDirection == 1) {
                    playerPos.x = position.getX() - controller.getPlayerWidth() * 0.5f;
                } else {
                    playerPos.x = position.getX() + 1 + controller.getPlayerWidth() * 0.5f;
                }
                controller.playerVelocity.x = 0;
                return;
            }
        }
    }

    private static void topCollision(World world, PlayerController controller, BlockModelLoader modelLoader) {
        Vec3 headTop = controller.getPlayerPos().copy();
        headTop.y += controller.getPlayerHeight();

        BlockPosition[] testPositions = getVerticalTestPositions(controller, headTop);

        for (BlockPosition position : testPositions) {
            if (modelLoader.isFull(world.getBlock(position))) {
                controller.playerVelocity.y = 0;
                controller.playerPos.y = position.getY() - controller.getPlayerHeight();
            }
        }
    }

    public static void groundCollision(World world, PlayerController controller, BlockModelLoader modelLoader, Vec3 deltaPos) {
        Vec3 playerPos = controller.playerPos.copy();
        playerPos.y++;
        BlockPosition[] testPositions = getVerticalTestPositions(controller, playerPos);

        for (BlockPosition position : testPositions) {
            if (modelLoader.isFull(world.getBlock(position))) {
                controller.playerVelocity.y = 0;
                controller.playerPos.y = position.getY();
                controller.onGround = true;
            }
        }
    }

    private static BlockPosition[] getVerticalTestPositions(PlayerController controller, Vec3 testPos) {
        List<BlockPosition> result = new ArrayList<>();
        float width = controller.getPlayerWidth();

        List<Integer> xPositions = new ArrayList<>();
        for (int xCoordinate : valuesBetween(betterRound(testPos.x + 0.5 * width), betterRound(testPos.x - 0.5 * width))) {
            xPositions.add(xCoordinate);
        }

        List<Integer> zPositions = new ArrayList<>();
        for (int xCoordinate : valuesBetween(betterRound(testPos.z + 0.5 * width), betterRound(testPos.z - 0.5 * width))) {
            zPositions.add(xCoordinate);
        }

        for (int xPos : xPositions) {
            for (int zPos : zPositions) {
                result.add(new BlockPosition(xPos, (short) testPos.y, zPos));
            }
        }

        return result.toArray(new BlockPosition[0]);
    }

    private static BlockPosition[] getXAxisTestPositions(PlayerController controller, Vec3 testPos, int xVelocityDirection) {
        List<BlockPosition> result = new ArrayList<>();
        float width = controller.getPlayerWidth();

        List<Integer> yPositions = new ArrayList<>();

        for (int yCoordinate : valuesBetween(betterRound(testPos.y + 1), betterRound(testPos.y + controller.getPlayerHeight()))) {
            yPositions.add(yCoordinate);
        }

        List<Integer> zPositions = new ArrayList<>();
        for (int zCoordinate : valuesBetween(betterRound(testPos.z + 0.5 * width), betterRound(testPos.z - 0.5 * width))) {
            zPositions.add(zCoordinate);
        }

        for (int yPos : yPositions) {
            for (int zPos : zPositions) {
                result.add(new BlockPosition(betterRound(testPos.x), (short) yPos, zPos));
            }
        }

        return result.toArray(new BlockPosition[0]);
    }
}

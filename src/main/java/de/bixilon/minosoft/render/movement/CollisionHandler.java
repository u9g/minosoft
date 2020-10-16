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

package de.bixilon.minosoft.render.movement;

import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.data.world.World;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.blockModels.BlockModelLoader;
import de.bixilon.minosoft.render.utility.AdditionalMath;
import de.bixilon.minosoft.render.utility.Vec3;

public class CollisionHandler {
    private final World world;
    private final PlayerController controller;
    private final BlockModelLoader modelLoader;

    public CollisionHandler(PlayerController controller) {
        world = GameWindow.getConnection().getPlayer().getWorld();
        modelLoader = GameWindow.getRenderer().getAssetsLoader().getBlockModelLoader();
        this.controller = controller;
    }

    public void handleCollisions() {
        if (isPositionValid(controller.playerPos)) {
            // the player currently isn't colliding with with anything so the player Position does not have to be adjusted
            return;
        }
        xAxisCollision();
        yAxisCollision();
        zAxisCollision();
        if (!isPositionValid(controller.playerPos)) {
            controller.playerPos.x = controller.oldPos.x;
            controller.playerPos.z = controller.oldPos.z;
        }
    }

    private void xAxisCollision() {
        float deltaX = controller.playerPos.x - controller.oldPos.x;
        if (deltaX == 0) {
            return;
        }
        Vec3 testPos = controller.oldPos.copy().add(deltaX, 0, 0);
        if (isPositionValid(testPos)) {
            return;
        }
        controller.playerPos.x = controller.oldPos.x;
        controller.playerVelocity.x = 0;
    }

    private void yAxisCollision() {
        float deltaY = controller.playerPos.y - controller.oldPos.y;
        if (deltaY == 0) {
            return;
        }
        Vec3 testPos = controller.oldPos.copy().add(0, deltaY, 0);
        if (isPositionValid(testPos)) {
            return;
        }
        controller.playerPos.y = controller.oldPos.y;
        controller.playerVelocity.y = 0;

        if (deltaY < 0) {
            controller.setOnGround(true);
        }
    }

    private void zAxisCollision() {
        float deltaZ = controller.playerPos.z - controller.oldPos.z;
        if (deltaZ == 0) {
            return;
        }
        Vec3 testPos = controller.oldPos.copy().add(0, 0, deltaZ);
        if (isPositionValid(testPos)) {
            return;
        }
        controller.playerPos.z = controller.oldPos.z;
        controller.playerVelocity.z = 0;
    }

    private boolean isPositionValid(Vec3 testPos) {
        float width = controller.getPlayerWidth();

        int[] xPositions = AdditionalMath.valuesBetween(AdditionalMath.betterRound(testPos.x + width), AdditionalMath.betterRound(testPos.x - width));

        int[] yPositions = AdditionalMath.valuesBetween(AdditionalMath.betterRound(testPos.y), AdditionalMath.betterRound(testPos.y + controller.getPlayerHeight()));

        int[] zPositions = AdditionalMath.valuesBetween(AdditionalMath.betterRound(testPos.z + width), AdditionalMath.betterRound(testPos.z - width));

        for (int xPos : xPositions) {
            for (int yPos : yPositions) {
                for (int zPos : zPositions) {
                    BlockPosition pos = new BlockPosition(xPos, (short) yPos, zPos);
                    if (modelLoader.isFull(world.getBlock(pos))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

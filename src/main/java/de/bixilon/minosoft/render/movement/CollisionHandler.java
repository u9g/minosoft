/*
 * Minosoft
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

import de.bixilon.minosoft.data.mappings.versions.VersionMapping;
import de.bixilon.minosoft.data.world.BlockPosition;
import de.bixilon.minosoft.data.world.World;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.utility.AdditionalMath;
import de.bixilon.minosoft.render.utility.Vec3;

public class CollisionHandler {
    private final World world;
    private final PlayerController controller;
    private final VersionMapping versionMapping;

    public CollisionHandler(Connection connection) {
        world = connection.getPlayer().getWorld();
        this.controller = connection.getRenderProperties().getController();
        this.versionMapping = connection.getMapping();
    }

    public void handleCollisions() {
        if (isPositionValid(controller.playerPosition)) {
            // the player currently isn't colliding with with anything so the player Position does not have to be adjusted
            return;
        }
        xAxisCollision();
        yAxisCollision();
        zAxisCollision();
        if (!isPositionValid(controller.playerPosition)) {
            controller.playerPosition.x = controller.oldPosition.x;
            controller.playerPosition.z = controller.oldPosition.z;
        }
    }

    private void xAxisCollision() {
        double deltaX = controller.playerPosition.x - controller.oldPosition.x;
        if (deltaX == 0) {
            return;
        }
        Vec3 testPos = controller.oldPosition.copy().add(deltaX, 0, 0);
        if (isPositionValid(testPos)) {
            return;
        }
        controller.playerPosition.x = controller.oldPosition.x;
        controller.playerVelocity.x = 0;
    }

    private void yAxisCollision() {
        double deltaY = controller.playerPosition.y - controller.oldPosition.y;
        if (deltaY == 0) {
            return;
        }
        Vec3 testPos = controller.oldPosition.copy().add(0, deltaY, 0);
        if (isPositionValid(testPos)) {
            return;
        }
        controller.playerPosition.y = controller.oldPosition.y;
        controller.playerVelocity.y = 0;

        if (deltaY < 0) {
            controller.setOnGround(true);
        }
    }

    private void zAxisCollision() {
        double deltaZ = controller.playerPosition.z - controller.oldPosition.z;
        if (deltaZ == 0) {
            return;
        }
        Vec3 testPos = controller.oldPosition.copy().add(0, 0, deltaZ);
        if (isPositionValid(testPos)) {
            return;
        }
        controller.playerPosition.z = controller.oldPosition.z;
        controller.playerVelocity.z = 0;
    }

    private boolean isPositionValid(Vec3 testPosition) {
        float width = controller.getPlayerWidth();

        int[] xPositions = AdditionalMath.valuesBetween(AdditionalMath.betterRound(testPosition.x + width), AdditionalMath.betterRound(testPosition.x - width));
        int[] yPositions = AdditionalMath.valuesBetween(AdditionalMath.betterRound(testPosition.y), AdditionalMath.betterRound(testPosition.y + controller.getPlayerHeight()));
        int[] zPositions = AdditionalMath.valuesBetween(AdditionalMath.betterRound(testPosition.z + width), AdditionalMath.betterRound(testPosition.z - width));

        for (int xPos : xPositions) {
            for (int yPos : yPositions) {
                for (int zPos : zPositions) {
                    BlockPosition pos = new BlockPosition(xPos, (short) yPos, zPos);
                    if (versionMapping.isBlockFull(world.getBlock(pos))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

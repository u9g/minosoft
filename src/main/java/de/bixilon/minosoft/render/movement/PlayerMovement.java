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

import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.utility.Vec3;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerMovement {
    private static final float FLY_SPEED = 0.2f;
    private static final Vec3 CAMERA_UP = new Vec3(0f, 1f, 0f);
    private final Connection connection;

    private Vec3 cameraFront;
    private Vec3 playerPosition;

    public PlayerMovement(Connection connection) {
        this.connection = connection;
    }

    private void processInput(float deltaTime) {
        float cameraSpeed = FLY_SPEED / deltaTime;

        if (glfwGetKey(GameWindow.getOpenGLWindow().getWindowId(), GLFW_KEY_W) == GLFW_PRESS) {
            playerPosition.add(Vec3.mul(cameraFront, -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(GameWindow.getOpenGLWindow().getWindowId(), GLFW_KEY_S) == GLFW_PRESS) {
            playerPosition.add(Vec3.mul(cameraFront, cameraSpeed * deltaTime));
        }
        if (glfwGetKey(GameWindow.getOpenGLWindow().getWindowId(), GLFW_KEY_A) == GLFW_PRESS) {
            playerPosition.add(Vec3.mul(Vec3.cross(CAMERA_UP, cameraFront), -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(GameWindow.getOpenGLWindow().getWindowId(), GLFW_KEY_D) == GLFW_PRESS) {
            playerPosition.add(Vec3.mul(Vec3.cross(CAMERA_UP, cameraFront), cameraSpeed * deltaTime));
        }

        if (glfwGetKey(GameWindow.getOpenGLWindow().getWindowId(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            if (!connection.getRenderProperties().getController().isGravityEnabled()) {
                playerPosition.add(0, -cameraSpeed * deltaTime, 0);
            }
        }
        if (glfwGetKey(GameWindow.getOpenGLWindow().getWindowId(), GLFW_KEY_SPACE) == GLFW_PRESS) {
            if (!connection.getRenderProperties().getController().isGravityEnabled()) {
                playerPosition.add(0, cameraSpeed * deltaTime, 0);
            }
            if (connection.getRenderProperties().getController().isOnGround()) {
                connection.getRenderProperties().getController().jump();
            }
        }
    }

    public void loop(float deltaTime) {
        cameraFront = connection.getRenderProperties().getController().getCameraMovement().getCameraFront();
        playerPosition = connection.getRenderProperties().getController().getPlayerPosition();
        processInput(deltaTime);
    }

    public void setPlayerPosition(Vec3 playerPosition) {
        this.playerPosition = playerPosition;
    }
}

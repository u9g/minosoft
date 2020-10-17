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

package de.bixilon.minosoft.render.movement;

import de.bixilon.minosoft.render.GameWindow;
import de.bixilon.minosoft.render.utility.Vec3;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerMovement {
    private static final float FLY_SPEED = 0.2f;
    private static final Vec3 CAMERA_UP = new Vec3(0f, 1f, 0f);

    private final long window;
    private Vec3 cameraFront;
    private Vec3 playerPos;

    public PlayerMovement(long window) {
        this.window = window;
    }

    private void processInput(float deltaTime) {
        float cameraSpeed = FLY_SPEED / deltaTime;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            playerPos.add(Vec3.mul(cameraFront, -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            playerPos.add(Vec3.mul(cameraFront, cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            playerPos.add(Vec3.mul(Vec3.cross(CAMERA_UP, cameraFront), -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            playerPos.add(Vec3.mul(Vec3.cross(CAMERA_UP, cameraFront), cameraSpeed * deltaTime));
        }

        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            if (!GameWindow.getPlayerController().isGravityEnabled()) {
                playerPos.add(0, -cameraSpeed * deltaTime, 0);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            if (!GameWindow.getPlayerController().isGravityEnabled()) {
                playerPos.add(0, cameraSpeed * deltaTime, 0);
            }
            if (GameWindow.getPlayerController().isOnGround()) {
                GameWindow.getPlayerController().jump();
            }
        }
    }

    public void loop(float deltaTime) {
        cameraFront = GameWindow.getPlayerController().getCameraMovement().getCameraFront();
        playerPos = GameWindow.getPlayerController().getPlayerPos();
        processInput(deltaTime);
    }
}
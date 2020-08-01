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

/*
 * A simple camera controller to fly around the scene
 */

package de.bixilon.minosoft.render.movement;

import de.bixilon.minosoft.render.MainWindow;
import de.bixilon.minosoft.render.utility.Vec3;

import static de.bixilon.minosoft.render.utility.Vec3.cross;
import static de.bixilon.minosoft.render.utility.Vec3.mul;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerMovement {
    private final long window;
    Vec3 cameraFront;
    Vec3 cameraUp = new Vec3(0f, 1f, 0f);

    float flySpeed = 0.1f;

    Vec3 playerPos;

    public PlayerMovement(long window) {
        this.window = window;
    }

    private void processInput(float deltaTime) {
        float cameraSpeed = flySpeed / deltaTime;

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            MainWindow.pause();
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            playerPos.add(mul(cameraFront, -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            playerPos.add(mul(cameraFront, cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            playerPos.add(mul(cross(cameraUp, cameraFront), -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            playerPos.add(mul(cross(cameraUp, cameraFront), cameraSpeed * deltaTime));
        }

        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            if (!MainWindow.getPlayerController().isEnableGravity()) {
                playerPos.add(0, -cameraSpeed * deltaTime, 0);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            if (!MainWindow.getPlayerController().isEnableGravity()) {
                playerPos.add(0, cameraSpeed * deltaTime, 0);
            }
            if (MainWindow.getPlayerController().isOnGround()) {
                MainWindow.getPlayerController().jump();
            }
        }
    }

    public void loop(float deltaTime) {
        cameraFront = MainWindow.getPlayerController().getCameraMovement().getCameraFront();
        playerPos = MainWindow.getPlayerController().getPlayerPos();
        processInput(deltaTime);
    }
}
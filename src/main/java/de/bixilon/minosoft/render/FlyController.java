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

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.render.utility.Vec3;

import static de.bixilon.minosoft.render.utility.Vec3.*;
import static java.lang.StrictMath.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class FlyController {
    private final long window;
    Vec3 cameraPos = new Vec3(0.0f, 0.0f, 0.0f);
    Vec3 cameraFront = new Vec3(0.0f, 0.0f, -1.0f);
    Vec3 cameraUp = new Vec3(0.0f, 1.0f, 0.0f);
    float lastX;
    float lastY;

    float yaw = 0f;
    float pitch = 0f;

    float flySpeed = 0.1f;
    private boolean firstMouse = true;

    public FlyController(long window) {
        this.window = window;
    }

    private void processInput(long window, float deltaTime) {
        float cameraSpeed = flySpeed / deltaTime;

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            MainWindow.pause();
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            cameraPos.add(mul(cameraFront, cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            cameraPos.add(mul(cameraFront, -cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            cameraPos.add(mul(cross(cameraUp, cameraFront), cameraSpeed * deltaTime));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            cameraPos.add(mul(cross(cameraUp, cameraFront), -cameraSpeed * deltaTime));
        }

        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cameraPos.add(0, cameraSpeed * deltaTime, 0);
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            cameraPos.add(0, -cameraSpeed * deltaTime, 0);
        }
    }

    public void loop(float deltaTime) {
        processInput(window, deltaTime);
        //glLoadIdentity();
        //glTranslatef(cameraPos.x, cameraPos.y, cameraPos.z);
        //glPushMatrix();
        //glTranslatef(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        glRotatef(pitch, 1, 0, 0);
        glRotatef(yaw, 0, 1, 0);
        glTranslatef(cameraPos.x, cameraPos.y, cameraPos.z);
        glPushMatrix();
    }

    public void mouseCallback(long l, double xPos, double yPos) {
        if (firstMouse) {
            lastX = (float) xPos;
            lastY = (float) yPos;
            firstMouse = false;
        }

        float xoffset = (float) (xPos - lastX);
        float yoffset = (float) (lastY - yPos); // reversed since y-coordinates go from bottom to top
        lastX = (float) xPos;
        lastY = (float) yPos;

        float sensitivity = 0.1f; // change this value to your liking
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        Vec3 front = new Vec3();
        front.x = (float) -(sin(toRadians(yaw)) * cos(toRadians(pitch)));
        front.y = 0;//(float) sin(toRadians(pitch));
        front.z = (float) ((cos(toRadians(yaw))) * cos(toRadians(pitch)));
        cameraFront = normalize(front);
    }
}
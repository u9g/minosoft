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

import static de.bixilon.minosoft.render.utility.Vec3.normalize;
import static java.lang.StrictMath.*;
import static org.lwjgl.opengl.GL11.glRotatef;

public class CameraMovement {
    private static final float sensitivity = 0.1f;
    private Vec3 cameraFront = new Vec3(0.0f, 0.0f, -1.0f);

    private boolean firstMouse = false;
    private float lastX;
    private float lastY;
    private float yaw;
    private float pitch;

    public void mouseCallback(long l, double xPos, double yPos) {
        // variable l is unused but always given by openGL so it is needed in the method signature
        if (GameWindow.paused) {
            return;
        }

        if (firstMouse) {
            lastX = (float) xPos;
            lastY = (float) yPos;
            firstMouse = false;
        }

        float xoffset = (float) (xPos - lastX);
        float yoffset = (float) (lastY - yPos); // reversed since y-coordinates go from bottom to top
        lastX = (float) xPos;
        lastY = (float) yPos;

        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f) {
            pitch = 89.0f;
        }
        if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        Vec3 front = new Vec3();
        front.x = (float) -(sin(toRadians(yaw)) * cos(toRadians(pitch)));
        front.y = 0;
        front.z = (float) ((cos(toRadians(yaw))) * cos(toRadians(pitch)));
        cameraFront = normalize(front);
    }

    public void loop() {
        glRotatef(pitch, 1, 0, 0);
        glRotatef(yaw, 0, 1, 0);
    }

    public void setRotation(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Vec3 getCameraFront() {
        return cameraFront;
    }
}

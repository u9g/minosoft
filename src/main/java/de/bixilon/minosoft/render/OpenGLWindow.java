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

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.protocol.network.Connection;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLWindow {
    private static final float fovY = 45f; // degrees
    private static final boolean fullscreenEnabled = false;
    private static int width = 800;
    private static int height = 800;
    private boolean escDown = false;
    private long windowId;
    private double mouseX;
    private double mouseY;
    private float lastFrame;

    public static void gluPerspective(float fovY, float aspect, float near, float far) {
        float bottom = -near * (float) Math.tan(fovY / 2);
        float top = -bottom;
        float left = aspect * bottom;
        float right = -left;
        glFrustum(left, right, bottom, top, near, far);
    }

    public static void setupPerspective() {
        gluPerspective(fovY, width / (float) height, 0.1f, 500f);
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        if (fullscreenEnabled) {
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            assert mode != null;
            width = mode.width();
            height = mode.height();
        }

        windowId = glfwCreateWindow(width, height, "Minosoft Game Window", NULL, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth  = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert videoMode != null;
            glfwSetWindowPos(windowId, (videoMode.width() - pWidth.get(0)) / 2, (videoMode.height() - pHeight.get(0)) / 2);
        }
        // the context belongs to this ond only this thread. It enables displaying anything
        glfwMakeContextCurrent(windowId);
        // cursor Callback
        glfwSetCursorPosCallback(windowId, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long windowId, double xPos, double yPos) {
                mouseX = xPos;
                mouseY = yPos;
            }
        });

        // resizeable window
        GLFW.glfwSetFramebufferSizeCallback(windowId, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                OpenGLWindow.width = width;
                OpenGLWindow.height = height;
                glViewport(0,0,width, height);
            }
        });
        // Enable v-sync
        glfwSwapInterval(1);

        createCapabilities();

        // background color
        glClearColor(.2f, .2f, .6f, 1f);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // no overlapping textures
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0f);

        // if one face is behind another, don't show it
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        // mouse is captured
        mouseEnable(false);

        // enable usage of textures
        glEnable(GL_TEXTURE_2D);
    }

    public long getWindowId() {
        return windowId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public float loop() {
        if (glfwGetKey(windowId, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            if (!escDown) {
                GameWindow.pause();
                escDown = true;
            }
        } else {
            escDown = false;
        }
        // clear the screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float currentFrame = (float) glfwGetTime();
        float deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        setupPerspective();

        glfwSetWindowTitle(windowId, String.format("Minosoft game window (%s FPS)", 1/deltaTime));
        return deltaTime;
    }

    public void start() {
        glfwShowWindow(windowId);
    }

    public void setCurrentConnection(Connection connection) {
        // use the mouse for the new connection and not for the old one
        glfwSetCursorPosCallback(windowId, connection.getRenderProperties().getController().getCameraMovement()::mouseCallback);
    }

    public void mouseEnable(boolean mouse) {
        if (mouse) {
            glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            return;
        }
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
}

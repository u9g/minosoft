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

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.movement.PlayerController;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameWindow {
    private static final float FOV_Y = 45f;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final boolean FULLSCREEN = false;
    public static boolean paused = false;
    private static OpenGLWindow openGLWindow;
    private static WorldRenderer renderer;
    private static Connection connection;
    private static PlayerController playerController;

    public static void prepare() {
        new Thread(() -> {
            Log.debug("Starting render preparations...");
            openGLWindow = new OpenGLWindow(WIDTH, HEIGHT, FULLSCREEN);
            playerController = new PlayerController(openGLWindow.getWindowId());
            openGLWindow.init();
            renderer = new WorldRenderer();
            Log.debug("Render preparations done.");
            try {
                while (connection == null) {
                    Thread.sleep(100);
                }
                openGLWindow.start();
                Log.debug("Render window preparations done.");
                mainLoop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "GameWindow").start();
    }

    private static void mainLoop() {
        while (!glfwWindowShouldClose(openGLWindow.getWindowId())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glfwPollEvents();
            float deltaTime = openGLWindow.loop();

            if (paused) {
                glColor4f(0.5f, 0.5f, 0.5f, 1f);
            } else {
                glColor4f(1f, 1f, 1f, 1f);
            }

            OpenGLWindow.gluPerspective(FOV_Y, WIDTH / (float) HEIGHT, 0.1f, 500f);
            playerController.loop(deltaTime);
            renderer.draw();
            glPopMatrix();
            glfwSwapBuffers(openGLWindow.getWindowId());
        }
    }

    public static WorldRenderer getRenderer() {
        return renderer;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static PlayerController getPlayerController() {
        return playerController;
    }

    public static void start(Connection connection) {
        if (GameWindow.connection != null) {
            return;
        }
        GameWindow.connection = connection;
        playerController = new PlayerController(openGLWindow.getWindowId());
        renderer.startChunkPreparation(connection);
    }

    public static void pause() {
        paused = !paused;
        openGLWindow.mouseEnable(paused);
    }
}

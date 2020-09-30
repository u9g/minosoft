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

package de.bixilon.minosoft.render;

import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.movement.PlayerController;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameWindow {
    private static final float FOVY = 45f;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final boolean FULLSCREEN = false;
    private static OpenGLWindow openGLWindow;
    private static WorldRenderer renderer;
    private static Connection connection;
    private static PlayerController playerController;

    private static boolean running = false;
    public static boolean paused = false;

    public static void prepare() {
        Thread guiLoaderThread = new Thread(() -> {
            openGLWindow = new OpenGLWindow(WIDTH, HEIGHT, FULLSCREEN);
            playerController = new PlayerController(openGLWindow.getWindow());
            openGLWindow.init();
            renderer = new WorldRenderer();
            renderer.init();
            Log.info("Finished loading game Assets");
            try {
                while (! running) {
                    Thread.sleep(100);
                }
                openGLWindow.start();
                mainLoop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        guiLoaderThread.setName("GameWindow");
        guiLoaderThread.start();
    }

    private static void mainLoop() {
        while (!glfwWindowShouldClose(openGLWindow.getWindow())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glfwPollEvents();
            float deltaTime = openGLWindow.loop();

            if (paused) {
                glColor4f(0.5f, 0.5f, 0.5f, 1f);
            } else {
                glColor4f(1f, 1f, 1f, 1f);
            }

            OpenGLWindow.gluPerspective(FOVY, (float) WIDTH / (float) HEIGHT, 0.1f, 500f);
            playerController.loop(deltaTime);
            if (connection != null && connection.getPlayer().isSpawnConfirmed()) {
                renderer.draw();
            }
            glPopMatrix();
            glfwSwapBuffers(openGLWindow.getWindow());
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
        if (running) {
            return;
        }
        GameWindow.connection = connection;
        running = true;
        playerController = new PlayerController(openGLWindow.getWindow());
    }

    public static void pause() {
        paused = ! paused;
        openGLWindow.mouseEnable(paused);
    }
}

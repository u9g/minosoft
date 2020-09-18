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

import de.bixilon.minosoft.protocol.network.Connection;
import de.bixilon.minosoft.render.movement.PlayerController;
import de.bixilon.minosoft.render.utility.RenderMode;

import static de.bixilon.minosoft.render.utility.RenderMode.MAIN_MENU;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameWindow {

    private static final float FOVY = 45f;
    static int WIDTH = 800;
    static int HEIGHT = 800;
    static boolean FULLSCREEN = false;
    static OpenGLWindow openGLWindow;
    static WorldRenderer renderer;
    static Connection connection;
    private static PlayerController playerController;

    static boolean running = false;

    static Thread renderLoop;

    public static void prepare() {
        Thread guiLoaderThread = new Thread(() -> {
            openGLWindow = new OpenGLWindow(WIDTH, HEIGHT, FULLSCREEN);
            openGLWindow.init();
            renderer = new WorldRenderer();
            renderer.init();

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

        renderLoop = new Thread(() -> {
            openGLWindow.start();
            mainLoop();
        });
    }

    private static void mainLoop() {
        while (!glfwWindowShouldClose(openGLWindow.getWindow())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glfwPollEvents();
            float deltaTime = openGLWindow.loop();

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
    }
}

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
import de.bixilon.minosoft.render.MainMenu.MainMenu;
import de.bixilon.minosoft.render.movement.PlayerController;
import de.bixilon.minosoft.render.utility.RenderMode;

import static de.bixilon.minosoft.render.utility.RenderMode.MAIN_MENU;
import static de.bixilon.minosoft.render.utility.RenderMode.PLAY;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class MainWindow {

    private static final float FOVY = 45f;
    static int WIDTH = 800;
    static int HEIGHT = 800;
    static boolean FULLSCREEN = false;
    static OpenGLWindow openGLWindow;
    static RenderMode renderMode = MAIN_MENU;
    static MainMenu mainMenu;
    static WorldRenderer renderer;
    static Connection connection;
    private static PlayerController playerController;

    public static void start(Connection serverConnection) {
        Thread guiThread = new Thread(() -> {
            connection = serverConnection;
            openGLWindow = new OpenGLWindow(WIDTH, HEIGHT, FULLSCREEN);
            openGLWindow.init();
            renderer = new WorldRenderer();
            renderer.init();
            playerController = new PlayerController(openGLWindow.getWindow());
            renderMode = MAIN_MENU;
            mainMenu = new MainMenu(openGLWindow.getWidth(), openGLWindow.getHeight());
            mainLoop();
            System.exit(0);
        });
        guiThread.start();
    }

    private static void mainLoop() {
        while (!glfwWindowShouldClose(openGLWindow.getWindow())) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glfwPollEvents();
            float deltaTime = openGLWindow.loop();
            float mouseX = (float) openGLWindow.getMouseX();
            float mouseY = (float) openGLWindow.getMouseY();
            switch (renderMode) {
                case MAIN_MENU:
                    glOrtho(0.f, openGLWindow.getWidth(), openGLWindow.getHeight(), 0.f, 0.f, 1.f);
                    mainMenu.draw(mouseX, mouseY);
                    break;
                case PLAY:
                    OpenGLWindow.gluPerspective(FOVY, (float) WIDTH / (float) HEIGHT, 0.1f, 500f);
                    playerController.loop(deltaTime);
                    renderer.draw();
                    break;
            }

            glPopMatrix();
            glfwSwapBuffers(openGLWindow.getWindow());
        }
    }

    public static OpenGLWindow getOpenGLWindow() {
        return openGLWindow;
    }

    public static WorldRenderer getRenderer() {
        return renderer;
    }

    public static void play() {
        renderMode = PLAY;
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glfwSetCursorPosCallback(openGLWindow.getWindow(), playerController.getCameraMovement()::mouseCallback);
        glfwSetInputMode(openGLWindow.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glEnable(GL_TEXTURE_2D);
        connection.connect();
    }

    public static void pause() {
        renderMode = MAIN_MENU;
        glfwSetInputMode(openGLWindow.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        assert connection != null;
        connection.disconnect();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static PlayerController getPlayerController() {
        return playerController;
    }
}

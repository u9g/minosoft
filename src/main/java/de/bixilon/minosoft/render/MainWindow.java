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
import de.bixilon.minosoft.render.utility.RenderMode;

import static de.bixilon.minosoft.render.utility.RenderMode.MAIN_MENU;
import static de.bixilon.minosoft.render.utility.RenderMode.PLAY;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glOrtho;

public class MainWindow {

    static int WIDTH = 800;
    static int HEIGHT = 800;
    static boolean FULLSCREEN = false;
    static OpenGLWindow openGLWindow;
    static RenderMode renderMode = MAIN_MENU;
    static MainMenu mainMenu;
    static WorldRenderer renderer;
    static Connection connection;
    static FlyController flyController;
    static private float mouseX;
    static private float mouseY;
    private static float lastFrame;

    public static void start(Connection serverConnection) {
        Thread guiThread = new Thread(() -> {
            connection = serverConnection;
            openGLWindow = new OpenGLWindow(WIDTH, HEIGHT, FULLSCREEN);
            openGLWindow.init();
            renderer = new WorldRenderer();
            flyController = new FlyController(openGLWindow.getWindow());
            renderMode = MAIN_MENU;
            mainMenu = new MainMenu(openGLWindow.getWidth(), openGLWindow.getHeight());
            runWindow();
        });
        guiThread.start();
    }

    private static void runWindow() {
        while (!glfwWindowShouldClose(openGLWindow.getWindow())) {
            float deltaTime = openGLWindow.loop();
            mouseX = (float) openGLWindow.getMouseX();
            mouseY = (float) openGLWindow.getMouseY();
            switch (renderMode) {
                case MAIN_MENU:
                    if (glfwGetKey(openGLWindow.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS)
                        close();
                    glOrtho(0.f, openGLWindow.getWidth(), openGLWindow.getHeight(), 0.f, 0.f, 1.f);
                    mainMenu.draw(mouseX, mouseY);
                    break;
                case PLAY:
                    flyController.loop(deltaTime);
                    renderer.draw();
                    break;
            }
            glLoadIdentity();
            glfwPollEvents();

            glfwSwapBuffers(openGLWindow.getWindow());
        }
    }

    public static RenderMode getRenderMode() {
        return renderMode;
    }

    public static void setRenderMode(RenderMode mode) {
        renderMode = mode;
    }

    public static OpenGLWindow getOpenGLWindow() {
        return openGLWindow;
    }

    public static WorldRenderer getRenderer() {
        return renderer;
    }

    public static void play() {
        renderMode = PLAY;
        glfwSetInputMode(openGLWindow.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        OpenGLWindow.gluPerspective(45, WIDTH / HEIGHT, 0.1f, 100f);
        connection.connect();
    }

    public static void pause() {
        renderMode = MAIN_MENU;
        glfwSetInputMode(openGLWindow.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        connection.disconnect();
    }

    public static void close() {
        System.exit(1);
    }
}

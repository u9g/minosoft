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

import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.protocol.network.Connection;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameWindow {
    public static boolean paused = false;
    private static OpenGLWindow openGLWindow;
    private static final Object waiter = new Object();
    private static final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static Connection currentConnection;
    private static boolean running;

    public static void prepare() {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            openGLWindow = new OpenGLWindow();
            openGLWindow.init();
            Log.info("Finished loading block models");
            latch.countDown();
            mainLoop();
        }, "GameWindow").start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mainLoop() {
        while (!running) {
            try {
                queue.take().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        while (!glfwWindowShouldClose(openGLWindow.getWindowId())) {
            if (queue.size() > 0) {
                try {
                    queue.take().run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glfwPollEvents();
            if (paused) {
                glColor4f(0.5f, 0.5f, 0.5f, 1f);
            } else {
                glColor4f(1f, 1f, 1f, 1f);
            }
            float deltaTime = openGLWindow.loop();
            currentConnection.getRenderProperties().getController().loop(deltaTime);
            currentConnection.getRenderProperties().getRenderer().draw();
            glPopMatrix();
            glfwSwapBuffers(openGLWindow.getWindowId());
        }
        // The window was closed, close everything
        currentConnection.disconnect();
        currentConnection = null;
        running = false;
        openGLWindow.close();
    }

    public static void pause() {
        paused = !paused;
        openGLWindow.mouseEnable(paused);
    }

    public static OpenGLWindow getOpenGLWindow() {
        return openGLWindow;
    }

    public static void setCurrentConnection(Connection currentConnection) {
        if (GameWindow.currentConnection == null) {
            queue.add(() -> {
                running = true;
                openGLWindow.start();
            });
        }
        openGLWindow.setCurrentConnection(currentConnection);
        GameWindow.currentConnection = currentConnection;
        synchronized (waiter) {
            waiter.notifyAll();
        }
    }

    public static void queue(Runnable runnable) {
        queue.add(runnable);
    }
}

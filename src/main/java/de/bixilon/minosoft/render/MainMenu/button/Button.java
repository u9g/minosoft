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

package de.bixilon.minosoft.render.MainMenu.button;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Button {
    private final int x, y;

    private final int width, height;

    private final String text;
    private final long window;
    private final ButtonAction action;
    public boolean pressed;
    private float r, g, b; // these are the values we store and adapt if the mouse is located over the button
    private float trueR, trueG, trueB; //these are the values we will actually display

    public Button(int x, int y, int width, int height, String text, float r, float g, float b,
                  long window, ButtonAction action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        trueR = this.r = r;
        trueG = this.g = g;
        trueB = this.b = b;
        this.window = window;
        this.action = action;
    }

    private void setTrueColor(float r, float g, float b) {
        trueR = r;
        trueG = g;
        trueB = b;
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public boolean isOn(int x, int y) {
        return x >= this.x && x <= this.x + this.width &&
                y >= this.y && y <= this.y + this.height;
    }

    public void draw() {
        glBegin(GL_QUADS);
        //glColor3f(trueR, trueG, trueB);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
        glFlush();
    }

    public void loop(float mouseX, float mouseY) {
        if (isOn((int) mouseX, (int) mouseY)) {
            setTrueColor(r, g, b);
            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS && !pressed) {
                pressed = true;
            } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_RELEASE && pressed) {
                pressed = false;
                action.onClick();
            }
        } else if ((!isOn((int) mouseX, (int) mouseY)) && pressed) {
            setTrueColor(r * 0.9f, g * 0.9f, b * 0.9f);
            pressed = false;
        }
        draw();
    }
}

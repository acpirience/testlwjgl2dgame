package io;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private long window;

    public Input(long window) {
        this.window = window;
    }

    public boolean isKeyDown(int key) {
           return (glfwGetKey(window, key) == 1);
    }

    public boolean isMouseDown(int button) {
        return (glfwGetMouseButton(window, button) == 1);
    }

}

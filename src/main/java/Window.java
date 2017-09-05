import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private long window;

    private int width, height;
    private boolean fullscreen;

    private Input input;

    public static void setCallbacks() {
        // to be deleted ?
    }

    public Window() {
        setSize(640,480);
        setFullscreen(false);
    }

    public void createWindow(String title) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.out).set();

        // Get the resolution of the primary monitor
        GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (fullscreen) {
            // put windows size equals to resolution size
            setSize(vid.width(), vid.height());
        }

        window = glfwCreateWindow(
                width,
                height,
                title,
                fullscreen ? glfwGetPrimaryMonitor() : 0,
                0);

        if (window == 0)
            throw new IllegalStateException("Failed to create window!");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation

        if (!fullscreen) {
            // Center the window
            glfwSetWindowPos(
                    window,
                    (vid.width() - width) / 2,
                    (vid.height() - height) / 2
            );

            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        }
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);

        input = new Input(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public long getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }

}


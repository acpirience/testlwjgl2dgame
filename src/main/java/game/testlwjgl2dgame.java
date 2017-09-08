package game;

import io.*;
import render.*;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import world.Tile;
import world.TileRenderer;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class testlwjgl2dgame {

    private Window window;

    private void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        window.destroy();
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        window = new Window();
        window.setSize(640,480);
//        window.setFullscreen(true);
        window.createWindow("Game");


        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window.getWindow(), (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Camera camera = new Camera(window.getWidth(), window.getHeight());
        glEnable(GL_TEXTURE_2D);

        TileRenderer tiles = new TileRenderer();

        Shader shader = new Shader("shader");

        World world = new World();

        world.setTile(Tile.testTile2,0,0);
        world.setTile(Tile.testTile2,0,1);
        world.setTile(Tile.testTile2,1,0);
        world.setTile(Tile.testTile2,63,63);

        double frame_cap = 1.0 / 60.0; // 60 frame per second
        double frame_time = 0;
        int frames = 0;

        double time = Timer.getTime();
        double unprocessed = 0;

        // Set the clear color
        //glClearColor(0.5f, 0.1f, 0.3f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!window.shouldClose()) {
            boolean can_render = false;

            double time_2 = Timer.getTime();
            double passed = time_2 - time;
            unprocessed += passed;
            frame_time += passed;

            time = time_2;

            while (unprocessed >= frame_cap) {
                // in this loop everything that's not GL rendering
                unprocessed -= frame_cap;
                can_render = true;

                if (window.getInput().isKeyDown(GLFW_KEY_LEFT)) {
                    camera.getPosition().sub(new Vector3f(-5,0,0));
                }

                if (window.getInput().isKeyDown(GLFW_KEY_RIGHT)) {
                    camera.getPosition().sub(new Vector3f(5,0,0));
                }

                if (window.getInput().isKeyDown(GLFW_KEY_UP)) {
                    camera.getPosition().sub(new Vector3f(0,5,0));
                }

                if (window.getInput().isKeyDown(GLFW_KEY_DOWN)) {
                    camera.getPosition().sub(new Vector3f(0,-5,0));
                }

                world.correctCamera(camera, window);

                glfwPollEvents();

                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    System.out.println("Cam: " + camera.getPosition());
                    frames = 0;
                }

            }

            if (can_render) {
                // GL rendering
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                world.render(tiles, shader, camera, window);

                window.swapBuffers();
                frames ++;
            }

        }
    }

    public static void main(String[] args) {
        new testlwjgl2dgame().run();
    }

}
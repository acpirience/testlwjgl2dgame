package game;

import io.*;
import render.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import world.TileRenderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class testlwjgl2dgame {

    private Window win;

    private void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        win.destroy();
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        win = new Window();
        win.setSize(640,480);
//        win.setFullscreen(true);
        win.createWindow("Game");


        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(win.getWindow(), (window, key, scancode, action, mods) -> {
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

        Camera camera = new Camera(win.getWidth(),win.getHeight());
        glEnable(GL_TEXTURE_2D);

        TileRenderer tiles = new TileRenderer();

/*
        float[] vertices = new float[]  {
                -0.5f, 0.5f, 0, // TOP LEFT     0
                 0.5f, 0.5f, 0, // TOP RIGHT    1
                 0.5f,-0.5f, 0, // BOTTOM RIGHT 2
                -0.5f,-0.5f, 0  // BOTTOM LEFT  3
        };

        float[] tex_coords = new float[] {
                      //      0,0    0,1
                0, 0, //TL 0   +----->
                0, 1, //TR 1   |     |
                1, 1, //BR 2   |     |
                1, 0 // BL 3   v-----+
                     //       1,0    1,1
        };

        int[] indices = new int[] {
                0,1,2,
                2,3,0
        };

        Model model = new Model(vertices, tex_coords, indices);

        Texture tex = new Texture("./res/smiley.png");

*/

        Shader shader = new Shader("shader");


        Matrix4f scale = new Matrix4f()
                .translate(new Vector3f(0,0,0))
                .scale(32);

//        Matrix4f target = scale;

//        camera.setPosition(new Vector3f(0,0,0));

        double frame_cap = 1.0 / 60.0; // 60 frame per second
        double frame_time = 0;
        int frames = 0;

        double time = Timer.getTime();
        double unprocessed = 0;

        // Set the clear color
        //glClearColor(0.5f, 0.1f, 0.3f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!win.shouldClose()) {
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

  //              target = scale;

                glfwPollEvents();

                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }

            }

            if (can_render) {
                // GL rendering
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                for (int i = 0; i < 8; i++) {
                    tiles.renderTile((byte)0, i, 0 , shader, scale, camera);
                }
                
/*
                shader.bind();
                shader.setUniform("sampler", 0);
                shader.setUniform("projection", camera.getProjection().mul(target));
                tex.bind(0);
                model.render();
*/
                win.swapBuffers();
                frames ++;
            }

        }
    }

    public static void main(String[] args) {
        new testlwjgl2dgame().run();
    }

}
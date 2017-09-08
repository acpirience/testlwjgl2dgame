package entity;

import io.Window;
import org.joml.Vector3f;
import render.*;
import world.World;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private Model model;
    //private Texture texture;
    private Animation texture;
    private Transform transform;

    public Player() {
        float[] vertices = new float[]  {
                -1f, 1f, 0, // TOP LEFT     0
                1f,  1f, 0, // TOP RIGHT    1
                1f, -1f, 0, // BOTTOM RIGHT 2
                -1f,-1f, 0  // BOTTOM LEFT  3
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

        model = new Model(vertices, tex_coords, indices);
        this.texture = new Animation(5, 10, "xxx");

        transform = new Transform();
        transform.setScale(new Vector3f(16,16,1));
    }

    public void update(float delta, Window window, Camera camera, World world) {
        if (window.getInput().isKeyDown(GLFW_KEY_LEFT)) {
            transform.pos.add(new Vector3f(-10*delta,0,0));
        }

        if (window.getInput().isKeyDown(GLFW_KEY_RIGHT)) {
            transform.pos.add(new Vector3f(10*delta,0,0));
        }

        if (window.getInput().isKeyDown(GLFW_KEY_UP)) {
            transform.pos.add(new Vector3f(0,10*delta,0));
        }

        if (window.getInput().isKeyDown(GLFW_KEY_DOWN)) {
            transform.pos.add(new Vector3f(0,-10*delta,0));
        }

        camera.setPosition(transform.pos.mul(-world.getScale(), new Vector3f()));
    }

    public void render(Shader shader, Camera camera) {
        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(0);
        model.render();
    }

}

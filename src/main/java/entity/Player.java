package entity;

import collision.AABB;
import collision.Collision;
import io.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.*;
import world.World;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private Model model;
    private AABB boundingBox;
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

        boundingBox = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(1,1));
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

        boundingBox.getCenter().set(transform.pos.x, transform.pos.y);

        AABB[] boxes = new AABB[25];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boxes[i + j * 5] = world.getTileBoundingBox(
                        (int)(((transform.pos.x / 2) + 0.5f) - (5/2)) +i,
                        (int)(((- transform.pos.y / 2) + 0.5f) - (5/2)) +j
                        );
            }
        }

        AABB box = null;
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] != null) {
                if (box == null) {
                    box = boxes[i];
                }

                Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                if (length1.lengthSquared() > length2.lengthSquared()) {
                    box = boxes[i];
                }
            }
        }

        if (box != null) {
            Collision data = boundingBox.getCollision(box);
            if (data.isIntersecting) {
                boundingBox.correctPosition(box, data);
                transform.pos.set(boundingBox.getCenter(), 0);
            }

            // do it a second time with the new closest box
            // this way we avoid the diagonal collision jitter
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    boxes[i + j * 5] = world.getTileBoundingBox(
                            (int)(((transform.pos.x / 2) + 0.5f) - (5/2)) +i,
                            (int)(((- transform.pos.y / 2) + 0.5f) - (5/2)) +j
                    );
                }
            }

            box = null;
            for (int i = 0; i < boxes.length; i++) {
                if (boxes[i] != null) {
                    if (box == null) {
                        box = boxes[i];
                    }

                    Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                    Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                    if (length1.lengthSquared() > length2.lengthSquared()) {
                        box = boxes[i];
                    }
                }
            }

            data = boundingBox.getCollision(box);
            if (data.isIntersecting) {
                boundingBox.correctPosition(box, data);
                transform.pos.set(boundingBox.getCenter(), 0);
            }
        }

        // smoother camera
        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), 0.1f);
        //camera.setPosition(transform.pos.mul(-world.getScale(), new Vector3f()));
    }

    public void render(Shader shader, Camera camera) {
        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(0);
        model.render();
    }
}

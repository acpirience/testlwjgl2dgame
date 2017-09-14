package entity;

import collision.AABB;
import collision.Collision;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.Animation;
import render.Camera;
import render.Model;
import render.Shader;
import world.World;

import static org.lwjgl.glfw.GLFW.*;

public abstract class Entity {
    private static Model model;
    protected AABB boundingBox;
    //private Texture texture;
    protected Animation texture;
    protected Transform transform;

    public Entity(Animation animation, Transform transform) {
        this.texture = animation;
        this.transform = transform;

        boundingBox = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(transform.scale.x,transform.scale.y));
    }

    public void move(Vector2f direction) {
        transform.pos.add(new Vector3f(direction,0));

        boundingBox.getCenter().set(transform.pos.x, transform.pos.y);
    }

    public void collideWithTiles(World world) {
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


    }

    public abstract void update(float delta, Window window, Camera camera, World world);

    public void render(Shader shader, Camera camera, World world) {
        Matrix4f target = camera.getProjection();
        target.mul(world.getWorldMatrix());
        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(target));
        texture.bind(0);
        model.render();
    }

    public static void initAsset() {
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
    }

    public static void deleteAsset() {
        model = null;
    }


}

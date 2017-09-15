package entity;

import io.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.*;
import world.World;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_SIZE = 2;

    public Player(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(4,2, "player/idle"));
        setAnimation(ANIM_WALK, new Animation(4,4, "player/walking"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (window.getInput().isKeyDown(GLFW_KEY_LEFT)) {
            movement.add(-10 * delta, 0);
        }

        if (window.getInput().isKeyDown(GLFW_KEY_RIGHT)) {
            movement.add(10 * delta, 0);
        }

        if (window.getInput().isKeyDown(GLFW_KEY_UP)) {
            movement.add(0, 10 * delta);
        }

        if (window.getInput().isKeyDown(GLFW_KEY_DOWN)) {
            movement.add(0, -10 * delta);
        }

        move(movement);

        if ((movement.x != 0) || (movement.y != 0)) {
            useAnimation(ANIM_WALK);
        }
        else {
            useAnimation(ANIM_IDLE);
        }

        // smoother camera
        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), 0.1f);
        //camera.setPosition(transform.pos.mul(-world.getScale(), new Vector3f()));

    }


}

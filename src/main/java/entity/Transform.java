package entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f pos;
    public Vector3f scale;

    public Transform() {
        pos = new Vector3f();
        scale = new Vector3f(0,0,0);
    }

    public Matrix4f getProjection(Matrix4f target) {
        return target.scale(scale).translate(pos);
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}

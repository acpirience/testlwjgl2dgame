package gui;

import assets.Assets;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import render.Camera;
import render.Shader;

public class Gui {
    private Shader shader;

    public Gui() {
        shader = new Shader("gui");
    }

    public void render(Camera camera) {
        Matrix4f mat = new Matrix4f();
        // 80 = totally arbitrary scale ...
        camera.getUntransformedProjection().scale(80, mat);
        mat.translate(-3,2,0);

        shader.bind();
        shader.setUniform("projection", mat);
        shader.setUniform("color", new Vector4f(0,0,0,0.4f));

        Assets.getModel().render();
    }
}

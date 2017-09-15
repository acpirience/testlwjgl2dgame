package gui;

import assets.Assets;
import io.Window;
import org.joml.Matrix4f;
import render.Camera;
import render.Shader;
import render.TilesSheet;

public class Gui {
    private Shader shader;
    private Camera camera;
    private TilesSheet sheet;

    public Gui(Window window) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());
        sheet = new TilesSheet("test.png", 3);
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void render() {
        Matrix4f mat = new Matrix4f();
        // 80 = totally arbitrary scale ...
        camera.getUntransformedProjection().scale(80, mat);
        mat.translate(-3,-2,0);
        shader.bind();

        shader.setUniform("projection", mat);


        sheet.bindTile(shader,3);
        //sheet.bindTile(shader,1,1);
        //shader.setUniform("color", new Vector4f(0,0,0,0.4f));

        Assets.getModel().render();
    }
}

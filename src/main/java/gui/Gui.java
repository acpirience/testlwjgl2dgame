package gui;

import assets.Assets;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import render.Camera;
import render.Shader;
import render.TilesSheet;

public class Gui {
    private Shader shader;
    private Camera camera;
    private TilesSheet sheet;

    private Button temp;

    public Gui(Window window) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());
        sheet = new TilesSheet("gui.png", 9);
        temp = new Button(new Vector2f(0,0), new Vector2f(96,96));
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void render() {
        shader.bind();
        temp.render(camera, sheet, shader);
    }
}

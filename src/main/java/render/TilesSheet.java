package render;

import org.joml.Matrix4f;

public class TilesSheet {
    private Texture texture;

    private Matrix4f scale;
    private Matrix4f translation;

    private int tileAmount;


    public TilesSheet(String textureName, int tileAmount) {
        this.texture = new Texture("sheets/" + textureName);

        scale = new Matrix4f().scale(1.0f/ (float) tileAmount);
        translation = new Matrix4f();
        this.tileAmount = tileAmount;
    }

    public void bindTile(Shader shader, int x, int y) {
        scale.translate(x,y,0,translation);

        shader.setUniform("sampler", 0);
        shader.setUniform("texModifier", translation);
        texture.bind(0);

    }

    public void bindTile(Shader shader, int tile) {
        int y = tile % tileAmount;
        int x = tile / tileAmount;

        bindTile(shader, x, y);
    }


}




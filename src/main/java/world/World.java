package world;

import collision.AABB;
import entity.Entity;
import entity.Player;
import entity.Transform;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.Animation;
import render.Camera;
import render.Shader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {
    private final int view = 24;
    private byte[]tiles;
    private AABB[] boundindBoxes;
    private List<Entity> entities;
    private int width,height;
    private int scale = 16;

    private Matrix4f world;

    public World() {
        width = 64;
        height = 64;
        tiles = new byte[width * height];
        boundindBoxes = new AABB[width * height];
        world = new Matrix4f().setTranslation(new Vector3f(0,0,0));
        world.scale(scale);
    }

    public Matrix4f getWorldMatrix() {
        return world;
    }

    public World(String worldName) {
        try {
            BufferedImage tile_sheet = ImageIO.read(new File("./levels/" + worldName + "/tiles.png"));
            //BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + world + "_entity.png"));

            width = tile_sheet.getWidth();
            height = tile_sheet.getHeight();

            int[] colorTileSheet = tile_sheet.getRGB(0,0,width, height,null,0, width);

            tiles = new byte[width * height];
            boundindBoxes = new AABB[width * height];
            entities = new ArrayList<Entity>();


            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;

                    Tile t;
                    try {
                        t = Tile.tiles[red];
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        t = null;
                    }

                    if (t != null) {
                        setTile(t, x , y);
                    }

                }

            }

            //TODO finish level loader
            entities.add(new Player(new Transform()));
            Transform t = new Transform();
            t.pos.x = 0;
            t.pos.y = -11 * 2;

/*
            entities.add(new Entity(new Animation(1,1,"xxx"), t) {
                @Override
                public void update(float delta, Window window, Camera camera, World world) {
                    move(new Vector2f(5*delta, 0));
                }

            });
*/

        } catch (IOException e) {
            e.printStackTrace();
        }

        world = new Matrix4f().setTranslation(new Vector3f(0,0,0));
        world.scale(scale);
    }

    public void render(TileRenderer render, Shader shader, Camera camera, Window window) {
        int posX = (int)(camera.getPosition().x + (window.getWidth()/2)) / (scale * 2);
        int posY = (int)(camera.getPosition().y - (window.getHeight()/2)) / (scale * 2);

        for (int i = 0; i < view; i++) {
            for (int j = 0; j < view; j++) {
                Tile t = getTile(i - posX,j + posY);

                if (t != null) {
                    render.renderTile(t,i - posX,-j - posY,shader, world, camera);
                }
            }
        }

        for (Entity entity: entities) {
            entity.render(shader, camera, this);
        }
    }

    public void update(float delta, Window window, Camera camera) {
        for (Entity entity: entities) {
            entity.update(delta, window, camera, this);
        }

        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).collideWithTiles(this);
            for (int j = i+1; j < entities.size(); j++) {
                entities.get(i).collideWithEntity(entities.get(j));
            }
            entities.get(i).collideWithTiles(this);
        }
    }

    public void correctCamera(Camera camera, Window window) {
        Vector3f pos = camera.getPosition();

        int w = -width * scale * 2;
        int h = height * scale * 2;

        if (pos.x > -(window.getWidth()/2) + scale) {
            pos.x = -(window.getWidth()/2) + scale;
        }
        if (pos.x < w + (window.getWidth()/2) + scale) {
            pos.x = w + (window.getWidth()/2) + scale;
        }
        if (pos.y < (window.getHeight()/2) - scale) {
            pos.y = (window.getHeight()/2) - scale;
        }
        if (pos.y > h - (window.getHeight()/2) - scale) {
            pos.y = h - (window.getHeight()/2) - scale;
        }

    }

    public void setTile(Tile tile, int x, int y) {
        tiles[x + y*width] = tile.getId();
        if(tile.isSolid()) {
            boundindBoxes[x + y * width] =
                    new AABB(new Vector2f(x*2, -y*2), new Vector2f(1,1));
        }
        else {
            boundindBoxes[x + y * width] = null;
        }
    }

    public Tile getTile(int x, int y) {
        try {
            return Tile.tiles[tiles[x + y*width]];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public AABB getTileBoundingBox(int x, int y) {
        try {
            return boundindBoxes[x + y * width];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getScale() {
        return scale;
    }
}

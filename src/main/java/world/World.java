package world;

import collision.AABB;
import entity.Entity;
import entity.Player;
import entity.Transform;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.Camera;
import render.Shader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {
    private int viewX = 24;
    private int viewY = 24;
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

    public void calculateView(Window window) {
        viewX = (window.getWidth() / (scale *2)) + 4;
        viewY = (window.getHeight() / (scale *2)) + 4;
    }

    public Matrix4f getWorldMatrix() {
        return world;
    }

    public World(String worldName, Camera camera) {
        try {
            BufferedImage tile_sheet = ImageIO.read(new File("./levels/" + worldName + "/tiles.png"));
            BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + worldName + "/entities.png"));

            width = tile_sheet.getWidth();
            height = tile_sheet.getHeight();

            int[] colorTileSheet = tile_sheet.getRGB(0,0,width, height,null,0, width);
            int[] colorEntitySheet = entity_sheet.getRGB(0,0,width, height,null,0, width);


            tiles = new byte[width * height];
            boundindBoxes = new AABB[width * height];
            entities = new ArrayList<Entity>();

            Transform transform;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int redValue = (colorTileSheet[x + y * width] >> 16) & 0xFF;
                    int entityIndex = (colorEntitySheet[x + y * width] >> 16) & 0xFF;
                    int entityAlpha = (colorEntitySheet[x + y * width] >> 24) & 0xFF;

                    Tile t;
                    try {
                        t = Tile.tiles[redValue];
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        t = null;
                    }

                    if (t != null) {
                        setTile(t, x , y);
                    }

                    if (entityAlpha > 0) {
                        transform = new Transform();
                        transform.pos.x = x * 2;
                        transform.pos.y = -y * 2;

                        switch(entityIndex) {
                            case 1: // PLAYER
                                Player player = new Player(transform);
                                entities.add(player);
                                camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
                                break;

                            default:

                                break;
                        }
                    }

                }

            }

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

    public void render(TileRenderer render, Shader shader, Camera camera) {
        int posX = (int)(camera.getPosition().x) / (scale * 2);
        int posY = (int)(camera.getPosition().y) / (scale * 2);

        for (int i = 0; i < viewX; i++) {
            for (int j = 0; j < viewY; j++) {
                Tile t = getTile(i - posX - (viewX/2) + 1,j + posY - (viewY/2));

                if (t != null) {
                    render.renderTile(t,i - posX - (viewX/2) + 1,-j - posY + (viewY/2),shader, world, camera);
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

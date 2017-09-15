package world;

public class Tile {
    public static Tile tiles[] = new Tile[16];
    public static byte not = 0; // number_of_tiles

    public static final Tile testTile = new Tile("grass");
    public static final Tile testTile2 = new Tile("bricks").setSolid();

    private byte id;
    private boolean solid;
    private String texture;

    public Tile(String texture) {
        this.id = not;
        not++;
        this.texture = texture;
        this.solid = false;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tile of Id [" + id + "] is already used!");
        }
        tiles[id] = this;
    }

    public Tile setSolid() {
        this.solid = true;
        return this;
    }

    public boolean isSolid() {
        return this.solid;
    }


    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}

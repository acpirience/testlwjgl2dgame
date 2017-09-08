package world;

public class Tile {
    public static Tile tiles[] = new Tile[16];
    public static byte not = 0; // number_of_tiles

    public static final Tile testTile = new Tile("smiley");
    public static final Tile testTile2 = new Tile("checker");

    private byte id;
    private String texture;

    public Tile(String texture) {
        this.id = not;
        not++;
        this.texture = texture;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tile of Id [" + id + "] is already used!");
        }
        tiles[id] = this;
    }

    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}

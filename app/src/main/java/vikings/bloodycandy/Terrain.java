package vikings.bloodycandy;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Terrain
{
    private int     width;
    private int     height;
    private int[][] tiles;

    public Terrain(int width, int height)
    {
        this.width = 0;
        this.height = 0;
        create(width, height);
    }

    public boolean create(int width, int height)
    {
        if (width <= 0 || height <= 0)
            return (false);

        this.width = width;
        this.height = height;
        tiles = new int[width][height];
        return (true);
    }

    public boolean isInside(int x, int y)
    {
        return (x >= 0 && x < width &&
                y >= 0 && y < height);
    }

    public int get(int x, int y)
    {
        if (isInside(x, y))
            return (tiles[x][y]);
        else
            return (0);
    }

    public boolean set(int tile, int x, int y)
    {
        if (isInside(x, y))
        {
            tiles[x][y] = tile;
            return (true);
        }
        else
            return (false);
    }

    public int width()
    {
        return (width);
    }

    public int height()
    {
        return (height);
    }
}

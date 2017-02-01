package vikings.bloodycandy;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Board
{
    private int         width;
    private int         height;
    private Block[][]   blocks;

    public Boardgit(int width, int height)
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
        blocks = new Block[width][height];
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y)
                blocks[x][y] = new Block();

        return (true);
    }

    public boolean isInside(int x, int y)
    {
        return (x >= 0 && x < width &&
                y >= 0 && y < height);
    }

    public Block get(int x, int y)
    {
        if (isInside(x, y))
            return (blocks[x][y]);
        else
            return (new Block());
    }
    public boolean set(Block block, int x, int y)
    {
        if (isInside(x, y))
        {
            blocks[x][y] = block;
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

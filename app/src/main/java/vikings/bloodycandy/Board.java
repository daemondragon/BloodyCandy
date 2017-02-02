package vikings.bloodycandy;

import android.util.Log;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Board
{
    private int         width;
    private int         height;
    private Block[][]   blocks;

    public Board(int width, int height)
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

    public boolean canSwap(int x1, int y1, int x2, int y2)
    {
        if (isInside(x1, x2) && isInside(x2, y2) &&
                Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1)
        {
            //The blocks are next to each other
            fastSwap(x1, y1, x2, y2);
            boolean can_destroy = canDestroy(x1, y1) || canDestroy(x2, y2);
            fastSwap(x1, y1, x2, y2);
            return (can_destroy);
        }
        else
            return (false);
    }

    private void fastSwap(int x1, int y1, int x2, int y2)
    {
        if (isInside(x1, y2) && isInside(x2, y2) && (x1 != x2 || y1 != y2))
        {
            Block temp = blocks[x1][y1];
            blocks[x1][y1] = blocks[x2][y2];
            blocks[x2][y2] = temp;
        }
    }

    //Wil take into account the animation
    public void swap(int x1, int y1, int x2, int y2)
    {
        if (isInside(x1, y2) && isInside(x2, y2) && (x1 != x2 || y1 != y2))
        {
            Block temp = blocks[x1][y1];
            blocks[x1][y1] = blocks[x2][y2];
            blocks[x2][y2] = temp;
        }
    }

    public boolean canDestroy(int x, int y)
    {
        int sum_x = 1;
        int sum_y = 1;

        for (int i = x - 1; isInside(i, y) && blocks[i][y].isSame(blocks[x][y]); i--)
            sum_x++;
        for (int i = x + 1; isInside(i, y) && blocks[i][y].isSame(blocks[x][y]); i++)
            sum_x++;

        for (int j = y - 1; isInside(x, j) && blocks[x][j].isSame(blocks[x][y]); j--)
            sum_y++;
        for (int j = y + 1; isInside(x, j) && blocks[x][j].isSame(blocks[x][y]); j++)
            sum_y++;

        return (sum_x >= 3 || sum_y >= 3);
    }
}

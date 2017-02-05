package vikings.bloodycandy;

import android.util.Log;

import java.util.Random;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Board
{
    private int         width;
    private int         height;
    private Block[][]   blocks;

    Random random;

    public Board(int width, int height)
    {
        this.width = 0;
        this.height = 0;

        create(width, height);

        random = new Random();
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
                Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1 &&
                blocks[x1][y1].isMovable() && blocks[x2][y2].isMovable())
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

    private boolean canDestroyHorizontally(int x, int y)
    {
        if (get(x, y).is_falling)
            return (false);

        int sum_x = 1;
        for (int i = x - 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && !get(i, y).is_falling; i--)
            sum_x++;
        for (int i = x + 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && !get(i, y).is_falling; i++)
            sum_x++;

        return (sum_x >= 3);
    }

    private boolean canDestroyVertically(int x, int y)
    {
        if (get(x, y).is_falling)
            return (false);

        int sum_y = 1;
        for (int j = y - 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && !get(x, j).is_falling; j--)
            sum_y++;
        for (int j = y + 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && !get(x, j).is_falling; j++)
            sum_y++;

        return (sum_y >= 3);
    }

    public boolean canDestroy(int x, int y)
    {
        return (canDestroyHorizontally(x, y) || canDestroyVertically(x, y));
    }

    public void destroyVertically(int x, int y)
    {
        for (int j = y - 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && !get(x, j).is_falling; j--)
            blocks[x][j].destroy();
        for (int j = y + 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && !get(x, j).is_falling; j++)
            blocks[x][j].destroy();

        blocks[x][y].destroy();
    }

    public void destroyHorizontally(int x, int y)
    {
        for (int i = x - 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && !get(i, y).is_falling; i--)
            blocks[i][y].destroy();
        for (int i = x + 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && !get(i, y).is_falling; i++)
            blocks[i][y].destroy();

        blocks[x][y].destroy();
    }

    public void destroy(int x, int y)
    {
        if (canDestroyHorizontally(x, y))
            destroyHorizontally(x, y);
        else
            destroyVertically(x, y);
    }

    public void respawnBlocks()
    {
        for (int x = 0; x < width; ++x)
        {
            if (get(x, 0).getType() == Block.Type.Empty) {
                get(x, 0).setId(Math.abs(random.nextInt(4)));
                get(x, 0).setType(Block.Type.Normal);
                get(x, 0).resetFall();
            }
        }
    }

    public void update(float delta_time)
    {
        respawnBlocks();

        for (int y = height - 1; y >= 0; --y)
        {
            for (int x = 0; x < width; ++x)
            {
                if (get(x, y).isMovable())
                {
                    float remaining_time = get(x, y).update(delta_time);
                    if ((y == height - 1 || !get(x, y + 1).is_falling) && get(x, y).isInPlace())
                        get(x, y).is_falling = false;

                    int j = y;
                    while (remaining_time > 0.f && isInside(x, j + 1) && get(x, j + 1).getType() == Block.Type.Empty)
                    {
                        fastSwap(x, j, x, j + 1);
                        get(x, j + 1).resetFall();
                        remaining_time = get(x, j + 1).update(remaining_time);
                        get(x, j +  1).is_falling = true;
                        j++;
                    }
                }
            }
        }

        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                if (canDestroy(x, y))
                    destroy(x, y);
    }
}

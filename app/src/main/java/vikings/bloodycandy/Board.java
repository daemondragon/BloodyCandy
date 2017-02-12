package vikings.bloodycandy;

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
    int score;
    int combo;

    public Board(int width, int height)
    {
        this.width = 0;
        this.height = 0;
        combo = 1;

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
                blocks[x1][y1].isDestroyable() && blocks[x2][y2].isDestroyable())
        {
            boolean some_block_falling = false;
            for (int i = 0; i < width && !some_block_falling; ++i)
                for (int j = 0; j < height && !some_block_falling; ++j)
                    if (!get(i, j).isInPlace())
                        some_block_falling = true;
            if (some_block_falling)
                return (false);

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
        if (isInside(x1, y1) && isInside(x2, y2) && (x1 != x2 || y1 != y2))
        {
            combo = 1;
            Block temp = blocks[x1][y1];
            blocks[x1][y1] = blocks[x2][y2];
            blocks[x2][y2] = temp;
        }
    }

    private boolean canDestroyHorizontally(int x, int y)
    {
        if (!get(x, y).isDestroyable())
            return (false);

        int sum_x = 1;
        for (int i = x - 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && get(i, y).isDestroyable(); i--)
            sum_x++;
        for (int i = x + 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && get(i, y).isDestroyable(); i++)
            sum_x++;

        return (sum_x >= 3);
    }

    private boolean canDestroyVertically(int x, int y)
    {
        if (!get(x, y).isDestroyable())
            return (false);

        int sum_y = 1;
        for (int j = y - 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && get(x, j).isDestroyable(); j--)
            sum_y++;
        for (int j = y + 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && get(x, j).isDestroyable(); j++)
            sum_y++;

        return (sum_y >= 3);
    }

    public boolean canDestroy(int x, int y)
    {
        return (canDestroyHorizontally(x, y) || canDestroyVertically(x, y));
    }

    private int destroyVertically(int x, int y)
    {
        int sum = 0;
        for (int j = y - 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && get(x, j).isDestroyable(); j--, ++sum)
            blocks[x][j].destroy();
        for (int j = y + 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && get(x, j).isDestroyable(); j++, ++sum)
            blocks[x][j].destroy();

        blocks[x][y].destroy();
        return (sum);
    }

    private int destroyHorizontally(int x, int y)
    {
        int sum = 0;
        for (int i = x - 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && get(i, y).isDestroyable(); i--, ++sum)
            blocks[i][y].destroy();
        for (int i = x + 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && get(i, y).isDestroyable(); i++, ++sum)
            blocks[i][y].destroy();

        blocks[x][y].destroy();
        return (sum);
    }

    public void destroy(int x, int y)
    {
        int temp_score = 1;
        if (canDestroyHorizontally(x, y))
            temp_score += destroyHorizontally(x, y);
        if (canDestroyVertically(x, y))
            temp_score += destroyVertically(x, y);

        score += (combo++) * temp_score;
    }

    private void respawnBlocks()
    {
        for (int x = 0; x < width; ++x)
        {
            if (get(x, 0).getType() == Block.Type.Empty) {
                get(x, 0).setId(Math.abs(random.nextInt(4)));
                get(x, 0).setType(Block.Type.Normal);
                get(x, 0).resetFallStatus();
                get(x, 0).velocity = 0.f;
            }
        }
    }

    private void moveBlocks(float delta_time)
    {
        for (int x = 0; x < width; ++x)
        {
            Block block = get(x, height - 1);
            if (block.isMovable() && !block.isInPlace())
            {
                block.update(delta_time);
                if (block.isInPlace())
                    block.stopFall();
            }
        }

        for (int y = height - 2; y >= 0; --y)
        {
            for (int x = 0; x < width; ++x)
            {
                Block block = get(x, y);
                if (!block.isMovable())
                    continue;

                int j = y;
                float remaining_time = delta_time;
                boolean have_stopped_falling = false;

                do {
                    remaining_time = block.update(remaining_time);
                    Block under_block = get(x, j + 1);
                    if (under_block.isMovable() && block.getFallingStatus() < under_block.getFallingStatus())
                    {//Check for race condition
                        block.setFallingStatus(under_block.getFallingStatus());
                        block.velocity = under_block.velocity;
                    }
                    else if (block.isInPlace())
                    {
                        if (under_block.getType() == Block.Type.Hole ||
                                (under_block.isMovable() && !under_block.is_falling))
                        {//Stop fall
                            block.stopFall();
                            have_stopped_falling = true;
                        }
                        else
                        {//Continue falling
                            fastSwap(x, j, x, j + 1);
                            block.resetFallStatus();
                        }
                    }


                } while (!have_stopped_falling && remaining_time > 0.f && isInside(x, j + 1));
            }
        }
    }

    public void update(float delta_time)
    {
        moveBlocks(delta_time);
        respawnBlocks();

        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                if (canDestroy(x, y))
                    destroy(x, y);
    }

    public int getScore()
    {
        return (score);
    }
}

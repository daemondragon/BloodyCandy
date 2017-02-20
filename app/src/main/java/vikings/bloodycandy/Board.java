package vikings.bloodycandy;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Board
{
    private int         width;
    private int         height;
    private Block[][]   blocks;
    private int         nb_blocks;

    Random random;
    int score;
    int combo;

    public Board(int width, int height)
    {
        this.width = 0;
        this.height = 0;
        combo = 1;
        nb_blocks = 4;

        create(width, height);

        //for (int i = 0; i < width - 1; ++i)
            //set(new Block(Block.Type.Hole), i, 1);


        random = new Random();
    }

    public void setNbBlocks(int nb_blocks)
    {
        this.nb_blocks = (nb_blocks <= 0 ? 1 : nb_blocks);
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


    public boolean someBlocksAreFalling()
    {
        boolean some_block_falling = false;
        for (int i = 0; i < width && !some_block_falling; ++i)
            for (int j = 0; j < height && !some_block_falling; ++j)
                if ((get(i, j).isMovable() && get(i, j).is_falling))
                    return (true);

        return (false);
    }

    public boolean canSwap(int x1, int y1, int x2, int y2)
    {
        if (isInside(x1, y1) && isInside(x2, y2) &&
                Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1 &&
                blocks[x1][y1].isDestroyable() && blocks[x2][y2].isDestroyable())
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
        if (isInside(x1, y1) && isInside(x2, y2) && (x1 != x2 || y1 != y2))
        {
            combo = 1;
            Block temp = blocks[x1][y1];
            blocks[x1][y1] = blocks[x2][y2];
            blocks[x2][y2] = temp;

            if (x1 != x2)
            {
                get(x1, y1).setOffsetX(x1 > x2 ? -1.f : 1.f);
                get(x2, y2).setOffsetX(x1 < x2 ? -1.f : 1.f);
            }
            else
            {
                get(x1, y1).setOffsetY(y1 > y2 ? -1.f : 1.f);
                get(x2, y2).setOffsetY(y1 < y2 ? -1.f : 1.f);
            }
        }
    }

    private boolean canMakeAnySwap()
    {
        if (someBlocksAreFalling())
            return (true);

        for (int y = 0; y < height - 1; y++)
            for (int x = 0; x < width; ++x)
                if (canSwap(x, y, x, y + 1))
                    return (true);

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width - 1; ++x)
                if (canSwap(x, y, x + 1, y))
                    return (true);

        return (false);
    }

    public boolean firstAvailableSwap(ArrayList<Block> blocks)
    {
        if (someBlocksAreFalling())
            return (true);

        for (int x = 0; x < width - 1; ++x)
            for (int y = 0; y < height; ++y)
                if (canSwap(x, y, x + 1, y))
                {
                    fastSwap(x, y, x + 1, y);
                    if (canDestroy(x, y))
                    {
                        blocks.add(get(x, y));
                        getDestroyHorizontalList(x, y, blocks);
                        getDestroyVerticalList(x, y, blocks);
                    }
                    else
                    {
                        blocks.add(get(x + 1, y));
                        getDestroyHorizontalList(x + 1, y, blocks);
                        getDestroyVerticalList(x + 1, y, blocks);
                    }
                    fastSwap(x, y, x + 1, y);
                    return (true);
                }


        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height - 1; ++y)
                if (canSwap(x, y, x, y + 1))
                {
                    fastSwap(x, y, x, y + 1);
                    if (canDestroy(x, y))
                    {
                        blocks.add(get(x, y));
                        getDestroyHorizontalList(x, y, blocks);
                        getDestroyVerticalList(x, y, blocks);
                    }
                    else
                    {
                        blocks.add(get(x, y + 1));
                        getDestroyHorizontalList(x, y + 1, blocks);
                        getDestroyVerticalList(x, y + 1, blocks);
                    }
                    fastSwap(x, y, x, y + 1);
                    return (true);
                }

        return (false);
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

    private void getDestroyHorizontalList(int x, int y, ArrayList<Block> blocks, int recursion_limit)
    {
        if (recursion_limit > 0 && canDestroyHorizontally(x, y))
        {
            for (int i = x - 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && get(i, y).isDestroyable(); i--)
            {
                getDestroyVerticalList(i, y, blocks, recursion_limit - 1);
                blocks.add(get(i, y));
            }
            for (int i = x + 1; isInside(i, y) && get(i, y).isSame(get(x, y)) && get(i, y).isDestroyable(); i++)
            {
                getDestroyVerticalList(i, y, blocks, recursion_limit - 1);
                blocks.add(get(i, y));
            }
        }
    }

    private void getDestroyHorizontalList(int x, int y, ArrayList<Block> blocks)
    {
        getDestroyHorizontalList(x, y, blocks, 3);
    }

    private void getDestroyVerticalList(int x, int y, ArrayList<Block> blocks, int recursion_limit)
    {
        if (recursion_limit > 0 && canDestroyVertically(x, y))
        {
            for (int j = y - 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && get(x, j).isDestroyable(); j--) {
                getDestroyHorizontalList(x, j, blocks, recursion_limit - 1);
                blocks.add(get(x, j));
            }
            for (int j = y + 1; isInside(x, j) && get(x, j).isSame(get(x, y)) && get(x, j).isDestroyable(); j++) {
                getDestroyHorizontalList(x, j, blocks, recursion_limit - 1);
                blocks.add(get(x, j));
            }
        }
    }

    private void getDestroyVerticalList(int x, int y, ArrayList<Block> blocks)
    {
        getDestroyVerticalList(x, y, blocks, 3);
    }

    public boolean canDestroy(int x, int y)
    {
        return (canDestroyHorizontally(x, y) || canDestroyVertically(x, y));
    }

    public void destroy(int x, int y)
    {
        ArrayList<Block> to_destroy = new ArrayList<>();
        to_destroy.add(get(x, y));
        getDestroyHorizontalList(x, y, to_destroy);
        getDestroyVerticalList(x, y, to_destroy);

        score += (combo++) * to_destroy.size();
        for (Block b : to_destroy)
            if (b.isDestroyable())
                b.destroy();
    }

    public void reset()
    {
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                if (get(x, y).isMovable())
                    get(x, y).destroy();
    }

    private boolean canLeftFall(int x, int y)
    {
        return (isInside(x - 1, y - 1) &&
                get(x - 1, y + 1).getType() == Block.Type.Empty &&
                !get(x - 1, y).isMovable());
    }

    private boolean canRightFall(int x, int y)
    {
        return (isInside(x + 1, y - 1) &&
                get(x + 1, y + 1).getType() == Block.Type.Empty &&
                !get(x + 1, y).isMovable());
    }

    private void respawnBlocks()
    {
        for (int x = 0; x < width; ++x)
        {
            if (get(x, 0).getType() == Block.Type.Empty) {
                get(x, 0).setId(Math.abs(random.nextInt(nb_blocks)));
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
                block.updateSwap(delta_time);
                block.updateFall(delta_time);
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

                block.updateSwap(delta_time);

                int j = y;
                float remaining_time = delta_time;
                boolean have_stopped_falling = false;

                do {
                    remaining_time = block.updateFall(remaining_time);
                    Block under_block = get(x, j + 1);
                    if (under_block.isMovable() && block.getFallingStatus() < under_block.getFallingStatus())
                    {//Check for race condition
                        block.setFallingStatus(under_block.getFallingStatus());
                        block.velocity = under_block.velocity;
                    }
                    else if (block.getFallingStatus() <= 0.f)
                    {
                        if (under_block.getType() == Block.Type.Hole ||
                                (under_block.isMovable() && !under_block.is_falling))
                        {//Stop fall or move them to another column if it can
                            boolean can_left_fall = canLeftFall(x, j);
                            boolean can_right_fall = canRightFall(x, j);

                            if (can_left_fall || can_right_fall)
                            {
                                fastSwap(x, j, x + (can_left_fall ? -1 : 1), j + 1);
                                block.setOffsetX((can_left_fall ? 1 : -1));
                                block.resetFallStatus();
                            }
                            else
                            {
                                block.stopFall();
                                have_stopped_falling = true;
                            }
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

    public void selectAll(boolean b)
    {
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y)
                get(x, y).select(b);
    }
}

package vikings.bloodycandy;

/**
 * Created by Jeremy on 20/02/2017.
 */

public class MoveLimitedBoard extends Board
{
    int remaining_move;

    public MoveLimitedBoard(int width, int height, int remaining_move)
    {
        super(width, height);
        this.remaining_move = (remaining_move <= 0 ? 1 : remaining_move);
    }

    public boolean canSwap(int x1, int y1, int x2, int y2)
    {
        return (super.canSwap(x1, y1, x2, y2));
    }

    public void swap(int x1, int y1, int x2, int y2)
    {
        if (remaining_move > 0)
        {
            remaining_move--;
            super.swap(x1, y1, x2, y2);
        }
    }

    public int getRemainingMove()
    {
        return (remaining_move);
    }
}

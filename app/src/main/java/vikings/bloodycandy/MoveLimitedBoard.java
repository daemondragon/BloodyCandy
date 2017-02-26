package vikings.bloodycandy;

/**
 * Created by Jeremy on 20/02/2017.
 */

public class MoveLimitedBoard extends Board
{
    int max_move;
    int remaining_move;

    public MoveLimitedBoard(int width, int height, int nb_move_allowed)
    {
        super(width, height);

        max_move = (nb_move_allowed <= 0 ? 1 : nb_move_allowed);
        this.remaining_move = max_move;
    }

    public boolean canSwap(int x1, int y1, int x2, int y2)
    {
        return (remaining_move > 0 && super.canSwap(x1, y1, x2, y2));
    }

    public void swap(int x1, int y1, int x2, int y2)
    {
        remaining_move--;
        super.swap(x1, y1, x2, y2);
    }

    public void fullReset()
    {
        super.fullReset();
        remaining_move = max_move;
    }

    public int getRemainingMove()
    {
        return (remaining_move);
    }

    public boolean isFinished()
    {
        return (remaining_move <= 0);
    }
}

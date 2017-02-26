package vikings.bloodycandy;

/**
 * Created by Jeremy on 20/02/2017.
 */

public class TimedBoard extends Board
{
    private float time_limit;
    private float actual_time;

    public TimedBoard(int width, int height, float time_limit)
    {
        super(width, height);
        this.time_limit = (time_limit < 0.f ? 0.f : time_limit);
        actual_time = this.time_limit;
    }

    public void update(float delta_time)
    {
        super.update(delta_time);
        actual_time -= delta_time;
    }

    public void fullReset()
    {
        super.fullReset();
        actual_time = time_limit;
    }

    public float getRemainingTime()
    {
        return (actual_time);
    }

    public boolean isFinished()
    {
        return (actual_time <= 0.f);
    }
}

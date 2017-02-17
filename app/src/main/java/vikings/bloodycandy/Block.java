package vikings.bloodycandy;

/**
 * Created by Jeremy on 31/01/2017.
 */

public class Block
{
    public enum Type
    {
        Empty,//Nothing is in the block
        Normal,//A block is in
        Hole//An unmovable block, are not affected by gravity or swapping
    }

    static private float gravity;
    static private float swap_velocity;

    private Type        type;
    private int         id;
    private float       offset_x;
    private float       offset_y;
    private float       falling;
    public boolean      is_falling;
    public float        velocity;

    public Block(Type type, int id)
    {
        offset_x = 0.f;
        offset_y = 0.f;
        velocity = 0.f;

        setType(type);
        setId(id);
        resetFallStatus();
    }
    public Block(Type type)
    {
        this(type, 0);
    }
    public Block()
    {
        this(Type.Empty);
    }

    public void setType(Type type)
    {
        this.type = type;
    }
    public Type getType()
    {
        return (type);
    }
    public void setId(int id)
    {
        this.id = (id < 0 ? 0 : id);
    }
    public int getId()
    {
        return (id);
    }

    public static void setGravity(float gravity)
    {
        Block.gravity = (gravity < 0.f ? -gravity : gravity);
    }
    public static float getGravity()
    {
        return (gravity);
    }

    public static void setSwapVelocity(float velocity)
    {
        Block.swap_velocity = (velocity < 0.f ? - velocity : velocity);
    }

    public static float getSwapVelocity()
    {
        return (swap_velocity);
    }

    //return how much time the block didn't move
    public void updateSwap(float time)
    {
        if (!isMovable())
            return;

        float swap_movement = swap_velocity * time;
        if (offset_x != 0.f)
        {
            if (swap_movement >= Math.abs(offset_x))
                offset_x = 0.f;
            else
                offset_x += (offset_x < 0.f ? swap_movement : -swap_movement);
        }
        if (offset_y != 0.f)
        {
            if (swap_movement >= Math.abs(offset_y))
                offset_y = 0.f;
            else
                offset_y += (offset_y < 0.f ? swap_movement : -swap_movement);
        }
    }
    public float updateFall(float time)
    {
        if (!isMovable())
            return (0.f);

        float movement = (velocity + gravity * time) * time;
        if (movement < falling)
        {
            falling -= movement;
            velocity += gravity * time;
            return (0.f);
        }
        else
        {
            float not_processed = time - (falling / gravity);
            velocity += (gravity * time);

            falling = 0.f;
            return (not_processed);
        }
    }

    public boolean isInPlace()
    {
        return (!isMovable() || (offset_x == 0.f && offset_y == 0.f && falling <= 0.f));
    }
    public void resetFallStatus()
    {
        falling = 1.f;
        is_falling = true;
    }
    public void stopFall()
    {
        is_falling = false;
        velocity = 0.f;
    }
    public float getFallingStatus()
    {
        return (falling);
    }
    public void setOffsetX(float offset)
    {
        if (offset < -1.f)
            offset_x = -1.f;
        else if (offset > 1.f)
            offset_x = 1.f;
        else
            offset_x = offset;
    }

    public void setOffsetY(float offset)
    {
        if (offset < -1.f)
            offset_y = -1.f;
        else if (offset > 1.f)
            offset_y = 1.f;
        else
            offset_y = offset;
    }

    public float getOffsetX()
    {
        return (offset_x);
    }

    public float getOffsetY()
    {
        return (offset_y);
    }

    public void setFallingStatus(float status)
    {
        if (status < 0.f)
            falling = 0.f;
        else if (status > 1.f)
            falling = 1.f;
        else
            falling = status;
    }

    public boolean isSame(Block block)
    {
        return (type == block.getType() && id == block.getId());
    }
    public void destroy()
    {
        type = Type.Empty;
        id = 0;
        velocity = 0.f;
        offset_x = 0.f;
        offset_y = 0.f;
    }
    public boolean isMovable()
    {
        return (type != Type.Empty && type != Type.Hole);
    }
    public boolean isDestroyable()
    {
        return (isMovable() && isInPlace());
    }
}

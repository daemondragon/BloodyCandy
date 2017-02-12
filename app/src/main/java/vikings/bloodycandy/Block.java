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

    private Type        type;
    private int         id;
    private float       falling;//[0.f:1.f] 0.f at position, 1.f falling
    public boolean      is_falling;
    public float        velocity;

    public Block(Type type, int id)
    {
        setType(type);
        setId(id);
        resetFallStatus();
        velocity = 0.f;
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

    //return how much time the block didn't move
    public float update(float time)
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
        return (falling <= 0.f);
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
    }
    public boolean isMovable()
    {
        return (type != Type.Empty && type != Type.Hole);
    }
    public boolean isDestroyable()
    {
        return (isMovable() && !is_falling);
    }
}

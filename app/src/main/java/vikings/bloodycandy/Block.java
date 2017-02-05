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
    public  boolean     is_falling;

    public Block(Type type, int id)
    {
        setType(type);
        setId(id);
        resetFall();
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

        float movement = gravity * time;
        if (movement < falling)
        {
            falling -= movement;
            is_falling = true;
            return (0.f);
        }
        else
        {
            float not_processed = time - (falling / gravity);
            falling = 0.f;
            is_falling = (not_processed != time);
            return (not_processed);
        }
    }
    public boolean isInPlace()
    {
        return (falling <= 0.f);
    }
    public void resetFall()
    {
        falling = 1.f;
        is_falling = true;
    }
    public float getFallingStatus()
    {
        return (falling);
    }

    public boolean isSame(Block block)
    {
        return (type == block.getType() && id == block.getId());
    }
    public void destroy()
    {
        type = Type.Empty;
        id = 0;
    }
    public boolean isMovable()
    {
        return (type != Type.Empty && type != Type.Hole);
    }
}

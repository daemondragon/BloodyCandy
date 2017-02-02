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

    private Type        type;
    private int        id;

    public Block(Type type, int id)
    {
        setType(type);
        setId(id);
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
    public boolean isSame(Block block)
    {
        return (type == block.getType() && id == block.getId());
    }
}

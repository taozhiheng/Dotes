package ui;

/**
 * Created by taozhiheng on 15-4-6.
 *
 */
public class Spot {

    public final static int DEFAULT_RANGE = 5;

    private int x;
    private int y;
    private int range;

    public Spot(int x, int y)
    {
        this(x, y, DEFAULT_RANGE);
    }

    public Spot(int x, int y, int range)
    {
        this.x = x;
        this.y = y;
        this.range = range;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setRange(int range)
    {
        this.range = range;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getRange()
    {
        return range;
    }

    public boolean includePoint(int x, int y)
    {
        if( (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) <= range * range )
            return true;
        return false;
    }
}
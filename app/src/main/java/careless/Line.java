package careless;

/**
 * Created by taozhiheng on 15-3-20.
 */
public class Line {

    private float mStartX;
    private float mStartY;
    private float mStopX;
    private float mStopY;

    public Line(float startX, float startY, float stopX, float stopY)
    {
        this.mStartX = startX;
        this.mStartY = startY;
        this.mStopX = stopX;
        this.mStopY = stopY;
    }

    public void setStartX(float startX)
    {
        this.mStartX = startX;
    }

    public void setStartY(float startY)
    {
        this.mStartY = startY;
    }

    public void setStopX(float stopX)
    {
        this.mStopX = stopX;
    }

    public void setStopY(float stopY)
    {
        this.mStopY = stopY;
    }

    public float getStartX()
    {
        return mStartX;
    }

    public float getStartY()
    {
        return mStartY;
    }

    public float getStopX()
    {
        return mStopX;
    }

    public float getStopY()
    {
        return mStopY;
    }
}

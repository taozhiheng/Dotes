package drag;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by taozhiheng on 15-4-3.
 *
 */
public class Star extends Shape {

    private static Path mPath;
    private final static float sin18 = (float)Math.sin(Math.PI/10);
    private final static float cos18 = (float)Math.cos(Math.PI/10);
    private final static float sin54 = (float)Math.sin(Math.PI*3/10);
    private final static float cos54 = (float)Math.cos(Math.PI*3/10);

    public Star(int id, int ownerId, int index, float centerX, float centerY, float radius, String title, String content)
    {
        super(id, ownerId, index, centerX, centerY, radius, title, content);
        if(mPath == null)
            mPath = new Path();
    }

    public void drawShape(Canvas canvas, Paint paint)
    {
        float x = getCenterX();
        float y = getCenterY();
        float r = getRadius()*1.4f;
        mPath.reset();
        mPath.moveTo(x, y-r);
        mPath.lineTo(x-r*cos54*sin18/sin54, y-r*sin18);
        mPath.lineTo(x-r*cos18, y-r*sin18);
        mPath.lineTo(x-r*cos18*sin18/sin54, y+r*sin18*sin18/sin54);
        mPath.lineTo(x-r*cos54, y+r*sin54);
        mPath.lineTo(x, y+r*sin18/sin54);
        mPath.lineTo(x+r*cos54, y+r*sin54);
        mPath.lineTo(x+r*cos18*sin18/sin54, y+r*sin18*sin18/sin54);
        mPath.lineTo(x+r*cos18, y-r*sin18);
        mPath.lineTo(x+r*cos54*sin18/sin54, y-r*sin18);
        mPath.close();

        canvas.drawPath(mPath, paint);
    }

    public int getType()
    {
        return ShapeCreator.STAR;
    }

    public float getWritableLength()
    {
        return getRadius()*2*sin18*cos18/sin54;
    }
}

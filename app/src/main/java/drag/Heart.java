package drag;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by taozhiheng on 15-4-3.
 *
 */
public class Heart extends Shape{
    private static Path mPath;
    private final static float sqrt3 = (float) Math.sqrt(3);
    public Heart(int id, int ownerId, int index, float centerX, float centerY, float radius, String title, String content)
    {
        super(id, ownerId, index, centerX, centerY, radius, title, content);
        if(mPath == null)
            mPath = new Path();
    }

    public void drawShape(Canvas canvas, Paint paint)
    {
        float x = getCenterX();
        float y = getCenterY();
        float r = getRadius()/2*1.2f;
        mPath.reset();
        mPath.moveTo(x, y + r * sqrt3);
        mPath.lineTo(x - r * 3 / 2, y + r * (sqrt3 - 1) / 2);
        RectF rectF = new RectF(x - r * 2, y - r * 3 / 2, x, y + r / 2);
        mPath.arcTo(rectF, 120, 240);
        rectF.set(x, y - r * 3 / 2, x + r * 2, y + r / 2);
        mPath.arcTo(rectF, 180, 240);
        mPath.close();

        canvas.drawPath(mPath, paint);
    }

    public int getType()
    {
        return ShapeCreator.HEART;
    }

    public float getWritableLength()
    {
        return getRadius()*3/4;
    }
}

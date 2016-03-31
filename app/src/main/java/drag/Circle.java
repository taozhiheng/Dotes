package drag;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by taozhiheng on 15-4-3.
 *
 */
public class Circle extends Shape {

    public Circle(int id, int ownerId, int index, float centerX, float centerY, float radius, String title, String content)
    {
        super(id, ownerId, index, centerX, centerY, radius, title, content);
    }

    public void drawShape(Canvas canvas, Paint paint)
    {
        canvas.drawCircle(getCenterX(), getCenterY(), getRadius(), paint);
    }

    public int getType()
    {
        return ShapeCreator.CIRCLE;
    }
}

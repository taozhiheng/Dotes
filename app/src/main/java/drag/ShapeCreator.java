package drag;

/**
 * Created by taozhiheng on 15-4-3.
 *
 */
public class ShapeCreator {
    public final static int CIRCLE = 0;
    public final static int STAR = 1;
    public final static int HEART = 2;
    public static Shape createShape(int id, int ownerId, int index, float centerX, float centerY, float radius, String title, String content, int type)
    {
        switch(type)
        {
            case CIRCLE:
                return new Circle(id, ownerId, index, centerX, centerY, radius, title, content);
            case STAR:
                return new Star(id, ownerId, index, centerX, centerY, radius, title, content);
            case HEART:
                return new Heart(id, ownerId, index, centerX, centerY, radius, title, content);
        }
        return new Circle(id, ownerId, index, centerX, centerY, radius, title, content);
    }
}

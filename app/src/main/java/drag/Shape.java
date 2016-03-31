package drag;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taozhiheng on 15-3-21.
 * circle对象,记录圆心坐标,半径,文字
 */
public class Shape implements Parcelable{

    public static int DEFAULT_RADIUS = 70;

    private int mId;
    private int mOwnerId;
    private int mIndex;

    private float mCenterX;
    private float mCenterY;
    private float mRadius;

    private String mTitle;
    private String mContent;


    private boolean mChange;

    public void drawShape(Canvas canvas, Paint paint)
    {}

    public int getType()
    {
        return ShapeCreator.CIRCLE;
    }

    public float getWritableLength()
    {
        return mRadius*2;
    }

    private Shape()
    {}

    public Shape(int id, int ownerId, int index, float centerX, float centerY)
    {
        this(id, ownerId, index, centerX, centerY, null, null);
    }

    public Shape(int id, int ownerId, int index, float centerX, float centerY, float radius)
    {
        this(id, ownerId, index, centerX, centerY, radius, null, null);
    }

    public Shape(int id, int ownerId, int index, float centerX, float centerY, String title, String content)
    {
        this(id, ownerId, index, centerX, centerY,DEFAULT_RADIUS, title, content);
    }

    public Shape(int id, int ownerId, int index, float centerX, float centerY, float radius, String title, String content)
    {
        this.mId = id;
        this.mOwnerId = ownerId;
        this.mIndex = index;
        this.mCenterX = centerX;
        this.mCenterY = centerY;
        this.mRadius = radius;
        this.mTitle = title;
        this.mContent = content;
        mChange = false;
    }

    public void setCenterX(float centerX)
    {
        this.mCenterX = centerX;
        if(!mChange)
            mChange = true;
    }

    public void setCenterY(float centerY)
    {
        this.mCenterY = centerY;
        if(!mChange)
            mChange = true;
    }

    public void setRadius(float radius)
    {
        this.mRadius = radius;
        if(!mChange)
            mChange = true;
    }

    public void setTitle(String title)
    {
        this.mTitle = title;
        if(!mChange)
            mChange = true;
    }

    public void setContent(String content, boolean append)
    {
        if(append)
            this.mContent += content;
        else
            this.mContent = content;
        if(!mChange)
            mChange = true;
    }

    public void setChange(boolean change)
    {
        this.mChange = change;
    }

    public int getId()
    {
        return mId;
    }

    public int getOwnerId()
    {
        return mOwnerId;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public float getCenterX()
    {
        return mCenterX;
    }

    public float getCenterY()
    {
        return mCenterY;
    }

    public float getRadius()
    {
        return mRadius;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getContent()
    {
        return mContent;
    }


    public boolean getChange()
    {
        return mChange;
    }

    public static final Parcelable.Creator<Shape> CREATOR = new Creator<Shape>()
    {
        @Override
        public Shape createFromParcel(Parcel source) {
            Shape shape = new Shape();
            shape.mId = source.readInt();
            shape.mOwnerId = source.readInt();
            shape.mIndex = source.readInt();
            shape.mCenterX = source.readFloat();
            shape.mCenterY = source.readFloat();
            shape.mRadius = source.readFloat();
            shape.mTitle = source.readString();
            shape.mContent = source.readString();
            return shape;
        }

        @Override
        public Shape[] newArray(int size) {
            return new Shape[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeInt(mOwnerId);
        parcel.writeInt(mIndex);
        parcel.writeFloat(mCenterX);
        parcel.writeFloat(mCenterY);
        parcel.writeFloat(mRadius);
        parcel.writeString(mTitle);
        parcel.writeString(mContent);
    }
}

package recycler;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by taozhiheng on 15-3-26.
 *
 */
public class Record implements Parcelable{

    private int mId;
    private String mName;
    private int mState;
    private String mImagePath;
    private String mCreateTime;

    private Record()
    {}

    public Record(int id, String name, int state, String imagePath, long createTime)
    {
        this.mId = id;
        this.mName = name;
        this.mState = state;
        this.mImagePath = imagePath;
        this.mCreateTime = getTimeString(createTime);
    }

    private static String getTimeString(long time)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DATE);
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public void setState(int state)
    {
        this.mState = state;
    }

    public void setImagePath(String path)
    {
        this.mImagePath = path;
    }

    public int getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public int getState()
    {
        return mState;
    }

    public String getImagePath()
    {
        return mImagePath;
    }

    public String getCreateTime()
    {
        return mCreateTime;
    }

    public static final Parcelable.Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel source) {
            Record record = new Record();
            record.mId = source.readInt();
            record.mName = source.readString();
            record.mState = source.readInt();
            record.mImagePath = source.readString();
            return record;
        }
        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeInt(mState);
        parcel.writeString(mImagePath);
    }
}

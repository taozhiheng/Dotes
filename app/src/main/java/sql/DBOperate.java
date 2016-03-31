package sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import drag.Shape;
import drag.ShapeCreator;
import recycler.Record;

/**
 * Created by taozhiheng on 15-3-27.
 *
 */
public class DBOperate {

    private static DBOperate instance;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    public static DBOperate getInstance(Context context)
    {
        if(instance == null)
            instance = new DBOperate(context);
        return instance;
    }

    private DBOperate(Context context)
    {
        mDBHelper = new DBHelper(context);
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close()
    {
        if(!mDatabase.isOpen())
            mDatabase.close();
    }

    public long insertRecord(String name)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        return mDatabase.insert("record", null, createRecordValues(name));
    }

    public int deleteRecord(int id)
    {
        mDatabase.delete("shape", "owner_id="+id, null);
        return mDatabase.delete("record", "_id="+id, null);
    }

    public List<Record> getRecords()
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        List<Record> recordList;
        Cursor cursor = mDatabase.query("record", new String[]{"_id", "name", "state", "image_path", "create_time"},null, null, null, null, null);
        if(cursor.getCount()>0)
            recordList = new ArrayList<>();
        else
            return null;
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int state = cursor.getInt(2);
            String imagePath = cursor.getString(3);
            long createTime = cursor.getLong(4);
            recordList.add(new Record(id, name, state, imagePath, createTime));
        }
        cursor.close();
        return recordList;
    }

    public void updateRecord(Record record)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        mDatabase.update("record", createRecordValues(record), "_id="+record.getId(), null);
    }

    public long insertShape(int owner_id, int index,
                            float x, float y, float radius,
                            String title, String content, int type)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        return mDatabase.insert("shape", null, createShapeValues(owner_id, index, x, y, radius, title, content, type));
    }

    public int deleteShape(int id)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        return mDatabase.delete("shape", "_id="+id, null);
    }

    public List<Shape> getShapes(long owner)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        List<Shape> shapeList;
        Cursor cursor = mDatabase.query("shape", new String[]{"_id", "owner_id", "position", "x", "y", "radius", "title", "content", "type"},
            "owner_id="+owner, null, null, null, null);
        if(cursor.getCount()>0)
            shapeList = new ArrayList<>();
        else
            return null;
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            int ownerId = cursor.getInt(1);
            int index = cursor.getInt(2);
            float x = cursor.getFloat(3);
            float y = cursor.getFloat(4);
            float radius = cursor.getFloat(5);
            String title = cursor.getString(6);
            String content = cursor.getString(7);
            int type = cursor.getInt(8);
            shapeList.add(ShapeCreator.createShape(id, ownerId, index, x, y, radius, title, content, type));
        }
        cursor.close();
        return shapeList;
    }

    public void updateShape(Shape shape)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        mDatabase.update("shape", createShapeValues(shape),
                "_id="+ shape.getId(), null);
    }

    public void updateShapes(List<Shape> shapeList)
    {
        if(!mDatabase.isOpen())
        {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        for(Shape shape : shapeList)
        {
            if(shape.getChange()) {
                updateShape(shape);
            }
        }
    }

    private static ContentValues createRecordValues(String name)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("create_time", System.currentTimeMillis());
        contentValues.put("modify_time", System.currentTimeMillis());
        contentValues.put("state", 0);
        return contentValues;
    }

    private static ContentValues createRecordValues(Record record)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", record.getName());
        contentValues.put("modify_time", System.currentTimeMillis());
        contentValues.put("state", record.getState());
        contentValues.put("image_path", record.getImagePath());
        return contentValues;
    }

    private static ContentValues createShapeValues(int owner_id, int index,
                                                   float x, float y, float radius,
                                                   String title, String content, int type)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("owner_id", owner_id);
        contentValues.put("position", index);
        contentValues.put("x", x);
        contentValues.put("y", y);
        contentValues.put("radius", radius);
        contentValues.put("title", title);
        contentValues.put("content", content);
        contentValues.put("type", type);
        return contentValues;
    }

    private static ContentValues createShapeValues(Shape shape)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("owner_id", shape.getOwnerId());
        contentValues.put("position", shape.getIndex());
        contentValues.put("x", shape.getCenterX());
        contentValues.put("y", shape.getCenterY());
        contentValues.put("radius", shape.getRadius());
        contentValues.put("title", shape.getTitle());
        contentValues.put("content", shape.getContent());
        return contentValues;
    }
}

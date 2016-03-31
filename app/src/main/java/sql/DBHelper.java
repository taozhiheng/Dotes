package sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by taozhiheng on 14-12-17.
 *  自定义数据库辅助类
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "dotes.db";//数据库名称
    private static final int DB_VERSION = 1;         //数据库版本
    private Context myContext;                       //数据库context

    //创建record表
    //_id 自增
    //name　记录名
    //create_time　创建时间
    //modify_time　最后编辑时间
    //state　完成状态 ０－－新建　１－－确定起点　２－－确定终点　３－－完成
    //image_path 图片路径
    private static final String CREATE_RECORD = "CREATE TABLE record(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name varchar(50), create_time long, modify_time long, state INTEGER, image_path varchar(50));";
    //删除record表
    private static final String DELETE_RECORD = "DROP TABLE IF EXISTS record;";

    //创建shape表
    //_id
    //owner_id 隶属
    //index　索引
    //x　圆心坐标x
    //y　圆心坐标y
    //radius　半径
    //title　标题
    //content　内容
    //type 类型 ０－圆形　１－星形　２－心形
    private static final String CREATE_CIRCLE = "CREATE TABLE shape("+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, owner_id INTEGER, position INTEGER ,"+
            "x FLOAT, y FLOAT, radius FLOAT, title varchar(50), content varchar(200),type INTEGER);";
    private static final String DELETE_CIRCLE = "DROP TABLE IF EXISTS circle;";
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_CIRCLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(DELETE_RECORD);
        db.execSQL(CREATE_CIRCLE);
    }
}

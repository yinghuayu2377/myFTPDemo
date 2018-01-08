package Database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by lenovo on 2017/5/8.
 */
public class DownDB extends SQLiteOpenHelper{

    public static String Title_down="title_down";
    public static String Url_down="url_down";
    public static String State_down="state_down";
    public static String Index_down="index_down";
    public static String Url_down_path="url_down_path";
    private static final String database_name="wyq_db_3.db";
    public static String table_name="mytable";
    public DownDB(Context context){

        super(context,database_name,null,1);  //创建数据库
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table if not exists mytable(_id integer primary key autoincrement," +
                "Title_down text,Url_down text,Url_down_path text,State_down long,Index_down long)";   //创建一张表,_id会自增
        db.execSQL(sql); //执行sql语句

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists constants");  //删除表
        onCreate(db);  //并没有更新，只是重新建了一张表
    }
}

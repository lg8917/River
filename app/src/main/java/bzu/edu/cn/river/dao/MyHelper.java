package bzu.edu.cn.river.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LG on 2017/10/17.
 */

public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context){
        super(context,"edu.db",null,2);
    }
    public void onCreate(SQLiteDatabase db){
        System.out.println("onCreate");
        db.execSQL("CREATE TABLE account(_id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),balance INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        System.out.println("onUpgrade");
    }
}

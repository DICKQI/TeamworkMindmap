package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 直接操作数据库
 */
public class DBHelper extends SQLiteOpenHelper {

    private String dbName;


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.dbName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS tb_mindmap");
        db.execSQL("CREATE TABLE IF NOT EXISTS tb_mindmap(" +
                "_id long PRIMARY KEY," +
                "name varchar not null," +
                "share_id varchar," +
                "share_on Integer not null default 0," +
                "pwd varchar," +
                "owner_id long not null" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS tb_user_own_mindmap(" +
                "_id INTEGER PRIMARY KEY autoincrement," +
                "user_id long not null," +
                "mm_id long not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //  删除数据库
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(dbName);
    }

}
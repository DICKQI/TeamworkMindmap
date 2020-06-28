package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aqinn.mobilenetwork_teamworkmindmap.MyApplication;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aqinn
 * @date 2020/6/24 3:02 PM
 */
public class DBUtil {

//    /***********
//     * 单例模式 *
//     ***********/
//    private DBUtil(){}
//    public static DBUtil getInstance() {
//        return DBUtil.Inner.instance;
//    }
//    private static class Inner {
//        private static final DBUtil instance = new DBUtil();
//    }

    private static DBHelper dbHelper;
    private static SQLiteDatabase db;
    private static Cursor cursor;

    static {
        dbHelper = MyApplication.dbHelper;
    }

    public static long insertUserOwnMindmap(Long userId, Long mmId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("mm_id", mmId);
        long res = db.insert("tb_user_own_mindmap", null, values);
        if (res == -1)
            Log.d("xxx", "插入失败");
        else
            Log.d("xxx", "插入成功");
        db.close();
        return res;
    }

    public static int removeUserOwnMindmap(Long mmId) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete("tb_user_own_mindmap", "mm_id=?", new String[]{String.valueOf(mmId)});
        db.close();
        return result;
    }

    public static List<Long> getUserOwnMindmap(Long userId) {
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tb_user_own_mindmap WHERE user_id=" + userId, null);
        List<Long> longList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long mm_id = cursor.getLong(cursor.getColumnIndex("mm_id"));
            longList.add(mm_id);
        }
        cursor.close();
        db.close();
        return longList;
    }

    /**
     * 查询所有本机存储的思维导图
     *
     * @return
     */
    public static List<Mindmap> queryAllMindmap() {
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tb_mindmap", null);
        List<Mindmap> mmList = new ArrayList<>();
        Mindmap mm;
        while (cursor.moveToNext()) {
            Long _id = cursor.getLong(cursor.getColumnIndex("_id"));
            Long ownerId = cursor.getLong(cursor.getColumnIndex("owner_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            Long shareId = cursor.getLong(cursor.getColumnIndex("share_id"));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            int shareOn = Integer.parseInt(cursor.getString(cursor.getColumnIndex("share_on")));
            mm = new Mindmap(_id, ownerId, name, shareId, shareOn, pwd);
            mmList.add(mm);
        }
        cursor.close();
        db.close();
        return mmList;
    }

    /**
     * 根据mmId查询单个思维导图
     *
     * @param mmId
     * @return
     */
    public static Mindmap queryMindmapByMmId(Long mmId) {
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tb_mindmap WHERE _id=" + mmId, null);
        Mindmap mm = null;
        while (cursor.moveToNext()) {
            Long _id = cursor.getLong(cursor.getColumnIndex("_id"));
            Long ownerId = cursor.getLong(cursor.getColumnIndex("owner_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            Long shareId = cursor.getLong(cursor.getColumnIndex("share_id"));
            int shareOn = Integer.parseInt(cursor.getString(cursor.getColumnIndex("share_on")));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            mm = new Mindmap(_id, ownerId, name, shareId, shareOn, pwd);
        }
        cursor.close();
        db.close();
        return mm;
    }

    /**
     * 插入一个思维导图
     *
     * @param mm
     */
    public static long insertMindmap(Mindmap mm) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id", mm.getMmId());
        values.put("owner_id", mm.getOwnerId());
        values.put("name", mm.getName());
        values.put("share_id", mm.getShareId());
        values.put("share_on", mm.getShareOn());
        values.put("pwd", mm.getPwd());
        long res = db.insert("tb_mindmap", null, values);
        if (res == -1)
            Log.d("xxx", "插入失败");
        else
            Log.d("xxx", "插入成功");
        Log.d("xxx", mm.toString());
        db.close();
        return res;
    }

    /**
     * 根据mmId更新一个思维导图
     * 当参数为1的时候更新对应内容
     *
     * @param mm
     * @return
     */
    public static int updateMindmap(Mindmap mm, int name_, int shareId_, int shareOn_, int pwd_) {
        int result = -1;
        Mindmap t = queryMindmapByMmId(mm.getMmId());
        if (t == null)
            return result;
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (name_ == 1)
            values.put("name", mm.getName());
        if (shareId_ == 1)
            values.put("share_id", mm.getShareId());
        if (shareOn_ == 1)
            values.put("share_on", mm.getShareOn());
        if (pwd_ == 1)
            values.put("pwd", mm.getPwd());
        if (name_ != 1 && shareId_ != 1 && shareOn_ != 1 && pwd_ != 1)
            return result;
        result = db.update("tb_mindmap", values, "_id=?", new String[]{String.valueOf(t.getMmId())});
        db.close();
        return result;
    }

    /**
     * 根据mmId删除思维导图
     *
     * @param mmId
     * @return
     */
    public static int deleteToDoItem(Long mmId) {
        db = dbHelper.getWritableDatabase();
        int result = db.delete("tb_mindmap", "_id=?", new String[]{String.valueOf(mmId)});
        db.close();
        return result;
    }

}

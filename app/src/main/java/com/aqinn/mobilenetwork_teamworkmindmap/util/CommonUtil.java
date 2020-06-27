package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Aqinn
 * @date 2020/6/27 11:34 AM
 */
public class CommonUtil {


    /**
     * 存目前登录的账户
     * @param context
     * @param userId
     */
    public static void setUser(Context context, Long userId) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong("twmm_user", userId);
        edit.commit();
    }

    /**
     * 获取目前登录的账户
     * @param context
     * @return
     */
    public static Long getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        return preferences.getLong("twmm_user", -1L);
    }

    /**
     * 存记住的账户
     * @param context
     * @param rememberUserId
     */
    public static void setRememberUser(Context context, Long rememberUserId) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong("twmm_remember_user", rememberUserId);
        edit.commit();
    }

    /**
     * 获取记住的账户
     * @param context
     * @return
     */
    public static Long getRememberUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        return preferences.getLong("twmm_remember_user", -1L);
    }

    /**
     * 存记住的密码
     * @param context
     * @param rememberPwd
     */
    public static void setRememberPwd(Context context, String rememberPwd) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("twmm_remember_pwd", rememberPwd);
        edit.commit();
    }

    /**
     * 获取记住的密码
     * @param context
     * @return
     */
    public static String getRememberPwd(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        return preferences.getString("twmm_remember_pwd", "");
    }


}
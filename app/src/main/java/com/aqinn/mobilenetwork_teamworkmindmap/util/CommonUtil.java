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
     * 储存登录的cookie
     * @param context
     * @param userCookie
     */
    public static void setUserCookie(Context context, String userCookie) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("twmm_user_cookie", userCookie);
        edit.commit();
    }

    /**
     * 获取登录的cookie
     * @param context
     * @return
     */
    public static String getUserCookie(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        return preferences.getString("twmm_user_cookie", null);
    }

    /**
     * 存记住的账户
     * @param context
     * @param rememberUsername
     */
    public static void setRememberUser(Context context, String rememberUsername) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("twmm_remember_user", rememberUsername);
        edit.commit();
    }

    /**
     * 获取记住的账户
     * @param context
     * @return
     */
    public static String getRememberUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        return preferences.getString("twmm_remember_user", null);
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
        return preferences.getString("twmm_remember_pwd", null);
    }

    /**
     * 删除用户名
     * @param context
     */
    public static void deleteRememberUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("twmm_remember_user");
        edit.commit();
    }

    /**
     * 删除密码
     * @param context
     */
    public static void deleteRememberPwd(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("twmm_remember_pwd");
        edit.commit();
    }

    /**
     * 删除用户cookie
     * @param context
     */
    public static void deleteUserCookie(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("TWMMCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove("twmm_user_cookie");
        edit.commit();
    }
}
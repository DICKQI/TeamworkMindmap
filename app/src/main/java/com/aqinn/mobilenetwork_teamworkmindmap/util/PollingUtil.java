package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.aqinn.mobilenetwork_teamworkmindmap.service.PollingService;

/**
 * 轮询工具类
 * @author Aqinn
 * @date 2020/6/29 10:01 AM
 */
public class PollingUtil {

    public static final String ACTION = "com.aqinn.mobilenetwork_teamworkmindmap.service.PollingService";

    public static void teamworkBegin(Context context, Long shareId, int seconds, PollingService.PollingSuccessCallBack pollingSuccessCallBack) {
        PollingService.pollingSuccessCallBack = pollingSuccessCallBack;
        startPollingService(context, shareId,  seconds, PollingService.class, ACTION);
    }

    public static void stopTeamwork(Context context) {
        PollingService.pollingSuccessCallBack = null;
        stopPollingService(context, PollingService.class, ACTION);
    }

    //开启轮询服务
    public static void startPollingService(Context context,Long shareId, int seconds, Class<?> cls, String action) {
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //包装需要执行Service的Intent
        Intent intent = new Intent(context, cls);
        Bundle bundle = new Bundle();
        bundle.putLong("shareId", shareId);
        intent.putExtras(bundle);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //触发服务的起始时间
        long triggerAtTime = SystemClock.elapsedRealtime() + 5000;
        //使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
//        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
//                seconds, pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
    }

    //停止轮询服务
    public static void stopPollingService(Context context, Class<?> cls,String action) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //取消正在执行的服务
        manager.cancel(pendingIntent);
    }
}

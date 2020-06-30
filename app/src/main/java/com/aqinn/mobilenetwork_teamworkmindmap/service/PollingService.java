package com.aqinn.mobilenetwork_teamworkmindmap.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/29 10:02 AM
 */
public class PollingService extends Service {

    private static final String TAG = "PollingService";
    public static final String ACTION = "com.ryantang.service.PollingService";

    private MindMapManager mmm = MindMapManager.getInstance();
    public static PollingSuccessCallBack pollingSuccessCallBack = null;
    private Notification mNotification;
    private NotificationManager mManager;

    private static Long shareId_;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Long shareId = -1L;
        try {
            Bundle bundle = intent.getExtras();
            shareId = bundle.getLong("shareId");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (shareId != -1L) {
            Log.d(TAG, "onStartCommand: shareId获取正常, 轮询服务正常启动");
            new PollingThread(shareId).start();
            shareId_ = shareId;
        } else {
            Log.d(TAG, "onStartCommand: shareId获取出错, 轮询服务使用上次shareId继续执行");
            new PollingThread(shareId_).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    public void onStart(Intent intent, int startId) {
//        new PollingThread().start();
//    }

    //初始化通知栏配置
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 当进入消息详情Activity时，顶部状态栏的消息通知就会取消，使用如下方式也可以取消状态栏顶部的消息通知显示:
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancelAll();
//        mNotification = new Notification();
//        mNotification.tickerText = "New Message";
//        mNotification.defaults |= Notification.DEFAULT_SOUND;
//        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     *
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */
    int count = 0;

    class PollingThread extends Thread {

        private Long shareId;

        public PollingThread(Long shareId) {
            this.shareId = shareId;
        }

        @Override
        public void run() {
            Log.d(TAG, "onDestroy: 轮询更新节点服务开启");
            count++;
            //当计数能被5整除时弹出通知
            if (count % 1 == 0) {
                Log.d(TAG, "run: 第" + (count / 1 + 1) + "次轮询开始");
                if (pollingSuccessCallBack != null) {
                    Map<String, String> header = new HashMap<>();
                    header.put("Cookie", CommonUtil.getUserCookie(getApplicationContext()));
                    MyHttpUtil.get(PublicConfig.url_get_getMindmapDetail(shareId)
                            , header
                            , new MyHttpUtil.HttpCallbackListener() {
                                @Override
                                public void beforeFinish(HttpURLConnection connection) {
                                }

                                @Override
                                public void onFinish(String response) {
                                    JSONObject jo = JSONObject.parseObject(response);
                                    boolean status = jo.getBoolean("status");
                                    if (status) {
                                        Log.d(TAG, "run: 第" + (count / 1 + 1) + "次轮询成功");
                                    } else {
                                        Log.d(TAG, "run: 第" + (count / 1 + 1) + "次轮询出错");
                                        Log.d(TAG, "run: 轮询出错信息 => " + jo.getString("errMsg"));
                                    }
                                    Long shareId = jo.getInteger("shareId").longValue();
                                    String name = jo.getString("name");
                                    String auth = jo.getString("auth");
                                    Log.d(TAG, "onFinish: " + response);
                                    TreeModel<String> tm = mmm.json2tm(response);
                                    Log.d(TAG, "onFinish: " + tm.getNodeChildNodes(tm.getRootNode()));
                                    pollingSuccessCallBack.onSuccess(tm);
                                }

                                @Override
                                public void onError(Exception e, String response) {
                                    Log.d(TAG, "run: 第" + (count / 1 + 1) + "次轮询失败");
                                    e.printStackTrace();
                                    Log.d(TAG, "run: 轮询失败返回信息 => " + response);
                                    pollingSuccessCallBack.onError(e, response);
                                }
                            });
                } else {
                    Log.d(TAG, "run: 由于未设置处理方法, 第" + (count / 1 + 1) + "次轮询并未继续执行");
                }
            }
        }
    }

    public interface PollingSuccessCallBack {

        void onSuccess(TreeModel<String> tm);

        void onError(Exception e, String response);

    }

    public PollingSuccessCallBack getPollingSuccessCallBack() {
        return pollingSuccessCallBack;
    }

    public void setPollingSuccessCallBack(PollingSuccessCallBack pollingSuccessCallBack) {
        this.pollingSuccessCallBack = pollingSuccessCallBack;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: 轮询更新节点服务已关闭");
    }

}

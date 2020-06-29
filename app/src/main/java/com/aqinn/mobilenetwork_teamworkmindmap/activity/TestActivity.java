package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DensityUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.RightTreeLayoutManager;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeView;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private Button bt_test;

    // halo

    private Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initAllView();
    }

    private final String URL_REGISTER = "http://49.234.71.210/user/register/";
    private final String URL_LOGIN = "http://49.234.71.210/user/";

    @Override
    protected void onResume() {
        super.onResume();

//        bt_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, String> header = new HashMap<>();
//                header.put("Cookie", CommonUtil.getUserCookie(getApplicationContext()));
//                MyHttpUtil.get(PublicConfig.url_get_getMindmapDetail(shareId)
//                        , header
//                        , new MyHttpUtil.HttpCallbackListener() {
//                            @Override
//                            public void beforeFinish(HttpURLConnection connection) {
//                            }
//
//                            @Override
//                            public void onFinish(String response) {
//                                JSONObject jo = JSONObject.parseObject(response);
//                                boolean status = jo.getBoolean("status");
//                                if (status) {
//                                    Log.d(TAG, "run: 第" + (count / 5 + 1) + "次轮询成功");
//                                } else {
//                                    Log.d(TAG, "run: 第" + (count / 5 + 1) + "次轮询出错");
//                                    Log.d(TAG, "run: 轮询出错信息 => " + jo.getString("errMsg"));
//                                }
//                                Long shareId = jo.getInteger("shareId").longValue();
//                                String name = jo.getString("name");
//                                String auth = jo.getString("auth");
//                                TreeModel<String> tm = mmm.json2tm(response);
//                                pollingSuccessCallBack.onSuccess(tm);
//                            }
//
//                            @Override
//                            public void onError(Exception e, String response) {
//                                Log.d(TAG, "run: 第" + (count / 5 + 1) + "次轮询失败");
//                                e.printStackTrace();
//                                Log.d(TAG, "run: 轮询失败返回信息 => " + response);
//                                pollingSuccessCallBack.onError(e, response);
//                            }
//                        });
//            }
//        });
    }

    private void initAllView() {
        bt_test = findViewById(R.id.bt_test);
    }

}

// 各种写法

// 注册
/**
 * JSONObject jo = new JSONObject();
 *                 jo.put("email", "aqinnfortest@qq.com");
 *                 jo.put("nickname", "aqinn");
 *                 jo.put("password", "123");
 *                 String json = jo.toJSONString();
 *                 String url = URL_REGISTER;
 *                 MyHttpUtil.post(url, json, new MyHttpUtil.HttpCallbackListener() {
 *                     @Override
 *                     public void beforeFinish(HttpURLConnection connection) {
 *                         Log.d("xxx", "beforeFinish:\n" + connection.getHeaderFields().toString() + "\n");
 *                         Log.d("xxx", "beforeFinish: Set-Cookie\n" + connection.getHeaderField("Set-Cookie") + "\n");
 *                     }
 *                     @Override
 *                     public void onFinish(String response) {
 *                         Log.d("xxx", "onFinish:\n" + response + "\n");
 *                     }
 *
 *                     @Override
 *                     public void onError(Exception e, String response) {
 *                         Log.d("xxx", "onError e:\n" + e.getMessage() + "\n");
 *                         Log.d("xxx", "onError response:\n" + response + "\n");
 *                     }
 *                 });
 */

// 登录    登录要保存Header里的Set-Cookie
/**
 *  JSONObject jo = new JSONObject();
 *                 jo.put("email", "aqinnfortest@qq.com");
 *                 jo.put("password", "123");
 *                 String json = jo.toJSONString();
 *                 String url = URL_LOGIN;
 *                 Map<String, String> header = new HashMap<>();
 *                 MyHttpUtil.post(url, json, new MyHttpUtil.HttpCallbackListener() {
 *                     @Override
 *                     public void beforeFinish(HttpURLConnection connection) {
 *                         Log.d("xxx", "beforeFinish:\n" + connection.getHeaderFields().toString() + "\n");
 *                         Log.d("xxx", "beforeFinish: Set-Cookie\n" + connection.getHeaderField("Set-Cookie") + "\n");
 *                     }
 *                     @Override
 *                     public void onFinish(String response) {
 *                         Log.d("xxx", "onFinish:\n" + response + "\n");
 *                     }
 *
 *
 *                     @Override
 *                     public void onError(Exception e, String response) {
 *                         Log.d("xxx", "onError e:\n" + e.getMessage() + "\n");
 *                         Log.d("xxx", "onError response:\n" + response + "\n");
 *                     }
 *                 });
 */
/**
 * String url = URL_LOGIN;
 *                 Map<String, String> header = new HashMap<>();
 *                 header.put("Cookie", "sessionid=8p6ayn3i0fxyop96k0r47t5dhm2eeegb; expires=Sun, 12 Jul 2020 06:46:54 GMT; HttpOnly; Max-Age=1209600; Path=/; SameSite=Lax");
 *                 MyHttpUtil.get(url, header, new MyHttpUtil.HttpCallbackListener() {
 *                     @Override
 *                     public void beforeFinish(HttpURLConnection connection) {
 *                         Log.d("xxx", "beforeFinish:\n" + connection.getHeaderFields().toString() + "\n");
 *                         Log.d("xxx", "beforeFinish: Set-Cookie\n" + connection.getHeaderField("Set-Cookie") + "\n");
 *                     }
 *                     @Override
 *                     public void onFinish(String response) {
 *                         Log.d("xxx", "onFinish:\n" + response + "\n");
 *                     }
 *
 *                     @Override
 *                     public void onError(Exception e, String response) {
 *                         Log.d("xxx", "onError e:\n" + e.getMessage() + "\n");
 *                         Log.d("xxx", "onError response:\n" + response + "\n");
 *                     }
 *                 });
 */



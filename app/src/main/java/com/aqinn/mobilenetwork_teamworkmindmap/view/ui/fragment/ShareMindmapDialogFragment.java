package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.config.StyleConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DBUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/26 5:56 PM
 */
public class ShareMindmapDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private AbsoluteLayout al_share;
    private EditText et_share_id, et_pwd;
    private ImageView iv_share_or_not;

    // 其它
    private static final String TAG = "ShareMindmapDF";
    public static Dialog dialog;
    private MindMapManager mmm = MindMapManager.getInstance();
    private FileUtil fileUtil = FileUtil.getInstance();
    private Handler mHandler = new Handler();
    private Long mmId;
    private String name;
    private String pwd = "";
    private boolean shareOrNot;
    private Long shareId;
    private Drawable share_cancel, share_mm_blue;

    public ShareMindmapDialogFragment(Long mmId) {
        Mindmap mm = mmm.getMindmapByMmId(mmId);
        this.mmId = mmId;
        this.name = mm.getName();
        this.shareOrNot = mm.getShareOn() == 0 ? false : true;
        this.shareId = mm.getShareId();
        this.pwd = mmm.getMindmapByMmId(mmId).getPwd();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v_sm = inflater.inflate(R.layout.fragment_share_mindmap, container, false);
        initAllView(v_sm);
        return v_sm;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog = getDialog();
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        win.setAttributes(params);

        et_pwd.setText(this.pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_or_not:
                // 检查是否是自己的思维导图
                boolean flag = mmm.queryIsMeByMmId(mmId);
                if (!flag) {

                    break;
                }

                // 在此开/关协助 即 共享思维导图 上传到云端
                if (shareOrNot) {
                    Map<String, String> header = new HashMap<>();
                    header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                    header.put("Content-Type", "application/json");
                    MyHttpUtil.delete(PublicConfig.url_delete_closeShare(shareId), header, new MyHttpUtil.HttpCallbackListener() {
                        @Override
                        public void beforeFinish(HttpURLConnection connection) {

                        }

                        @Override
                        public void onFinish(String response) {
                            JSONObject jo = JSONObject.parseObject(response);
                            if (jo.getBoolean("status") == true) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mmm.mindmapShareOff(mmId) < 1) {
                                            Log.d(TAG, "onFinish: 云端导图协作关闭成功, 本地导图共享状态改变失败");
                                            return;
                                        }
                                        iv_share_or_not.setImageDrawable(share_mm_blue);
                                        et_share_id.setText("");
                                    }
                                });
                                Snackbar.make(getView(), "关闭共享成功", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                                Log.d(TAG, "onFinish: 关闭导图成功 => " + response);
                            } else {
                                Snackbar.make(getView(), "关闭共享失败", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                                Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                Log.d(TAG, "onFinish: 关闭协作失败 response => " + response);
                            }
                        }

                        @Override
                        public void onError(Exception e, String response) {
                            e.printStackTrace();
                            Log.d(TAG, "onFinish: 关闭协作失败 response => " + response);
                            Snackbar.make(getView(), "关闭共享失败", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    if (shareId == -1L) {
                        // 思维导图第一次共享的情况 即 后台数据库还没给本导图ShareId
                        Map<String, String> header = new HashMap<>();
                        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header.put("Content-Type", "application/json");
                        TreeModel<String> tree = null;
                        String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
                        try {
                            tree = fileUtil.readTreeObject(path + String.valueOf(mmId) + ".twmm");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        JSONObject jo = new JSONObject();
                        jo.put("name", name);
                        jo.put("password", et_pwd.getText().toString());
                        String jsonTree = mmm.tm2json(tree);
                        jo.put("node", JSONArray.parseArray(jsonTree));
                        MyHttpUtil.post(PublicConfig.url_post_firstShareOn(), header, jo.toJSONString(), new MyHttpUtil.HttpCallbackListener() {
                            @Override
                            public void beforeFinish(HttpURLConnection connection) {

                            }

                            @Override
                            public void onFinish(String response) {
                                System.out.println(response);
                                JSONObject jo = JSONObject.parseObject(response);
                                System.out.println(jo.get("shareID"));
                                JSONObject joo = (JSONObject)jo.get("roomMaster");
                                System.out.println(joo.get("name"));
                                System.out.println(joo.get("id"));
                                shareId = ((Integer)jo.get("shareID")).longValue();
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mmm.mindmapFirstShareOn(mmId, shareId, et_pwd.getText().toString()) >= 1) {
                                            shareOrNot = true;
                                            iv_share_or_not.setImageDrawable(share_cancel);
                                            et_share_id.setText(String.valueOf(shareId));
                                            Log.d(TAG, "第一次分享思维导图网络请求成功");
                                        } else {
                                            Log.d(TAG, "第一次分享思维导图网络请求完成，本地数据库存储未完成");
                                        }
                                        Snackbar.make(getView(), "开启共享成功", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                });

                            }

                            @Override
                            public void onError(Exception e, String response) {
                                e.printStackTrace();
                                Log.d(TAG, "onError: 第一次分享导图请求失败, response => " + response);
                            }
                        });
                    } else { // 非第一次共享该思维导图的情况 即 后台数据库已经有了本导图的ShareId
                        Map<String, String> header = new HashMap<>();
                        header.put("Cookie",  CommonUtil.getUserCookie(getActivity()));
                        header.put("Content-Type", "application/json");
                        TreeModel<String> tree = null;
                        String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
                        try {
                            tree = fileUtil.readTreeObject(path + String.valueOf(mmId) + ".twmm");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        JSONObject jo = new JSONObject();
                        jo.put("mapName", name);
                        // TODO 注意 现在后台请求的数据可以把密码加上了 即 第二次修改导图协作状态的时候可以更改密码
//                        jo.put("password", et_pwd.getText().toString());
                        String jsonTree = mmm.tm2json(tree);
                        jo.put("node", JSONArray.parseArray(jsonTree));
                        MyHttpUtil.put(PublicConfig.url_put_shareOnAgain(shareId), header, jo.toJSONString(), new MyHttpUtil.HttpCallbackListener() {
                            @Override
                            public void beforeFinish(HttpURLConnection connection) {

                            }

                            @Override
                            public void onFinish(String response) {
                                System.out.println(response);
                                JSONObject jo = JSONObject.parseObject(response);
                                JSONObject joo = (JSONObject)jo.get("roomMaster");
                                String username = joo.getString("name");
                                Long userId = joo.getInteger("id").longValue();
                                // TODO 记得把userId用起来 要是用不到的话 就把这句话删了
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mmm.mindmapShareOn(mmId, et_pwd.getText().toString()) >= 1) {
                                            shareOrNot = true;
                                            iv_share_or_not.setImageDrawable(share_cancel);
                                            et_share_id.setText(String.valueOf(shareId));
                                            Log.d(TAG, "非第一次分享思维导图网络请求成功");
                                        } else {
                                            Log.d(TAG, "非第一次分享思维导图网络请求完成，本地数据库存储未完成");
                                        }
                                        Snackbar.make(getView(), "开启共享成功", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                });

                            }
                            @Override
                            public void onError(Exception e, String response) {
                                e.printStackTrace();
                                Log.d(TAG, "onError: 非第一次分享导图请求失败, response => " + response);
                            }
                        });
                    }
                }
                break;
        }
    }

    private void initAllView(View v_sm) {
        al_share = v_sm.findViewById(R.id.al_share);
        et_share_id = v_sm.findViewById(R.id.et_share_id);
        et_pwd = v_sm.findViewById(R.id.et_pwd);
        iv_share_or_not = v_sm.findViewById(R.id.iv_share_or_not);

        et_share_id.setKeyListener(null);
        et_pwd.setMaxEms(10);
        iv_share_or_not.setOnClickListener(this);
        share_cancel = getResources().getDrawable(R.drawable.share_cancel);
        share_mm_blue = getResources().getDrawable(R.drawable.share_mm_blue);
        if (shareOrNot) {
            et_share_id.setText(String.valueOf(shareId));
            iv_share_or_not.setImageDrawable(share_cancel);
        } else {
            iv_share_or_not.setImageDrawable(share_mm_blue);
        }

    }

}

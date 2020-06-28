package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/26 5:56 PM
 */
public class ShareMindmapDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private EditText et_share_id, et_pwd;
    private ImageView iv_share_or_not;

    // 其它
    private MindMapManager mmm = MindMapManager.getInstance();
    private FileUtil fileUtil = FileUtil.getInstance();
    private Long mmId;
    private String name;
    private boolean shareOrNot;
    private Long shareId;
    public static Dialog dialog;
    private Drawable share_cancel, share_mm_blue;

    public ShareMindmapDialogFragment(Long mmId, String name, boolean shareOrNot, Long shareId) {
        this.mmId = mmId;
        this.name = name;
        this.shareOrNot = shareOrNot;
        this.shareId = shareId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);

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
//        win.setLayout(850, 985);
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        win.setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_or_not:
                // TODO 在此共享思维导图 上传到云端
                if (shareOrNot) {
                    // TODO 目前是开共享状态的，点击后会变关，等科科的后台：关协助
                    iv_share_or_not.setImageDrawable(share_mm_blue);
                    et_share_id.setText("");
                } else {
                    if (shareId != -1L) {
                        // 第一次共享
                        Map<String, String> header = new HashMap<>();
//                        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header.put("Cookie",  "sessionid=8p6ayn3i0fxyop96k0r47t5dhm2eeegb; expires=Sun, 12 Jul 2020 06:46:54 GMT; HttpOnly; Max-Age=1209600; Path=/; SameSite=Lax");
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
                        System.out.println(jo.toJSONString());
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
                                mmm.mindmapFirstShareOn(mmId, (Long) jo.get("shareID"));
                                shareOrNot = true;
                            }

                            @Override
                            public void onError(Exception e, String response) {
                                e.printStackTrace();
                                System.out.println("response => " + response);
                            }
                        });
                    } else { // 非第一次共享了
                        Map<String, String> header = new HashMap<>();
//                        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header.put("Cookie",  "sessionid=8p6ayn3i0fxyop96k0r47t5dhm2eeegb; expires=Sun, 12 Jul 2020 06:46:54 GMT; HttpOnly; Max-Age=1209600; Path=/; SameSite=Lax");
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
//                        jo.put("password", et_pwd.getText().toString());
                        String jsonTree = mmm.tm2json(tree);
                        jo.put("node", JSONArray.parseArray(jsonTree));
                        System.out.println(jo.toJSONString());
                        MyHttpUtil.put(PublicConfig.url_put_shareOnAgain(shareId), header, jo.toJSONString(), new MyHttpUtil.HttpCallbackListener() {
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
                                mmm.mindmapShareOn(mmId);
                                shareOrNot = true;
                            }

                            @Override
                            public void onError(Exception e, String response) {
                                e.printStackTrace();
                                System.out.println("response => " + response);
                            }
                        });
                    }
                    if (!shareOrNot)
                        break;
                    iv_share_or_not.setImageDrawable(share_cancel);
                    et_share_id.setText("123456");
                }
                break;
        }
    }

    private void initAllView(View v_sm) {
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

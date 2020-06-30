package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.MindmapActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindmapAdapter;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.IndexFragment.mma;

/**
 * @author Aqinn
 * @date 2020/6/27 2:29 AM
 */
public class FindMindmapDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private AbsoluteLayout al_find;
    private EditText et_share_id, et_pwd;
    private Button bt_confirm;

    // 其它
    private static final String TAG = "FindMindmapDF";
    public static Dialog dialog;
    private MindMapManager mmm = MindMapManager.getInstance();
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v_fm = inflater.inflate(R.layout.fragment_find_mindmap, container, false);
        initAllView(v_fm);
        return v_fm;
    }

    private void initAllView(View v_fm) {
        al_find = v_fm.findViewById(R.id.al_find);
        bt_confirm = v_fm.findViewById(R.id.bt_confirm);
        et_share_id = v_fm.findViewById(R.id.et_share_id);
        et_pwd = v_fm.findViewById(R.id.et_pwd);

        bt_confirm.setOnClickListener(this::onClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        dialog = getDialog();
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        win.setAttributes(params);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_confirm:
                Map<String, String> header = new HashMap<>();
                header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                header.put("Content-Type", "application/json");
                JSONObject jo = new JSONObject();
                jo.put("password", et_pwd.getText().toString());
                String data = jo.toJSONString();
                MyHttpUtil.post(PublicConfig.url_post_joinTeamWorkMindmap(Long.valueOf(et_share_id.getText().toString()))
                        , header
                        , data
                        , new MyHttpUtil.HttpCallbackListener() {
                            @Override
                            public void beforeFinish(HttpURLConnection connection) {

                            }

                            @Override
                            public void onFinish(String response) {
                                System.out.println(response);
                                JSONObject jo = JSONObject.parseObject(response);
                                if (jo.getBoolean("status") == true) {
                                    Long createUserId = jo.getJSONObject("roomMaster").getInteger("id").longValue();
                                    String mapName = jo.getString("mapName");
                                    MyHttpUtil.get(PublicConfig.url_get_getMindmapDetail(Long.valueOf(et_share_id.getText().toString()))
                                            , header
                                            , new MyHttpUtil.HttpCallbackListener() {
                                                @Override
                                                public void beforeFinish(HttpURLConnection connection) {

                                                }

                                                @Override
                                                public void onFinish(String response) {
                                                    JSONObject joo = JSONObject.parseObject(response);
                                                    if (joo.getBoolean("status") == true) {
                                                        TreeModel<String> tm = mmm.json2tm(response);
                                                        System.out.println("xxxxxxxxxxxxxxx ====>" +response);
                                                        List<NodeModel<String>> list = tm.getNodeChildNodes(tm.getRootNode());
                                                        System.out.println(list);
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Mindmap mm = mmm.createMindmap(CommonUtil.getUser(getActivity()),createUserId, mapName, false);
                                                                mm.setTm(tm);
                                                                if (mmm.saveMindmap(mm)) {
                                                                    Log.d(TAG, "onFinish: 加上协作成功, 获取协作导图信息成功, 保存导图信息成功");
                                                                    // TODO 后期待改进：这样直接更换一个Adapter有点low，
                                                                    //  但是没办法，正常的notifyDataSetChanged()会把第一个"新建思维导图"给删掉
                                                                    List<Mindmap> mindmapsTemp = new ArrayList<>();
                                                                    mma.getMindmaps().add(1, mm);
                                                                    for (int i = 0; i < mma.getMindmaps().size(); i++) {
                                                                        mindmapsTemp.add(i, mma.getMindmaps().get(i));
                                                                    }
                                                                    mma = new MindmapAdapter(getActivity(), mindmapsTemp);
                                                                    IndexFragment.gv_main.setAdapter(mma);
                                                                    Intent intent = new Intent(getActivity(), MindmapActivity.class);
                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putLong("mmId", mm.getMmId());
                                                                    bundle.putString("name", mm.getName());
                                                                    bundle.putBoolean("isMe", false);
                                                                    bundle.putLong("shareId", Long.parseLong(et_share_id.getText().toString()));
                                                                    intent.putExtras(bundle);
                                                                    startActivity(intent);
                                                                    dismiss();
                                                                } else {
                                                                    Log.d(TAG, "onFinish: 加上协作成功, 获取协作导图信息成功, 保存导图信息失败");
                                                                }
                                                            }
                                                        });
                                                        Log.d(TAG, "onFinish: 加上协作成功, 获取协作导图信息成功");
                                                    } else {
                                                        Log.d(TAG, "onFinish: 加入协作成功, 但是获取协作导图信息出错 response => " + response);
                                                        Snackbar.make(al_find, "加入协作成功, 但是获取协作导图信息出错", Snackbar.LENGTH_SHORT)
                                                                .setAction("Action", null).show();
                                                    }
                                                }

                                                @Override
                                                public void onError(Exception e, String response) {
                                                    Log.d(TAG, "onError: errMsg => " + jo.getString("errMsg"));
                                                    Log.d(TAG, "onError: 加入协作成功, 但是获取协作导图信息请求失败 response => " + response);
                                                    Snackbar.make(al_find, "加入协作成功, 但是获取协作导图信息请求失败", Snackbar.LENGTH_SHORT)
                                                            .setAction("Action", null).show();
                                                }
                                            });

                                    Log.d(TAG, "onFinish: 加入协作成功");
                                } else {
                                    Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                    Snackbar.make(al_find, "加入协作失败", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                            }

                            @Override
                            public void onError(Exception e, String response) {
                                e.printStackTrace();
                                Log.d(TAG, "onError: 加入协作时请求失败 response => " + response);
                                Snackbar.make(al_find, "加入协作失败", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        });
                break;
        }
    }
}

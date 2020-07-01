package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.IndexActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.MindmapActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindmapAdapter;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.MyGridView;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/14 10:40 PM
 */
public class IndexFragment extends Fragment {

    // 组件
    public static GridView gv_main;

    // 其它
    private static final String TAG = "IndexFragment";
    public static MindmapAdapter mma;
    private MindMapManager mmm = MindMapManager.getInstance();
    private Handler mHandler = new Handler();
    private int selectItemIndex = 0;

    public static IndexFragment newInstance() {
        IndexFragment fragment = new IndexFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(ARG_SECTION_NUMBER, index);
//        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initAllView();

        final List<Mindmap> mindmaps = initData();
        mma = new MindmapAdapter(getActivity(), mindmaps);
        gv_main.setAdapter(mma);
        gv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    CreateMindmapDialogFragment cmdf = new CreateMindmapDialogFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    cmdf.show(ft, "createMindmapDialogFragment");
                } else {
                    Mindmap mm = mindmaps.get(position);
                    if (position != 0) { // 改变此条件可以实现测试效果 position == 1
                        boolean isMe = mmm.queryIsMeByMmId(mm.getMmId());
                        Intent intent = new Intent(getActivity(), MindmapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("mmId", mm.getMmId());
                        bundle.putString("name", mm.getName());
                        bundle.putBoolean("isMe", isMe);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
//                    Snackbar.make(view, "您点击的思维导图的名称是: " + mm.getName(), Snackbar.LENGTH_SHORT)
//                            .setAction("Action", null).show();
                }
            }
        });
        gv_main.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                selectItemIndex = info.position;
                if (selectItemIndex == 0)
                    return;
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.mindmap_grid_item_menu, menu);
            }
        });

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Long mmIdTemp = mma.getMindmaps().get(selectItemIndex).getMmId();
                ShareMindmapDialogFragment sdf = new ShareMindmapDialogFragment(mmIdTemp);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                sdf.show(ft, "shareMindmapDialogFragment");
                Snackbar.make(gv_main, "分享给他人协作", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.delete:
                // 删除本地思维导图文件和数据库的文件，
                // TODO 如果已经分享出去的导图且是思维导图的创建者，还需要执行关协作以及删除shareId
                Long mmIdDelTemp = mma.getMindmaps().get(selectItemIndex).getMmId();
                if (mmm.deleteMindmap(mmIdDelTemp)) {
                    // TODO 后期待改进：这样直接更换一个Adapter有点low，
                    //  但是没办法，正常的notifyDataSetChanged()会把第一个"新建思维导图"给删掉
                    List<Mindmap> mindmapsTemp = new ArrayList<>();
                    Mindmap tempMindmap = mma.getMindmaps().get(selectItemIndex);
                    mma.getMindmaps().remove(selectItemIndex);
                    for (int i = 0; i < mma.getMindmaps().size(); i++) {
                        mindmapsTemp.add(i, mma.getMindmaps().get(i));
                    }
                    mma = new MindmapAdapter(getActivity(), mindmapsTemp);
                    gv_main.setAdapter(mma);
                    Log.d(TAG, "onContextItemSelected: 思维导图本地删除成功");

                    boolean flag = mmm.queryIsMeByMmId(mmIdDelTemp);
                    if (!flag) {
                        Mindmap mm  = mmm.getMindmapByMmId(mmIdDelTemp);
                        Map<String, String> header2 = new HashMap<>();
                        header2.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header2.put("Content-Type", "application/json");
                        MyHttpUtil.delete(PublicConfig.url_delete_exitTeamWorkMindmap(mm.getShareId())
                                , header2
                                , new MyHttpUtil.HttpCallbackListener() {
                                    @Override
                                    public void beforeFinish(HttpURLConnection connection) {

                                    }

                                    @Override
                                    public void onFinish(String response) {
                                        JSONObject jo = JSONObject.parseObject(response);
                                        if (jo.getBoolean("status")) {
                                            Snackbar.make(gv_main, "退出协作成功", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(gv_main, "退出协作失败", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e, String response) {
                                        Snackbar.make(gv_main, "退出协作失败", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                });
                    }


                    if (tempMindmap.getShareId() != 0L
                            || tempMindmap.getShareId() != -1L) {
                        Map<String, String> header = new HashMap<>();
                        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header.put("Content-Type", "application/json");
                        Log.d(TAG, "onContextItemSelected: " + PublicConfig.url_delete_deleteMindmap(mmIdDelTemp));
                        MyHttpUtil.delete(PublicConfig.url_delete_deleteMindmap(tempMindmap.getShareId())
                                , header
                                , new MyHttpUtil.HttpCallbackListener() {
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
                                                }
                                            });
                                            Log.d(TAG, "onFinish: 删除在线导图成功 => " + response);
                                            Snackbar.make(gv_main, "导图删除成功", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                            Log.d(TAG, "onFinish: 删除导图失败 => " + response);
                                            Snackbar.make(gv_main, "本地导图删除成功,云端导图未删除", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                            Mindmap mm  = mmm.getMindmapByMmId(mmIdDelTemp);
                                            Map<String, String> header2 = new HashMap<>();
                                            header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                                            header.put("Content-Type", "application/json");
                                            MyHttpUtil.delete(PublicConfig.url_delete_exitTeamWorkMindmap(mm.getShareId())
                                                    , header2
                                                    , new MyHttpUtil.HttpCallbackListener() {
                                                        @Override
                                                        public void beforeFinish(HttpURLConnection connection) {

                                                        }

                                                        @Override
                                                        public void onFinish(String response) {
                                                            JSONObject jo = JSONObject.parseObject(response);
                                                            if (jo.getBoolean("status")) {
                                                                Snackbar.make(gv_main, "退出协作成功", Snackbar.LENGTH_SHORT)
                                                                        .setAction("Action", null).show();
                                                            } else {
                                                                Snackbar.make(gv_main, "退出协作失败", Snackbar.LENGTH_SHORT)
                                                                        .setAction("Action", null).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Exception e, String response) {
                                                            Snackbar.make(gv_main, "退出协作失败", Snackbar.LENGTH_SHORT)
                                                                    .setAction("Action", null).show();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e, String response) {
                                        e.printStackTrace();
                                        Log.d(TAG, "onError: 删除导图失败: " + response);
                                        Snackbar.make(gv_main, "本地导图删除成功,云端导图删除失败", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                });
                        break;
                    }
                } else {
                    Snackbar.make(gv_main, "删除失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.d(TAG, "onContextItemSelected: 删除失败");
                    break;
                }
                // TODO 后期待改进：这样直接更换一个Adapter有点low，
                //  但是没办法，正常的notifyDataSetChanged()会把第一个"新建思维导图"给删掉
                List<Mindmap> mindmapsTemp = new ArrayList<>();
                mma.getMindmaps().remove(selectItemIndex);
                for (int i = 0; i < mma.getMindmaps().size(); i++) {
                    mindmapsTemp.add(i, mma.getMindmaps().get(i));
                }
                mma = new MindmapAdapter(getActivity(), mindmapsTemp);
                gv_main.setAdapter(mma);
                break;
        }
        return true;
    }

    private List<Mindmap> initData() {
        List<Mindmap> mindmaps = mmm.getUserAllMindmap(CommonUtil.getUser(getActivity()));
        Mindmap add = new Mindmap("add");
        mindmaps.add(0, add);
        return mindmaps;
    }

    private void initAllView() {
        gv_main = getActivity().findViewById(R.id.gv_main);
    }


}
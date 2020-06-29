package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DensityUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.NodeView;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.RightTreeLayoutManager;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeView;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeViewItemClick;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeViewItemLongClick;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.CreateMindmapDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.EditMindnodeDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/15 2:48 PM
 */
public class MindmapActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // 组件
    private RelativeLayout rl_treeView;
    private TreeView treev_mainTreeView;
    private RelativeLayout rl_fbts;
    private FloatingActionButton fbt_saveAndExit, fbt_subNode, fbt_node, fbt_focusMid;
    private ImageView iv_app_icon;

    // 基本
    private String name;
    private Long mmId;

    //其它
    private static final String TAG = "MindmapActivity";
    private MindMapManager mmm = MindMapManager.getInstance();
    private FileUtil fileUtil = FileUtil.getInstance();
    private Handler mHandler = new Handler();
    private Mindmap mm;
    private float x_down = -1, y_down = -1, x_down_fbts = -1, y_down_fbts = -1;
    private int[] rl_fbts_location = new int[2];
    private int x_move, y_move, x_move_fbts, y_move_fbts;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindmap);

        int checkWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            //如果没有权限则获取权限 requestCode在后面回调中会用到
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }

        try {
            Bundle bundle = getIntent().getExtras();
            name = bundle.getString("name");
            mmId = bundle.getLong("mmId");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        initAllView();

        initTreeModel();

        treev_mainTreeView.setTreeViewItemClick(new TreeViewItemClick() {
            @Override
            public void onItemClick(View item) {
                // TODO 暂时不知道做什么 2020.6.15.20:55
            }
        });

        treev_mainTreeView.setTreeViewItemLongClick(new TreeViewItemLongClick() {
            @Override
            public void onLongClick(View view) {
                showEdit("修改节点", treev_mainTreeView.getCurrentFocusNode().value, 3);
            }
        });

        rl_treeView.setOnTouchListener(this);

        rl_fbts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x_down_fbts = event.getRawX();
                        y_down_fbts = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x_sub = event.getRawX() - x_down_fbts;
                        float y_sub = event.getRawY() - y_down_fbts;
                        rl_fbts.getLocationOnScreen(rl_fbts_location);
                        x_move_fbts = rl_fbts_location[0];
                        y_move_fbts = rl_fbts_location[1];
                        rl_fbts.setX(rl_fbts.getX() + x_sub);
                        rl_fbts.setY(rl_fbts.getY() + y_sub);
                        x_down_fbts = event.getRawX();
                        y_down_fbts = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                treev_mainTreeView.focusMidLocation();
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mmm.saveTree(mmId, treev_mainTreeView.getTreeModel());
    }

    private void initTreeModel() {
        TreeModel<String> tree = null;
        String path = Environment.getExternalStorageDirectory().getPath() + PublicConfig.MINDMAPS_FILE_LOCATION + PublicConfig.CONTENT_LOCATION;
        try {
            tree = fileUtil.readTreeObject(path + String.valueOf(mmId) + ".twmm");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        if (tree == null) {
//            final NodeModel<String> teamwork_mindmap = new NodeModel<>("teamwork_mindmap");
//            final NodeModel<String> dk = new NodeModel<>("dk");
//            final NodeModel<String> zzq = new NodeModel<>("zzq");
//            final NodeModel<String> zzf = new NodeModel<>("zzf");
//             NodeModel<String> gjr = new NodeModel<>("gjr");
//            final NodeModel<String> gjn = new NodeModel<>("gjn");
//            gjr.childNodes.add(gjn);
//
//
//            tree = new TreeModel<>(teamwork_mindmap);
//            tree.addNode(teamwork_mindmap, dk, zzq, gjr);
//            tree.addNode(zzq, zzf);
////            /**
////             * 测试一下
////             */
////            String json = "{\"node\":[{\"content\":\"A\",\"pid\":0,\"nid\":1},{\"content\":\"B\",\"pid\":1,\"nid\":2},{\"content\":\"C\",\"pid\":1,\"nid\":3},{\"content\":\"D\",\"pid\":5,\"nid\":4},{\"content\":\"E\",\"pid\":2,\"nid\":5}],\"shareId\":\"1\"}";
////            tree = mmm.json2tm(json);
//        }

        int dx = DensityUtils.dp2px(this, 20);
        int dy = DensityUtils.dp2px(this, 20);
        int mHeight = DensityUtils.dp2px(this, 720);

        treev_mainTreeView.setTreeLayoutManager(new RightTreeLayoutManager(dx, dy, mHeight));
        treev_mainTreeView.setTreeModel(tree);
    }

    private void showEdit(String title, String content, int status) {
        mm = mmm.getMindmapByMmId(mmId);
        final EditMindnodeDialogFragment emdf = new EditMindnodeDialogFragment(title, content, status);
        emdf.setOnEditFragmentListener((String mnContent) -> {
            switch (status) {
                case 1:
                    if (treev_mainTreeView.getCurrentFocusNode().parentNode == null) {
                        Snackbar.make(treev_mainTreeView, "根节点不能添加同级节点", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                    Long nIdTemp = -1L;
                    if (mm.getShareOn() == 0) {
                        nIdTemp = mmm.getNewNodeId(mmId);
                        treev_mainTreeView.addNode(nIdTemp, mnContent);
                    } else {
                        Map<String, String> header = new HashMap<>();
//                        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header.put("Cookie", CommonUtil.getUserCookie(this));
                        header.put("Content-Type", "application/json");
                        JSONObject jo = new JSONObject();
                        jo.put("content", mnContent);
                        String data = jo.toJSONString();
                        MyHttpUtil.post(PublicConfig.url_post_addNode(mm.getShareId()
                                , treev_mainTreeView.getCurrentFocusNode().parentNode.nId)
                                , header, data
                                , new MyHttpUtil.HttpCallbackListener() {
                                    @Override
                                    public void beforeFinish(HttpURLConnection connection) {

                                    }

                                    @Override
                                    public void onFinish(String response) {
                                        System.out.println(response);
                                        JSONObject jo = JSONObject.parseObject(response);
                                        if (jo.getBoolean("status") == true) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    treev_mainTreeView.addNode(jo.getLong("nodeId"), mnContent);
                                                }
                                            });
                                            Log.d(TAG, "showEdit: 添加同级节点成功 => " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                        } else {
                                            Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                            Log.d(TAG, "showEdit: 添加同级节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                            Snackbar.make(treev_mainTreeView, "添加同级节点失败", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e, String response) {
                                        e.printStackTrace();
                                        System.out.println("response: " + response);
                                        Log.d(TAG, "showEdit: 添加同级节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                        Snackbar.make(treev_mainTreeView, "添加同级节点失败", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                });
                    }
                    break;
                case 2:
//                    Long nId2 = mmm.getNewNodeId(mmId);
//                    if (nId2 != -1L) {
//                        treev_mainTreeView.addSubNode(nId2, mnContent);
//                    } else {
//                        Log.d(TAG, "showEdit: 添加子级节点失败 => " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
//                        Snackbar.make(treev_mainTreeView, "添加子级节点失败", Snackbar.LENGTH_SHORT)
//                                .setAction("Action", null).show();
//                    }
                    Long nIdTempSub = -1L;
                    if (mm.getShareOn() == 0) {
                        nIdTempSub = mmm.getNewNodeId(mmId);
                        treev_mainTreeView.addSubNode(nIdTempSub, mnContent);
                    } else {
                        Map<String, String> header = new HashMap<>();
//                        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
                        header.put("Cookie", CommonUtil.getUserCookie(this));
                        header.put("Content-Type", "application/json");
                        JSONObject jo = new JSONObject();
                        jo.put("content", mnContent);
                        String data = jo.toJSONString();
                        MyHttpUtil.post(PublicConfig.url_post_addNode(mm.getShareId()
                                , treev_mainTreeView.getCurrentFocusNode().nId)
                                , header, data
                                , new MyHttpUtil.HttpCallbackListener() {
                                    @Override
                                    public void beforeFinish(HttpURLConnection connection) {

                                    }

                                    @Override
                                    public void onFinish(String response) {
                                        System.out.println(response);
                                        JSONObject jo = JSONObject.parseObject(response);
                                        if (jo.getBoolean("status") == true) {
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    treev_mainTreeView.addSubNode(jo.getLong("nodeId"), mnContent);
                                                }
                                            });
                                            Log.d(TAG, "showEdit: 添加子级节点成功 => " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                        } else {
                                            Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                            Log.d(TAG, "showEdit: 添加子级节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                            Snackbar.make(treev_mainTreeView, "添加子级节点失败", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e, String response) {
                                        e.printStackTrace();
                                        System.out.println("response: " + response);
                                        Log.d(TAG, "showEdit: 添加子级节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                        Snackbar.make(treev_mainTreeView, "添加子级节点失败", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                });
                    }
                    break;
                case 3:
                    // TODO
                    if ("zzf删除专用代码aqinn删除专用代码biu删除专用代码".equals(mnContent)) {
                        if (treev_mainTreeView.getTreeModel().getRootNode() == treev_mainTreeView.getCurrentFocusNode()) {
                            Snackbar.make(treev_mainTreeView, "根节点不能删除", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {
                            if (mm.getShareOn() == 0) {
                                treev_mainTreeView.deleteNode(treev_mainTreeView.getCurrentFocusNode());
                                Log.d(TAG, "showEdit: 删除节点成功: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                            } else {
                                Map<String, String> header = new HashMap<>();
                                header.put("Cookie", CommonUtil.getUserCookie(this));
                                header.put("Content-Type", "application/json");
                                JSONObject jo = new JSONObject();
                                jo.put("content", mnContent);
                                String data = jo.toJSONString();
                                MyHttpUtil.delete(PublicConfig.url_delete_deleteNodeAndSubNode(mm.getShareId()
                                        , treev_mainTreeView.getCurrentFocusNode().nId)
                                        , header
                                        , new MyHttpUtil.HttpCallbackListener() {
                                            @Override
                                            public void beforeFinish(HttpURLConnection connection) {

                                            }

                                            @Override
                                            public void onFinish(String response) {
                                                System.out.println(response);
                                                JSONObject jo = JSONObject.parseObject(response);
                                                if (jo.getBoolean("status") == true) {
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            treev_mainTreeView.deleteNode(treev_mainTreeView.getCurrentFocusNode());
                                                        }
                                                    });
                                                    Log.d(TAG, "showEdit: 删除节点成功 => " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                                } else {
                                                    Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                                    Log.d(TAG, "showEdit: 删除节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                                    Snackbar.make(treev_mainTreeView, "删除失败", Snackbar.LENGTH_SHORT)
                                                            .setAction("Action", null).show();
                                                }
                                            }

                                            @Override
                                            public void onError(Exception e, String response) {
                                                e.printStackTrace();
                                                System.out.println("response: " + response);
                                                Log.d(TAG, "showEdit: 删除节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                                Snackbar.make(treev_mainTreeView, "删除节点失败", Snackbar.LENGTH_SHORT)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                            }
                        }
                        break;
                    } else {
                        if (mm.getShareOn() == 0) {
                            treev_mainTreeView.changeNodeValue(treev_mainTreeView.getCurrentFocusNode(), mnContent);
                            Log.d(TAG, "showEdit: 修改节点成功: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                        } else {
                            Map<String, String> header = new HashMap<>();
                            header.put("Cookie", CommonUtil.getUserCookie(this));
                            header.put("Content-Type", "application/json");
                            JSONObject jo = new JSONObject();
                            jo.put("content", mnContent);
                            String data = jo.toJSONString();
                            MyHttpUtil.put(PublicConfig.url_put_editNode(mm.getShareId()
                                    , treev_mainTreeView.getCurrentFocusNode().nId)
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
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        treev_mainTreeView.changeNodeValue(treev_mainTreeView.getCurrentFocusNode(), mnContent);
                                                    }
                                                });
                                                Log.d(TAG, "showEdit: 修改节点成功 => " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                            } else {
                                                Log.d(TAG, "onFinish: errMsg => " + jo.getString("errMsg"));
                                                Log.d(TAG, "showEdit: 修改节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                                Snackbar.make(treev_mainTreeView, "修改失败", Snackbar.LENGTH_SHORT)
                                                        .setAction("Action", null).show();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e, String response) {
                                            e.printStackTrace();
                                            System.out.println("response: " + response);
                                            Log.d(TAG, "showEdit: 修改节点失败: " + (mm.getShareOn() == 0 ? "关共享模式" : "开共享模式"));
                                            Snackbar.make(treev_mainTreeView, "修改节点失败", Snackbar.LENGTH_SHORT)
                                                    .setAction("Action", null).show();
                                        }
                                    });
                        }
                    }
                    break;
            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        emdf.show(ft, "editMindmapDialogFragment");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbt_node:
                showEdit("添加同级节点", "", 1);
                break;
            case R.id.fbt_subNode:
                showEdit("添加子节点", "", 2);
                break;
            case R.id.fbt_focusMid:
                treev_mainTreeView.focusMidLocation();
                break;
            case R.id.fbt_saveAndExit:
                Snackbar.make(v, "代码视图功能敬请期待，长按可保存并返回主界面", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
        }
    }

    private void initAllView() {
        rl_treeView = findViewById(R.id.rl_treeView);
        treev_mainTreeView = findViewById(R.id.treev_mainTreeView);
        fbt_subNode = findViewById(R.id.fbt_subNode);
        fbt_node = findViewById(R.id.fbt_node);
        fbt_focusMid = findViewById(R.id.fbt_focusMid);
        fbt_saveAndExit = findViewById(R.id.fbt_saveAndExit);
//        iv_app_icon = findViewById(R.id.iv_app_icon);
        rl_fbts = findViewById(R.id.rl_fbts);
        rl_fbts.setX(rl_fbts.getX() - 50);
        rl_fbts.setY(rl_fbts.getY() - 50);

        fbt_subNode.setOnClickListener(this);
        fbt_node.setOnClickListener(this);
        fbt_focusMid.setOnClickListener(this);
        fbt_saveAndExit.setOnClickListener(this);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0f); // 设置饱和度
        ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
//        iv_app_icon.setColorFilter(grayColorFilter);


        fbt_saveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mindmap mm = new Mindmap(mmId, name);
                mm.setTm(treev_mainTreeView.getTreeModel());
                // 保存
                mmm.saveMindmap(mm);
                System.out.println(mmm.tm2json(treev_mainTreeView.getTreeModel()));
                // 返回主页
                Intent intent = new Intent(MindmapActivity.this, IndexActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_down = event.getRawX();
                y_down = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x_sub = event.getRawX() - x_down;
                float y_sub = event.getRawY() - y_down;
                rl_fbts.getLocationOnScreen(rl_fbts_location);
                x_move = rl_fbts_location[0];
                y_move = rl_fbts_location[1];
//                if (event.getRawX() > x_move && event.getRawY() > y_move
//                        && event.getRawX() < x_move + rl_fbts.getWidth()
//                        && event.getRawY() < y_move + rl_fbts.getHeight()) {
//                    rl_fbts.setX(rl_fbts.getX() + x_sub);
//                    rl_fbts.setY(rl_fbts.getY() + y_sub);
//                } else {
                treev_mainTreeView.setX(treev_mainTreeView.getX() + x_sub);
                treev_mainTreeView.setY(treev_mainTreeView.getY() + y_sub);
//                }
                x_down = event.getRawX();
                y_down = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

}

package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DensityUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.NodeView;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.RightTreeLayoutManager;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeView;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeViewItemClick;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeViewItemLongClick;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.CreateMindmapDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.EditMindnodeDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

/**
 * @author Aqinn
 * @date 2020/6/15 2:48 PM
 */
public class MindmapActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // 组件
    private AbsoluteLayout al_treeView;
    private TreeView treev_mainTreeView;
    private Button bt_add_sub;
    private Button bt_add_node;
    private Button bt_focus_mid;
    private Button bt_code_mode;
    private ImageView iv_app_icon;

    // 基本
    private String name;
    private Long mmId;

    //其它
    private MindMapManager mmm = MindMapManager.getInstance();
    private float x1, y1, x2 = -1, y2 = -1;
    private boolean flag = true;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindmap);

        /**
         * TODO 获得写文件权限，暂时先写在这里
         */
        new FileUtil().createAppDirectory();
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

        al_treeView.setOnTouchListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        treev_mainTreeView.focusMidLocation();
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
            tree = FileUtil.readTreeObject(path + String.valueOf(mmId) + ".twmm");
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
//            final NodeModel<String> gjr = new NodeModel<>("gjr");
//            final NodeModel<String> gjn = new NodeModel<>("gjn");
//
//
//            tree = new TreeModel<>(teamwork_mindmap);
//            tree.addNode(teamwork_mindmap, dk, zzq, gjr);
//            tree.addNode(gjr, gjn);
//            tree.addNode(zzq, zzf);
//        }

        int dx = DensityUtils.dp2px(this, 20);
        int dy = DensityUtils.dp2px(this, 20);
        int mHeight = DensityUtils.dp2px(this, 720);

        treev_mainTreeView.setTreeLayoutManager(new RightTreeLayoutManager(dx, dy, mHeight));
        treev_mainTreeView.setTreeModel(tree);
    }

    private void showEdit(String title, String content, int status) {
        final EditMindnodeDialogFragment emdf = new EditMindnodeDialogFragment(title, content, status);
        emdf.setOnEditFragmentListener((String mnContent) -> {
            switch (status) {
                case 1:
                    if (treev_mainTreeView.getCurrentFocusNode().parentNode == null) {
                        Snackbar.make(treev_mainTreeView, "根节点不能添加同级节点", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                    treev_mainTreeView.addNode(mnContent);
                    break;
                case 2:
                    treev_mainTreeView.addSubNode(mnContent);
                    break;
                case 3:
                    treev_mainTreeView.changeNodeValue(treev_mainTreeView.getCurrentFocusNode(), mnContent);
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
            case R.id.bt_add_node:
                showEdit("添加同级节点", "", 1);
                break;
            case R.id.bt_add_sub:
                showEdit("添加子节点", "", 2);
                break;
            case R.id.bt_focus_mid:
                treev_mainTreeView.focusMidLocation();
                break;
            case R.id.bt_code_mode:
                Snackbar.make(v, "代码视图功能敬请期待，长按可保存并返回主界面", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
        }
    }

    private void initAllView() {
        al_treeView = findViewById(R.id.al_treeView);
        treev_mainTreeView = findViewById(R.id.treev_mainTreeView);
        bt_add_sub = findViewById(R.id.bt_add_sub);
        bt_add_node = findViewById(R.id.bt_add_node);
        bt_focus_mid = findViewById(R.id.bt_focus_mid);
        bt_code_mode = findViewById(R.id.bt_code_mode);
//        iv_app_icon = findViewById(R.id.iv_app_icon);

        bt_add_sub.setOnClickListener(this);
        bt_add_node.setOnClickListener(this);
        bt_focus_mid.setOnClickListener(this);
        bt_code_mode.setOnClickListener(this);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0f); // 设置饱和度
        ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
//        iv_app_icon.setColorFilter(grayColorFilter);

        bt_add_node.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                treev_mainTreeView.setX(treev_mainTreeView.getX() + 10);
                treev_mainTreeView.setY(treev_mainTreeView.getY() + 10);
                return false;
            }
        });

        bt_code_mode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO 保存 并 返回主页
                Mindmap mm = new Mindmap(mmId, name);
                mm.setTm(treev_mainTreeView.getTreeModel());
                // 保存
                mmm.saveMindmap(mm);
                // 返回主页
                Intent intent = new Intent(MindmapActivity.this, IndexActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x2 = event.getRawX();
                y2 = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float xx = event.getRawX() - x2;
                float yy = event.getRawY() - y2;
                treev_mainTreeView.setX(treev_mainTreeView.getX() + xx);
                treev_mainTreeView.setY(treev_mainTreeView.getY() + yy);
                x2 = event.getRawX();
                y2 = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    //    /**
//     * 更改节点内容
//     * @param model
//     * @param value
//     */
//    public void changeNodeValue(NodeModel<String> model, String value) {
//        NodeView treeNodeView = (NodeView) treev_mainTreeView.findNodeViewFromNodeModel(model);
//        NodeModel<String> treeNode = treeNodeView.getTreeNode();
//        treeNode.setValue(value);
//        treeNodeView.setTreeNode(treeNode);
//    }

}

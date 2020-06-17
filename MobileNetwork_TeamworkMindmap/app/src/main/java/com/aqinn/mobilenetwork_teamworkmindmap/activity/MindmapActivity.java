package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DensityUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.NodeView;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.RightTreeLayoutManager;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeView;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeViewItemClick;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeViewItemLongClick;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.CreateMindmapDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.EditMindnodeDialogFragment;
import com.google.android.material.snackbar.Snackbar;

/**
 * @author Aqinn
 * @date 2020/6/15 2:48 PM
 */
public class MindmapActivity extends AppCompatActivity implements View.OnClickListener{

    // 组件
    private TreeView treev_mainTreeView;
    private Button bt_add_sub;
    private Button bt_add_node;
    private Button bt_focus_mid;
    private Button bt_code_mode;
    private ImageView iv_app_icon;

    // 基本
    private String name;
    private String mmId;
    private String lastEditDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindmap);

        try {
            Bundle bundle = getIntent().getExtras();
            name = bundle.getString("name");
            mmId = bundle.getString("mmId");
            lastEditDate = bundle.getString("lastEditDate");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        initAllView();

        final NodeModel<String> teamwork_mindmap = new NodeModel<>("teamwork_mindmap");
        final NodeModel<String> dk = new NodeModel<>("dk");
        final NodeModel<String> zzq = new NodeModel<>("zzq");
        final NodeModel<String> zzf = new NodeModel<>("zzf");
        final NodeModel<String> gjr = new NodeModel<>("gjr");
        final NodeModel<String> gjn = new NodeModel<>("gjn");


        final TreeModel<String> tree = new TreeModel<>(teamwork_mindmap);
        tree.addNode(teamwork_mindmap, dk, zzq, gjr);
        tree.addNode(gjr, gjn);
        tree.addNode(zzq, zzf);

        int dx = DensityUtils.dp2px(this, 20);
        int dy = DensityUtils.dp2px(this, 20);
        int mHeight = DensityUtils.dp2px(this, 720);

        treev_mainTreeView.setTreeLayoutManager(new RightTreeLayoutManager(dx, dy, mHeight));
        treev_mainTreeView.setTreeModel(tree);

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

        treev_mainTreeView.focusMidLocation();

    }

    private void showEdit(String title, String content, int status){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_add_sub:
                showEdit("添加同级节点", "", 1);
                break;
            case R.id.bt_add_node:
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

        bt_code_mode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO 保存 并 返回主页
                // 保存

                // 返回主页
                Intent intent = new Intent(MindmapActivity.this, IndexActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }


}

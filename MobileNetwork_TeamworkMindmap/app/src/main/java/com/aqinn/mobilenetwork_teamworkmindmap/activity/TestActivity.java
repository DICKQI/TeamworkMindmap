package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DensityUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.RightTreeLayoutManager;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeView;

public class TestActivity extends AppCompatActivity {

    private TreeView treev_testTreeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        treev_testTreeView.setTreeLayoutManager(new RightTreeLayoutManager(dx, dy, mHeight));
        treev_testTreeView.setTreeModel(tree);

    }

    private void initAllView() {
        treev_testTreeView = findViewById(R.id.treev_testTreeView);
    }

}

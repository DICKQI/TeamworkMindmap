package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.IndexActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.MindmapActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindmapAdapter;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.MyGridView;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aqinn
 * @date 2020/6/14 10:40 PM
 */
public class IndexFragment extends Fragment{

    // 组件
    private GridView gv_main;

    // 其它
    public static MindmapAdapter mma;
    private MindMapManager mmm = MindMapManager.getInstance();
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

        final List<Mindmap> mindmaps = testData();
        mma = new MindmapAdapter(getActivity(), mindmaps);
        gv_main.setAdapter(mma);
        gv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // TODO 新建思维导图 应该是先弹出一个AlertFragment，确定了以后就开启MindmapActivity
                    CreateMindmapDialogFragment cmdf = new CreateMindmapDialogFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    cmdf.show(ft, "createMindmapDialogFragment");
                } else {
                    Mindmap mm = mindmaps.get(position);
                    if (position != 0) { // 改变此条件可以实现测试效果 position == 1
                        Intent intent = new Intent(getActivity(), MindmapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("mmId", mm.getMmId());
                        bundle.putString("name", mm.getName());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
//                    Snackbar.make(view, "您点击的思维导图的名称是: " + mm.getName(), Snackbar.LENGTH_SHORT)
//                            .setAction("Action", null).show();
                }
            }
        });
//        gv_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                // 长按每一思维导图应该能显示 删除 修改名字
//                // 第一个思维导图（新建思维导图），不需要有任何操作
//                return false;
//            }
//        });
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
                Snackbar.make(gv_main, "分享给他人协作", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.delete:
                // 1.删除本地思维导图文件和数据库的文件，
                // 2. TODO 如果已经分享出去的导图且是思维导图的创建者，还需要执行关协作以及删除shareId
                if (!mmm.deleteMindmao(mma.getMindmaps().get(selectItemIndex).getMmId())){
                    Snackbar.make(gv_main, "删除失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                }
                // TODO 这样直接更换一个Adapter有点low，
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

    private List<Mindmap> testData() {
        List<Mindmap> mindmaps = new ArrayList<>();
        mindmaps = mmm.getAllMindmap();
        Mindmap add = new Mindmap("add");
        mindmaps.add(0, add);
        return mindmaps;
    }

    private void initAllView() {
        gv_main = getActivity().findViewById(R.id.gv_main);
    }


}
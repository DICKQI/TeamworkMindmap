package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.MindmapActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindmapAdapter;
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
    private MindmapAdapter mma;

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
                    if (position == 1) {
                        Intent intent = new Intent(getActivity(), MindmapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("mmId", mm.getMmId());
                        bundle.putString("name", mm.getName());
                        bundle.putString("date", mm.getDate());
                        bundle.putString("imgPath", mm.getImgPath());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    Snackbar.make(view, "您点击的思维导图的名称是: " + mm.getName(), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
        gv_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 长按每一思维导图应该能显示 删除 修改名字
                // 第一个思维导图（新建思维导图），不需要有任何操作
                return false;
            }
        });
    }



    private List<Mindmap> testData() {
        List<Mindmap> mindmaps = new ArrayList<>();
        Mindmap m0 = new Mindmap("add", "");
        Mindmap m1 = new Mindmap("1001", "Mindmap One");
        Mindmap m2 = new Mindmap("1002", "Mindmap Two");
        Mindmap m3 = new Mindmap("1003", "Mindmap Three");
        Mindmap m4 = new Mindmap("1004", "Mindmap Four");
        Mindmap m5 = new Mindmap("1005", "Mindmap Five");
        Mindmap m6 = new Mindmap("1006", "Mindmap Six");
        Mindmap m7 = new Mindmap("1007", "Mindmap Seven");
        Mindmap m8 = new Mindmap("1008", "Mindmap Eight");
        Mindmap m9 = new Mindmap("1009", "Mindmap Nine");
        Mindmap m10 = new Mindmap("1010", "Mindmap Ten");
        Mindmap m11 = new Mindmap("1011", "Mindmap Eleven");
        Mindmap m12 = new Mindmap("1012", "Mindmap Twelve");
        mindmaps.add(m0);
        mindmaps.add(m1);
        mindmaps.add(m2);
        mindmaps.add(m3);
        mindmaps.add(m4);
        mindmaps.add(m5);
        mindmaps.add(m6);
        mindmaps.add(m7);
        mindmaps.add(m8);
        mindmaps.add(m9);
        mindmaps.add(m10);
        mindmaps.add(m11);
        mindmaps.add(m12);
        return mindmaps;
    }

    private void initAllView() {
        gv_main = getActivity().findViewById(R.id.gv_main);
    }


}































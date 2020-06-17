package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aqinn.mobilenetwork_teamworkmindmap.R;

/**
 * @author Aqinn
 * @date 2020/6/14 10:42 PM
 */
public class MyFragment extends Fragment {

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(ARG_SECTION_NUMBER, index);
//        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        return view;
    }

}
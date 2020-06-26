package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.StyleConfig;

/**
 * @author Aqinn
 * @date 2020/6/26 5:56 PM
 */
public class ShareMindmapDialogFragment extends DialogFragment {

    // 组件
    private EditText et_share_id, et_pwd;

    // 其它
    public static Dialog dialog;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
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

    private void initAllView(View v_sm) {
        et_share_id = v_sm.findViewById(R.id.et_share_id);
        et_pwd = v_sm.findViewById(R.id.et_pwd);

        et_share_id.setFocusable(false);
        et_pwd.setMaxEms(10);
    }

}

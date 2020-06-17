package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.config.StyleConfig;
import com.google.android.material.snackbar.Snackbar;


/**
 * @author Aqinn
 * @date 2020/6/15 11:10 AM
 */
public class CreateMindmapDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private Button bt_confirm, bt_cancel;
    private TextView tv_new_mm, tv_tips_mmid, tv_tips_pwd, tv_name, tv_mmid, tv_pwd;
    private EditText et_name, et_mmid, et_pwd;


    // 其它
    public static Dialog dialog;
    private DialogInterface.OnDismissListener mOnClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v_cm = inflater.inflate(R.layout.fragment_create_mindmap, container, false);
        initAllView(v_cm);
        return v_cm;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        dialog = getDialog();
        Window win = getDialog().getWindow();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setLayout(850, 985);
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        win.setAttributes(params);
        bt_confirm.setTextAppearance(StyleConfig.bt_theme);
        bt_cancel.setTextAppearance(StyleConfig.bt_theme);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
        }
    }

    private void initAllView(View v){
        bt_confirm = v.findViewById(R.id.bt_confirm);
        bt_cancel = v.findViewById(R.id.bt_cancel);
        tv_new_mm = v.findViewById(R.id.tv_new_mm);
        tv_tips_mmid = v.findViewById(R.id.tv_tips_mmid);
        tv_tips_pwd = v.findViewById(R.id.tv_tips_pwd);
        tv_name = v.findViewById(R.id.tv_name);
        tv_mmid = v.findViewById(R.id.tv_mmid);
        tv_pwd = v.findViewById(R.id.tv_pwd);
        et_name = v.findViewById(R.id.et_name);
        et_mmid = v.findViewById(R.id.et_mmid);
        et_pwd = v.findViewById(R.id.et_pwd);

        bt_cancel.setOnClickListener(this);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mOnClickListener = listener;
    }

}

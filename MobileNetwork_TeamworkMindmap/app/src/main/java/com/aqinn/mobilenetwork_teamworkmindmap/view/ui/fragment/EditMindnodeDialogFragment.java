package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.model.OnEditFragmentListener;

/**
 * @author Aqinn
 * @date 2020/6/15 4:27 PM
 */
public class EditMindnodeDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private TextView tv_title;
    private ImageView iv_clear;
    private EditText et_main;
    private Button bt_cancel, bt_confirm;

    // 其它
    private String title;
    private String content;
    private int status = 0;
    public static Dialog dialog;

    /**
     * 初始化编辑弹窗
     * @param title
     * @param status 0: 错误  1: 添加同级节点  2: 添加子节点  3: 修改该节点
     */
    public EditMindnodeDialogFragment(String title, String content, int status) {
        this.title = title;
        this.content = content;
        this.status = status;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v_em = inflater.inflate(R.layout.fragment_edit_mindnode, container, false);
        initAllView(v_em);
        return v_em;
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

    private void initAllView(View v) {
        tv_title = v.findViewById(R.id.tv_title);
        iv_clear = v.findViewById(R.id.iv_clear);
        et_main = v.findViewById(R.id.et_main);
        bt_cancel = v.findViewById(R.id.bt_cancel);
        bt_confirm = v.findViewById(R.id.bt_confirm);

        iv_clear.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);

        tv_title.setText(this.title);
        if (this.status == 3) {
            et_main.setText(this.content);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear:
                et_main.setText("");
                break;
            case R.id.bt_confirm:
                onEditFragmentListener.editMindnode(et_main.getText().toString());
                dismiss();
//                switch (this.status) {
//                    case 0:
//                        // 错误状态
//                        break;
//                    case 1:
//                        // 添加同级节点
//                        break;
//                    case 2:
//                        // 添加子节点
//                        break;
//                    case 3:
//                        // 修改该节点
//                        break;
//                }
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
        }
    }

    public interface OnEditFragmentListener {

        void editMindnode(String mnContent);

    }

    public OnEditFragmentListener onEditFragmentListener;

    public void setOnEditFragmentListener(OnEditFragmentListener onEditFragmentListener){
        this.onEditFragmentListener = onEditFragmentListener;
    }

}

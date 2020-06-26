package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

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
public class ShareMindmapDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private EditText et_share_id, et_pwd;
    private ImageView iv_share_or_not;

    // 其它
    private boolean shareOrNot;
    private Long shareId;
    public static Dialog dialog;
    private Drawable share_cancel, share_mm_blue;

    public ShareMindmapDialogFragment(boolean shareOrNot, Long shareId) {
        this.shareOrNot = shareOrNot;
        this.shareId = shareId;
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_or_not:
                // TODO 在此共享思维导图 上传到云端
                shareOrNot = !shareOrNot;
                if (shareOrNot) {
                    iv_share_or_not.setImageDrawable(share_cancel);
                    et_share_id.setText("123456");
                } else {
                    iv_share_or_not.setImageDrawable(share_mm_blue);
                    et_share_id.setText("");
                }
                break;
        }
    }

    private void initAllView(View v_sm) {
        et_share_id = v_sm.findViewById(R.id.et_share_id);
        et_pwd = v_sm.findViewById(R.id.et_pwd);
        iv_share_or_not = v_sm.findViewById(R.id.iv_share_or_not);

        et_share_id.setKeyListener(null);
        et_pwd.setMaxEms(10);
        iv_share_or_not.setOnClickListener(this);
        share_cancel = getResources().getDrawable(R.drawable.share_cancel);
        share_mm_blue = getResources().getDrawable(R.drawable.share_mm_blue);
        if (shareOrNot){
            et_share_id.setText(String.valueOf(shareId));
            iv_share_or_not.setImageDrawable(share_cancel);
        }
        else{
            iv_share_or_not.setImageDrawable(share_mm_blue);
        }

    }

}

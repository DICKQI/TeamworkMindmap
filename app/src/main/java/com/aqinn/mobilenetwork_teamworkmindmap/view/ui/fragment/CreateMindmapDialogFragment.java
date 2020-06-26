package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.MindmapActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.config.StyleConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.controller.MindMapManager;
import com.aqinn.mobilenetwork_teamworkmindmap.vo.Mindmap;
import com.google.android.material.snackbar.Snackbar;


/**
 * @author Aqinn
 * @date 2020/6/15 11:10 AM
 */
public class CreateMindmapDialogFragment extends DialogFragment implements View.OnClickListener {

    // 组件
    private Button bt_confirm, bt_cancel;
    private TextView tv_new_mm;
    private EditText et_name;
    private ImageView iv_clear;

    // 其它
    public static Dialog dialog;
    private DialogInterface.OnDismissListener mOnClickListener;
    private MindMapManager mmm = MindMapManager.getInstance();

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
//        win.setLayout(850, 985);
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        win.setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear:
                et_name.setText("");
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_confirm:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            3);
                }
                Mindmap mm = mmm.createMindmap(et_name.getText().toString());
                if (mm == null) {
                    Snackbar.make(getView(), "创建失败", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {
                    IndexFragment.mma.getMindmaps().add(1, mm);
                    IndexFragment.mma.notifyDataSetChanged();
                    Intent intent = new Intent(getActivity(), MindmapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("mmId", mm.getMmId());
                    bundle.putString("name", mm.getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dismiss();
                }
                break;
        }
    }

    private void initAllView(View v) {
        bt_confirm = v.findViewById(R.id.bt_confirm);
        bt_cancel = v.findViewById(R.id.bt_cancel);
        tv_new_mm = v.findViewById(R.id.tv_new_mm);
        et_name = v.findViewById(R.id.et_name);
        iv_clear = v.findViewById(R.id.iv_clear);

        iv_clear.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mOnClickListener = listener;
    }

}

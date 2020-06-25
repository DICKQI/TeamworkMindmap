package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aqinn.mobilenetwork_teamworkmindmap.R;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * @author Aqinn
 * @date 2020/6/14 10:42 PM
 */
public class MyFragment extends Fragment  implements View.OnClickListener{

    private ImageView iv_userIcon;
    private EditText edtTxt_nickname;
    private EditText edtTxt_account;
    private EditText edtTxt_passwd;
    private EditText edtTxt_signature;
    private EditText edtTxt_email;
    private RadioGroup rdoGp_gender;
    private RadioButton rdoBt_gender_man;
    private RadioButton rdoBt_gender_female;
    private RadioButton rdoBt_gender_private;
    private File cropImageFile;

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
        initAllView(view);
        return view;
    }

    private void initAllView(View v) {
        iv_userIcon = v.findViewById(R.id.my_picture);
        edtTxt_account = v.findViewById(R.id.my_account);
        edtTxt_nickname = v.findViewById(R.id.my_name);
        edtTxt_passwd = v.findViewById(R.id.my_password);
        edtTxt_signature = v.findViewById(R.id.my_signature);
        edtTxt_email = v.findViewById(R.id.my_email);
        rdoGp_gender = v.findViewById(R.id.my_rdoGp);
        rdoBt_gender_man = v.findViewById(R.id.my_rdo_genderMan);
        rdoBt_gender_female = v.findViewById(R.id.my_rdo_genderFemale);
        rdoBt_gender_private = v.findViewById(R.id.my_rdo_genderPrivate);

        rdoGp_gender.setOnCheckedChangeListener(radioGrouplisten);
        iv_userIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_picture:
                try {
                    //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    iv_userIcon.setImageURI(data.getData());
                    break;

                default:
                    break;
            }
        }
    }



    //监听Radio
    private RadioGroup.OnCheckedChangeListener radioGrouplisten = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            int id = group.getCheckedRadioButtonId();
            switch (group.getCheckedRadioButtonId()) {
                case R.id.my_rdo_genderMan:
//                    orientation = Orientation.landscape;
//                    Log.i("orientation",orientation.toString());
                    //Toast.makeText(PrintSettingActivity.this, orientation.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.my_rdo_genderFemale:
//                    orientation = Orientation.Portrait;
//                    Log.i("orientation",orientation.toString());
                    //Toast.makeText(PrintSettingActivity.this, orientation.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.my_rdo_genderPrivate:
                    break;
                default:
                    break;
            }
        }
    };
}
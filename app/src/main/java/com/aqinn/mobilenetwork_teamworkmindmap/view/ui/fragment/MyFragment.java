package com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.activity.LoginActivity;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * @author Aqinn
 * @date 2020/6/14 10:42 PM
 */
public class MyFragment extends Fragment  implements View.OnClickListener{

    public static ImageView iv_userIcon;
    public static EditText edtTxt_nickname;
    private EditText edtTxt_account;
    public static EditText edtTxt_oldPasswd;
    public static EditText edtTxt_newPasswd;
    public static EditText edtTxt_signature;
    private EditText edtTxt_date;
    public static RadioGroup rdoGp_gender;
    public static RadioButton rdoBt_gender_man;
    public static RadioButton rdoBt_gender_female;
    public static RadioButton rdoBt_gender_private;
    public static String img_base64 = "";
    private File cropImageFile;
    private Handler mHandler = new Handler();

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList();
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
        edtTxt_account.setKeyListener(null);
        return view;
    }

    private void initAllView(View v) {
        iv_userIcon = v.findViewById(R.id.my_picture);
        edtTxt_account = v.findViewById(R.id.my_account);
        edtTxt_nickname = v.findViewById(R.id.my_name);
        edtTxt_oldPasswd = v.findViewById(R.id.my_oldPassword);
        edtTxt_newPasswd = v.findViewById(R.id.my_newPassword);
        edtTxt_signature = v.findViewById(R.id.my_signature);
        edtTxt_date = v.findViewById(R.id.my_date);
        rdoGp_gender = v.findViewById(R.id.my_rdoGp);
        rdoBt_gender_man = v.findViewById(R.id.my_rdo_genderMan);
        rdoBt_gender_female = v.findViewById(R.id.my_rdo_genderFemale);
        rdoBt_gender_private = v.findViewById(R.id.my_rdo_genderPrivate);

//        rdoGp_gender.setOnCheckedChangeListener(radioGrouplisten);
        iv_userIcon.setOnClickListener(this);

        Map<String, String> header = new HashMap<>();
        header.put("Cookie", CommonUtil.getUserCookie(getActivity()));
        MyHttpUtil.get(PublicConfig.url_get_dashboard(), header, new MyHttpUtil.HttpCallbackListener() {
            @Override
            public void beforeFinish(HttpURLConnection connection) {

            }

            @Override
            public void onFinish(String response) {
                JSONObject json = JSON.parseObject(response);
                if (json.getBoolean("status")) {
                    JSONObject jsonObject = json.getJSONObject("user");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            edtTxt_account.setText(jsonObject.getString("email"));
                            edtTxt_nickname.setText(jsonObject.getString("nickname"));
                            edtTxt_signature.setText(jsonObject.getString("signature"));
                            edtTxt_date.setText(jsonObject.getString("join_date"));
                            if (jsonObject.getString("sex").equals("男"))
                                rdoBt_gender_man.setChecked(true);
                            else if (jsonObject.getString("sex").equals("女"))
                                rdoBt_gender_female.setChecked(true);
                            else
                                rdoBt_gender_private.setChecked(true);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e, String response) {

            }
        });
        Handler mHandler1 = new Handler();
        MyHttpUtil.get(PublicConfig.url_get_head(), header, new MyHttpUtil.HttpCallbackListener() {
            @Override
            public void beforeFinish(HttpURLConnection connection) {

            }

            @Override
            public void onFinish(String response) {
                JSONObject json = JSON.parseObject(response);
                if (json.getBoolean("status")) {
                    mHandler1.post(new Runnable() {
                        @Override
                        public void run() {
                            img_base64 = bitmapToBase64(toRoundBitmap(base64ToBitmap(json.getString("head"))));
                            iv_userIcon.setImageBitmap(toRoundBitmap(base64ToBitmap(json.getString("head"))));
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e, String response) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_picture:
                try {
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
                    Bitmap bitmap = ((BitmapDrawable)iv_userIcon.getDrawable()).getBitmap();
                    bitmap  = Bitmap.createScaledBitmap(bitmap,120,120,true);
                    String bit = bitmapToBase64(toRoundBitmap(bitmap));
                    iv_userIcon.setImageBitmap(toRoundBitmap(base64ToBitmap(bit)));
                    break;

                default:
                    break;
            }
        }
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
        Bitmap bm = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawCircle(200, 200, 200, paint);
        paint.reset();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bm;
    }

    //bitmap转base64
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //base64转bitmap
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
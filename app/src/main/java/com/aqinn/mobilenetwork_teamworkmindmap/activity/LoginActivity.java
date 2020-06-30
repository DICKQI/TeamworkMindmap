package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.http.MyHttpPost;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.MyFragment;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    //按钮设置
    private EditText edtTxt_userEmail;
    private EditText edtTxt_passwd;
    private EditText edtTxt_passwd2;
    private EditText edtTxt_userNickname;
    private Button bt_login;
    private Button bt_register;
    private Button bt_back;
    private Button bt_offLine;
    private ImageView iv_passwd2;
    private ImageView iv_verify;
    private TextView tv_verifyText;
    private CheckBox chk_remember_user;
    private CheckBox chk_remeber_passwd;
    private LinearLayout linearLayout_nickname;
    private String userEmail;
    private String password;
    private String password2;
    private String errorString;
    Context context = this;
    private boolean isRemember_passwd = false;
    private boolean isRemember_user = false;
    //判断是否符合标准
    private boolean isRegister = false;
    private boolean isCorrected = false;
    private boolean isEqual = false;
    private boolean isVerify = false;
    private boolean isLoginOK = false;
    private boolean isRegisterOK = false;

    //其它
    private FileUtil fileUtil = FileUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * TODO 获得写文件权限，暂时先写在这里
         */
        CommonUtil.verifyStoragePermissions(this);
        fileUtil.createAppDirectory();


        setContentView(R.layout.activity_login);
        edtTxt_userEmail = findViewById(R.id.useremail_edit);
        edtTxt_passwd = findViewById(R.id.password_edit);
        edtTxt_passwd2 = findViewById(R.id.password2_edit);
        edtTxt_userNickname = findViewById(R.id.nickname_edit);
        bt_login = findViewById(R.id.login_btn);
        bt_back = findViewById(R.id.login_back_btn);
        bt_register = findViewById(R.id.register_btn);
        bt_offLine = findViewById(R.id.login_offLine);
        iv_passwd2 = findViewById(R.id.login_passwd2_img);
        iv_verify = findViewById(R.id.login_verify_img);
        tv_verifyText = findViewById(R.id.textviewverify);
        chk_remember_user = findViewById(R.id.login_remember_user);
        chk_remeber_passwd = findViewById(R.id.login_remember_passwd);
        linearLayout_nickname = findViewById(R.id.login_nickname_line);

        chk_remember_user.setOnCheckedChangeListener(this);
        chk_remeber_passwd.setOnCheckedChangeListener(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (CommonUtil.getRememberUser(this) != null) {
            isRemember_user = true;
            chk_remember_user.setChecked(true);
            edtTxt_userEmail.setText(CommonUtil.getRememberUser(this));
        }
        //记住密码直接登录
        if (CommonUtil.getRememberPwd(this) != null) {
            isRemember_passwd = true;
            chk_remeber_passwd.setChecked(true);
            edtTxt_passwd.setText(CommonUtil.getRememberPwd(this));
            if (CommonUtil.getUserCookie(context) != null) {
                Map<String, String> header = new HashMap<>();
                header.put("Cookie", CommonUtil.getUserCookie(context));
                MyHttpUtil.get(PublicConfig.url_get_verifyLogin(), header, new MyHttpUtil.HttpCallbackListener() {
                    @Override
                    public void beforeFinish(HttpURLConnection connection) {

                    }

                    @Override
                    public void onFinish(String response) {
                        if (response != null) {
                            JSONObject json = JSON.parseObject(response);
                            if (json.getBoolean("status")) {
                                isLoginOK = true;
                                CommonUtil.setUser(context, json.getLong("id"));
                            } else {
                                isLoginOK = false;
                                errorString = json.getString("errMsg");
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e, String response) {

                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isLoginOK) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, IndexActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }

        //离线按钮
        bt_offLine.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                CommonUtil.setUser(context, -1L);
                CommonUtil.deleteUserCookie(context);
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, IndexActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });

        //登录按钮
        bt_login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                userEmail = edtTxt_userEmail.getText().toString().trim();
                password = edtTxt_passwd.getText().toString().trim();
                loginIn();
            }

        });

        //注册按钮
        bt_register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                bt_login.setVisibility(View.INVISIBLE);
                edtTxt_passwd2.setVisibility(View.VISIBLE);
                iv_passwd2.setVisibility(View.VISIBLE);
                iv_verify.setVisibility(View.VISIBLE);
                tv_verifyText.setVisibility(View.VISIBLE);
                bt_back.setVisibility(View.VISIBLE);
                linearLayout_nickname.setVisibility(View.VISIBLE);
                layoutParams.setMargins(0, 40, 0, 0);
                linearLayout_nickname.setLayoutParams(layoutParams);
                bt_register.setText("立即注册");
                chk_remeber_passwd.setVisibility(View.INVISIBLE);
                chk_remember_user.setVisibility(View.INVISIBLE);
                if (isRegister) {
                    if (isVerify != true) {
                        tv_verifyText.setText("该邮箱已被注册，请重新输入");
                        tv_verifyText.setBackgroundColor(Color.RED);
                        iv_verify.setImageResource(R.mipmap.ic_unverified);
                    } else if (isCorrected != true) {
                        tv_verifyText.setText("密码格式不正确");
                        tv_verifyText.setBackgroundColor(Color.RED);
                        iv_verify.setImageResource(R.mipmap.ic_unverified);
                    } else if (isEqual != true) {
                        tv_verifyText.setText("两次密码不一致");
                        tv_verifyText.setBackgroundColor(Color.RED);
                        iv_verify.setImageResource(R.mipmap.ic_unverified);
                    } else if (edtTxt_userNickname.getText().toString().trim().equals("")) {
                        tv_verifyText.setText("请输入昵称");
                        tv_verifyText.setBackgroundColor(Color.RED);
                        iv_verify.setImageResource(R.mipmap.ic_unverified);
                    } else {
                        userEmail = edtTxt_userEmail.getText().toString().trim();
                        password = edtTxt_passwd.getText().toString().trim();
                        password2 = edtTxt_passwd2.getText().toString().trim();
                        register_post();
                    }
                } else {
                    edtTxt_userEmail.setText("");
                    edtTxt_passwd.setText("");
                }
                isRegister = true;
            }

        });

        //返回按钮
        bt_back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                isRegister = false;
                bt_login.setVisibility(View.VISIBLE);
                edtTxt_passwd2.setVisibility(View.INVISIBLE);
                iv_passwd2.setVisibility(View.INVISIBLE);
                iv_verify.setVisibility(View.INVISIBLE);
                tv_verifyText.setVisibility(View.INVISIBLE);
                bt_back.setVisibility(View.INVISIBLE);
                linearLayout_nickname.setVisibility(View.INVISIBLE);
                layoutParams.setMargins(0, -40, 0, 0);
                linearLayout_nickname.setLayoutParams(layoutParams);
                chk_remember_user.setVisibility(View.VISIBLE);
                chk_remeber_passwd.setVisibility(View.VISIBLE);
                edtTxt_userEmail.setText("");
                edtTxt_passwd.setText("");
                edtTxt_passwd2.setText("");
                edtTxt_userNickname.setText("");
                if (CommonUtil.getRememberUser(context) != null) {
                    isRemember_user = true;
                    chk_remember_user.setChecked(true);
                    edtTxt_userEmail.setText(CommonUtil.getRememberUser(context));
                }
                if (CommonUtil.getRememberPwd(context) != null) {
                    isRemember_passwd = true;
                    chk_remeber_passwd.setChecked(true);
                    edtTxt_passwd.setText(CommonUtil.getRememberPwd(context));
                }
            }

        });

        //用户名监听
        edtTxt_userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //判断用户名是否被占用
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = edtTxt_userEmail.getText().toString().trim();
                Pattern pattern = Pattern.compile("@");
                Pattern pattern1 = Pattern.compile("\\.com");
                Matcher matcher1 = pattern1.matcher((user));
                Matcher matcher = pattern.matcher(user);
                if (!matcher.find() || !matcher1.find()) {
                    tv_verifyText.setText("请输入正确的邮箱");
                    tv_verifyText.setBackgroundColor(Color.RED);
                    iv_verify.setImageResource(R.mipmap.ic_unverified);
                } else {
                    tv_verifyText.setText("邮箱地址正确");
                    tv_verifyText.setBackgroundColor(Color.GREEN);
                    iv_verify.setImageResource(R.mipmap.ic_verified);

                    userInfo_available();
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (isVerify != true) {
                        iv_verify.setImageResource(R.mipmap.ic_unverified);
                        tv_verifyText.setText(errorString);
                        tv_verifyText.setBackgroundColor(Color.RED);
                    } else {
                        tv_verifyText.setText("该邮箱可用");
                        tv_verifyText.setBackgroundColor(Color.GREEN);
                        iv_verify.setImageResource(R.mipmap.ic_verified);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //昵称监听
        edtTxt_userNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    userInfo_available();
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (isVerify != true) {
                        iv_verify.setImageResource(R.mipmap.ic_unverified);
                        tv_verifyText.setText(errorString);
                        tv_verifyText.setBackgroundColor(Color.RED);
                    } else {
                        tv_verifyText.setText("该昵称可用");
                        tv_verifyText.setBackgroundColor(Color.GREEN);
                        iv_verify.setImageResource(R.mipmap.ic_verified);
                    }
                }
            }
        });

        //密码符合
        edtTxt_passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwd = edtTxt_passwd.getText().toString().trim();
                Pattern pattern = Pattern.compile("([a-z]|[A-Z])*");
                Pattern pattern1 = Pattern.compile("[0-9]*");
                Matcher matcher = pattern.matcher(passwd);
                Matcher matcher1 = pattern1.matcher((passwd));
                tv_verifyText.setText("请输入正确的密码（包含字母和数字,6位或以上）");
                tv_verifyText.setBackgroundColor(Color.RED);
                iv_verify.setImageResource(R.mipmap.ic_unverified);
                isCorrected = false;
                if (matcher.find()) {
                    if (matcher1.find()) {
                        if (passwd.length() >= 6) {
                            isCorrected = true;
                            iv_verify.setImageResource(R.mipmap.ic_verified);
                            tv_verifyText.setText("该密码可用");
                            tv_verifyText.setBackgroundColor(Color.GREEN);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //密码确认
        edtTxt_passwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwd2 = edtTxt_passwd2.getText().toString().trim();
                String passwd = edtTxt_passwd.getText().toString().trim();
                if (isCorrected) {
                    tv_verifyText.setText("请输入相同的密码");
                    tv_verifyText.setBackgroundColor(Color.RED);
                    iv_verify.setImageResource(R.mipmap.ic_unverified);
                    isEqual = false;
                    if (passwd.equals(passwd2)) {
                        if (isVerify) {
                            iv_verify.setImageResource(R.mipmap.ic_verified);
                            tv_verifyText.setText("该邮箱密码可用");
                            isEqual = true;
                            tv_verifyText.setBackgroundColor(Color.GREEN);
                        } else {
                            tv_verifyText.setText("请输入正确不重复的邮箱");
                            tv_verifyText.setBackgroundColor(Color.RED);
                            iv_verify.setImageResource(R.mipmap.ic_unverified);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //记住用户名或密码
    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
        switch (checkBox.getId()) {
            case R.id.login_remember_user:
                if (checkBox.isPressed()) {
                    if (checked) {
                        isRemember_user = true;
                    } else {
                        isRemember_user = false;
                        isRemember_passwd = false;
                        chk_remeber_passwd.setChecked(false);
                    }
                }
                break;
            case R.id.login_remember_passwd:
                if (checkBox.isPressed()) {
                    if (checked) {
                        isRemember_passwd = true;
                        isRemember_user = true;
                        chk_remember_user.setChecked(true);
                    } else {
                        isRemember_passwd = false;
                    }
                }
                break;
            default:
                break;
        }
    }

    //用户登录
    private void loginIn() {
        JSONObject jo = new JSONObject();
        jo.put("email", userEmail);
        jo.put("password", password);
        String json = jo.toJSONString();
        Map<String, String> header = new HashMap<>();
        Handler mHandler = new Handler();
        MyHttpUtil.post(PublicConfig.url_post_login(), header, json, new MyHttpUtil.HttpCallbackListener() {
            @Override
            public void beforeFinish(HttpURLConnection connection) {
                CommonUtil.setUserCookie(context, connection.getHeaderField("Set-Cookie"));
            }

            @Override
            public void onFinish(String response) {
                if (response != null) {
                    JSONObject json = JSON.parseObject(response);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (json.getBoolean("status")) {
                                isLoginOK = true;
                                CommonUtil.setUser(context, json.getLong("id"));
                            }
                            if (isLoginOK) {
                                if (isRemember_user) {
                                    CommonUtil.setRememberUser(context, edtTxt_userEmail.getText().toString().trim());
                                } else {
                                    CommonUtil.deleteRememberUser(context);
                                }
                                if (isRemember_passwd) {
                                    CommonUtil.setRememberPwd(context, edtTxt_passwd.getText().toString().trim());
                                } else {
                                    CommonUtil.deleteRememberPwd(context);
                                }
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, IndexActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(context, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e, String response) {
                Log.d("xxx", "onError e:\n" + e.getMessage() + "\n");
                Log.d("xxx", "onError response:\n" + response + "\n");
            }
        });
    }

    //判断邮箱和昵称是否重复
    private void userInfo_available() {
        JSONObject jo = new JSONObject();
        jo.put("email", edtTxt_userEmail.getText().toString().trim());
        jo.put("nickname", edtTxt_userNickname.getText().toString().trim());
        String json = jo.toJSONString();
        Map<String, String> header = new HashMap<>();
        Handler mHandler = new Handler();
        MyHttpUtil.post(PublicConfig.url_post_register(), header, json, new MyHttpUtil.HttpCallbackListener() {
            @Override
            public void beforeFinish(HttpURLConnection connection) {

            }

            @Override
            public void onFinish(String response) {
                if (response != null) {
                    JSONObject json = JSON.parseObject(response);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (json.getString("errMsg").equals("密码不能为空")) {
                                isVerify = true;
                            } else {
                                isVerify = false;
                                errorString = json.getString("errMsg");
                            }
                            if (isVerify != true) {
                                iv_verify.setImageResource(R.mipmap.ic_unverified);
                                tv_verifyText.setText(errorString);
                                tv_verifyText.setBackgroundColor(Color.RED);
                            } else {
                                tv_verifyText.setText("该邮箱可用");
                                tv_verifyText.setBackgroundColor(Color.GREEN);
                                iv_verify.setImageResource(R.mipmap.ic_verified);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e, String response) {

            }
        });
    }

    //注册
    private void register_post() {
        JSONObject jo = new JSONObject();
        jo.put("email", edtTxt_userEmail.getText().toString().trim());
        jo.put("nickname", edtTxt_userNickname.getText().toString().trim());
        jo.put("password", edtTxt_passwd2.getText().toString().trim());
        String json = jo.toJSONString();
        Map<String, String> header = new HashMap<>();
        Handler mHandler = new Handler();
        MyHttpUtil.post(PublicConfig.url_post_register(), header, json, new MyHttpUtil.HttpCallbackListener() {
            @Override
            public void beforeFinish(HttpURLConnection connection) {

            }

            @Override
            public void onFinish(String response) {
                if (response != null) {
                    JSONObject json = JSON.parseObject(response);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (json.getBoolean("status")) {
                                isRegisterOK = true;
                            } else {
                                isRegisterOK = false;
                                errorString = json.getString("errMsg");
                            }
                            if (isRegisterOK) {
                                CommonUtil.deleteRememberUser(context);
                                CommonUtil.deleteUserCookie(context);
                                isRemember_passwd = true;
                                isRemember_user = false;
                                loginIn();
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, IndexActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                tv_verifyText.setText(errorString);
                                tv_verifyText.setBackgroundColor(Color.RED);
                                iv_verify.setImageResource(R.mipmap.ic_unverified);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e, String response) {

            }
        });
    }

}
package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aqinn.mobilenetwork_teamworkmindmap.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    //按钮设置
    private EditText user_edit;
    private EditText passwd_edit;
    private EditText passwd2_edit;
    private Button login_btn;
    private Button register_btn;
    private Button back_btn;
    private ImageView img3view;
    private ImageView img4view;
    private TextView text4view;
    private String username;
    private String password;
    private String password2;
    //判断是否符合标准
    private boolean isRegister = false;
    private boolean isCorrected = false;
    private boolean isEqual = false;
    private boolean isVerify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        user_edit = findViewById(R.id.username_edit);
        passwd_edit = findViewById(R.id.password_edit);
        passwd2_edit = findViewById(R.id.password2_edit);
        login_btn = findViewById(R.id.login_btn);
        back_btn = findViewById(R.id.login_back_btn);
        register_btn = findViewById(R.id.register_btn);
        img3view = findViewById(R.id.imgView3);
        img4view = findViewById(R.id.imgView4);
        text4view = findViewById(R.id.textviewverify);

        //登录按钮
        login_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                username = user_edit.getText().toString().trim();
                password = passwd_edit.getText().toString().trim();
                loginin_socket();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, IndexActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });

        //注册按钮
        register_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                login_btn.setVisibility(View.INVISIBLE);
                passwd2_edit.setVisibility(View.VISIBLE);
                img3view.setVisibility(View.VISIBLE);
                img4view.setVisibility(View.VISIBLE);
                text4view.setVisibility(View.VISIBLE);
                back_btn.setVisibility(View.VISIBLE);
                register_btn.setText("立即注册");
                if (isRegister) {
                    if (isVerify != true) {
                        text4view.setText("该用户名已被注册，请重新输入");
                        text4view.setBackgroundColor(Color.RED);
                        img4view.setImageResource(R.mipmap.ic_unverified);
                    } else if (isCorrected != true) {
                        text4view.setText("密码格式不正确");
                        text4view.setBackgroundColor(Color.RED);
                        img4view.setImageResource(R.mipmap.ic_unverified);
                    } else if (isEqual != true) {
                        text4view.setText("两次密码不一致");
                        text4view.setBackgroundColor(Color.RED);
                        img4view.setImageResource(R.mipmap.ic_unverified);
                    } else {

                        username = user_edit.getText().toString().trim();
                        password = passwd_edit.getText().toString().trim();
                        password2 = passwd2_edit.getText().toString().trim();
                        register_socket();
                    }
                }
                isRegister = true;
            }

        });

        //返回按钮
        back_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                isRegister = false;
                login_btn.setVisibility(View.VISIBLE);
                passwd2_edit.setVisibility(View.INVISIBLE);
                img3view.setVisibility(View.INVISIBLE);
                img4view.setVisibility(View.INVISIBLE);
                text4view.setVisibility(View.INVISIBLE);
                back_btn.setVisibility(View.INVISIBLE);
                user_edit.setText("");
                passwd_edit.setText("");
                passwd2_edit.setText("");
            }

        });

        //用户名监听
        user_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //判断用户名是否被占用
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user = user_edit.getText().toString().trim();
                //HttpHelper.get("");
                if (isVerify != true) {
                    img4view.setImageResource(R.mipmap.ic_unverified);
                    text4view.setText("该用户名已被注册，请重新输入");
                    text4view.setBackgroundColor(Color.RED);
                } else {
                    text4view.setText("该用户名可用");
                    text4view.setBackgroundColor(Color.GREEN);
                    img4view.setImageResource(R.mipmap.ic_verified);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //密码符合
        passwd_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwd = passwd_edit.getText().toString().trim();
                Pattern pattern = Pattern.compile("([a-z]|[A-Z])*");
                Pattern pattern1 = Pattern.compile("[0-9]*");
                Matcher matcher = pattern.matcher(passwd);
                Matcher matcher1 = pattern1.matcher((passwd));
                text4view.setText("请输入正确的密码（包含字母和数字,6位以上）");
                text4view.setBackgroundColor(Color.RED);
                img4view.setImageResource(R.mipmap.ic_unverified);
                isCorrected = false;
                if (matcher.find()) {
                    if (matcher1.find()) {
                        if (passwd.length() >= 6) {
                            isCorrected = true;
                            img4view.setImageResource(R.mipmap.ic_verified);
                            text4view.setText("该用密码可用");
                            text4view.setBackgroundColor(Color.GREEN);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //密码确认
        passwd2_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwd2 = passwd2_edit.getText().toString().trim();
                String passwd = passwd_edit.getText().toString().trim();
                if (isCorrected) {
                    text4view.setText("请输入相同的密码");
                    text4view.setBackgroundColor(Color.RED);
                    img4view.setImageResource(R.mipmap.ic_unverified);
                    isEqual = false;
                    if (passwd.equals(passwd2)) {
                        if (isVerify) {
                            img4view.setImageResource(R.mipmap.ic_verified);
                            text4view.setText("该用户名密码可用");
                            isEqual = true;
                            text4view.setBackgroundColor(Color.GREEN);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    void loginin_socket() {
    }

    void register_socket() {

    }

}
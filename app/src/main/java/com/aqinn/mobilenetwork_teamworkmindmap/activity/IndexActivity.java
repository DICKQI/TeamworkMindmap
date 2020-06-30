package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.config.PublicConfig;
import com.aqinn.mobilenetwork_teamworkmindmap.util.CommonUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.FileUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.MyRadioButton;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.CreateMindmapDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.FindMindmapDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.IndexFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.MyFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.ShareMindmapDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aqinn
 * @date 2020/6/14 9:42 PM
 */
public class IndexActivity extends AppCompatActivity implements View.OnClickListener {

    // 组件
    private RelativeLayout root;
    private ViewPager vp_main;
    private RadioGroup rg_main;
    private RadioButton rb_index, rb_my;
    private Toolbar tb_main;
    private TextView tv_main;
    private ImageView iv_cloud;

    // 其它
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private MyPageChangeListener myPageChangeListener;
    private MyOnCheckedChangeListener myOnCheckedChangeListener;
    private boolean optionMenuOn = false;  //标示是否要显示optionmenu
    private Menu mMenu;
    private String sex = "保密";
    public static Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initAllView();
        context=getApplicationContext();
        CommonUtil.verifyStoragePermissions(this);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initAllView() {
        root = findViewById(R.id.root);
        vp_main = findViewById(R.id.vp_main);
        rg_main = findViewById(R.id.rg_main);
        tb_main = findViewById(R.id.tb_main);
        tv_main = findViewById(R.id.tv_main);
        rb_index = findViewById(R.id.rb_index);
        rb_my = findViewById(R.id.rb_my);
        iv_cloud = findViewById(R.id.iv_cloud);

        setSupportActionBar(tb_main);
        getSupportActionBar().setTitle("");
        setRadioButton(R.drawable.index_select, rb_index);
        setRadioButton(R.drawable.me_unselect, rb_my);

        mFragments = new ArrayList<>(2);
        mFragments.add(IndexFragment.newInstance());
        mFragments.add(MyFragment.newInstance());
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp_main.setAdapter(mAdapter);
        myPageChangeListener = new MyPageChangeListener();
        myOnCheckedChangeListener = new MyOnCheckedChangeListener();
        vp_main.addOnPageChangeListener(myPageChangeListener);
        rg_main.setOnCheckedChangeListener(myOnCheckedChangeListener);
        iv_cloud.setOnClickListener(this);

        MyFragment.initMydDshboard();
    }

    private void setRadioButton(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable.setBounds(0, 0, 70, 70);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        checkOptionMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkOptionMenu() {
        if (null != mMenu) {
            if (optionMenuOn) {
                for (int i = 0; i < mMenu.size(); i++) {
                    mMenu.getItem(i).setVisible(true);
                    mMenu.getItem(i).setEnabled(true);
                }
            } else {
                for (int i = 0; i < mMenu.size(); i++) {
                    mMenu.getItem(i).setVisible(false);
                    mMenu.getItem(i).setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cloud:
                FindMindmapDialogFragment fdf = new FindMindmapDialogFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fdf.show(ft, "findMindmapDialogFragment");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_update:
                if (CommonUtil.getUserCookie(context)!=null) {
                    Map<String, String> update_header = new HashMap<>();
                    update_header.put("Cookie", CommonUtil.getUserCookie(this));
                    com.alibaba.fastjson.JSONObject jo = new JSONObject();
                    if (MyFragment.edtTxt_nickname.getText().toString().trim().equals(""))
                        Toast.makeText(context, "请输入昵称", Toast.LENGTH_SHORT).show();
                    else {
                        jo.put("nickname", MyFragment.edtTxt_nickname.getText().toString().trim());
                        if (MyFragment.rdoGp_gender.getCheckedRadioButtonId() == R.id.my_rdo_genderMan)
                            sex = "男";
                        else if (MyFragment.rdoGp_gender.getCheckedRadioButtonId() == R.id.my_rdo_genderFemale)
                            sex = "女";
                        else
                            sex = "保密";
                        jo.put("sex", sex);
                        jo.put("signature", MyFragment.edtTxt_signature.getText().toString().trim());
                        Handler mHandler = new Handler();
                        MyHttpUtil.put(PublicConfig.url_put_dashboard(), update_header, jo.toJSONString(), new MyHttpUtil.HttpCallbackListener() {
                            @Override
                            public void beforeFinish(HttpURLConnection connection) {

                            }

                            @Override
                            public void onFinish(String response) {
                                JSONObject json = JSON.parseObject(response);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (json.getBoolean("status")) {
                                            Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onError(Exception e, String response) {

                            }
                        });
                    }
                    if (MyFragment.edtTxt_oldPasswd.getText().toString().trim().length() > 5 && MyFragment.edtTxt_newPasswd.getText().toString().trim().length() > 5) {
                        Pattern pattern = Pattern.compile("([a-z]|[A-Z])*");
                        Pattern pattern1 = Pattern.compile("[0-9]*");
                        Matcher matcher = pattern.matcher(MyFragment.edtTxt_newPasswd.getText().toString().trim());
                        Matcher matcher1 = pattern1.matcher((MyFragment.edtTxt_newPasswd.getText().toString().trim()));
                        if (matcher.find()) {
                            if (matcher1.find()) {
                                Map<String, String> password_header = new HashMap<>();
                                password_header.put("Cookie", CommonUtil.getUserCookie(this));
                                com.alibaba.fastjson.JSONObject json = new JSONObject();
                                json.put("oldPassword", MyFragment.edtTxt_oldPasswd.getText().toString().trim());
                                json.put("newPassword", MyFragment.edtTxt_newPasswd.getText().toString().trim());
                                Handler mHandler2 = new Handler();
                                MyHttpUtil.post(PublicConfig.url_put_dashboard(), password_header, json.toJSONString(), new MyHttpUtil.HttpCallbackListener() {
                                    @Override
                                    public void beforeFinish(HttpURLConnection connection) {

                                    }

                                    @Override
                                    public void onFinish(String response) {
                                        JSONObject json = JSON.parseObject(response);
                                        mHandler2.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (json.getBoolean("status")) {
                                                    Toast.makeText(context, "更改密码成功", Toast.LENGTH_SHORT).show();
                                                    MyFragment.edtTxt_newPasswd.setText("");
                                                    MyFragment.edtTxt_oldPasswd.setText("");
                                                } else
                                                    Toast.makeText(context, "更改密码失败,检查密码是否正确", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(Exception e, String response) {

                                    }
                                });
                            } else
                                Toast.makeText(context, "请输入正确的密码（包含字母和数字,6位或以上）", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, "请输入正确的密码（包含字母和数字,6位或以上）", Toast.LENGTH_SHORT).show();
                    }
                    if (MyFragment.img_base64.equals(MyFragment.bitmapToBase64(((BitmapDrawable) MyFragment.iv_userIcon.getDrawable()).getBitmap()))) {
                    } else {
                        Map<String, String> head_header = new HashMap<>();
                        head_header.put("Cookie", CommonUtil.getUserCookie(this));
                        com.alibaba.fastjson.JSONObject headjson = new JSONObject();
                        headjson.put("head", MyFragment.bitmapToBase64(((BitmapDrawable) MyFragment.iv_userIcon.getDrawable()).getBitmap()));
                        Handler mHandler3 = new Handler();
                        MyHttpUtil.put(PublicConfig.url_put_head(), head_header, headjson.toJSONString(), new MyHttpUtil.HttpCallbackListener() {
                            @Override
                            public void beforeFinish(HttpURLConnection connection) {

                            }

                            @Override
                            public void onFinish(String response) {
                                JSONObject json = JSON.parseObject(response);
                                mHandler3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (json.getBoolean("status")) {
                                            Toast.makeText(context, "更改头像成功", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(context, "更改头像失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onError(Exception e, String response) {

                            }
                        });
                    }
                }
                else
                    Toast.makeText(context, "请先注册再使用该页面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mi_setting:
                Snackbar.make(root, "前往设置页面功能敬请期待", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
            case R.id.mi_signOut:
                if (CommonUtil.getUserCookie(this) != null) {
                    Map<String, String> header = new HashMap<>();
                    header.put("Cookie", CommonUtil.getUserCookie(this));
                    MyHttpUtil.delete(PublicConfig.url_delete_logout(), header, new MyHttpUtil.HttpCallbackListener() {
                        @Override
                        public void beforeFinish(HttpURLConnection connection) {

                        }

                        @Override
                        public void onFinish(String response) {
                        }

                        @Override
                        public void onError(Exception e, String response) {

                        }
                    });
                }
                CommonUtil.deleteUserCookie(this);
                CommonUtil.setUser(this, -1L);
                Intent intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    RadioGroup.OnCheckedChangeListener radioGrouplisten = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            int id = group.getCheckedRadioButtonId();
            switch (group.getCheckedRadioButtonId()) {
                case R.id.my_rdo_genderMan:
                    sex = "男";
                    break;
                case R.id.my_rdo_genderFemale:
                    sex = "女";
                    break;
                case R.id.my_rdo_genderPrivate:
                    sex = "保密";
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vp_main.removeOnPageChangeListener(myPageChangeListener);
    }

    class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rg_main.getChildAt(position);
            radioButton.setChecked(true);
            if (position == 0) {
                setRadioButton(R.drawable.index_select, rb_index);
                setRadioButton(R.drawable.me_unselect, rb_my);
                iv_cloud.setVisibility(View.VISIBLE);
            } else {
                MyFragment.initMydDshboard();
                setRadioButton(R.drawable.index_unselect, rb_index);
                setRadioButton(R.drawable.me_select, rb_my);
                iv_cloud.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    vp_main.setCurrentItem(i);
                    if (i == 0) {
                        tv_main.setText("我的思维导图");
                        optionMenuOn = false;
                        checkOptionMenu();
                    } else {
                        tv_main.setText("个人");
                        optionMenuOn = true;
                        checkOptionMenu();
                    }
                    return;
                }
            }
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return this.mList == null ? null : this.mList.get(position);
        }

        @Override
        public int getCount() {
            return this.mList == null ? 0 : this.mList.size();
        }
    }

}

package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.CreateMindmapDialogFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.IndexFragment;
import com.aqinn.mobilenetwork_teamworkmindmap.view.ui.fragment.MyFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aqinn
 * @date 2020/6/14 9:42 PM
 */
public class IndexActivity extends AppCompatActivity {

    // 组件
    private RelativeLayout root;
    private ViewPager vp_main;
    private RadioGroup rg_main;
    private Toolbar tb_main;
    private TextView tv_main;

    // 其它
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private MyPageChangeListener myPageChangeListener;
    private MyOnCheckedChangeListener myOnCheckedChangeListener;
    private boolean optionMenuOn = false;  //标示是否要显示optionmenu
    private Menu mMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initAllView();

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

        setSupportActionBar(tb_main);
        getSupportActionBar().setTitle("");

        mFragments = new ArrayList<>(2);
        mFragments.add(IndexFragment.newInstance());
        mFragments.add(MyFragment.newInstance());
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp_main.setAdapter(mAdapter);
        myPageChangeListener = new MyPageChangeListener();
        myOnCheckedChangeListener = new MyOnCheckedChangeListener();
        vp_main.addOnPageChangeListener(myPageChangeListener);
        rg_main.setOnCheckedChangeListener(myOnCheckedChangeListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        checkOptionMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkOptionMenu(){
        if(null != mMenu){
            if(optionMenuOn){
                for (int i = 0; i < mMenu.size(); i++){
                    mMenu.getItem(i).setVisible(true);
                    mMenu.getItem(i).setEnabled(true);
                }
            } else {
                for (int i = 0; i < mMenu.size(); i++){
                    mMenu.getItem(i).setVisible(false);
                    mMenu.getItem(i).setEnabled(false);
                }
            }
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
        switch (item.getItemId()){
            case R.id.mi_update:
                Snackbar.make(root, "更新功能敬请期待", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
            case R.id.mi_setting:
                Snackbar.make(root, "前往设置页面功能敬请期待", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vp_main.removeOnPageChangeListener(myPageChangeListener);
    }

    class MyPageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rg_main.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
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

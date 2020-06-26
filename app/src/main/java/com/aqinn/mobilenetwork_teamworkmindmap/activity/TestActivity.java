package com.aqinn.mobilenetwork_teamworkmindmap.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aqinn.mobilenetwork_teamworkmindmap.R;
import com.aqinn.mobilenetwork_teamworkmindmap.model.NodeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.model.TreeModel;
import com.aqinn.mobilenetwork_teamworkmindmap.util.DensityUtils;
import com.aqinn.mobilenetwork_teamworkmindmap.util.MyHttpUtil;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.RightTreeLayoutManager;
import com.aqinn.mobilenetwork_teamworkmindmap.view.mindmap.TreeView;

public class TestActivity extends AppCompatActivity {

    private Button bt_test;

    // halo

    private Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initAllView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.caiyunapp.com/v2.5/TAkhjf8d1nlSlspN/121.6544,25.1552/weather.json";
                MyHttpUtil.get(url, new MyHttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.d("xxx", "Finish:\n" + response);
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.d("xxx", e.getMessage());
                    }
                });
            }
        });

    }

    private void initAllView() {
        bt_test = findViewById(R.id.bt_test);
    }

}

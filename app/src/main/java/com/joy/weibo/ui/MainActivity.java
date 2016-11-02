package com.joy.weibo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.joy.baselib.util.CoreUtils;
import com.joy.weibo.R;
import com.joy.weibo.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onKillProcess(context);
        CoreUtils.exitApp(context);
    }
}

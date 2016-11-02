package com.joy.weibo.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.joy.baselib.util.CoreUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/11/2.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context context;
    protected Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreUtils.removeAppActivity(this);
    }
}

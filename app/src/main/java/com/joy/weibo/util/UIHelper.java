package com.joy.weibo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import com.joy.weibo.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

public class UIHelper {

    /**
     * 跳转到主页面
     * @param context
     */
    public static void forwardMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到设置网络页面(手机内置)
     * @param activity
     */
    public static void forwardNetWorkSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivityForResult(intent, Constants.NETWORK_QUEST_CODE);
    }
}

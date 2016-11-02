package com.joy.weibo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.joy.baselib.network.NetWorkStateService;
import com.joy.baselib.util.CoreUtils;
import com.joy.baselib.util.DialogUtils;
import com.joy.baselib.util.L;
import com.joy.baselib.util.NetworkUtils;
import com.joy.baselib.util.T;
import com.joy.weibo.R;
import com.joy.weibo.base.BaseActivity;
import com.joy.weibo.util.AccessTokenKeeper;
import com.joy.weibo.util.Constants;
import com.joy.weibo.util.UIHelper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/13.
 */
public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.time_schedule)
    public TextView showtime;

    private Context context;
    private Oauth2AccessToken accessToken;
    private SsoHandler ssoHandler;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x123) {
                initToken();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //对日志加密,提高安全性
        MobclickAgent.enableEncrypt(false);
        context = this;
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //启动网络监听服务
        startService(new Intent(this, NetWorkStateService.class));
        handler.sendEmptyMessageDelayed(0x123, 3000L);
    }

    private void initToken() {
        if (!NetworkUtils.isHaveNetWork) {
            DialogUtils.showDialog(context, R.string.dialog_setting_title, R.string.dialog_setting_content,
                    R.string.dialog_setting_confirm, R.string.dialog_setting_logout,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UIHelper.forwardNetWorkSetting(WelcomeActivity.this);
                        }
                    }
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CoreUtils.exitApp(context);
                        }
                    }
            );
        } else {
            //获取token
            accessToken = AccessTokenKeeper.readAccessToken(context);
            if (accessToken.isSessionValid()) { //判断token是否有效
                MobclickAgent.onProfileSignIn(accessToken.getUid());
                UIHelper.forwardMain(context);
                finish();
            } else {
                initAuth();
            }
        }
    }

    private void initAuth() {
        AuthInfo authInFo = new AuthInfo(context, Constants.APP_KEY, Constants.REDIRECT_URL,
                Constants.SCOPE);
        ssoHandler = new SsoHandler(this, authInFo);
        ssoHandler.authorize(new AuthListener());
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle bundle) {
            accessToken = Oauth2AccessToken.parseAccessToken(bundle);
            if (accessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(context, accessToken);
                T.showShort(context, R.string.toast_auth_success);
                MobclickAgent.onProfileSignIn(accessToken.getUid());
                UIHelper.forwardMain(context);
                finish();
            } else {
                String code = bundle.getString("code");
                String message = getString(R.string.toast_auth_fail);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "code:" + code;
                }
                T.showShort(context, message);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            T.showShort(context, "Auth Exception:" + e.getMessage());
        }

        @Override
        public void onCancel() {
            T.showShort(context, R.string.toast_auth_cancel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NETWORK_QUEST_CODE) {
            init();
        } else {
            if (ssoHandler != null) {
                //发起sso授权必须回调该方法
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        context.stopService(new Intent(context, NetWorkStateService.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

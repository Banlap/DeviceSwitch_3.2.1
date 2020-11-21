package com.aliyun.iot.ilop.demo.page.account;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.iot.ilop.demo.view.ALoadView2;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.demo.R;

/**
 * Created by feijie.xfj on 17/11/7.
 */

public class AccountActivity extends AActivity {

    private View loginTV;
    private View logoutTV;

    private ALoadView2 loadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.account_activity);
        super.onCreate(savedInstanceState);

        loadView = findViewById(R.id.account_loading_loadview);

        ((TextView) this.findViewById(R.id.topbar_title_textview)).setText(R.string.account_title);
        this.findViewById(R.id.topbar_back_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.loginTV = this.findViewById(R.id.account_login_textview);
        this.logoutTV = this.findViewById(R.id.account_logout_textview);

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBusiness.login(new ILoginCallback() {
                    @Override
                    public void onLoginSuccess() {
                        toast(getApplication().getString(R.string.account_login_success));
                    }

                    @Override
                    public void onLoginFailed(int code, String error) {
                        toast(getApplication().getString(R.string.account_login_failed) + error);
                    }
                });
            }
        });


        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginBusiness.isLogin()) {
                    toast("还没有登录，请先登录");
                    return;
                }
                loadView.showLoading();
                LoginBusiness.logout(new ILogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        if (loadView != null) {
                            loadView.hide();
                        }
                        toast(getApplication().getString(R.string.account_logout_success));
                        finish();
                    }

                    @Override
                    public void onLogoutFailed(int code, String error) {
                        if (loadView != null) {
                            loadView.hide();
                        }
                        toast(getApplication().getString(R.string.account_logout_failed) + error);
                    }
                });
            }
        });
    }


    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AccountActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

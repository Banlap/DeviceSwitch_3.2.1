package com.aliyun.iot.ilop.demo.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.config.GlobalConfig;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.SDKInitHelper;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.aliyun.iot.ilop.startpage.list.main.countryselect.CountryListActivity;
import com.aliyun.iot.link.ui.component.LinkToast;

import static com.aliyun.iot.aep.sdk.IoTSmart.REGION_CHINA_ONLY;

public class StartActivity extends AActivity {
    private static final String TAG = "StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);


        if (LoginBusiness.isLogin()) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            //if (GlobalConfig.getInstance().getInitConfig().getRegionType() == REGION_CHINA_ONLY) {
            startLogin();
            /*} else {
                gotoCountryList();
            }*/
        }


    }

    private void gotoCountryList() {
        IoTSmart.ICountrySelectCallBack callBack = (country) -> {
            IoTSmart.setCountry(country, new IoTSmart.ICountrySetCallBack() {
                @Override
                public void onCountrySet(boolean needRestartApp) {

                    if (needRestartApp) {
                        killProcess();
                    } else {
                        SDKInitHelper.init(AApplication.getInstance());
                        startLogin();
                    }
                }
            });

        };

        boolean useDefault = false;
        // 是否使用默认的国家选择页面
        if (!useDefault) {
            Intent intent = new Intent(StartActivity.this, CountryListActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finishLater();
        } else {
            IoTSmart.showCountryList(callBack);
        }

    }

    private void startLogin() {
        //跳转到登录界面
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                MainActivity.start(StartActivity.this);
                overridePendingTransition(0, 0);
            }

            @Override
            public void onLoginFailed(int i, String s) {
                // LinkToast.makeText(getApplicationContext(), s).show();
            }
        });
        finishLater();

    }

    private void killProcess() {
        LinkToast.makeText(StartActivity.this, R.string.region_restart_confirm).show();
        ThreadPool.MainThreadHandler.getInstance().post(() -> Process.killProcess(Process.myPid()), 2000);
    }

    private void finishLater() {
        ThreadPool.MainThreadHandler.getInstance().post(() -> finish(), 500);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

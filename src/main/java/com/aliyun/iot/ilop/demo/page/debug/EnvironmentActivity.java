package com.aliyun.iot.ilop.demo.page.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.config.GlobalConfig;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;

import java.util.Arrays;

/**
 * Created by sinyuk on 03,December,2019
 **/

public class EnvironmentActivity extends AppCompatActivity {
    public static void show(@NonNull Context context) {
        Intent intent = new Intent(context, EnvironmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    private TextView restartButton;

    int[] oldIndex = new int[3];
    int[] newIndex = new int[3];

    Env mEnv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEnv = Env.getInstance();
        setContentView(R.layout.activity_environment);
        restartButton = findViewById(R.id.button_restart);
        restartButton.setOnClickListener(view -> logoutAndQuit());


        RadioGroup radioGroup1 = findViewById(R.id.radio_group_product);
        RadioGroup radioGroup2 = findViewById(R.id.radio_group_api);
        RadioGroup radioGroup3 = findViewById(R.id.radio_group_bone);


        radioGroup1.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.product_dev:
                    mEnv.setProductEnv("develop");
                    break;
                case R.id.product_online:
                    mEnv.setProductEnv("production");
                    break;
                default:
                    break;
            }
            newIndex[0] = i;
            toggleRestartButton();
        });

        radioGroup2.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.apiclient_test:
                    mEnv.setApiEnv("test");
                    break;
                case R.id.apiclient_pre:
                    mEnv.setApiEnv("pre");
                    break;
                case R.id.apiclient_online:
                    mEnv.setApiEnv("release");
                    break;
                default:
                    break;
            }
            newIndex[1] = i;
            toggleRestartButton();
        });

        radioGroup3.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.bone_test:
                    mEnv.setBoneEnv("test");
                    break;
                case R.id.bone_pretest:
                    mEnv.setBoneEnv("pretest");
                    break;
                case R.id.bone_release:
                    mEnv.setBoneEnv("release");
                    break;
                default:
                    break;
            }
            newIndex[2] = i;
            toggleRestartButton();
        });
    }


    private void toggleRestartButton() {
        ALog.d("sinyuk", "app: " + mEnv.getProductEnv() + ", api: " + mEnv.getApiEnv() + ", bone: " + mEnv.getBoneEnv());
        if (Arrays.equals(newIndex, oldIndex)) {
            restartButton.setVisibility(View.GONE);
        } else {
            restartButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IoTSmart.InitConfig oldConfig = GlobalConfig.getInstance().getInitConfig();


        RadioGroup radioGroup1 = findViewById(R.id.radio_group_product);
        RadioGroup radioGroup2 = findViewById(R.id.radio_group_api);
        RadioGroup radioGroup3 = findViewById(R.id.radio_group_bone);

        if ("develop".equalsIgnoreCase(oldConfig.getProductEnv())) {
            radioGroup1.check(R.id.product_dev);
        } else if ("production".equalsIgnoreCase(oldConfig.getProductEnv())) {
            radioGroup1.check(R.id.product_online);
        } else {
            Log.d("sinyuk", "product: " + oldConfig.getProductEnv());
        }

        if ("test".equalsIgnoreCase(GlobalConfig.getInstance().getApiEnv())) {
            radioGroup2.check(R.id.apiclient_test);
        } else if ("pre".equalsIgnoreCase(GlobalConfig.getInstance().getApiEnv())) {
            radioGroup2.check(R.id.apiclient_pre);
        } else if ("release".equalsIgnoreCase(GlobalConfig.getInstance().getApiEnv())) {
            radioGroup2.check(R.id.apiclient_online);
        } else {
            Log.d("sinyuk", "api: " + GlobalConfig.getInstance().getApiEnv());
        }

        if ("test".equalsIgnoreCase(GlobalConfig.getInstance().getBoneEnv())) {
            radioGroup3.check(R.id.bone_test);
        } else if ("pretest".equalsIgnoreCase(GlobalConfig.getInstance().getBoneEnv()) || "pre".equalsIgnoreCase(GlobalConfig.getInstance().getBoneEnv())) {
            radioGroup3.check(R.id.bone_pretest);
        } else if ("release".equalsIgnoreCase(GlobalConfig.getInstance().getBoneEnv())) {
            radioGroup3.check(R.id.bone_release);
        } else {
            Log.d("sinyuk", "bone: " + GlobalConfig.getInstance().getBoneEnv());
        }

        oldIndex[0] = radioGroup1.getCheckedRadioButtonId();
        oldIndex[1] = radioGroup2.getCheckedRadioButtonId();
        oldIndex[2] = radioGroup3.getCheckedRadioButtonId();

        newIndex = oldIndex.clone();
        toggleRestartButton();

    }

    public void logoutAndQuit() {
        Env.getInstance().setSwitched(true);
        Env.getInstance().storeEnv();
        try {
            LoginBusiness.logout(new ILogoutCallback() {
                @Override
                public void onLogoutSuccess() {
                    killProcess();
                }

                @Override
                public void onLogoutFailed(int i, String s) {
                    killProcess();
                }
            });
        } catch (Exception ignored) {

        } finally {
            killProcess();
        }
    }

    private void killProcess() {
        Toast.makeText(this, R.string.region_restart_confirm, Toast.LENGTH_SHORT).show();
        finish();
        ThreadPool.MainThreadHandler.getInstance().post(() -> Process.killProcess(Process.myPid()), 500);
    }


}

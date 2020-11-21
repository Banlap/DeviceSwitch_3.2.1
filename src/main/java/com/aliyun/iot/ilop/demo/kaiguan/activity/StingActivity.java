package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.dialog.ASlideDialog;
import com.aliyun.iot.ilop.demo.page.main.StartActivity;
import com.aliyun.iot.ilop.demo.utils.CleanCacheUtil;


public class StingActivity extends AppCompatActivity implements View.OnClickListener {
    

    
    private TextView outlogin_text, talto_text, allCache_text;
    private RelativeLayout updateVersion_layout, cleanCache_layout;
    // account
    ASlideDialog menuDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_list);
        initView();
    }


    private void initView() {
        outlogin_text=findViewById(R.id.outlogin_text);
        outlogin_text.setOnClickListener(this);
        talto_text=findViewById(R.id.talto_text);
        talto_text.setText("设置");
        updateVersion_layout = findViewById(R.id.updateVersion_layout);
        updateVersion_layout.setOnClickListener(this);
        cleanCache_layout = findViewById(R.id.cleanCache_layout);
        cleanCache_layout.setOnClickListener(this);
        allCache_text = findViewById(R.id.tv_allCache);

    }
    @Override
    protected void onResume() {
        super.onResume();
        //banlap： 计算当前app缓存
        String totalCacheSize = null;
        try {
            totalCacheSize = CleanCacheUtil.getTotalCacheSize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        allCache_text.setText(totalCacheSize);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cleanCache_layout:
                //清除缓存
                CleanCacheUtil.clearAllCache(getApplicationContext());
                Toast.makeText(getApplicationContext(), "已清除缓存", Toast.LENGTH_SHORT).show();

                //重新计算缓存大小并显示
                String reTotalCacheSize = null;
                try {
                    reTotalCacheSize = CleanCacheUtil.getTotalCacheSize(getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                allCache_text.setText(reTotalCacheSize);
                break;
            case R.id.updateVersion_layout:
                Toast.makeText(getApplicationContext(), "当前为最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.outlogin_text:
                accountShowMenuDialog();
                break;

        }
    }


    private void accountShowMenuDialog() {
        if (menuDialog == null) {
            menuDialog = ASlideDialog.newInstance(StingActivity.this, ASlideDialog.Gravity.Bottom, R.layout.menu_dialog);
            menuDialog.findViewById(R.id.menu_logout_textview).setOnClickListener(v -> {

                LoginBusiness.logout(new ILogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        Toast.makeText(StingActivity.this, getString(R.string.account_logout_success), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StingActivity.this.getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (menuDialog != null) {
                            menuDialog.dismiss();
                        }
                        StingActivity.this.finish();
                        StingActivity.this.overridePendingTransition(0, 0);

                    }

                    @Override
                    public void onLogoutFailed(int code, String error) {
                        Toast.makeText(StingActivity.this, getString(R.string.account_logout_failed) + error, Toast.LENGTH_SHORT);
                    }
                });
                accountHideMenuDialog();
            });
            menuDialog.findViewById(R.id.menu_cancel_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountHideMenuDialog();
                }
            });
            menuDialog.setCanceledOnTouchOutside(true);
        }
        //设置当前登录用户名
        ((TextView) menuDialog.findViewById(R.id.menu_name_textview)).setText(getUserNick());
        menuDialog.show();
    }

    private void accountHideMenuDialog() {
        if (menuDialog != null) {
            menuDialog.hide();
        }
    }

    private String getUserNick() {
        //设置当前登录用户名
        if (LoginBusiness.isLogin()) {
            UserInfo userInfo = LoginBusiness.getUserInfo();
            String userName = "";
            if (userInfo != null) {
                if (!TextUtils.isEmpty(userInfo.userNick) && !"null".equalsIgnoreCase(userInfo.userNick)) {
                    userName = userInfo.userNick;
                } else if (!TextUtils.isEmpty(userInfo.userPhone)) {
                    userName = userInfo.userPhone;
                } else if (!TextUtils.isEmpty(userInfo.userEmail)) {
                    userName = userInfo.userEmail;
                } else if (!TextUtils.isEmpty(userInfo.openId)) {
                    userName = userInfo.openId;
                } else {
                    userName = "未获取到用户名";
                }
            }
            return userName;
        }
        return null;
    }
}
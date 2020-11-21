package com.aliyun.iot.ilop.demo.page.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.iot.aep.sdk.EnvConfigure;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.aep.sdk.framework.AActivity;

public class AboutActivity extends AActivity implements View.OnLongClickListener {
    static private final String TAG = "AboutActivity";

    private ClickListener clickListener;

    private TextView appkeyTV, deviceIdTV, traceIdTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about_activity);
        super.onCreate(savedInstanceState);

        appkeyTV = findViewById(R.id.about_appkey_textview);
        deviceIdTV = findViewById(R.id.about_deviceid_textview);
        traceIdTV = findViewById(R.id.about_traceid_textview);

        this.clickListener = new ClickListener();

        ((TextView) this.findViewById(R.id.topbar_title_textview)).setText(R.string.about_title_text);

        appkeyTV.setText((String) EnvConfigure.getEnvArg(EnvConfigure.KEY_APPKEY));

        String deviceId = (String) EnvConfigure.getEnvArg(EnvConfigure.KEY_DEVICE_ID);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = "";
        }
        deviceIdTV.setText(deviceId);

        String traceId = (String) EnvConfigure.getEnvArg(EnvConfigure.KEY_TRACE_ID);
        if (TextUtils.isEmpty(traceId)) {
            traceId = "";
        }
        traceIdTV.setText(traceId);


        ((TextView) this.findViewById(R.id.about_version_textview)).setText(getVersionName());

        this.findViewById(R.id.topbar_back_imageview).setOnClickListener(this.clickListener);
        this.findViewById(R.id.about_version_notice_relativelayout).setOnClickListener(this.clickListener);


        appkeyTV.setOnLongClickListener(this);
        deviceIdTV.setOnLongClickListener(this);
        traceIdTV.setOnLongClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.clickListener = null;
    }

    /* methods: help */
    private String getVersionName() {

        String ret = "";

        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo;
        try {
            if (null != packageManager) {
                packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
                ret = null != packageInfo ? packageInfo.versionName : "";
            }
        } catch (Exception ex) {
            ALog.e(TAG, "request version name failed", ex);
        }

        return ret;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.about_appkey_textview:
                copyToClipboard(appkeyTV.getText().toString(), "appkey已复制到剪贴板");
                break;
            case R.id.about_deviceid_textview:
                copyToClipboard(deviceIdTV.getText().toString(), "deviceId已复制到剪贴板");
                break;
            case R.id.about_traceid_textview:
                copyToClipboard(traceIdTV.getText().toString(), "traceId已复制到剪贴板");
                break;
        }
        return false;
    }


    /* inner type */

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.topbar_back_imageview: {
                    AboutActivity.this.finish();
                    break;
                }
                case R.id.about_version_notice_relativelayout: {
                    startActivity(new Intent(AboutActivity.this, CopyrightActivity.class));
                }
                default: {
                    break;
                }
            }

        }
    }


    private void copyToClipboard(String str, String toastStr) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (!TextUtils.isEmpty(toastStr)) {
            Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("About", str);
        cm.setPrimaryClip(mClipData);
    }

}

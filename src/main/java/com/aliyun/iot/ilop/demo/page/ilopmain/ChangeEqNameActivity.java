package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.demo.R;

public class ChangeEqNameActivity extends AActivity {
    private final String path = "/uc/setDeviceNickName";
    private String iotId;
    private EditText editText;
    private String title;
    private Context context;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.change_eq_name_activity);
        initView();
        initData();
        context = this;
    }

    private void initView() {
        title = getIntent().getStringExtra("title");

        editText = findViewById(R.id.change_name_et);
        findViewById(R.id.change_name_back).setOnClickListener(v -> onPressBack(title));
        findViewById(R.id.change_name_save).setOnClickListener(v -> {
            if (TextUtils.isEmpty(editText.getText())) {
                Toast.makeText(this, "备注名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            requestChangeName(iotId, editText.getText().toString());
        });
        findViewById(R.id.change_name_cancel).setOnClickListener(v -> editText.setText(""));
        if (!TextUtils.isEmpty(title)) {
            editText.setText(title);
        }
    }

    private void initData() {
        iotId = getIntent().getStringExtra("iotId");
    }

    public void requestChangeName(String iotId, String nickName) {
        Log.d("EqSettingHelp", "_______________" + iotId);
        String apiVersion = "1.0.2";
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setScheme(Scheme.HTTPS)
                .setPath(path)
                .setApiVersion(apiVersion)
                .addParam("nickName", nickName)
                .addParam("iotId", iotId);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                editText.post(() -> Toast.makeText(ChangeEqNameActivity.this, "修改失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                Log.d("ChangeEqNameActivity", "onResponse");
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    Toast.makeText(ChangeEqNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    onPressBack(editText.getText().toString());
                });
            }
        });
    }

    public void onPressBack(String title) {
        Intent intent = new Intent();
        intent.putExtra("title", title);
        setResult(LampsActivity.titleResultCode, intent);
        //关闭当前activity
        finish();
    }
}

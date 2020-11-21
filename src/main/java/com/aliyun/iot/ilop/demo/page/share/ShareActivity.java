package com.aliyun.iot.ilop.demo.page.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.link.ui.component.LinkToast;
import com.aliyun.iot.link.ui.component.LoadingCompact;
import com.aliyun.iot.link.ui.component.nav.UINavigationBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareActivity extends AppCompatActivity {

    public static void start(Context context, ArrayList<String> iotIdList) {
        Intent starter = new Intent(context, ShareActivity.class);
        starter.putExtra("iotIdList", iotIdList);
        context.startActivity(starter);
    }

    TextInputEditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_share);
        UINavigationBar navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setOnClickListener(view -> finish());


        FloatingActionButton fab = findViewById(R.id.fab);
        inputEditText = findViewById(R.id.input_box);

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean valid = validate(editable.toString());
                fab.setEnabled(valid);
                if (TextUtils.isEmpty(editable) || valid) {
                    inputEditText.setError(null);
                } else {
                    inputEditText.setError("invalid phone number or e-mail");
                }
            }
        });

        fab.setEnabled(false);
        fab.setOnClickListener(view -> onShare(Objects.requireNonNull(inputEditText.getText()).toString()));

    }

    private void onShare(String text) {
        String type;
        if (text.contains("@")) {
            type = "EMAIL";
        } else {
            type = "MOBILE";
        }

        Map<String, Object> param = new HashMap<>();
        switch (type) {
            case "MOBILE":
                param.put("accountAttr", text);
                param.put("accountAttrType", "MOBILE");
                break;
            case "EMAIL":
                param.put("accountAttr", text);
                param.put("accountAttrType", "EMAIL");
                break;
            default:
                break;
        }

        String mobileLocationCode = "86";

        if (getIntent() == null || !getIntent().hasExtra("iotIdList")) {
            return;
        }

        param.put("iotIdList", getIntent().getStringArrayListExtra("iotIdList"));
        param.put("mobileLocationCode", mobileLocationCode);

        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setApiVersion("1.0.3");
        builder.setPath("/uc/shareDevicesAndScenes");
        builder.setScheme(Scheme.HTTPS);
        builder.setParams(param);
        builder.setAuthType("iotAuth");
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        LoadingCompact.showLoading(this);
        ioTAPIClient.send(builder.build(), new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> {
                    LoadingCompact.dismissLoading(ShareActivity.this);
                    String error = e == null ? "share failed" : e.getLocalizedMessage();
                    Toast.makeText(ShareActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> {
                    LoadingCompact.dismissLoading(ShareActivity.this);
                    if (200 != ioTResponse.getCode()) {
                        String error = ioTResponse.getLocalizedMsg();
                        Toast.makeText(ShareActivity.this, error, Toast.LENGTH_LONG).show();
                    } else {
                        LinkToast.makeText(ShareActivity.this, "share succeed").show();
                        finish();
                    }
                });
            }
        });

    }


    private boolean validate(String string) {
        return TextUtils.isDigitsOnly(string) || isEmail(string);
    }


    private boolean isEmail(String email) {
        try {
            final String pattern1 = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$";
            final Pattern pattern = Pattern.compile(pattern1);
            final Matcher mat = pattern.matcher(email);
            return mat.matches();
        } catch (Exception ignored) {
        }
        return false;
    }
}

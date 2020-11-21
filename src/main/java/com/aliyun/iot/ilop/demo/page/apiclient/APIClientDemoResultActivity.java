package com.aliyun.iot.ilop.demo.page.apiclient;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.aep.sdk.framework.AActivity;

/**
 * Created by guikong on 17/11/18.
 */

public class APIClientDemoResultActivity extends AActivity {

    public static final String KEY_CODE = "code";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_LOCALIZED_MSG = "localizedMsg";
    public static final String KEY_DATA = "data";
    public static final String KEY_RAW_DATA = "rawData";
    public static final String KEY_EXCEPTION_MSG = "exceptionMsg";

    int code;
    String message;
    String localizedMsg;
    String data;
    String rawData;

    String exceptionMsg;

    EditText resultEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.api_client_demo_result_activity);

        resultEditText = findViewById(R.id.api_client_demo_result_edittext);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ImageView backImageView = findViewById(R.id.topbar_back_imageview);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleTextView = findViewById(R.id.topbar_title_textview);
        titleTextView.setText(R.string.api_client_demo_result_title);

        Intent intent = getIntent();

        if (null == intent) {
            return;
        }

        code = intent.getIntExtra(KEY_CODE, 0);
        message = intent.getStringExtra(KEY_MESSAGE);
        localizedMsg = intent.getStringExtra(KEY_LOCALIZED_MSG);
        data = intent.getStringExtra(KEY_DATA);
        rawData = intent.getStringExtra(KEY_RAW_DATA);
        exceptionMsg = intent.getStringExtra(KEY_EXCEPTION_MSG);

        // build string
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(exceptionMsg)) {
            builder.append("Exception:").append("\r\n")
                    .append(exceptionMsg);
        } else {
            builder.append("Code:").append("\r\n").append(code).append("\r\n").append("\r\n")
                    .append("data:").append("\r\n").append(data).append("\r\n").append("\r\n")
                    .append("Message:").append("\r\n").append(TextUtils.isEmpty(message) ? "null" : message).append("\r\n").append("\r\n")
                    .append("LocalizedMsg:").append("\r\n").append(TextUtils.isEmpty(localizedMsg) ? "null" : localizedMsg).append("\r\n").append("\r\n")
                    .append("RawData:").append("\r\n").append(TextUtils.isEmpty(rawData) ? "null" : rawData).append("\r\n").append("\r\n");
        }

        resultEditText.setText(builder.toString());
    }

    public void copy(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("apiclient_demo", resultEditText.getText().toString());

        if (null != cm) {
            cm.setPrimaryClip(mClipData);
        }

        Toast.makeText(this, R.string.api_client_demo_result_copy_done, Toast.LENGTH_LONG).show();
    }
}

package com.aliyun.iot.ilop.demo.page.apiclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * IoTAPIClient Demo 展示页
 */
public class APIClientDemoActivity extends Activity {

    EditText hostEditText;
    EditText pathEditText;
    EditText apiVersionEditText;
    EditText paramsEditText;

    RadioGroup schemeRadioGroup;
    RadioGroup languageRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.api_client_demo_activity);

        hostEditText = (EditText) findViewById(R.id.api_client_demo_host_edittext);
        pathEditText = (EditText) findViewById(R.id.api_client_demo_path_edittext);
        apiVersionEditText = (EditText) findViewById(R.id.api_client_demo_api_version_edittext);
        paramsEditText = (EditText) findViewById(R.id.api_client_demo_params_edittext);
        schemeRadioGroup = (RadioGroup) findViewById(R.id.api_client_demo_scheme_radiogroup);
        languageRadioGroup = (RadioGroup) findViewById(R.id.api_client_demo_language_radiogroup);
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
        titleTextView.setText(R.string.api_client_demo_title);

        TextView menuTextView = findViewById(R.id.topbar_menu_textview);
        menuTextView.setVisibility(View.VISIBLE);
        menuTextView.setText(R.string.api_client_demo_menu);
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostEditText.setText("");
                pathEditText.setText("/say/hello");
                apiVersionEditText.setText("1.1.1");
                paramsEditText.setText("{}");
                schemeRadioGroup.check(R.id.api_client_demo_scheme_https_radiobutton);
            }
        });
    }

    public void switchHttps(View view) {
        schemeRadioGroup.check(R.id.api_client_demo_scheme_https_radiobutton);
    }

    public void switchHttp(View view) {
        schemeRadioGroup.check(R.id.api_client_demo_scheme_http_radiobutton);
    }

    public void switchZh(View view) {
        languageRadioGroup.check(R.id.api_client_demo_language_cn_radiobutton);
        IoTAPIClientImpl.getInstance().setLanguage("zh-CN");
    }

    public void switchEn(View view) {
        languageRadioGroup.check(R.id.api_client_demo_language_en_radiobutton);
        IoTAPIClientImpl.getInstance().setLanguage("en-US");
    }

    public void testAPI(View view) {

        String host, path, apiVersion, params;
        Scheme scheme;

        host = hostEditText.getText().toString();

        path = pathEditText.getText().toString();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, R.string.api_client_demo_error_empty_path, Toast.LENGTH_LONG).show();
            return;
        }

        apiVersion = apiVersionEditText.getText().toString();
        if (TextUtils.isEmpty(apiVersion)) {
            Toast.makeText(this,  R.string.api_client_demo_error_empty_api_version, Toast.LENGTH_LONG).show();
            return;
        }

        params = paramsEditText.getText().toString();

        if (schemeRadioGroup.getCheckedRadioButtonId() == R.id.api_client_demo_scheme_https_radiobutton) {
            scheme = Scheme.HTTPS;
        } else {
            scheme = Scheme.HTTP;
        }

        Map<String, Object> maps = new HashMap<>();
        if (!TextUtils.isEmpty(params)) {
            try {
                maps = JSON.parseObject(params, maps.getClass());
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(this, R.string.api_client_demo_error_invalid_json_format, Toast.LENGTH_LONG).show();
                return;
            }
        }

        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setScheme(scheme)
                .setPath(path)
                .setApiVersion(apiVersion)
                .setParams(maps);

        if (!TextUtils.isEmpty(host)) {
            builder.setHost(host);
        }

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest request, Exception e) {
                Intent intent = new Intent(APIClientDemoActivity.this, APIClientDemoResultActivity.class);
                intent.putExtra(APIClientDemoResultActivity.KEY_EXCEPTION_MSG, e.getMessage());
                startActivity(intent);
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                Intent intent = new Intent(APIClientDemoActivity.this, APIClientDemoResultActivity.class);
                intent.putExtra(APIClientDemoResultActivity.KEY_CODE, response.getCode());
                intent.putExtra(APIClientDemoResultActivity.KEY_MESSAGE, response.getMessage());
                intent.putExtra(APIClientDemoResultActivity.KEY_LOCALIZED_MSG, response.getLocalizedMsg());

                Object data = response.getData();
                if (null != data) {
                    if (data instanceof JSONObject) {
                        try {
                            intent.putExtra(APIClientDemoResultActivity.KEY_DATA, ((JSONObject) data).toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (data instanceof JSONArray) {
                        try {
                            intent.putExtra(APIClientDemoResultActivity.KEY_DATA, ((JSONArray) data).toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        intent.putExtra(APIClientDemoResultActivity.KEY_DATA, data.toString());
                    }
                }

                byte[] rawData = response.getRawData();
                if (null != rawData) {
                    String str = null;
                    try {
                        str = new String(rawData, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (null != str) {
                        intent.putExtra(APIClientDemoResultActivity.KEY_RAW_DATA, str);
                    }
                }

                startActivity(intent);
            }
        });
    }
}
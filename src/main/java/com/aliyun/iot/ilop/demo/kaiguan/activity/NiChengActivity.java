package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.demo.R;

import java.util.LinkedHashMap;
import java.util.Map;


public class NiChengActivity extends AppCompatActivity {

    private EditText nicheng_edit;
    private TextView sen_facility_text,talto_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ni_cheng);
        initView();
    }

    private void initView() {
        nicheng_edit=findViewById(R.id.nicheng_edit);
        UserInfo userInfo = LoginBusiness.getUserInfo();
        if ((userInfo.userNick).equals("null")){
            nicheng_edit.setText("");
        }else {
            nicheng_edit.setText(userInfo.userNick);

        }
        sen_facility_text=findViewById(R.id.sen_facility_text);
        sen_facility_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(nicheng_edit.getText().toString())){
                    sveData(nicheng_edit.getText().toString());
                }

            }
        });
        talto_text=findViewById(R.id.talto_text);
        talto_text.setText("修改昵称");

    }

    private void sveData(String text) {
        Map<String, Object> map = new LinkedHashMap<>();
        //map.put("userNick", text);
        map.put("displayName", text);
        OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
        oas.updateProfile(getApplicationContext(), map, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {
                Toast.makeText(NiChengActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                 finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(NiChengActivity.this,"提交失败"+s,Toast.LENGTH_SHORT).show();
            }
        });



    }
}
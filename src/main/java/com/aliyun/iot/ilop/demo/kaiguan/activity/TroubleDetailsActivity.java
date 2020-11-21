package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.iot.demo.R;


public class TroubleDetailsActivity extends AppCompatActivity {

    private TextView talto_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trouble_details);
        initView();
    }

    private void initView() {
        talto_text=findViewById(R.id.talto_text);
        talto_text.setText("详情信息");
    }
}
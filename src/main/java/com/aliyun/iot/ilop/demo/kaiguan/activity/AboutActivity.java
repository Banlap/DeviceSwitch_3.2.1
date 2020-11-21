package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.iot.demo.R;

public class AboutActivity extends AppCompatActivity {

    private TextView topbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        topbarTitle=findViewById(R.id.topbar_title_textview);
        topbarTitle.setText("关于我们");
    }


}

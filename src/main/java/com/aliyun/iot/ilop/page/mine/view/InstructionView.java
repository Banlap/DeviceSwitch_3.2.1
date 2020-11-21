package com.aliyun.iot.ilop.page.mine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;


public class InstructionView extends FrameLayout{

    private TextView firstCtTv;

    public InstructionView(@NonNull Context context) {
        super(context);init();
    }

    public InstructionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);init();
    }

    public InstructionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init();
    }
    public void init(){
        inflate(getContext(), R.layout.ilop_mine_tmall_instruction_item,this);
        firstCtTv = findViewById(R.id.mine_tm_instruction_ct1_tv);
    }
    public void setContent(String text){
        firstCtTv.setText(text);
    }
}

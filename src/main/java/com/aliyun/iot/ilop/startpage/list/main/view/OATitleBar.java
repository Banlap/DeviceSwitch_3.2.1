package com.aliyun.iot.ilop.startpage.list.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.link.ui.component.nav.UINavigationBar;


public class OATitleBar extends LinearLayout {
    public static final int TYPE_SIMPLE = 0x00000001;
    public static final int TYPE_IMAGE = 0x00000000;

    private int mType = TYPE_IMAGE;
    private ImageView mBtnBack;
    private TextView mTitle;
    private UINavigationBar mTopBar;

    public OATitleBar(Context context) {
        this(context, null);
    }

    public OATitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OATitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setType(TYPE_IMAGE);
    }


    public void setType(int type) {
        mType = type;
        initView();
    }


    private void initView() {
        removeAllViews();
        LayoutInflater.from(getContext()).inflate(R.layout.ali_sdk_openaccount_title_layout_2, this, true);
        RelativeLayout mRlTitle = findViewById(R.id.rl_title);
        View mLineTitle = findViewById(R.id.line_title);
        mTopBar = findViewById(R.id.top_bar);
        mBtnBack = findViewById(R.id.ali_sdk_openaccount_back);
        mTitle = findViewById(R.id.ali_sdk_openaccount_title);
        if (mType == TYPE_SIMPLE) {
            mTopBar.setVisibility(View.VISIBLE);
            mRlTitle.setVisibility(View.GONE);
            mLineTitle.setVisibility(View.GONE);
        } else {
            mTopBar.setVisibility(View.GONE);
            mRlTitle.setVisibility(View.VISIBLE);
            mLineTitle.setVisibility(View.VISIBLE);
        }

    }


    public void setBackClickListener(final OnClickListener onClickListener) {
        if (mType == TYPE_SIMPLE) {
            mTopBar.setNavigationBackAction(view -> onClickListener.onClick(view));
        } else {
            mBtnBack.setOnClickListener(onClickListener);
        }
    }


    public void setTitle(String title) {
        if (mType == TYPE_SIMPLE) {
            mTopBar.setTitle(title);
        } else {
            mTitle.setText(title);
        }
    }

    public void setmBtnBackVisible(int visible) {
        //mBtnBack.setVisibility(visible);
    }

}

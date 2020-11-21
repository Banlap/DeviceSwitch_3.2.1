package com.aliyun.iot.ilop.demo.view;

import android.content.Context;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;

/**
 *
 */
public class SimpleToolBar extends FrameLayout {

    private ImageButton backIb;
    private ImageButton menuIb;
    private TextView titleTv;
    private final int BACK = 0, TITLE = 1, MENU = 2;
    private final String TAG = "SimpleToolBar";
    private View line;
    private LinearLayout linearLayout;

    public SimpleToolBar(@NonNull Context context) {
        super(context);
        initView();
    }

    public SimpleToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SimpleToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.simple_toolbar, this);
        backIb = findViewById(R.id.toolbar_back);
        menuIb = findViewById(R.id.toolbar_menu);
        titleTv = findViewById(R.id.toolbar_title);
        linearLayout = findViewById(R.id.toolbar_ll);
        line = findViewById(R.id.toolbar_line);
    }

    /**
     * 设置title
     *
     * @param title
     */
    public SimpleToolBar setTitle(String title) {
        if (!TextUtils.isEmpty(title) && null != titleTv) {
            titleTv.setText(title);
            onClick(titleTv, TITLE);
        }
        return this;
    }

    /**
     * 设置返回按钮 默认是白色图片
     */
    public SimpleToolBar setBack(boolean flag) {
        if (null == backIb) {
            return this;
        }
        if (flag) {
            backIb.setVisibility(VISIBLE);
            backIb.setImageResource(R.drawable.back_arrow_white);
            onClick(backIb, BACK);
        } else {
            backIb.setVisibility(GONE);
        }
        return this;
    }

    public SimpleToolBar setBack(@DrawableRes int resId) {
        if (null == backIb) {
            return this;
        }
        backIb.setVisibility(VISIBLE);
        backIb.setImageResource(resId);
        onClick(backIb, BACK);
        return this;
    }

    /**
     * 设置右侧图片 默认三个点
     */
    public SimpleToolBar setMenu(boolean flag) {
        if (null == menuIb) {
            return this;
        }
        if (flag) {
            menuIb.setVisibility(VISIBLE);
            menuIb.setImageResource(R.drawable.toolbar_more);
            onClick(menuIb, MENU);
        } else {
            menuIb.setVisibility(GONE);
        }
        return this;
    }

    public SimpleToolBar setMenu(@DrawableRes int resId) {
        if (null == menuIb) {
            return this;
        }
        menuIb.setVisibility(VISIBLE);
        menuIb.setImageResource(resId);
        onClick(menuIb, MENU);
        return this;
    }

    public SimpleToolBar setBackGround(@ColorInt int background) {
        if (null == linearLayout) return this;
        linearLayout.setBackgroundColor(background);
        return this;
    }

    public SimpleToolBar isShowBottomLine(boolean flag) {
        if (flag) {
            line.setVisibility(VISIBLE);
        } else {
            line.setVisibility(INVISIBLE);
        }
        return this;
    }
    public SimpleToolBar setTitleColor(@ColorInt int colorId){
        if (null == titleTv) return this;
        titleTv.setTextColor(colorId);
        return this;
    }

    /**
     * 点击事件
     *
     * @param view
     * @param type
     */
    public void onClick(View view, final int type) {
        if (view == null && null == onToolBarClickListener) {
            Log.e(TAG, "onToolBarClickListener or view is null");
            return;
        }
        view.setOnClickListener(view1 -> {
            switch (type) {
                case BACK:
                    onToolBarClickListener.onBackClick(view1);
                    break;
                case TITLE:
                    onToolBarClickListener.onTitleClick(view1);
                    break;
                case MENU:
                    onToolBarClickListener.onMenuClick(view1);
                    break;
            }
        });
    }

    public OnToolBarClickListener onToolBarClickListener;

    public void setOnToolBarClickListener(OnToolBarClickListener onToolBarClickListener) {
        this.onToolBarClickListener = onToolBarClickListener;
    }

    public interface OnToolBarClickListener {
        void onBackClick(View var1);

        void onTitleClick(View var1);

        void onMenuClick(View var1);
    }

}

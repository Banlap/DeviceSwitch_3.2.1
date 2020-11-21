package com.aliyun.iot.ilop.demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;

public class CircleView extends View {

    //画笔
    private Paint mPaint;

    //圆的半径
    private float mRadius = 5f;
    private int color;


    public CircleView(Context context) {
        super(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.circle_view_style);
        mRadius = typedArray.getDimension(R.styleable.circle_view_style_radius, 0);
        color = typedArray.getColor(R.styleable.circle_view_style_color, Color.RED);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setColor(String color) {
        if (TextUtils.isEmpty(color)) return;

        mPaint.setColor(Color.parseColor("#" + color));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
    }

}
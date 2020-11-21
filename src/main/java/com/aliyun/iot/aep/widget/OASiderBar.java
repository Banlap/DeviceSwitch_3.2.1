package com.aliyun.iot.aep.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.alibaba.sdk.android.openaccount.ui.widget.SiderBar;

/**
 * Created by feijie.xfj on 18/4/11.
 */

public class OASiderBar extends SiderBar {
    private float mInterval;
    private int mTextColor = android.R.color.black;

    public OASiderBar(Context context) {
        this(context, null);
    }

    public OASiderBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OASiderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        b = new String[]{"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        paint.setAntiAlias(true);
        setTextSize(12);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);

        if (mode != MeasureSpec.EXACTLY) {
            size = (int) ((paint.descent() - paint.ascent() + mInterval) * b.length);
        }

        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), size);
    }


    @Override
    protected void setTextSize(float size) {
        super.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics()));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int height = this.getHeight();
        int width = this.getWidth();
        float letterHeight = (float) this.getViewHeight() * 1.0F / (float) b.length;

        for (int i = 0; i < b.length; ++i) {
            float yPos = letterHeight * (float) i + letterHeight;
            if (yPos > (float) height) {
                break;
            }

            float xPos = (float) (width / 2);
            paint.setColor(getResources().getColor(mTextColor));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(dip2px(getContext(), 11));
            if (i == this.choose) {
                this.setColor(android.R.color.white);
                this.setFakeBoldText(true);
            }
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(b[i], xPos, yPos, this.paint);
            this.paint.reset();
        }

    }


    protected int getViewHeight() {
        if (this.viewHeight == 0) {
            this.viewHeight = this.getHeight();
        }

        return this.viewHeight;
    }

    /**
     * @param interval dp
     */
    public void setInterval(float interval) {
        this.mInterval = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, interval, getResources().getDisplayMetrics());
        requestLayout();

    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = getScale(context);
        return (int) (dpValue * scale + 0.5f);
    }

    private static float getScale(Context context) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return findScale(fontScale);
    }

    private static float findScale(float scale) {
        if (scale <= 1) {
            scale = 1;
        } else if (scale <= 1.5) {
            scale = 1.5f;
        } else if (scale <= 2) {
            scale = 2f;
        } else if (scale <= 3) {
            scale = 3f;
        }
        return scale;
    }

}

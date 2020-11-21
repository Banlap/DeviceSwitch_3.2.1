package com.aliyun.iot.ilop.startpage.list.main.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.aliyun.iot.demo.R;


public class LineTextView extends AppCompatTextView {

    public static int sp2px(Context context, float spValue) {
        final float fontScale = getScale(context);
        return (int) (spValue * fontScale + 0.5f);
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


    public LineTextView(Context context) {
        super(context);
    }

    public LineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private TextPaint tp = new TextPaint();
    private Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        tp.setColor(this.getTextColors().getDefaultColor());
        tp.setStyle(Paint.Style.FILL);
        tp.setTextSize(sp2px(this.getContext(), 16));
        String text = getResources().getString(R.string.location_failed);
        String message = getResources().getString(R.string.location_failed) + getResources().getString(R.string.location_failed_again);
        StaticLayout againLayout = new StaticLayout(text, tp, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        StaticLayout allLayout = new StaticLayout(message, tp, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int numHeight = againLayout.getLineCount();
        int num = this.getLineCount();
        paint.setColor(this.getTextColors().getDefaultColor());
        paint.setStrokeWidth(3);
        for (int i = numHeight - 1; i < num; i++) {
            float startX = 0;
            if (i == (numHeight - 1)) {
                startX = againLayout.getLineWidth(numHeight - 1);
            }
            canvas.drawLine(startX, this.getLineHeight() * (i + 1), allLayout.getLineWidth(i), this.getLineHeight() * (i + 1), paint);
        }
    }

}

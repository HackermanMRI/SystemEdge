package com.example.systemedge;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.annotation.Nullable;


public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF oval;
    private float progress = 0;

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFF222222); // blackish shadow
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(25);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(0xFFFFFFFF); // white
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(25);
        progressPaint.setAntiAlias(true);

        oval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float padding = 25;
        float size = Math.min(getWidth(), getHeight()) - padding;

        oval.set(padding, padding, size, size);

        // Draw background circle (full 360 degrees)
        canvas.drawArc(oval, 90, 360, false, backgroundPaint);

        // Draw progress arc starting from 90 deg (bottom), sweeping clockwise
        canvas.drawArc(oval, 90, (360 * progress / 100), false, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate(); // redraw the view
    }


}

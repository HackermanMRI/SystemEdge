package com.example.systemedge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MultiTouchView extends View {

    private static final int[] COLORS = {
            0xFF3F51B5, 0xFFF44336, 0xFF4CAF50, 0xFFFFC107, 0xFFFF9800,
            0xFFE91E63, 0xFF9C27B0, 0xFF00BCD4, 0xFF795548, 0xFF607D8B
    };

    private final SparseArray<PointF> activePointers;
    private final Paint textPaint;
    private final Paint touchPaint;
    private int maxPointers = 0;

    public MultiTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        activePointers = new SparseArray<>();

        touchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        touchPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(150);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a dark background
        canvas.drawColor(0xFF212121);

        // Draw a circle under each active pointer
        for (int i = 0; i < activePointers.size(); i++) {
            PointF point = activePointers.valueAt(i);
            if (point != null) {
                // Assign a unique color to each pointer
                touchPaint.setColor(COLORS[i % COLORS.length]);
                canvas.drawCircle(point.x, point.y, 100, touchPaint);
            }
        }

        // Draw the text showing the maximum number of pointers detected
        String text = String.valueOf(maxPointers);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
        canvas.drawText(text, xPos, yPos, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer index from the event
        int pointerIndex = event.getActionIndex();
        // Get the pointer ID
        int pointerId = event.getPointerId(pointerIndex);
        // Use getActionMasked to handle multi-touch actions
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // A new finger touches the screen
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                activePointers.put(pointerId, f);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // A finger moves
                for (int i = 0; i < event.getPointerCount(); i++) {
                    PointF point = activePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                // A finger is lifted
                activePointers.remove(pointerId);
                break;
            }
        }

        // Update the maximum pointer count if the current count is higher
        if (activePointers.size() > maxPointers) {
            maxPointers = activePointers.size();
        }

        // Invalidate the view to force a redraw
        invalidate();
        return true;
    }

    /**
     * A public method to reset the counter, called from the activity's reset button.
     */
    public void reset() {
        maxPointers = 0;
        invalidate();
    }
}
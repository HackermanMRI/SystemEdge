package com.example.systemedge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class TouchGridView extends View {

    private static final int NUM_COLUMNS = 20;
    private static final int NUM_ROWS = 30;

    private boolean[][] touchedCells;
    private Paint gridPaint;
    private Paint touchPaint;
    private float cellWidth;
    private float cellHeight;

    public TouchGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Initializes the Paint objects used for drawing.
     */
    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);

        touchPaint = new Paint();
        touchPaint.setColor(0xFF4CAF50); // A nice green color
        touchPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * This is called once the view's size is known. We set up our grid dimensions here.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellWidth = (float) w / NUM_COLUMNS;
        cellHeight = (float) h / NUM_ROWS;
        touchedCells = new boolean[NUM_ROWS][NUM_COLUMNS];
    }

    /**
     * This method handles all the drawing. It draws the grid and fills in touched cells.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (touchedCells == null) return;

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                float left = col * cellWidth;
                float top = row * cellHeight;
                float right = left + cellWidth;
                float bottom = top + cellHeight;

                if (touchedCells[row][col]) {
                    // If the cell has been touched, fill it with green
                    canvas.drawRect(left, top, right, bottom, touchPaint);
                }
                // Draw the gray grid outline for every cell
                canvas.drawRect(left, top, right, bottom, gridPaint);
            }
        }
    }

    /**
     * This method is called whenever the user touches the screen.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // We only care about when the finger is first pressed down or dragged
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();

            // Convert the touch coordinates (in pixels) to grid cell indices
            int col = (int) (x / cellWidth);
            int row = (int) (y / cellHeight);

            // Check bounds and mark the cell as touched
            if (row >= 0 && row < NUM_ROWS && col >= 0 && col < NUM_COLUMNS) {
                if (!touchedCells[row][col]) {
                    touchedCells[row][col] = true;
                    invalidate(); // Tell the view to redraw itself
                }
            }
            return true; // We handled the touch event
        }
        return super.onTouchEvent(event);
    }

    /**
     * A public method to reset the grid, which we'll call from a button in the activity.
     */
    public void reset() {
        touchedCells = new boolean[NUM_ROWS][NUM_COLUMNS];
        invalidate(); // Redraw the cleared grid
    }
}
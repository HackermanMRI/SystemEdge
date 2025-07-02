package com.example.systemedge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class RamLineGraphView extends View {
    private Paint linePaint, textPaint, fillPaint;
    private List<Integer> ramValues = new ArrayList<>(Arrays.asList(100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100)); // Sample data
    private int maxRam = 1000;
    private static final float LEFT_PADDING = 80f;

    // Required constructors
    public RamLineGraphView(Context context) {
        super(context);
        init();
    }

    public RamLineGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RamLineGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Line paint (orange like your screenshot)
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(4f);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStyle(Paint.Style.STROKE);

        // Text paint for Y-axis labels
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16f);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();
        float graphHeight = height * 0.8f; // 80% of view height

        // Draw Y-axis labels (3600, 3400, 3200)
        float yInterval = maxRam / 3f; //  steps
        for (int i = 0; i <= maxRam; i += yInterval) {
            float yPos = height - (i * graphHeight / maxRam);
            if (i== 0){
                canvas.drawText(" ", 5, yPos, textPaint);
                continue;
            }
            canvas.drawText(i + " MB", 5, yPos, textPaint);

        }

        // Draw line graph
        float xStep = (width - LEFT_PADDING) / (ramValues.size() - 1);
        Path path = new Path();
        path.moveTo(LEFT_PADDING, height - (ramValues.get(0) * graphHeight / maxRam));

        for (int i = 1; i < ramValues.size(); i++) {
            path.lineTo(LEFT_PADDING + i * xStep, height - (ramValues.get(i) * graphHeight / maxRam));
        }


        //fill paint for the graph area
        fillPaint = new Paint();
        fillPaint.setShader(new LinearGradient(
                0, 0, 0, getHeight(),
                Color.parseColor("#FF5722"), Color.TRANSPARENT,
                Shader.TileMode.CLAMP
        ));
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAlpha(100); // Transparency

        // Fill the area under the graph
        Path fillPath = new Path();
        fillPath.moveTo(LEFT_PADDING, height);
        fillPath.lineTo(LEFT_PADDING, height - (ramValues.get(0) * graphHeight / maxRam));

        for (int i = 1; i < ramValues.size(); i++) {
            float x = LEFT_PADDING + i * xStep;
            float y = height - (ramValues.get(i) * graphHeight / maxRam);
            fillPath.lineTo(x, y);
        }

        fillPath.lineTo(LEFT_PADDING + (ramValues.size() - 1) * xStep, height);
        fillPath.close();

        // Draw the filled area and the line
        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(path, linePaint);
    }


    // Update data dynamically
    public void updateRamData(long newValues) {
        if(ramValues.size() >= 80){
            ramValues.remove(0);
        }
        this.ramValues.add((int) newValues); // Add new value
        this.maxRam = Collections.max(ramValues) + 500; // Auto-adjust scale
        invalidate(); // Redraw
    }
}

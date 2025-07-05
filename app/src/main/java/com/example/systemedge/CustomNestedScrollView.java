package com.example.systemedge; // Make sure this package name is correct for your project

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * A definitive, state-aware NestedScrollView that correctly arbitrates touch
 * events with a parent ViewPager2. It uses the system touch slop and an
 * explicit state flag to robustly determine the user's intent.
 */
public class CustomNestedScrollView extends NestedScrollView {

    private int touchSlop;
    private float initialX;
    private float initialY;
    private boolean isDragging = false;

    public CustomNestedScrollView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Record the initial touch point and reset the dragging state.
                initialX = ev.getX();
                initialY = ev.getY();
                isDragging = false;
                // Let the superclass start handling the event.
                super.onInterceptTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                // If we are already dragging vertically, keep intercepting.
                if (isDragging) {
                    return true;
                }

                float deltaX = Math.abs(ev.getX() - initialX);
                float deltaY = Math.abs(ev.getY() - initialY);

                // Check if the movement is primarily vertical AND has exceeded the touch slop.
                if (deltaY > touchSlop && deltaY * 0.5f > deltaX) {
                    // This is a clear vertical scroll. Set the flag and tell the parent to let go.
                    isDragging = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true; // We are now intercepting the touch stream.
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // The gesture is over. Reset the dragging state.
                isDragging = false;
                // Allow the parent to intercept again for future gestures.
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // For all other cases, let the default behavior decide.
        return super.onInterceptTouchEvent(ev);
    }
}
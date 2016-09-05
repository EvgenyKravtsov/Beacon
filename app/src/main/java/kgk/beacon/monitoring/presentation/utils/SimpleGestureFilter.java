package kgk.beacon.monitoring.presentation.utils;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SimpleGestureFilter extends GestureDetector.SimpleOnGestureListener {

    public interface SimpleGestureListener {

        void onSwipe(int direction);

        void onDoubleTap();
    }

    public static final int SWIPE_UP = 1;
    public static final int SWIPE_DOWN = 2;
    public static final int SWIPE_LEFT = 3;
    public static final int SWIPE_RIGHT = 4;

    public static final int MODE_TRANSPARENT = 0;
    public static final int MODE_SOLID = 1;
    public static final int MODE_DYNAMIC = 2;

    private final static int ACTION_FAKE = -13;
    private int swipeMinDistance = 100;
    private int swipeMaxDistance = 350;
    private int swipeMinVelocity = 100;

    private int mode = MODE_DYNAMIC;
    private boolean running = true;
    private boolean tapIndicator = false;

    private Activity context;
    private GestureDetector gestureDetector;
    private SimpleGestureListener gestureListener;

    ////

    public SimpleGestureFilter(Activity context, SimpleGestureListener gestureListener) {
        this.context = context;
        this.gestureDetector = new GestureDetector(context, this);
        this.gestureListener = gestureListener;
    }

    ////

    public void onTouchEvent(MotionEvent event) {
        if (!running) return;

        boolean result = this.gestureDetector.onTouchEvent(event);

        if (mode == MODE_SOLID) event.setAction(MotionEvent.ACTION_CANCEL);
        else if (mode == MODE_DYNAMIC) {
            if (event.getAction() == ACTION_FAKE) event.setAction(MotionEvent.ACTION_UP);
            else if (result) event.setAction(MotionEvent.ACTION_CANCEL);
            else if (this.tapIndicator) {
                event.setAction(MotionEvent.ACTION_DOWN);
                tapIndicator = false;
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setEnabled(boolean status) {
        running = status;
    }

    public int getSwipeMaxDistance() {
        return swipeMaxDistance;
    }

    public void setSwipeMaxDistance(int distance) {
        swipeMaxDistance = distance;
    }

    public int getSwipeMinDistance() {
        return swipeMinDistance;
    }

    public void setSwipeMinDistance(int distance) {
        swipeMinDistance = distance;
    }

    public int getSwipeMinVelocity() {
        return swipeMinVelocity;
    }

    public void setSwipeMinVelocity(int velocity) {
        swipeMinVelocity = velocity;
    }

    ////


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final float xDistance = Math.abs(e1.getX() - e2.getX());
        final float yDistance = Math.abs(e1.getY() - e2.getY());

        if (xDistance > swipeMaxDistance || yDistance > swipeMaxDistance) return false;

        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);

        boolean result = false;

        if (velocityX > swipeMinDistance && xDistance > swipeMinDistance) {
            if (e1.getX() > e2.getX()) gestureListener.onSwipe(SWIPE_LEFT);
            else gestureListener.onSwipe(SWIPE_RIGHT);
            result = true;
        } else if (velocityY > swipeMinVelocity && yDistance > swipeMinDistance) {
            if (e1.getY() > e2.getY()) gestureListener.onSwipe(SWIPE_UP);
            else gestureListener.onSwipe(SWIPE_DOWN);
            result = true;
        }

        return result;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        tapIndicator = true;
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        gestureListener.onDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mode == MODE_DYNAMIC) {
            e.setAction(ACTION_FAKE);
            context.dispatchTouchEvent(e);
        }

        return false;
    }
}










































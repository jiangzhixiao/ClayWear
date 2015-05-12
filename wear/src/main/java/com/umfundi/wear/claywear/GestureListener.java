/*
 * Copyright 2015 umFundi ltd
 *
 */

package com.umfundi.wear.claywear;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

// public class GestureListener extends GestureDetector.SimpleOnGestureListener {
public class GestureListener implements GestureDetector.OnGestureListener {

    public static final String TAG = "GestureListener";

    // These overrides are not called if they are defined in the constructor in the activity
    // but they need to be defined or the compile fails.
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Up motion completing a single tap occurred.
        Log.i(TAG, "Single Tap Up");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // Touch has been long enough to indicate a long press.
        // Does not indicate motion is complete yet (no up event necessarily)
        Log.i(TAG, "Long Press");
    }

    @Override
    public boolean onFling(MotionEvent event, MotionEvent event2, float v, float v2) {
        Log.i(TAG, "Fling");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
    float distanceY) {
        // User attempted to scroll
        Log.i(TAG, "Scroll");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // User performed a down event, and hasn't moved yet.
        Log.i(TAG, "Show Press");
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // "Down" event - User touched the screen.
        Log.i(TAG, "Down in fragment ");
        return true;
    }
/*
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // User tapped the screen twice.
        Log.i(TAG, "Double tap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // Since double-tap is actually several events which are considered one aggregate
        // gesture, there's a separate callback for an individual event within the doubletap
        // occurring.  This occurs for down, up, and move.
        Log.i(TAG, "Event within double tap");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // A confirmed single-tap event has occurred.  Only called when the detector has
        // determined that the first tap stands alone, and is not part of a double tap.
        Log.i(TAG, "Single tap confirmed by ian");
        return true;
    }
*/
}

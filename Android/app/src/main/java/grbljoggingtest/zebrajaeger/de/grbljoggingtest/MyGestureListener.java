package grbljoggingtest.zebrajaeger.de.grbljoggingtest;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

import grbljoggingtest.zebrajaeger.de.grbljoggingtest.moveable.Move;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.moveable.Moveable;

enum Direction {
    FORWARD, BACKWARD, ZERO;

    static Direction ofDelta(float delta) {
        Direction result = ZERO;
        if (delta < 0) {
            result = BACKWARD;
        } else if (delta > 0) {
            result = FORWARD;
        }
        return result;
    }
}

/**
 * Created by Lars Brandt on 16.07.2017.
 */
public class MyGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, Moveable {

    public static final int TIMER_PERIOD = 25;

    private float toMove = 0;
    private boolean requireStop = false;

    private Direction lastDirection = Direction.ZERO;

    private int movementTimeoutThreshold = 25;
    private int movementTimeoutCounter = 0;

    public MyGestureListener() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    tic();
                } catch (Exception e) {
                    Log.e("MyGestureListener:Timer", "Failed", e);
                }
            }
        }, 0, TIMER_PERIOD);
    }

    private void tic() {
        if (movementTimeoutCounter > movementTimeoutThreshold) {
            reset();
        }
    }

    private void resetTimeout() {
        movementTimeoutCounter = 0;
    }

    private void reset() {
        toMove = 0;
        requireStop = true;
        lastDirection = Direction.ZERO;
    }

    public Move pickMove() {
        Move result = new Move(toMove, requireStop);
        toMove = 0;
        requireStop = false;
        return result;
    }

    public void processX(float deltaX, int pointerCount) {
        resetTimeout();
        Direction dirX = Direction.ofDelta(deltaX);

        if (pointerCount == 1) {
            // snake speed?
            deltaX /= 200f;
        } else if (pointerCount == 2) {
            // snake speed?
            deltaX /= 25f;
        } else{
            deltaX /= 5f;
        }

        if (dirX == Direction.FORWARD) {
            if (lastDirection == Direction.BACKWARD) {
                requireStop = true;
                toMove = deltaX;
            } else {
                toMove += deltaX;
            }
        }

        if (dirX == Direction.BACKWARD) {
            if (lastDirection == Direction.FORWARD) {
                requireStop = true;
                toMove = deltaX;
            } else {
                toMove += deltaX;
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent firstEvent, MotionEvent currentEvent, float deltaX, float deltaY) {
        if (deltaX == 0f) {
            toMove = 0;
            requireStop = true;
            lastDirection = Direction.ZERO;
        } else {
            processX(deltaX, currentEvent.getPointerCount());
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

}

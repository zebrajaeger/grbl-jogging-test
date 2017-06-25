package de.zebrajaeger.grbl.jogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

class MyListener implements MouseMotionListener, MouseListener {

    private static final Logger LOG = LoggerFactory.getLogger(MyListener.class);

    enum Direction {
        FORWARD, BACKWARD, ZERO;

        static Direction ofDelta(int delta) {
            Direction result = ZERO;
            if (delta < 0) {
                result = BACKWARD;
            } else if (delta > 0) {
                result = FORWARD;
            }
            return result;
        }
    }

    private Timer timer = new Timer("movement timeout timer ");
    private int toMove = 0;
    private boolean requireStop = false;

    private Point lastPos = null;
    private int lastNonZeroDiffX = 0;

    private int movementTimeoutThreshold = 25;
    private int movementTimeoutCounter = 0;

    public MyListener() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (movementTimeoutCounter > movementTimeoutThreshold) {
                    reset();
                }
            }
        }, 0, 10);
    }

    private void resetTimeout() {
        movementTimeoutCounter = 0;
    }

    private void reset() {
        toMove = 0;
        requireStop = true;
        lastPos = null;
        lastNonZeroDiffX = 0;
    }

    public Move pickMove() {
        Move result = new Move(toMove, requireStop);
        toMove = 0;
        requireStop = false;
        return result;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        resetTimeout();
        if (lastPos == null) {
            lastPos = e.getPoint();
            requireStop = false;

        } else {
            int dX = (int) (lastPos.getX() - e.getX());
            Direction dirX = Direction.ofDelta(dX);

            Direction lastNonZeroDirX = Direction.ofDelta(lastNonZeroDiffX);

            //LOG.warn("{} -> {}", lastNonZeroDirX, dirX);
            if (dirX == Direction.FORWARD) {
                if (lastNonZeroDirX == Direction.BACKWARD) {
                    requireStop = true;
                    toMove = dX;
                } else {
                    toMove += dX;
                }
            }

            if (dirX == Direction.BACKWARD) {
                if (lastNonZeroDirX == Direction.FORWARD) {
                    requireStop = true;
                    toMove = dX;
                } else {
                    toMove += dX;
                }
            }

            if (dX != 0) {
                lastNonZeroDiffX = dX;
            }
            lastPos = e.getPoint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        reset();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        reset();
    }
}

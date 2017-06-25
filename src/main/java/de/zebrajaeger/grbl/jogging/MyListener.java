package de.zebrajaeger.grbl.jogging;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

class MyListener implements MouseMotionListener, MouseListener {
    enum Direction {
        FORWARD, BACKWARD, ZERO;

        static Direction ofDelta(int delta) {
            Direction result = ZERO;
            if (delta < 0) {
                result = BACKWARD;
            } else if (delta > 0) {
                result = BACKWARD;
            }
            return result;
        }
    }

    private int toMove = 0;
    private boolean requireStop = false;

    private Point lastPos = null;
    private int lastDiffX = 0;

    private void reset() {
        toMove = 0;
        requireStop = true;
        lastPos = null;
        lastDiffX = 0;
    }

    public Move pickMove() {
        Move result = new Move(toMove, requireStop);
        toMove = 0;
        requireStop = false;
        return result;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastPos == null) {
            lastDiffX = 0;
            lastPos = e.getPoint();
            requireStop = false;

        } else {
            int dX = (int) (lastPos.getX() - e.getX());
            Direction dir = Direction.ofDelta(dX);
            Direction lastDir = Direction.ofDelta(lastDiffX);

            if (dir == Direction.FORWARD) {
                if (lastDir == Direction.BACKWARD) {
                    requireStop = true;
                    toMove = dX;
                } else {
                    toMove += dX;
                }
            }

            if (dir == Direction.BACKWARD) {
                if (lastDir == Direction.FORWARD) {
                    requireStop = true;
                    toMove = dX;
                } else {
                    toMove += dX;
                }
            }

//            if (dir == Direction.ZERO) {
//                requireStop = true;
//                toMove = 0;
//            }

            lastDiffX = dX;
            lastPos = e.getPoint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //LOG.info("MOVE" + System.currentTimeMillis());
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

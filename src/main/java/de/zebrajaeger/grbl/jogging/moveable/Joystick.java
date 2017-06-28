package de.zebrajaeger.grbl.jogging.moveable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Joystick extends JPanel implements MouseMotionListener, MouseListener, Moveable {

    private Container container;
    private Point currentPos = null;
    private float speedX = 0;

    public Joystick(Container container) {
        this.container = container;
        container.add(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Move pickMove() {
        return new Move(speedX, speedX==0);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;

        Insets insets = getInsets();
        Rectangle bounds = getBounds();

        int x1 = insets.left;
        int x2 = bounds.width - insets.left - insets.right;
        int xw = x2 - x1;
        int xc = (x1 + x2) / 2;

        int y1 = insets.top;
        int y2 = bounds.height - insets.top - insets.bottom;
        int yw = y2 - y1;
        int yc = (y1 + y2) / 2;

        int minW = Math.min(xw, yw);

        Point pos;
        if (currentPos == null) {
            pos = new Point(xc, yc);
        } else {
            pos = currentPos;
        }

        g.drawLine(xc, yc, pos.x, pos.y);
        g.setColor(Color.red.darker());
        int cw = minW / 10;
        g.fillOval(pos.x - (cw / 2), pos.y - (cw / 2), minW / 10, minW / 10);
    }

    private void setX(int x) {
        if(x==0){
            speedX = 0;
        }else {
            Insets insets = getInsets();
            Rectangle bounds = getBounds();

            int x1 = insets.left;
            int x2 = bounds.width - insets.left - insets.right;
            int xw = x2 - x1;
            int xc = (x1 + x2) / 2;
            float dx = xc - x;
            dx = dx * 10f / (float) xw;
            speedX = dx;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentPos = e.getPoint();
        setX(e.getX());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentPos = null;
        setX(0);
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        currentPos = null;
        setX(0);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentPos = e.getPoint();
        setX(e.getX());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

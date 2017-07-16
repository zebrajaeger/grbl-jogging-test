package de.zebrajaeger.grbl.jogging.moveable;

public class Move {
    private float deltaX;
    private boolean requireStopX;

    public Move(float deltaX, boolean requireStopX) {
        this.deltaX = deltaX;
        this.requireStopX = requireStopX;
    }

    public boolean actionRequired() {
        return deltaX != 0f || requireStopX;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public boolean isRequireStopX() {
        return requireStopX;
    }


    @Override
    public String toString() {
        return "Move{" +
                "deltaX=" + deltaX +
                ", requireStopX=" + requireStopX +
                '}';
    }
}

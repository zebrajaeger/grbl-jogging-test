package de.zebrajaeger.grbl.jogging;

class Move {
    private int deltaX;
    private boolean requireStopX;

    public Move(int deltaX, boolean requireStopX) {
        this.deltaX = deltaX;
        this.requireStopX = requireStopX;
    }

    public boolean actionRequired() {
        return deltaX != 0 || requireStopX;
    }

    public int getDeltaX() {
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

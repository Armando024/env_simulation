package earth;

class Air implements Mappable {
    private final int x;
    private final int y;

    Air(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final int getY() {
        return y;
    }

    @Override
    public final char toChar() {
        return '.';
    }

    @Override
    public final String toString() {
        return String.valueOf(toChar());
    }
}
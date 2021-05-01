package game;

public class Coords {
    int y;
    int x;

    public Coords(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coords) {
            Coords c = (Coords) obj;
            return c.x == x && c.y == y;
        }

        return false;
    }
}

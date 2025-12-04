package pt.feup.tvvs.soulknight.model.dataStructs;

public class Position {

    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public Position getLeft() {
        return new Position(x - 1, y);
    }
    public Position getRight() {
        return new Position(x + 1, y);
    }
    public Position getUp() {
        return new Position(x, y - 1);
    }
    public Position getDown() {
        return new Position(x, y + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

}
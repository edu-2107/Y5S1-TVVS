package pt.feup.tvvs.soulknight.model.dataStructs;

public class Vector {

    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (! (obj instanceof Vector)) return false;
        Vector v = (Vector) obj;
        return v.x == this.x && v.y == this.y;
    }
}

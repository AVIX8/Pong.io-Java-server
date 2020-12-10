package pongPackage;

public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector subtr(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector mult(double n) {
        return new Vector(x * n, y * n);
    }

    public Vector normal() {
        return new Vector(-y, x).unit();
    }

    public Vector unit() {
        double m = mag();
        if (m == 0) {
            return new Vector(0, 0);
        }
        return new Vector(x / m, y / m);
    }

    public static double scalar(Vector v1, Vector v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }
}

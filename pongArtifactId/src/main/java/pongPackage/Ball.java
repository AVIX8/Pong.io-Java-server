package pongPackage;

public class Ball {
    public Vector pos = new Vector(0, 0);
    public Vector vel = new Vector(0, 0);
    public Player owner;
    public double r;

    public Ball(double x, double y, double r) {
        this.pos = new Vector(x, y);
        this.r = r;
    }

    public void reposition() {
        this.pos = this.pos.add(this.vel);
    }
}

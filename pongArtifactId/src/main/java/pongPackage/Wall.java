package pongPackage;

public class Wall {
    public Vector start;
    public Vector end;
    public Vector pos;
    public Vector vel;
    public double size;

    public Wall(double x1, double y1, double x2, double y2) {
        this.start = new Vector(x1, y1);
        this.end = new Vector(x2, y2);
        this.pos = new Vector((x1 + x2) / 2, (y1 + y2) / 2);
        this.size = this.mag() / 2;
        this.vel = new Vector(0, 0);
    }

    public void reposition() {
        this.pos = this.pos.add(this.vel);
        this.start = this.pos.add(this.unit().mult(-this.size));
        this.end = this.pos.add(this.unit().mult(this.size));
    }

    public double mag() {
        return this.end.subtr(this.start).mag();
    }

    public Vector unit() {
        return this.end.subtr(this.start).unit();
    }
}

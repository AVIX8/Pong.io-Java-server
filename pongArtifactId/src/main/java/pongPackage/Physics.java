package pongPackage;

public class Physics {
    
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }
  
    public static Vector closestPointBW(Ball b, Wall w) {
        Vector wallEndToBall = b.pos.subtr(w.end);
        if (Vector.scalar(w.unit(), wallEndToBall) > 0) {
            return w.end;
        }
        Vector ballToWallStart = w.start.subtr(b.pos);
        double closestDist = Vector.scalar(w.unit(), ballToWallStart);
        if (closestDist > 0) {
            return w.start;
        }
        Vector closestVect = w.unit().mult(closestDist);
        return w.start.subtr(closestVect);
    }
  
    public static boolean collDetBB(Ball b1, Ball b2) {
        if (b1.r + b2.r >= b1.pos.subtr(b2.pos).mag()) {
            return true;
        }
        return false;
    }
  
    public static boolean collDetBW(Ball b, Wall w) {
        Vector ballToClosest = closestPointBW(b, w).subtr(b.pos);
        if (ballToClosest.mag() < b.r) {
            return true;
        }
        return false;
    }
  
    public static void penResBB(Ball b1, Ball b2) {
        Vector distVec = b1.pos.subtr(b2.pos);
        double penDepth = b1.r + b2.r - distVec.mag();
        Vector penRes = distVec.unit().mult(penDepth / 2);
        b1.pos = b1.pos.add(penRes);
        b2.pos = b2.pos.add(penRes.mult(-1));
    }
  
    public static void penResBW(Ball b, Wall w) {
        Vector penVect = b.pos.subtr(closestPointBW(b, w));
        b.pos = b.pos.add(penVect.unit().mult(b.r - penVect.mag()));
    }
  
    public static void collResBB(Ball b1, Ball b2) {
        Vector normal = b1.pos.subtr(b2.pos).unit();
        Vector relVel = b1.vel.subtr(b2.vel);
        double sepVel = Vector.scalar(relVel, normal);
        double newSepVel = -sepVel;
        Vector sepVelVec = normal.mult(newSepVel);
    
        b1.vel = b1.vel.add(sepVelVec);
        b2.vel = b2.vel.add(sepVelVec.mult(-1));
    }
  
    public static void collResBW(Ball b, Wall w) {
        Vector normal = b.pos.subtr(closestPointBW(b, w)).unit();
        double sepVel = Vector.scalar(b.vel, normal);
        double newSepVel = -sepVel;
        double vsepDiff = sepVel - newSepVel;
        b.vel = b.vel.add(normal.mult(-vsepDiff));
    }
}

package pcd.ass01.thread_version.model.holes;

import pcd.ass01.thread_version.model.util.P2d;

public class Hole {
    private final P2d position;
    private final double radius;
    private static final double STANDARD_RADIUS = 0.25;

    public Hole(P2d pos) {
        this.position = pos;
        this.radius = STANDARD_RADIUS;
    }

    public Hole(P2d pos, double radius) {
        this.position = pos;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public P2d getPosition() {
        return position;
    }

    public boolean overlaps(P2d pos, double radius) {
        double dx = this.position.x() - pos.x();
        double dy = this.position.y() - pos.y();
        double dist = Math.hypot(dx, dy);
        return dist < (this.radius - radius * 0.5);
    }
}

package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.board.Boundary;
import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;
import pcd.ass01.thread_version.model.board.Board;

public abstract class AbstractBall implements Ball {

    private P2d pos;
    private V2d vel;
    private final double radius;
    private final double mass;

    private static final double FRICTION_FACTOR     = 0.25;
    private static final double RESTITUTION_FACTOR  = 1;

    protected AbstractBall(P2d pos, double radius, double mass, V2d vel) {
        this.pos    = pos;
        this.radius = radius;
        this.mass   = mass;
        this.vel    = vel;
    }

    @Override
    public void updateState(long dt, Board ctx) {
        double speed     = vel.abs();
        double dt_scaled = dt * 0.001;
        if (speed > 0.001) {
            double dec    = FRICTION_FACTOR * dt_scaled;
            double factor = Math.max(0, speed - dec) / speed;
            vel = vel.mul(factor);
        } else {
            vel = new V2d(0, 0);
        }
        pos = pos.sum(vel.mul(dt_scaled));
        applyBoundaryConstraints(ctx);
    }

    @Override
    public void kick(V2d vel) {
        this.vel = vel;
    }

    @Override
    public P2d getPos() {
        return pos;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public V2d getVel() {
        return vel;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    private void applyBoundaryConstraints(Board ctx) {
        Boundary bounds = ctx.getBounds();
        if (pos.x() + radius > bounds.x1()) {
            pos = new P2d(bounds.x1() - radius, pos.y());
            vel = vel.getSwappedX();
        } else if (pos.x() - radius < bounds.x0()) {
            pos = new P2d(bounds.x0() + radius, pos.y());
            vel = vel.getSwappedX();
        } else if (pos.y() + radius > bounds.y1()) {
            pos = new P2d(pos.x(), bounds.y1() - radius);
            vel = vel.getSwappedY();
        } else if (pos.y() - radius < bounds.y0()) {
            pos = new P2d(pos.x(), bounds.y0() + radius);
            vel = vel.getSwappedY();
        }
    }

    public void resolveCollision(AbstractBall a, AbstractBall b) {
        double dx   = b.pos.x() - a.pos.x();
        double dy   = b.pos.y() - a.pos.y();
        double dist = Math.hypot(dx, dy);
        double minD = a.radius + b.radius;

        if (dist < minD && dist > 1e-6) {
            double nx = dx / dist;
            double ny = dy / dist;

            double overlap = minD - dist;
            double totalM  = a.mass + b.mass;

            double a_factor = overlap * (b.mass / totalM);
            a.pos = new P2d(a.pos.x() - nx * a_factor, a.pos.y() - ny * a_factor);

            double b_factor = overlap * (a.mass / totalM);
            b.pos = new P2d(b.pos.x() + nx * b_factor, b.pos.y() + ny * b_factor);

            double dvx = b.vel.x() - a.vel.x();
            double dvy = b.vel.y() - a.vel.y();
            double dvn = dvx * nx + dvy * ny;

            if (dvn <= 0) {
                double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0 / a.mass + 1.0 / b.mass);
                a.vel = new V2d(a.vel.x() - (imp / a.mass) * nx, a.vel.y() - (imp / a.mass) * ny);
                b.vel = new V2d(b.vel.x() + (imp / b.mass) * nx, b.vel.y() + (imp / b.mass) * ny);
            }
        }
    }
}
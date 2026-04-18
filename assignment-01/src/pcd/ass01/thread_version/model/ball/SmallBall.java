package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

public class SmallBall extends AbstractBall{
    private boolean lastTouchedPlayer;
    private boolean lastTouchedBot;

    protected SmallBall(P2d pos, double radius, double mass, V2d vel) {
        super(pos, radius, mass, vel);
        this.lastTouchedBot = false;
        this.lastTouchedPlayer = false;
    }


}

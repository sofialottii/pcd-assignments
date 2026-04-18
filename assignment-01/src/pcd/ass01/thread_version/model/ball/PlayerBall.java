package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

public class PlayerBall extends AbstractBall{

    private static final double PLAYER_RADIUS = 20.0;
    private static final double PLAYER_MASS = 5.0;

    public PlayerBall(P2d startPos) {
        super(startPos, PLAYER_RADIUS, PLAYER_MASS, new V2d(0, 0));
    }

    @Override
    public void kick(V2d adding) {
        super.kick(this.getVel().sum(adding));
    }

}

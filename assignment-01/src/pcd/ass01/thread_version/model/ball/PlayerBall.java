package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

public class PlayerBall extends AbstractBall{

    private static final double PLAYER_RADIUS = 0.05;
    private static final double PLAYER_MASS = 5.0;
    private static final double MAX_SPEED = 15.0;

    public PlayerBall(P2d startPos) {
        super(startPos, PLAYER_RADIUS, PLAYER_MASS, new V2d(0, 0));
    }

    @Override
    public void kick(V2d adding) {
        V2d newVel = this.getVel().sum(adding);

        if (newVel.abs() > MAX_SPEED) {
            newVel = newVel.mul(MAX_SPEED / newVel.abs());
        }

        super.kick(newVel);
    }

}

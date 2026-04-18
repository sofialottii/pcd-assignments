package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

public class BotBall extends AbstractBall{

    private static final double BOT_RADIUS = 20.0;
    private static final double BOT_MASS = 5.0;

    public BotBall(P2d startPos) {
        super(startPos, BOT_RADIUS, BOT_MASS, new V2d(0, 0));
    }

    @Override
    public void kick(V2d adding) {
        super.kick(this.getVel().sum(adding));
    }

}

package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

public class SmallBall extends AbstractBall{
    private boolean lastTouchedPlayer;
    private boolean lastTouchedBot;

    public SmallBall(P2d pos, double radius, double mass, V2d vel) {
        super(pos, radius, mass, vel);
        this.lastTouchedBot = false;
        this.lastTouchedPlayer = false;
    }
    public boolean isLastTouchedPlayer(){
        return lastTouchedPlayer;
    }

    public boolean isLastTouchedBot() {
        return lastTouchedBot;
    }
    private void playerTouch(){
        lastTouchedPlayer = true;
        lastTouchedBot = false;
    }
    private void botTouch(){
        lastTouchedPlayer = false;
        lastTouchedBot = true;
    }
    private void smallBallTouch(){
        lastTouchedPlayer = false;
        lastTouchedBot = false;
    }

    @Override
    protected void onCollisionWith(AbstractBall b) {
        if(b instanceof PlayerBall)
            playerTouch();
        if (b instanceof BotBall)
            botTouch();
        if (b instanceof SmallBall)
            smallBallTouch();
    }
}

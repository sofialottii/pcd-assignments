package pcd.ass01.task_version.model.tasks;

import pcd.ass01.task_version.model.ball.AbstractBall;
import pcd.ass01.task_version.model.ball.BotBall;
import pcd.ass01.task_version.model.ball.PlayerBall;
import pcd.ass01.task_version.model.ball.SmallBall;

import java.util.List;

public class CollisionTask implements Runnable {

    private final int firstIndex;
    private final int lastIndex;
    private final List<SmallBall> balls;
    private final BotBall botBall;
    private final PlayerBall playerBall;

    public CollisionTask(int firstIndex, int lastIndex, List<SmallBall> balls, BotBall botBall, PlayerBall playerBall) {
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.balls = balls;
        this.botBall = botBall;
        this.playerBall = playerBall;
    }


    @Override
    public void run() {

        for (int i = this.firstIndex; i <= this.lastIndex; i++) {
            SmallBall b1 = this.balls.get(i);
            if(!b1.isInHole()) {
                //resolve collision
                for (int j = i + 1; j < this.balls.size(); j++) {

                    SmallBall b2 = this.balls.get(j);
                    if(!b2.isInHole()) {
                        synchronized (b1) {
                            synchronized (b2) {
                                AbstractBall.resolveCollision(b1, b2);
                            }
                        }
                    }
                }

                //collision with player and bot ball
                synchronized (b1) {
                    synchronized (playerBall) {
                        AbstractBall.resolveCollision(b1, playerBall);
                    }
                }

                synchronized (b1) {
                    synchronized (botBall) {
                        AbstractBall.resolveCollision(b1, botBall);
                    }
                }
            }
        }
    }
}

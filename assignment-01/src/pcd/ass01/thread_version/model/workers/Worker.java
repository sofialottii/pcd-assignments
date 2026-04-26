package pcd.ass01.thread_version.model.workers;

import pcd.ass01.thread_version.model.ball.AbstractBall;
import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.util.Barrier;

import java.util.List;

public class Worker extends Thread{

    private final int firstIndex;
    private final int lastIndex;
    private final Barrier barrier;
    private final List<SmallBall> balls;
    private final BotBall botBall;
    private final PlayerBall playerBall;



    public Worker(int firstIndex, int lastIndex, Barrier barrier, List<SmallBall> balls,
                  BotBall botBall, PlayerBall playerBall) {
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.barrier = barrier;
        this.balls = balls;
        this.botBall = botBall;
        this.playerBall = playerBall;
    }

    @Override
    public void run() {

        for (int i = this.firstIndex; i < this.lastIndex; i++) {


            //resolve collision
            for (int j = i + 1; j < this.balls.size(); j++) {

                AbstractBall.resolveCollision(this.balls.get(j), this.balls.get(i));
            }

            //collision with player and bot ball

            AbstractBall.resolveCollision(this.balls.get(i), playerBall);
            AbstractBall.resolveCollision(this.balls.get(i), botBall);

        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}

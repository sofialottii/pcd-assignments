package pcd.ass01.thread_version.testJpf;

import java.util.ArrayList;
import java.util.List;

import pcd.ass01.thread_version.model.util.V2d;
import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.util.Barrier;
import pcd.ass01.thread_version.model.workers.Worker;

public class TestCollision {
    public static void main(String[] args) {
        double r = 0.05;

        List<SmallBall> balls = new ArrayList<SmallBall>();

        // b1 è al centro
        var b1 = new SmallBall(new P2d(0.5, 0.5), r, 0.75, new V2d(0.1, 0));
        var b2 = new SmallBall(new P2d(0.6, 0.5), r, 0.75, new V2d(-0.1, 0));
        var b3 = new SmallBall(new P2d(0.5, 0.6), r, 0.75, new V2d(0, -0.1));
        var b4 = new SmallBall(new P2d(0.6, 0.6), r, 0.75, new V2d(-0.05, -0.05));

        balls.add(b1);
        balls.add(b2);
        balls.add(b3);
        balls.add(b4);

        BotBall botBall = new BotBall(new P2d(0,0));
        PlayerBall playerBall = new PlayerBall(new P2d(1,1));

        Barrier barrier = new Barrier(2);
        Worker worker1 = new Worker(0, 1, barrier, balls, botBall, playerBall);
        Worker worker2 = new Worker(2, 3, barrier, balls, botBall, playerBall);

        worker1.start();
        worker2.start();

        try {
            worker1.join();
            worker2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
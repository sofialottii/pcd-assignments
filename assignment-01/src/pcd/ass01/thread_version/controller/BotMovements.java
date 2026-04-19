package pcd.ass01.thread_version.controller;

import pcd.ass01.thread_version.model.board.Board;
import pcd.ass01.thread_version.model.util.V2d;


import java.util.Random;

public class BotMovements extends Thread {

    private final ActiveController controller;
    private volatile boolean running = true;
    private final Random random = new Random();
    private final Board board;

    public BotMovements(ActiveController controller, Board board) {
        this.controller = controller;
        this.board = board;
    }

    @Override
    public void run() {

        while(running) {

            try {
                Thread.sleep(500 + random.nextInt(1000));
            } catch (InterruptedException e) {
                running = false;
            }

            if (board.getBotBall().getVel().abs() < 0.05) {
                var angle = random.nextDouble()*Math.PI*0.25;
                var v = new V2d(Math.cos(angle),Math.sin(angle)).mul(1.5);
                controller.notifyNewCmd(b -> b.getBotBall().kick(v));
            }

        }
    }

}

package pcd.ass01.thread_version.view;

import pcd.ass01.thread_version.model.board.Board;
import java.util.ArrayList;
import java.util.List;

public class ViewModel {

    private final List<BallViewInfo> balls;
    private BallViewInfo playerBall;
    private BallViewInfo botBall;
    private int framePerSec;

    public ViewModel() {
        this.balls = new ArrayList<>();
        this.framePerSec = 0;
    }

    public synchronized void update(Board board, int fps) {
        this.balls.clear();
        for (var b : board.getBalls()) {
            this.balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
        }

        //player ball
        var p = board.getPlayerBall();
        this.playerBall = new BallViewInfo(p.getPos(), p.getRadius());

        //bot ball
        var bot = board.getBotBall();
        this.botBall = new BallViewInfo(bot.getPos(), bot.getRadius());

        this.framePerSec = fps;
    }

    public synchronized List<BallViewInfo> getBalls() {
        return new ArrayList<>(balls);
    }

    public synchronized BallViewInfo getPlayerBall() {
        return playerBall;
    }

    public synchronized BallViewInfo getBotBall() {
        return botBall;
    }

    public synchronized int getFramePerSec() {
        return framePerSec;
    }
}
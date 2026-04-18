package pcd.ass01.thread_version.view;

import pcd.ass01.thread_version.model.board.Board;
import java.util.ArrayList;
import java.util.List;

public class ViewModel {

    private final List<SmallBallViewInfo> balls;
    private final List<BallViewInfo> holes;
    private BallViewInfo playerBall;
    private BallViewInfo botBall;
    private int framePerSec;

    public ViewModel() {
        this.balls = new ArrayList<>();
        this.holes = new ArrayList<>();
        this.framePerSec = 0;
    }

    public synchronized void update(Board board, int fps) {
        this.balls.clear();
        for (var b : board.getBalls()) {
            this.balls.add(new SmallBallViewInfo(b.getPos(), b.getRadius(), b.isLastTouchedPlayer(), b.isLastTouchedPlayer()));
        }

        //player ball
        var p = board.getPlayerBall();
        this.playerBall = new BallViewInfo(p.getPos(), p.getRadius());

        //bot ball
        var bot = board.getBotBall();
        this.botBall = new BallViewInfo(bot.getPos(), bot.getRadius());

        //holes
        for (var h : board.getHoles()) {
            this.holes.add(new BallViewInfo(h.getPosition(), h.getRadius()));
        }

        this.framePerSec = fps;
    }

    public synchronized List<SmallBallViewInfo> getBalls() {
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

    public synchronized List<BallViewInfo> getHoles(){
        return new ArrayList<>(holes);
    }
}
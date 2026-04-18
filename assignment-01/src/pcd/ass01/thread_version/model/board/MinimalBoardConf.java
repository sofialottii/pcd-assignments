package pcd.ass01.thread_version.model.board;


import pcd.ass01.thread_version.model.ball.Ball;
import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

import java.util.ArrayList;
import java.util.List;

public class MinimalBoardConf implements BoardConf {

	@Override
	public PlayerBall getPlayerBall() {
    	return new PlayerBall(new P2d(0.5, -0.75));
	}

	@Override
	public BotBall getBotBall() {
		return new BotBall(new P2d(0.5, -0.75));
	}

	@Override
	public List<SmallBall> getSmallBalls() {
        var balls = new ArrayList<SmallBall>();
    	var b1 = new SmallBall(new P2d(0, 0.5), 0.05, 0.75, new V2d(0,0));
    	var b2 = new SmallBall(new P2d(0.05, 0.55), 0.025, 0.25, new V2d(0,0));
    	balls.add(b1);
    	balls.add(b2);
    	return balls;
	}

	@Override
	public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}

}

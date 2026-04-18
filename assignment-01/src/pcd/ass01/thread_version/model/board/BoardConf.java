package pcd.ass01.thread_version.model.board;

import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.sketch01.Ball;
import pcd.sketch01.Boundary;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	PlayerBall getPlayerBall();
	
	List<SmallBall> getSmallBalls();
}

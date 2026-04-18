package pcd.ass01.thread_version.model.board;

import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.holes.Hole;


import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	PlayerBall getPlayerBall();
	BotBall getBotBall();
	
	List<SmallBall> getSmallBalls();


	List<Hole> getHoles();
}

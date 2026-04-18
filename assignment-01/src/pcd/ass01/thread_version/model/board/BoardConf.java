package pcd.ass01.thread_version.model.board;

import pcd.sketch01.Ball;
import pcd.sketch01.Boundary;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();
	
	List<Ball> getSmallBalls();
}

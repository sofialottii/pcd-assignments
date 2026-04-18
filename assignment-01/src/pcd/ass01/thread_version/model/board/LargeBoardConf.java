package pcd.ass01.thread_version.model.board;



import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

import java.util.ArrayList;
import java.util.List;

public class LargeBoardConf implements BoardConf {

	@Override
	public PlayerBall getPlayerBall() {
		return  new PlayerBall(new P2d(0, -0.75));
	}

	@Override
	public List<SmallBall> getSmallBalls() {
		var ballRadius = 0.01;
        var balls = new ArrayList<SmallBall>();

    	for (int row = 0; row < 20; row++) {
    		for (int col = 0; col < 20; col++) {
        		var px = -0.25 + col*0.025;
        		var py =  row*0.025;
        		var b = new SmallBall(new P2d(px, py), ballRadius, 0.25, new V2d(0,0));
            	balls.add(b);    			
    		}
    	}		
    	return balls;
	}

	public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}
}

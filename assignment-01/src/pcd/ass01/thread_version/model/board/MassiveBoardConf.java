package pcd.ass01.thread_version.model.board;



import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.holes.Hole;
import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;

import java.util.ArrayList;
import java.util.List;

public class MassiveBoardConf implements BoardConf {

	private static final double X0 = -1.32;
	private static final double Y0 = -1.0;
	private static final double X1 = 1.32;
	private static final double Y1 = 1.0;
	@Override
	public PlayerBall getPlayerBall() {
		return  new PlayerBall(new P2d(-0.5, -0.75));
	}
	@Override
	public BotBall getBotBall() {
		return new BotBall(new P2d(0.5, -0.75));
	}

	@Override
	public List<SmallBall> getSmallBalls() {
		var ballRadius = 0.01;
        var balls = new ArrayList<SmallBall>();

    	for (int row = 0; row < 30; row++) {
    		for (int col = 0; col < 150; col++) {
        		var px = -1.0 + col*0.015;
        		var py =  row*0.015;
				var b = new SmallBall(new P2d(px, py), ballRadius, 0.25, new V2d(0,0));
            	balls.add(b);    			
    		}
    	}		
    	return balls;
	}

	@Override
	public List<Hole> getHoles() {
		var holes = new ArrayList<Hole>();
		var h1 = new Hole(new P2d(X0 + 0.2, Y1 - 0.01));
		var h2 = new Hole(new P2d(X1 - 0.2, Y1 - 0.01 ));

		holes.add(h1);
		holes.add(h2);
		return holes;
	}

	public Boundary getBoardBoundary() {
		return new Boundary(X0,Y0, X1, Y1);
	}
}

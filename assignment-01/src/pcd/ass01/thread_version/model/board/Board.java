package pcd.ass01.thread_version.model.board;



import pcd.ass01.thread_version.model.ball.AbstractBall;
import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;

import java.util.List;

public class Board {

    private List<SmallBall> balls;
    private PlayerBall playerBall;
    private BotBall botBall;
    private Boundary bounds;
    
    public Board(){} 
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	botBall.updateState(dt, this);

    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                AbstractBall.resolveCollision(balls.get(i), balls.get(j));
            }
        }
    	for (var b: balls) {
    		AbstractBall.resolveCollision(playerBall, b);
    	} 
    	   	    	
    }
    
    public List<SmallBall> getBalls(){
    	return balls;
    }
    
    public PlayerBall getPlayerBall() {
    	return playerBall;
    }

    public BotBall getBotBall() {
        return botBall;
    }
    
    public  Boundary getBounds(){
        return bounds;
    }
}
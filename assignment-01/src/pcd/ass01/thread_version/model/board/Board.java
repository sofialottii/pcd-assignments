package pcd.ass01.thread_version.model.board;



import pcd.ass01.thread_version.model.GameState;
import pcd.ass01.thread_version.model.ball.AbstractBall;
import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.holes.Hole;

import java.util.List;

public class Board {

    private List<SmallBall> balls;
    private PlayerBall playerBall;
    private BotBall botBall;
    private Boundary bounds;
    private List<Hole> holes;
    private GameState gameState;
    
    public Board(){} 
    
    public void init(BoardConf conf, GameState gameState) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
        holes = conf.getHoles();
        this.gameState = gameState;
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	botBall.updateState(dt, this);

    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	

        //collision ball-ball
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                AbstractBall.resolveCollision(balls.get(i), balls.get(j));
            }
        }
        //collision ball-player / ball-bot
    	for (var b: balls) {
    		AbstractBall.resolveCollision(b, playerBall);
            AbstractBall.resolveCollision(b, botBall);
    	}
        //collision player-bot
        AbstractBall.resolveCollision(playerBall, botBall);

        //check for balls entering the holes
        for (var h: holes){
            balls.removeIf(b -> {
                if (h.overlaps(b.getPos(), b.getRadius())) {
                    if (b.isLastTouchedPlayer()) gameState.addPointPlayer();
                    else if (b.isLastTouchedBot())  gameState.addPointBot();
                    return true;
                }
                return false;
            });

            if(h.overlaps(playerBall.getPos(), playerBall.getRadius())){
                gameState.botWin();
            }
            if(h.overlaps(botBall.getPos(), botBall.getRadius())){
                gameState.playerWin();
            }
        }
        if (balls.isEmpty()){
            if (gameState.getPointPlayer()>gameState.getPointBot())
                gameState.playerWin();
            else
                gameState.botWin();
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

    public List<Hole> getHoles(){
        return holes;
    }
}
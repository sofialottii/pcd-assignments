package pcd.ass01.task_version.model.board;



import pcd.ass01.task_version.model.GameState;
import pcd.ass01.task_version.model.ball.AbstractBall;
import pcd.ass01.task_version.model.ball.BotBall;
import pcd.ass01.task_version.model.ball.PlayerBall;
import pcd.ass01.task_version.model.ball.SmallBall;
import pcd.ass01.task_version.model.holes.Hole;
import pcd.ass01.task_version.model.tasks.CollisionTask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Board {

    private List<SmallBall> balls;
    private PlayerBall playerBall;
    private BotBall botBall;
    private Boundary bounds;
    private List<Hole> holes;
    private GameState gameState;
    private int numThreads;
    private int ballsInHole = 0;
    private int chunkSize;

    private ExecutorService executor;
    
    public Board(){} 
    
    public void init(BoardConf conf, GameState gameState) {
    	this.balls = conf.getSmallBalls();
    	this.playerBall = conf.getPlayerBall();
        this.botBall = conf.getBotBall();
    	this.bounds = conf.getBoardBoundary();
        this.holes = conf.getHoles();
        this.gameState = gameState;
        this.numThreads = Math.min(this.balls.size(), Runtime.getRuntime().availableProcessors());

        this.executor = Executors.newFixedThreadPool(numThreads);
        this.chunkSize = this.balls.size() / numThreads;
    }
    
    public void updateState(long dt) throws InterruptedException {

    	playerBall.updateState(dt, this);
    	botBall.updateState(dt, this);

    	for (var b: balls) {
    		b.updateState(dt, this);
    	}

        List<Future<?>> results = new LinkedList<Future<?>>();

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? this.balls.size() : (i + 1) * chunkSize;

            Future<?> res = executor.submit(new CollisionTask(start, end-1, balls, botBall, playerBall));

            results.add(res);

        }

        for (Future<?> res : results) {
            try {
                res.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //collision player-bot
        AbstractBall.resolveCollision(playerBall, botBall);

        //check for balls entering the holes
        for (var h: holes){
            for(var b: balls) {
                if(!b.isInHole()) {
                    if (h.overlaps(b.getPos(), b.getRadius())) {
                        b.setInHole();
                        ballsInHole++;
                        if (b.isLastTouchedPlayer()) gameState.addPointPlayer();
                        else if (b.isLastTouchedBot()) gameState.addPointBot();
                    }
                }
            }

            if(h.overlaps(playerBall.getPos(), playerBall.getRadius())){
                gameState.botWin();
            }
            if(h.overlaps(botBall.getPos(), botBall.getRadius())){
                gameState.playerWin();
            }
        }
        if (this.ballsInHole == this.balls.size()){
            if (this.gameState.getPointPlayer() > this.gameState.getPointBot()) {
                this.gameState.playerWin();
            } else {
                this.gameState.botWin();
            }
        }

      //  executor.shutdown();
      //  executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

    }

    public void shutdownExec(){
        if (this.executor != null && !this.executor.isShutdown()) {
            this.executor.shutdown();
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
    
    public Boundary getBounds(){
        return bounds;
    }

    public List<Hole> getHoles(){
        return holes;
    }

    public int getBallsInHole(){
        return ballsInHole;
    }
}
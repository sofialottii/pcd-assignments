package pcd.ass01.thread_version.model.board;



import pcd.ass01.thread_version.model.GameState;
import pcd.ass01.thread_version.model.ball.AbstractBall;
import pcd.ass01.thread_version.model.ball.BotBall;
import pcd.ass01.thread_version.model.ball.PlayerBall;
import pcd.ass01.thread_version.model.ball.SmallBall;
import pcd.ass01.thread_version.model.holes.Hole;
import pcd.ass01.thread_version.model.util.Barrier;
import pcd.ass01.thread_version.model.workers.Worker;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<SmallBall> balls;
    private PlayerBall playerBall;
    private BotBall botBall;
    private Boundary bounds;
    private List<Hole> holes;
    private GameState gameState;
    private int numThreads;;
    private final List<Worker> workers = new ArrayList<>();
    private Barrier barrier;
    
    public Board(){} 
    
    public void init(BoardConf conf, GameState gameState) {
    	this.balls = conf.getSmallBalls();
    	this.playerBall = conf.getPlayerBall();
        this.botBall = conf.getBotBall();
    	this.bounds = conf.getBoardBoundary();
        this.holes = conf.getHoles();
        this.gameState = gameState;
        this.numThreads = Math.min(this.balls.size(), Runtime.getRuntime().availableProcessors());
        this.barrier = new Barrier(numThreads + 1);

        //create workers
        int chunkSize = this.balls.size() / this.numThreads;

        System.out.println("ball size" + balls.size());

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? this.balls.size() : (i + 1) * chunkSize;

            this.workers.add(
                    new Worker(start, end-1, this.barrier,
                            this.balls, getBotBall(), getPlayerBall()));
            System.out.println("index da: " + start + " fino a: " + (end-1));
        }
    }

    public void startWorkers(){
        for (Worker worker : workers) {
            worker.start();
        }
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	botBall.updateState(dt, this);

    	for (var b: balls) {
    		b.updateState(dt, this);
    	}

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //collision ball-ball
    	/*for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                AbstractBall.resolveCollision(balls.get(i), balls.get(j));
            }
        }*/
        //collision ball-player / ball-bot
    	/*for (var b: balls) {
    		AbstractBall.resolveCollision(b, playerBall);
            AbstractBall.resolveCollision(b, botBall);
    	}*/

        //collision player-bot
        AbstractBall.resolveCollision(playerBall, botBall);

        //check for balls entering the holes
        for (var h: holes){
            //balls.removeIf(b -> {
            for(var b: balls) {
                if (h.overlaps(b.getPos(), b.getRadius())) {
                    if (b.isLastTouchedPlayer()) gameState.addPointPlayer();
                    else if (b.isLastTouchedBot()) gameState.addPointBot();
                    //return true;
                }
                //return false;
            }
            //});

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
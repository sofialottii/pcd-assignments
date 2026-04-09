package pcd.sketch01;

import java.util.*;

public class Board {

    private List<Ball> balls;    
    private Ball playerBall;
    private Boundary bounds;
    
    public Board(){} 
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall(); 
    	bounds = conf.getBoardBoundary();
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }
    	for (var b: balls) {
    		Ball.resolveCollision(playerBall, b);
    	} 
    	   	    	
    }
    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }
    
    public  Boundary getBounds(){
        return bounds;
    }
}

/* 
questa classe è il tavolo, dove ci sono la lista delle palline e anche la playerBall

quando aggiorno, aggiorno lo stato della pallina dell'utente, poi lo stato di tutte le 
ball, e poi devo risolvere le collisioni (vedi i for da riga 23 in poi)
L'approccio semplice usato qui: per ogni pallina, controlla le collisioni con tutte le palline
(riga da 27 a 29)
a riga 32 si va a verificare se c'è la collisione tra la player ball e ogni pallina 
*/
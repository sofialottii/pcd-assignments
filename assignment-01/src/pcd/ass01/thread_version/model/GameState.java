package pcd.ass01.thread_version.model;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameState {
    private int pointPlayer;
    private int pointBot;
    private boolean gameOver;
    private boolean playerWin;
    private boolean botWin;

    private Runnable onGameOver;
    private final AtomicBoolean gameOverFired = new AtomicBoolean(false);

    public GameState() {
        this.pointPlayer = 0;
        this.pointBot    = 0;
        this.gameOver    = false;
        this.playerWin   = false;
        this.botWin      = false;
    }

    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    public int getPointBot()    { return pointBot; }
    public int getPointPlayer() { return pointPlayer; }

    public void addPointBot()    { this.pointBot++; }
    public void addPointPlayer() { this.pointPlayer++; }

    public boolean isGameOver()  { return this.gameOver; }
    public boolean isPlayerWin() { return this.playerWin; }
    public boolean isBotWin()    { return this.botWin; }

    public void playerWin() {
        if (gameOverFired.compareAndSet(false, true)) {
            this.gameOver  = true;
            this.playerWin = true;
            if (onGameOver != null) onGameOver.run();
        }
    }

    public void botWin() {
        if (gameOverFired.compareAndSet(false, true)) {
            this.gameOver = true;
            this.botWin   = true;
            if (onGameOver != null) onGameOver.run();
        }
    }
}
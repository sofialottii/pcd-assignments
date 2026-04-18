package pcd.ass01.thread_version.model;

public class GameState {
    private int pointPlayer;
    private int pointBot;
    private boolean gameOver;
    private boolean playerWin;
    private boolean botWin;

    public GameState(){
        this.pointPlayer = 0;
        this.pointBot = 0;
        this.gameOver = false;
        this.playerWin = false;
        this.botWin = false;
    }

    public int getPointBot() {
        return pointBot;
    }

    public int getPointPlayer() {
        return pointPlayer;}

    public void addPointBot() {
        this.pointBot++;
    }

    public void addPointPlayer() {
        this.pointPlayer++;
    }

    public boolean isGameOver(){
        return this.gameOver;
    }
    public boolean isPlayerWin(){
        return this.playerWin;
    }
    public boolean isBotWin(){
        return this.botWin;
    }

    public void playerWin(){
        this.gameOver = true;
        this.playerWin = true;
    }

    public void botWin(){
        this.gameOver = true;
        this.botWin = true;
    }
}

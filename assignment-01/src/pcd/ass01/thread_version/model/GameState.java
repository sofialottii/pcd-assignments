package pcd.ass01.thread_version.model;

public class GameState {
    private int pointPlayer;
    private int pointBot;

    public GameState(){
        this.pointPlayer = 0;
        this.pointBot = 0;

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
}

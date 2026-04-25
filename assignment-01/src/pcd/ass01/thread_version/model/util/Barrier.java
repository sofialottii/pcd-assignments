package pcd.ass01.thread_version.model.util;

public class Barrier {

    private final int totalWorkers;
    private int waitingWorkers = 0;
    private int generation = 0;

    public Barrier(int totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    public synchronized void await() throws InterruptedException {
        int actualGeneration = this.generation;
        waitingWorkers++;

        if (waitingWorkers == totalWorkers) {
            waitingWorkers = 0;
            generation++;
            notifyAll();
        } else {
            while (actualGeneration == this.generation) {
                wait();
            }
        }
    }
}

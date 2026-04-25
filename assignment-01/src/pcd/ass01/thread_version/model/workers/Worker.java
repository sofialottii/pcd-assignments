package pcd.ass01.thread_version.model.workers;

import pcd.ass01.thread_version.model.util.Barrier;

public class Worker extends Thread{

    private final int firstIndex;
    private final int lastIndex;
    private final Barrier barrier;


    public Worker(int firstIndex, int lastIndex, Barrier barrier) {
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.barrier = barrier;
    }

    @Override
    public void run() {

    }

}

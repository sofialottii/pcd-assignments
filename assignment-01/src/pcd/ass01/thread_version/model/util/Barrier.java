package pcd.ass01.thread_version.model.util;

import pcd.ass01.thread_version.model.workers.Worker;

import java.util.List;

public class Barrier {

    private final List<Worker> totalWorkers;
    private int waitingWorkers = 0;

    public Barrier(List<Worker> totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    public synchronized void await() throws InterruptedException {

        waitingWorkers++;

        if (waitingWorkers == totalWorkers.size()) {
            waitingWorkers = 0;
            notifyAll();
        }

    }

}

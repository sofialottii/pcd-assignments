package pcd.ass01.task_version.controller;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-safe buffer for managing game commands.
 * This class implements the Monitor pattern to ensure mutual exclusion
 * when accessing the command queue.
 */
public class CmdBuffer {

    private final Queue<Cmd> queue = new LinkedList<>();

    public synchronized void add(Cmd cmd) {
        queue.add(cmd);
    }

    public synchronized Cmd poll() {
        //poll per fare in modo che vengano presi comandi
        //solo se ci sono. metodo non bloccante
        return queue.poll();
    }
}

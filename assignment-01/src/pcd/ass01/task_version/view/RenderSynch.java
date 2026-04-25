package pcd.ass01.task_version.view;

/**
 * This class is a timing system based on the wait() and notifyAll() commands.
 * It synchronizes the speeds of the threads GameLoop and EDT.
 * RenderSynch tells GameLoop to wait until graphics are still drawing the previous frame.
 */
public class RenderSynch {

    private long nextFrameToRender;
    private long lastFrameRendered;

    public RenderSynch() {
        nextFrameToRender = 0;
        lastFrameRendered = -1;
    }
    public synchronized long nextFrameToRender() {
        long f = nextFrameToRender;
        nextFrameToRender++;
        return f;
    }

    public synchronized void notifyFrameRendered() {
        lastFrameRendered++;
        notifyAll();
    }

    public synchronized void waitForFrameRendered(long frame) throws InterruptedException {
        while (lastFrameRendered < frame) {
            wait();
        }
    }

}

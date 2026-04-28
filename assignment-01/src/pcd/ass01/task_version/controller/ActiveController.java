package pcd.ass01.task_version.controller;

import pcd.ass01.task_version.model.board.Board;
import pcd.ass01.task_version.view.ViewFrame;
import pcd.ass01.task_version.view.ViewModel;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is the main Controller of the project. He extends Thread.
 * Through the while(running) loop, he ensures the game never stops
 * For a better experience, he calculates how many milliseconds have passed between frames:
 * if the PC slows down, the dt increases, and the physics "move" the balls further to compensate
 * (see fps variable).
 *
 * He tells the Model to calculate, the ViewModel to update, and the ViewFrame to draw.
 */
public class ActiveController extends Thread {

    private final Board board;
    private final ViewFrame view;
    private final ViewModel viewModel;
    private volatile boolean running = true;

    private final ConcurrentLinkedQueue<Cmd> cmdBuffer;

    public ActiveController(Board board, ViewFrame view, ViewModel viewModel) {
        this.board = board;
        this.view = view;
        this.viewModel = viewModel;
        this.cmdBuffer = new ConcurrentLinkedQueue<>();
        this.view.addKeyListener(new PlayerInputHandler(this));
    }

    @Override
    public void run() {
        long lastTimestamp = System.currentTimeMillis();

        System.out.println("GameController started.");

        while (running) {
            long currentTimestamp = System.currentTimeMillis();
            long dt = currentTimestamp - lastTimestamp;
            lastTimestamp = currentTimestamp;

            //if there is a command from the queue we execute it
            Cmd cmd = cmdBuffer.poll();
            if (cmd != null) {
                cmd.execute(board);
            }

            //update model
            try {
                board.updateState(dt);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //FPS per la view
            int fps = (dt > 0) ? (int)(1000 / dt) : 0;
            viewModel.update(board, fps);

            view.render();

            //60 fps
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    /**
     * method that the keylistener will use to send commands
     *
     * @param cmd the action
     */
    public void notifyNewCmd(Cmd cmd) {
        cmdBuffer.add(cmd);
    }


    public void stopGame() {
        this.running = false;
    }
}
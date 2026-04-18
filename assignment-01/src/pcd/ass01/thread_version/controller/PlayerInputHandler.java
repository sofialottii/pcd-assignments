package pcd.ass01.thread_version.controller;

import pcd.ass01.thread_version.model.util.V2d;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayerInputHandler extends KeyAdapter {

    private final ActiveController controller;

    private static final double POWER = 0.3;

    public PlayerInputHandler(ActiveController controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                controller.notifyNewCmd(b -> b.getPlayerBall().kick(new V2d(0, POWER)));
                break;

            case KeyEvent.VK_DOWN:
                controller.notifyNewCmd(b -> b.getPlayerBall().kick(new V2d(0, -POWER)));
                break;

            case KeyEvent.VK_RIGHT:
                controller.notifyNewCmd(b -> b.getPlayerBall().kick(new V2d(POWER, 0)));
                break;

            case KeyEvent.VK_LEFT:
                controller.notifyNewCmd(b -> b.getPlayerBall().kick(new V2d(-POWER, 0)));
                break;
        }
    }



}

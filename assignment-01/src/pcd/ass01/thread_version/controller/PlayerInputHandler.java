package pcd.ass01.thread_version.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayerInputHandler extends KeyAdapter {

    private final ActiveController controller;

    private static final double POWER = 2.0;

    public PlayerInputHandler(ActiveController controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                break;

            case KeyEvent.VK_DOWN:
                break;

            case KeyEvent.VK_RIGHT:
                break;

            case KeyEvent.VK_LEFT:
                break;
        }
    }



}

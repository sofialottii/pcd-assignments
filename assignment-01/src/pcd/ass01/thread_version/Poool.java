package pcd.ass01.thread_version;


import pcd.ass01.thread_version.controller.ActiveController;
import pcd.ass01.thread_version.controller.BotMovements;
import pcd.ass01.thread_version.model.GameState;
import pcd.ass01.thread_version.model.board.*;
import pcd.ass01.thread_version.view.ViewFrame;
import pcd.ass01.thread_version.view.ViewModel;

import javax.swing.SwingUtilities;

/**
 * Main class of the project.
 * There are 3 possibly configs, and you can choose one of them.
 *
 * This class start everything.
 */
public class Poool {

    public static void main(String[] args) {

        Board board = new Board();
        GameState gameState = new GameState();

        //BoardConf config = new MinimalBoardConf();
        BoardConf config = new LargeBoardConf();
        //BoardConf config = new MassiveBoardConf();

        board.init(config, gameState);

        ViewModel viewModel = new ViewModel();

        SwingUtilities.invokeLater(() -> {

            ViewFrame view = new ViewFrame(viewModel, 800, 600, gameState);

            ActiveController controller = new ActiveController(board, view, viewModel);

            BotMovements bot = new BotMovements(controller, board);
            bot.start();

            view.setFocusable(true);
            view.requestFocusInWindow();
            view.setVisible(true);

            //per fare partire il thread del gameloop
            controller.start();
        });
    }

}

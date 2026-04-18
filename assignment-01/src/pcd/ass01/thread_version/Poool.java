package pcd.ass01.thread_version;


import pcd.ass01.thread_version.controller.ActiveController;
import pcd.ass01.thread_version.model.board.*;
import pcd.ass01.thread_version.view.ViewFrame;
import pcd.ass01.thread_version.view.ViewModel;

import javax.swing.SwingUtilities;

public class Poool {

    public static void main(String[] args) {

        Board board = new Board();

        BoardConf config = new MinimalBoardConf();
        //BoardConf config = new LargeBoardConf();
        //BoardConf config = new MassiveBoardConf();

        board.init(config);

        ViewModel viewModel = new ViewModel();

        SwingUtilities.invokeLater(() -> {

            ViewFrame view = new ViewFrame(viewModel, 800, 600);

            ActiveController controller = new ActiveController(board, view, viewModel);

            /* event listener


             */

            view.setVisible(true);

            //per fare partire il thread del gameloop
            controller.start();
        });
    }

}

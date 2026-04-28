package pcd.ass01.task_version.controller;


import pcd.ass01.task_version.model.board.Board;

/**
 * interface that contains the execute method. The commands will modify the Board.
 * We use it in the ActiveController (which in turn sends the command to the board
 * to change the speed of the balls)
 */
public interface Cmd {

    void execute(Board board);
}


package pcd.ass01.thread_version.controller;


import pcd.ass01.thread_version.model.board.Board;

/**
 * interface that contains the execute method. The commands will modify the Board
 */
public interface Cmd {

    void execute(Board board);
}


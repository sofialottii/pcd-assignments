package pcd.ass01.thread_version.model.ball;

import pcd.ass01.thread_version.model.util.P2d;
import pcd.ass01.thread_version.model.util.V2d;
import pcd.ass01.thread_version.model.board.Board;

public interface Ball {

    void updateState(long dt, Board ctx);

    void kick(V2d vel);

    P2d getPos();

    double getMass();

    V2d getVel();

    double getRadius();
}
package pcd.ass01.task_version.model.ball;

import pcd.ass01.task_version.model.board.Board;
import pcd.ass01.task_version.model.util.P2d;
import pcd.ass01.task_version.model.util.V2d;


/**
 * interface implemented by AbstractBall, which in turn is extended
 * by every type of ball (user, bot, smallballs).
 */
public interface Ball {

    /**
     * this method updates the state of the ball
     *
     * @param dt delta time
     * @param ctx board
     */
    void updateState(long dt, Board ctx);

    /**
     * This method modifies the ball velocity
     *
     * @param vel new velocity
     */
    void kick(V2d vel);

    P2d getPos();

    double getMass();

    V2d getVel();

    double getRadius();
}
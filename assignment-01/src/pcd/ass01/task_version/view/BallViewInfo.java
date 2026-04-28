package pcd.ass01.task_version.view;

import pcd.ass01.task_version.model.util.P2d;

/**
 * It's a data container (a record). It stores only the information that
 * ViewFrame and ViewModel need to draw a ball: position (X, Y) and radius.
 *
 * @param pos position of the ball
 * @param radius radius of the ball
 */
public record BallViewInfo(P2d pos, double radius) { }
package pcd.ass01.thread_version.view;

import pcd.ass01.thread_version.model.util.P2d;

/**
 * contains pos and radius balls at a given moment + the state of the ball
 *
 */
public record SmallBallViewInfo(P2d pos, double radius, boolean isPlayerTouch, boolean isBotTouch, boolean inHole) { }
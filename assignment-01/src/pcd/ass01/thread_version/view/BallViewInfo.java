package pcd.ass01.thread_version.view;

import pcd.ass01.thread_version.model.util.P2d;

/**
 * contains pos and radius balls at a given moment
 *
 */
public record BallViewInfo(P2d pos, double radius) { }
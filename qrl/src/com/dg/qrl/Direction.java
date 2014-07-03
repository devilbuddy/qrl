package com.dg.qrl;

public enum Direction {
	NONE(0, 0),
	N(0, 1),
	NE(1, 1),
	E(1, 0),
	SE(1, -1),
	S(0, -1),
	SW(-1, -1),
	W(-1, 0),
	NW(1, 1);
	
	public static final Direction[] MOVE_DIRECTIONS = {
		N, NE, E, SE, S, SW, W, NW	
	};
	
	public final int dx;
	public final int dy;
	private Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
}

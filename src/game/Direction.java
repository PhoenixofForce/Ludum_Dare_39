package game;

import java.awt.*;

public enum Direction {
	LL(10, 8, 15, 18), RR(9, 9, 15, 18), UU(9, 10, 18, 15), DD(7, 9, 18, 15), UL(9, 7, 16, 17), UR(10, 9, 16, 16), DR(9, 10, 16, 16), DL(8, 9, 16, 16);

	public Rectangle box;
	Direction(int x, int y, int w, int h) {
		box = new Rectangle(x, y, w, h);
	}
}

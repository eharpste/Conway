package com.duckcult.conway.player;

import com.duckcult.conway.gol.BasicCell;
import com.duckcult.conway.gol.Board;
import com.duckcult.conway.gol.Cell;

public class BasicShot extends Weapon {
	public BasicShot (int x, int y) {
		this.x = x;
		this.y = y;
		vx = 0;
		vy = 1;
	}
	
	@Override
	public	void update() {
		y+=vy;
	}

	@Override
	public boolean hit(Board board) {
		board.getCell(x, y).kill();
		return true;
	}

	@Override
	public Cell[][] getShape() {
		Cell [][] ret = new Cell[1][1];
		ret[0][0] = new BasicCell(true);
		return ret;
	}

}

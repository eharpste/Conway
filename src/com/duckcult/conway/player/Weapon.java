package com.duckcult.conway.player;

import com.duckcult.conway.gol.Board;
import com.duckcult.conway.gol.Cell;

public abstract class Weapon {
	protected int x, y, vx, vy;
	
	public abstract void update();
	public abstract boolean hit(Board board);
	
	public void setPosition(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public void setVelocity(int vx, int vy) {
		this.vx=vx;
		this.vy=vy;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public abstract Cell[][] getShape();
}

package com.duckcult.conway.player;

import com.duckcult.conway.gol.BasicCell;
import com.duckcult.conway.gol.Cell;

public class Ship {
	public static final int DEFAULT_START_POSTION = 3;
	
	public int x, y;
	private boolean alive;
	
	public Ship() {
		x = 0;
		y = 0;
		alive = true;
	}
	
	public Ship(int x, int y) {
		this.x = x;
		this.y = y;
		alive = true;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setPostion(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public Cell [][] getShape() {
		Cell [][] ret = new Cell[1][1];
		ret[0][0] = new BasicCell(true);
		return ret;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill() {
		alive = false;
	}
	
	public Weapon fireShot(){
		return new BasicShot(x, y);
	}
}

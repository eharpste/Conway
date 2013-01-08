package com.duckcult.conway.gol;

import com.badlogic.gdx.math.Rectangle;

public class Cell {
	public boolean alive;
	public final Rectangle rect;
	public final int x,y;
	
	public Cell(int x, int y, Rectangle r, boolean alive) {
		this.alive = alive;
		this.x = x;
		this.y = y;
		this.rect = r;
	}
	
	public Cell() {
		this.x = -1;
		this.y = -1;
		rect = new Rectangle();
		alive = false;
	}
	
	public float xMax () {
		return rect.x+rect.width;
	}
	
	public float yMax () {
		return rect.y+rect.height;
	}
	
	public void flip() {
		alive = !alive;
	}
}

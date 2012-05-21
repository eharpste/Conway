package com.duckcult.conway.gol;

import com.badlogic.gdx.math.Rectangle;

public class Cell {
	public boolean alive;
	public Rectangle rect;
	public int x,y;
	
	public Cell(int x, int y, float xRender, float yRender, float width, float height, boolean alive) {
		this.alive = alive;
		this.x = x;
		this.y = y;
		rect = new Rectangle(xRender,yRender,width,height);
	}
	
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
}

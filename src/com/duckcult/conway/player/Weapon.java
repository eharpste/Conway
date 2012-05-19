package com.duckcult.conway.player;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.gol.Board;

public abstract class Weapon {
	//protected float x, y, vx, vy, size;
	protected float vx,vy;
	protected Rectangle rect;
	protected Color color;
	
	public abstract void update(float deltaTime);
	public abstract boolean hit(Board board);
	public abstract Mesh toMesh(float depth);
	
	public Rectangle getRect() {
		return rect;
	}
	
	public void setPosition(float x, float y) {
		rect.x=x;
		rect.y=y;
	}
	
	public void setVelocity(float vx, float vy) {
		this.vx=vx;
		this.vy=vy;
	}
}

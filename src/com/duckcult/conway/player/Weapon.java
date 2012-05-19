package com.duckcult.conway.player;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.duckcult.conway.gol.Board;

public abstract class Weapon {
	protected float x, y, vx, vy, size;
	protected Color color;
	
	public abstract void update(float deltaTime);
	public abstract boolean hit(Board board);
	public abstract Mesh toMesh(float depth);
	
	public void setPosition(float x, float y) {
		this.x=x;
		this.y=y;
	}
	
	public void setVelocity(float vx, float vy) {
		this.vx=vx;
		this.vy=vy;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}

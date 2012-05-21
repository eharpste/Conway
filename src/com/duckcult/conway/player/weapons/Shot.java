package com.duckcult.conway.player.weapons;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.utils.ColorUtils;

public abstract class Shot {
	//protected float x, y, vx, vy, size;
	protected float vx,vy;
	protected Rectangle rect;
	protected Color color;
	
	protected int originWeapon;
	protected int originPlayer;
	
	public abstract void update(float deltaTime);
	public abstract boolean hit(FastBoard board);
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
	
	public void setOriginInfo(int playerNumber, int weaponNumber,Color color) {
		originWeapon = weaponNumber;
		originPlayer = playerNumber;
		this.color = ColorUtils.desaturate(color, .3f);
	}
	
	public int getOriginPlayer() {return originPlayer;}
	
	public int getOriginWeapon() {return originWeapon;}
}

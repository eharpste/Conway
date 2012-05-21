package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.duckcult.conway.player.Ship;

public abstract class Weapon {
	protected String name;
	protected float rofDelay;
	protected float timeSinceFire;
	
	public abstract ArrayList<Shot> fire(Ship origin);
	
	public String getName(){return name;}
	public float getRofDelay(){return rofDelay;}
	
	public void update(float deltaTime) {
		if(timeSinceFire < rofDelay) 
			timeSinceFire+=deltaTime;
	}
}

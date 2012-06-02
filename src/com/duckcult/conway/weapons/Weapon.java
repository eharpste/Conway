package com.duckcult.conway.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.player.Ship;

public abstract class Weapon {
	/**
	 * The name of the weapon.
	 * Currently I don't use this but its feasible for a GUI to tell the player what weapon they are using.
	 */
	protected String name;
	/**
	 * The Rate of Fire delay on the weapon. 
	 * Translates to number of seconds between shots, usually <1.0.
	 * As firing is normally hooked directly up to update() its nice to have this to prevent a new shot for every frame.
	 */
	protected float rofDelay;
	/**
	 * Time since the weapon was last fired.
	 * Used in concert with rofDelay to control the firing rate of the weapon.
	 * essentially the fire method should only return meaningful results if timeSinceFire >= rofDelay.
	 */
	protected float timeSinceFire;
	
	/**
	 * Where all the magic happens in weapons.
	 * This should return some collection of shots that represent what happens when the weapon is fired once.
	 * This could be anything from a single straight shot to a defined pattern.
	 * Normally this method should only return meaningful input if timeSinceFire >= rofDelay.
	 * If the weapon should not fire yet it should return an empty ArrayList
	 * @param origin The Ship firing the weapon, its properties are commonly needed to spawn shots.
	 * @return	An ArrayList of the shots from a single firing of this weapon, or an empty ArrayList if the weapon is not ready to fire yet.
	 */
	public abstract ArrayList<Shot> fire(Ship origin);
	
	/**
	 * Returns the name of the weapon.
	 * @return The name of the weapon.
	 */
	public String getName(){return name;}
	/**
	 * Returns the rate of fire delay.
	 * @return The rate of fire delay.
	 */
	public float getRofDelay(){return rofDelay;}
	
	/**
	 * By default this method adds to deltaTime to timeSince fire for ROF control.
	 * As most weapons only need update to do this it is defined explicitly here though weapons could override it.
	 * @param deltaTime	The time since the last frame render.
	 */
	public void update(float deltaTime) {
		if(timeSinceFire < rofDelay) 
			timeSinceFire+=deltaTime;
	}
	
	/**
	 * Returns the x or y magnitude for a shot moving diagonally with the given speed.
	 * @param speed
	 * @return
	 */
	protected float getDiagonal(float speed) {
		return (float)Math.sqrt(speed*speed/2);
	}
	
	/**
	 * Returns the Vector for the given magnitude at the given angle.
	 * NOTE: angle is in degrees.
	 * @param magnitude
	 * @param degreesAngle
	 * @return
	 */
	protected Vector2 vectorComponents(float magnitude, float degreesAngle) {
		return new Vector2(magnitude*MathUtils.cosDeg(degreesAngle), magnitude*MathUtils.sinDeg(degreesAngle));
	}
}

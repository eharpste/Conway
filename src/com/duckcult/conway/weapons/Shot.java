package com.duckcult.conway.weapons;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.player.Ship;
import com.duckcult.conway.utils.ColorUtils;

public abstract class Shot {
	/**
	 * A 2D Vector representing the x and y components of the current velocity of the shot.
	 */
	protected Vector2 velocity;
	/**
	 * A Rectangle used to render the shot and for collision detection.
	 */
	protected Rectangle rect;
	/**
	 * The Color used to render the shot.
	 */
	protected Color color;
	/**
	 * The ID of the weapon used to fire this shot.
	 */
	protected int originWeapon;
	/**
	 * The ID of the player that fired this shot.
	 */
	protected int originPlayer;
	/**
	 * Runs a standard frame update on the shot.
	 * Called once per frame.
	 * @param deltaTime	Time since the last frame.
	 */
	public abstract void update(float deltaTime);
	/**
	 * Runs to see if the shot hit anything on a board, process the effect of the hit, and returns true if the hit is successful.
	 * Returning true from this method is a signal to the world to remove the shot. In general this means that the shot
	 * successfully hit its target but this is not a strict contract.
	 * @param board	The board to check for collisions
	 * @return true is the World should remove the shot after running the hit (usually meaning it hit something), false otherwise.
	 */
	public abstract boolean hit(FastBoard board);
	/**
	 * Runs to see if the shot hit the ship, process the effect of the hit, and returns true if the hit is successful.
	 * Returning true from this method is a signal to the world to remove the shot. In general this means that the shot
	 * successfully hit its target but this is not a strict contract.
	 * @param ship
	 * @return
	 */
	public abstract boolean hit(Ship ship);
	/**
	 * Returns the Mesh representation of the Shot for rendering
	 * @param depth	the depth to render at
	 * @return	The Mesh representation of the Shot for rendering.
	 */
	public abstract Mesh toMesh(float depth);
	
	/**
	 * Returns the Rectangle representing the Shot.
	 * @return	The Rectangle representing the Shot.
	 */
	public Rectangle getRect() {
		return rect;
	}
	
	/**
	 * Sets the position of the Shot programatically
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		rect.x=x;
		rect.y=y;
	}
	
	/**
	 * Sets the position of the Shot programatically
	 * @param position
	 */
	public void setPosition(Vector2 position) {
		rect.x = position.x;
		rect.y = position.y;
	}
	
	/**
	 * Sets the velocity of the Shot programatically
	 * @param vx
	 * @param vy
	 */
	public void setVelocity(float vx, float vy) {
		velocity.x=vx;
		velocity.y=vy;
	}
	
	/**
	 * Sets the velocity of the Shot programatically
	 * @param velocity
	 */
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Sets the information that encodes the origin of the shot.
	 * The color of the shot will be set to the same as its origin at 30% saturation.
	 * @param playerNumber	The ID of the player that fired the Shot
	 * @param weaponNumber	The ID of the weapon that fired the Shot
	 * @param color			The Color of the player that fired the Shot.
	 */
	public void setOriginInfo(int playerNumber, int weaponNumber,Color color) {
		originWeapon = weaponNumber;
		originPlayer = playerNumber;
		this.color = ColorUtils.desaturate(color, .3f);
	}
	
	/**
	 * Returns the ID of the player that fired the Shot.
	 * @return
	 */
	public int getOriginPlayer() {return originPlayer;}
	/**
	 * Returns the ID of the weapon that fired the Shot.
	 * @return
	 */
	public int getOriginWeapon() {return originWeapon;}
}

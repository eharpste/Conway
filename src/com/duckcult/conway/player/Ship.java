package com.duckcult.conway.player;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.player.weapons.BeamWeapon;
import com.duckcult.conway.player.weapons.QuadShotWeapon;
import com.duckcult.conway.player.weapons.RapidFireWeapon;
import com.duckcult.conway.player.weapons.SpiralWeapon;
import com.duckcult.conway.player.weapons.StandardWeapon;
import com.duckcult.conway.player.weapons.Shot;
import com.duckcult.conway.player.weapons.TrippleShotWeapon;
import com.duckcult.conway.player.weapons.Weapon;

public class Ship {
	/**
	 * Whether or not the Ship is alive.
	 */
	private boolean alive = true;
	/**
	 * The Rectangle representing the Ship.
	 */
	private Rectangle rect;
	/**
	 * The speed that ship will move in the x and y directions.
	 */
	private float xv,yv;
	/**
	 * The Color used to render the ship.
	 */
	private Color color;
	/**
	 * A set of Rectangles used to represent the ship when it dies.
	 * This remains null until the ship is dead.
	 */
	private Rectangle [] deathRects = null;
	/**
	 * An Array of the ship's weapons currently indexed as:
	 * 0 - StandardWeapon
	 * 1 - RapidFireWeapon
	 * 2 - TrippleShotWeapon
	 * 3 - QuadShotWeapon
	 * 4 - BeamWeapon
	 * 5 - SprialWeapon
	 */
	private Weapon [] weapons;
	/**
	 * The number of the player controlling the ship.
	 */
	private int playerNumber;
	/**
	 * The KeyBindings that the ship will respond to.
	 */
	private KeyBindings keyBindings;
	/**
	 * The index of the current active weapon.
	 */
	private int weaponMode = 0;
	/**
	 * The time since the ship died. 
	 * Used for death animation.
	 */
	private float timeSinceDeath = 0.0f;
	/**
	 * The length of time that the deathAnimation runs before the ship is no longer rendered.
	 */
	private float deathAnimationTime = 1.25f;
	/**
	 * The travel speed of the dead ship's shards.
	 */
	private float shardSpeed = .075f;
	/**
	 * The time delay between when the player is allowed to switch weapons.
	 * This isn't meant to be a balance hindrance its mainly to prevent the change weapon button from being spammed by the render loop.
	 */
	private float weaponModeSwitchTime = .2f;
	/**
	 * The time since the player last switch weapons.
	 */
	private float timeSinceWeaponSwitch = 0.0f;
	
	/**
	 * Creates the standard single player ship with the given size.
	 * @param size	The rectangle size of the desired ship.
	 */
	public Ship (float size) {
		alive = true;
		rect = new Rectangle(0.0f-size,-.75f-size,size,size);	
		xv = .5f;
		yv = .5f;
		color = Color.RED;
		playerNumber = 1;
		keyBindings = KeyBindings.WASD_QE_SPACE;
		weapons = new Weapon [6];
		weapons[0]= new StandardWeapon();
		weapons[1]= new RapidFireWeapon();
		weapons[2]= new TrippleShotWeapon();
		weapons[3]= new QuadShotWeapon();
		weapons[4]= new BeamWeapon();
		weapons[5]= new SpiralWeapon();
		
	}
	
	/**
	 * Creates a new ship.
	 * This version is intended for multiplayer.
	 * @param size			The rectangle size of the ship.
	 * @param playerNumber	The designated player number
	 * @param playerColor	The player's color.
	 * @param keyBindings	The keyBindings that the player will use.
	 */
	public Ship(float size, int playerNumber, Color playerColor, KeyBindings keyBindings){
		this(size);
		color = playerColor;
		this.playerNumber = playerNumber;
		this.keyBindings = keyBindings;
	}
	
	/**
	 * Runs an update on the ship, intended to be called once per frame.
	 * During the update the ship will call update on all it's weapons.
	 * update the value of its weaponSwitch timer.
	 * Respond to input to switch weapons, move, and fire.
	 * If the ship is dead it will update the death animation.
	 * @param deltaTime	The time since the last render frame
	 * @param shots		The shots currently out in the world, if the ship fires it will add new shots to this list.
	 */
	public  void update(float deltaTime, ArrayList<Shot> shots) {
		if(alive){
			for(Weapon w : weapons) {
				w.update(deltaTime);
			}
			if(timeSinceWeaponSwitch <= weaponModeSwitchTime) 
				timeSinceWeaponSwitch +=deltaTime;
			if(weaponMode > 0 && Gdx.input.isKeyPressed(keyBindings.prevWeapon()) && timeSinceWeaponSwitch > weaponModeSwitchTime) {
				weaponMode--;
				timeSinceWeaponSwitch = 0.0f;
			}
			if(weaponMode < weapons.length-1 && Gdx.input.isKeyPressed(keyBindings.nextWeapon()) && timeSinceWeaponSwitch > weaponModeSwitchTime) {
				weaponMode++;
				timeSinceWeaponSwitch = 0.0f;
			}
			if(Gdx.input.isKeyPressed(keyBindings.up())  && rect.y < 1 - rect.height) {
				rect.y += deltaTime * yv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.left()) && rect.x > -1){
				rect.x -= deltaTime * xv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.down()) && rect.y > -1){
				rect.y -= deltaTime * yv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.right()) && rect.x < 1 - rect.width) {
				rect.x += deltaTime * xv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.fire())) {
				fire(shots);
			}
		}
		else {
			timeSinceDeath += deltaTime;
			//0 is lower left
			deathRects[0].x-=deltaTime*shardSpeed;
			deathRects[0].y-=deltaTime*shardSpeed;
			//1 is lower right
			deathRects[1].x+=deltaTime*shardSpeed;
			deathRects[1].y-=deltaTime*shardSpeed;
			//2 is upper left
			deathRects[2].x-=deltaTime*shardSpeed;
			deathRects[2].y+=deltaTime*shardSpeed;
			//3 is upper right
			deathRects[3].x+=deltaTime*shardSpeed;
			deathRects[3].y+=deltaTime*shardSpeed;
 		}
	}
	
	/**
	 * Returns the Rectangle representing the ship.
	 * @return The Rectangle representing the ship.
	 */
	public Rectangle getRect() {return rect;}
	
	/**
	 * A method to programatically alter the ship's position.
	 * Input related movement happens on its own in the update method.
	 * @param deltaX	The ammount to change X by
	 * @param deltaY	The ammount to change Y by
	 */
	public void alterPosition(float deltaX, float deltaY) {
		rect.x += deltaX;
		rect.y += deltaY;
	}
	
	public void setPosition(Vector2 position) {
		rect.x = position.x;
		rect.y = position.y;
	}
	
	/**
	 * A method to change the directional velocities the ship will move with.
	 * Note: the values are multipled by deltaTime in update.
	 * @param xv
	 * @param yv
	 */
	public void setVelocity(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
	}
	
	/**
	 * Returns whether or not the ship is alive.
	 * @return
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/**
	 * This kills the Ship.
	 * When the ship is dead it turns into 4 rectangles that move away from the center of where the ship was.
	 */
	public void kill() {
		alive = false;
		deathRects = new Rectangle[4];
		deathRects[0] = new Rectangle(rect.x,rect.y,rect.width/2f,rect.height/2f);
		deathRects[1] = new Rectangle(rect.x+rect.width/2f, rect.y, rect.width/2f, rect.height/2f);
		deathRects[2] = new Rectangle(rect.x, rect.y+rect.height/2f,rect.width/2f,rect.height/2f);
		deathRects[3] = new Rectangle(rect.x+rect.width/2f,rect.y+rect.height/2f,rect.width/2f,rect.height/2f);
		timeSinceDeath = 0.0f;
	}
	
	/**
	 * Respawns the Ship at the given postion.
	 * @param position
	 */
	public void respawn(Vector2 position) {
		this.respawn();
		rect.x=position.x;
		rect.y=position.y;
	}
	
	/**
	 * Respawns the Ship.
	 * Sets alive to true, timeSinceWeaponSwitch to 0 and weaponMode to 0.
	 */
	public void respawn() {
		alive = true;
		deathRects = null;
		timeSinceWeaponSwitch = 0.0f;
		weaponMode = 0;
	}
	
	/**
	 * Returns a collection of Meshes to render the ship.
	 * Normally this is a collection of 1, when the ship is alive but there are 4 if the ship is dead.
	 * @param depth The depth to render at.
	 * @return	A collection of Meshes to render to represent the ship.
	 */
	public ArrayList<Mesh> toMeshes(float depth) {
		ArrayList<Mesh> ret = new ArrayList<Mesh>();
		if(alive) {
			Mesh m = new Mesh(true,4,4,
					new VertexAttribute(Usage.Position,3,"a_position"),
					new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
			m.setVertices(new float[] {rect.x, rect.y, depth, color.toFloatBits(),
					   (rect.x+rect.width), rect.y, depth, color.toFloatBits(),
					   rect.x, (rect.y+rect.height), depth, color.toFloatBits(),
					   (rect.x+rect.width), (rect.y+rect.height), depth, color.toFloatBits() });
			m.setIndices(new short[] {0,1,2,3});
			ret.add(m);
		}
		else if(timeSinceDeath < deathAnimationTime){
			for(Rectangle r : deathRects) {
				Mesh m = new Mesh(true,4,4,
						new VertexAttribute(Usage.Position,3,"a_position"),
						new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
				m.setVertices(new float[] {r.x, r.y, depth, color.toFloatBits(),
						   (r.x+r.width), r.y, depth, color.toFloatBits(),
						   r.x, (r.y+r.height), depth, color.toFloatBits(),
						   (r.x+r.width), (r.y+r.height), depth, color.toFloatBits() });
				m.setIndices(new short[] {0,1,2,3});
				ret.add(m);
			}
		}
		return ret;
	}
	
	/**
	 * Returns the number of the player controlling the ship.
	 * @return	The number of the player controlling the ship.
	 */
	public int getPlayerNumber() {return playerNumber;}
	
	/**
	 * Returns the Color used to render the ship.
	 * @return The Color used to render the ship.
	 */
	public Color getColor() {return new Color(color);}
	
	/**
	 * Returns the number of the currently active weapon.
	 * @return	The number o the  currently active weapon.
	 */
	public int getWeaponMode () { return weaponMode;}
	
	/**
	 * Returns the KeyBindings the ship uses for controls.
	 * @return the KeyBindings the ship uses for controls.
	 */
	public KeyBindings getKeyBindings() {return keyBindings;}
	
	/**
	 * Calls fire() on the currently active weapon, and adds any resulting Shots to the shots ArrayList.
	 * Note: it is the responsibility of the Weapon to keep track of rate of fire limitations, 
	 * this method will be called whether the weapon is ready or not.
	 * @param shots	An ArrayList of all the Shots currently in the World.
	 */
	private void fire(ArrayList<Shot> shots) {
		if(weaponMode < weapons.length && weaponMode > 0)
			shots.addAll(weapons[weaponMode].fire(this));
		else
			shots.addAll(weapons[0].fire(this));
	}
	
	public boolean checkCrash(Ship that) {
		if(this.rect.overlaps(that.getRect()))
			return true;
		else
			return false;
	}
}

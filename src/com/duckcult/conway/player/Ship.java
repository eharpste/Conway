package com.duckcult.conway.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.duckcult.conway.Conway;
import com.duckcult.conway.weapons.BeamWeapon;
import com.duckcult.conway.weapons.QuadShotWeapon;
import com.duckcult.conway.weapons.RapidFireWeapon;
import com.duckcult.conway.weapons.Shot;
import com.duckcult.conway.weapons.SpiralWeapon;
import com.duckcult.conway.weapons.StandardWeapon;
import com.duckcult.conway.weapons.TrippleShotWeapon;
import com.duckcult.conway.weapons.Weapon;

public class Ship {
	private static Texture texture;
	private static int numShips = 0;
	public static Array<Ship> ships = new Array<Ship>(true,2);

	public static void setGlobalTexture (Texture text) {
		texture = text;
	}
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
	private float shardSpeed = 20;
	/**
	 * The time delay between when the player is allowed to switch weapons.
	 * This isn't meant to be a balance hindrance its mainly to prevent the change weapon button from being spammed by the render loop.
	 */
	private float weaponModeSwitchTime = .2f;
	/**
	 * The time since the player last switch weapons.
	 */
	private float timeSinceWeaponSwitch = 0.0f;
	
	public int score = 0;
	
	/**
	 * Creates the standard single player ship with the given size.
	 * @param size	The rectangle size of the desired ship.
	 */
	public Ship (float size) {
		alive = true;
		rect = new Rectangle(Conway.screenWidth/2-size/2,size*4,size,size);	
		xv = 50f;
		yv = 50f;
		color = Color.RED;
		playerNumber = numShips++;
		keyBindings = KeyBindings.WASD_QE_SPACE;
		weapons = new Weapon [6];
		weapons[0]= new StandardWeapon();
		weapons[1]= new RapidFireWeapon();
		weapons[2]= new TrippleShotWeapon();
		weapons[3]= new QuadShotWeapon();
		weapons[4]= new BeamWeapon();
		weapons[5]= new SpiralWeapon();
		deathRects = new Rectangle[4];
		deathRects[0] = new Rectangle(0,0,size/2,size/2);
		deathRects[1] = new Rectangle(0,0,size/2,size/2);
		deathRects[2] = new Rectangle(0,0,size/2,size/2);
		deathRects[3] = new Rectangle(0,0,size/2,size/2);
		ships.add(this);
	}
	
	/**
	 * Creates a new ship.
	 * This version is intended for multiplayer.
	 * @param size			The rectangle size of the ship.
	 * @param playerNumber	The designated player number
	 * @param playerColor	The player's color.
	 * @param keyBindings	The keyBindings that the player will use.
	 */
	public Ship(float size, Color playerColor, KeyBindings keyBindings){
		this(size);
		color = playerColor;
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
	public  void update(float deltaTime, Array<Shot> shots) {
		if(alive){
			for(Weapon w : weapons) {
				w.update(deltaTime);
			}
			if(timeSinceWeaponSwitch <= weaponModeSwitchTime) 
				timeSinceWeaponSwitch +=deltaTime;
			if(Gdx.input.isKeyPressed(keyBindings.prevWeapon()) && timeSinceWeaponSwitch > weaponModeSwitchTime) {
				prevWeapon();
				timeSinceWeaponSwitch = 0.0f;
			}
			if(Gdx.input.isKeyPressed(keyBindings.nextWeapon()) && timeSinceWeaponSwitch > weaponModeSwitchTime) {
				nextWeapon();
				timeSinceWeaponSwitch = 0.0f;
			}
			if(Gdx.input.isKeyPressed(keyBindings.up())  && rect.y < Conway.screenHeight) {
				rect.y += deltaTime * yv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.left()) && rect.x > 0){
				rect.x -= deltaTime * xv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.down()) && rect.y > 0){
				rect.y -= deltaTime * yv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.right()) && rect.x < Conway.screenWidth) {
				rect.x += deltaTime * xv;
			}
			if(Gdx.input.isKeyPressed(keyBindings.fire())) {
				fire(shots);
			}
			if(!weapons[weaponMode].hasAmmo())
				prevWeapon();
		}
		else if(timeSinceDeath < deathAnimationTime) {
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
	
	private void nextWeapon() {
		weaponMode++;
		while(!weapons[weaponMode].hasAmmo()) {
			weaponMode++;
			if(weaponMode == weapons.length)
				weaponMode = 0;
		}
	}
	
	private void prevWeapon() {
		if(weaponMode==0)
			weaponMode = weapons.length-1;
		else
			weaponMode--;
		while(!weapons[weaponMode].hasAmmo()) {
			weaponMode--;
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
		if(!alive)
			return;
		alive = false;
		deathRects[0].x = rect.x;
		deathRects[0].y = rect.y;
		deathRects[1].x = rect.x + rect.width / 2f;
		deathRects[1].y = rect.y;
		deathRects[2].x = rect.x;
		deathRects[2].y = rect.y + rect.height / 2f;
		deathRects[3].x = rect.x + rect.width / 2f;
		deathRects[3].y = rect.y + rect.height / 2f;
		timeSinceDeath = 0.0f;
	}
	
	/**
	 * Respawns the Ship at the given postion.
	 * @param position
	 */
	public void respawn(Vector2 position) {
		this.respawn();
		/*rect.x=position.x;
		rect.y=position.y;*/
	}
	
	/**
	 * Respawns the Ship.
	 * Sets alive to true, timeSinceWeaponSwitch to 0 and weaponMode to 0.
	 */
	public void respawn() {
		rect.x=Conway.screenWidth/2-rect.width/2;
		rect.y=rect.width*4;	
		alive = true;
		//deathRects = null;
		timeSinceWeaponSwitch = 0.0f;
		weaponMode = 0;
	}
	
	/**
	 * Returns a collection of Meshes to render the ship.
	 * Normally this is a collection of 1, when the ship is alive but there are 4 if the ship is dead.
	 * @param depth The depth to render at.
	 * @return	A collection of Meshes to render to represent the ship.
	 */
	public Array<Mesh> toMeshes(float depth) {
		Array<Mesh> ret = new Array<Mesh>();
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
	private void fire(Array<Shot> shots) {
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
	
	public void draw(SpriteBatch batch) {
		if(alive) {
			batch.setColor(color);
			batch.draw(texture, rect.x, rect.y,rect.width,rect.height);
			batch.setColor(Color.WHITE);
		}
		else if(timeSinceDeath < deathAnimationTime) {
			batch.setColor(color);
			for(Rectangle r : deathRects) {
				batch.draw(texture, r.x, r.y, r.width, r.height);
			}
			batch.setColor(Color.WHITE);
		}
	}
	
	public void setScore(int score) {
		this.score=score;
	}
	
	public void addScore(int score) {
		this.score+=score;
	}
	
	public int getScore() {
		return this.score;
	}
}

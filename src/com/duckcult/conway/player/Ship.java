package com.duckcult.conway.player;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.player.weapons.BeamWeapon;
import com.duckcult.conway.player.weapons.RapidFireWeapon;
import com.duckcult.conway.player.weapons.StandardWeapon;
import com.duckcult.conway.player.weapons.Shot;
import com.duckcult.conway.player.weapons.TrippleShotWeapon;
import com.duckcult.conway.player.weapons.Weapon;

public class Ship {
	private boolean alive = true;
	private Rectangle rect;
	private float xv,yv;
	private Color color;
	private Rectangle [] deathRects = null;
	private Weapon [] weapons;
	private int playerNumber;
	private KeyBindings keyBindings;
	
	private int weaponMode = 0;
	
	private float timeSinceDeath = 0.0f;
	private float deathAnimationTime = 1.25f;
	private float shardSpeed = .075f;
	
	private float weaponModeSwitchTime = 1.0f;
	private float timeSinceWeaponSwitch = 0.0f;
	
	public Ship (float size) {
		alive = true;
		rect = new Rectangle(0.0f-size,-.75f-size,size,size);	
		xv = .5f;
		yv = .5f;
		color = Color.RED;
		playerNumber = 1;
		keyBindings = KeyBindings.WASD_QE_SPACE;
		weapons = new Weapon [4];
		weapons[0]= new StandardWeapon();
		weapons[1]= new RapidFireWeapon();
		weapons[2]= new TrippleShotWeapon();
		weapons[3]= new BeamWeapon();
	}
	
	public Ship(float size, int playerNumber, Color playerColor, KeyBindings keyBindings){
		this(size);
		color = playerColor;
		this.playerNumber = playerNumber;
		this.keyBindings = keyBindings;
	}
	
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
	
	public Rectangle getRect() {return rect;}
	
	public void alterPostion(float deltaX, float deltaY) {
		rect.x += deltaX;
		rect.y += deltaY;
	}
	
	public void setVelocity(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void kill() {
		alive = false;
		deathRects = new Rectangle[4];
		deathRects[0] = new Rectangle(rect.x,rect.y,rect.width/2f,rect.height/2f);
		deathRects[1] = new Rectangle(rect.x+rect.width/2f, rect.y, rect.width/2f, rect.height/2f);
		deathRects[2] = new Rectangle(rect.x, rect.y+rect.height/2f,rect.width/2f,rect.height/2f);
		deathRects[3] = new Rectangle(rect.x+rect.width/2f,rect.y+rect.height/2f,rect.width/2f,rect.height/2f);
	}
	
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
	
	public int getPlayerNumber() {return playerNumber;}
	
	public Color getColor() {return new Color(color);}
	
	public int getWeaponMode () { return weaponMode;}
	
	private void fire(ArrayList<Shot> shots) {
		switch(weaponMode) {
			case 1:
				shots.addAll(weapons[1].fire(this));
				break;
			case 2:
				shots.addAll(weapons[2].fire(this));
				break;
			case 3:
				shots.addAll(weapons[3].fire(this));
			case 0:
			default:
				shots.addAll(weapons[0].fire(this));
				break;
					
		}
	}
}

package com.duckcult.conway.player;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;

public class Ship {
	private boolean alive = true;
	private Rectangle rect;
	private float xv,yv;
	private Color color = Color.RED;
	private Rectangle [] deathRects = null; 
	
	private float timeSinceDeath = 0.0f;
	private float deathAnimationTime = 1.25f;
	private float shardSpeed = .075f;
	/**
	 * The number of seconds to wait between shots.
	 */
	private float rofDelay = .25f;
	/**
	 * the number of seconds since the last shot was fired.
	 */
	private float timeSinceFire;
	
	public Ship (float size) {
		alive = true;
		rect = new Rectangle(0.0f-size,-.75f-size,size,size);	
		xv = .5f;
		yv = .5f;
		timeSinceFire = rofDelay;
	}
	
	public  void update(float deltaTime, ArrayList<Weapon> weapons) {
		if(alive){
			timeSinceFire += deltaTime;
			if((Gdx.input.isKeyPressed(Input.Keys.W) || (Gdx.input.isKeyPressed(Input.Keys.UP)))  && rect.y < 1 - rect.height) {
				rect.y += deltaTime * yv;
			}
			if((Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) && rect.x > -1){
				rect.x -= deltaTime * xv;
			}
			if((Gdx.input.isKeyPressed(Input.Keys.S) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))) && rect.y > -1){
				rect.y -= deltaTime * yv;
			}
			if((Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) && rect.x < 1 - rect.width) {
				rect.x += deltaTime * xv;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && timeSinceFire > rofDelay) {
			//	System.out.println("FIRE!");
				weapons.add(new BasicShot(this.rect));
				timeSinceFire = 0.0f;
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
}

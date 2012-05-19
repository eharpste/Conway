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
			System.out.println("FIRE!");
			weapons.add(new BasicShot(this.rect));
			timeSinceFire = 0.0f;
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
	
	public Mesh toMesh(float depth) {
		Mesh m = new Mesh(true,4,4,
				new VertexAttribute(Usage.Position,3,"a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		m.setVertices(new float[] {rect.x, rect.y, depth, color.toFloatBits(),
				   (rect.x+rect.width), rect.y, depth, color.toFloatBits(),
				   rect.x, (rect.y+rect.height), depth, color.toFloatBits(),
				   (rect.x+rect.width), (rect.y+rect.height), depth, color.toFloatBits() });
		m.setIndices(new short[] {0,1,2,3});
		return m;
	}
}

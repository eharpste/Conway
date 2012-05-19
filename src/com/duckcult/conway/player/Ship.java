package com.duckcult.conway.player;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Ship {
	private boolean alive = true;
	private float x,y;
	private float xv,yv;
	private float size;
	private Color color = Color.RED;
	
	/**
	 * The number of seconds to wait between shots.
	 */
	private float rofDelay = .25f;
	/**
	 * the number of seconds since the last shot was fired.
	 */
	private float timeSinceFire;
	
	public Ship () {
		alive = true;
		x = 0.0f;
		y = -.75f;
		size = .07f;
		xv = .5f;
		yv = .5f;
		timeSinceFire = rofDelay;
	}
	
	public  void update(float deltaTime, ArrayList<Weapon> weapons) {
		timeSinceFire += deltaTime;
		if((Gdx.input.isKeyPressed(Input.Keys.W) || (Gdx.input.isKeyPressed(Input.Keys.UP)))  && y < 1 - size) {
			y += deltaTime * yv;
		}
		if((Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) && x > -1 + size) {
			x -= deltaTime * xv;
		}
		if((Gdx.input.isKeyPressed(Input.Keys.S) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))) && y > -1 + size){
			y -= deltaTime * yv;
		}
		if((Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) && x < 1 - size) {
			x += deltaTime * xv;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && timeSinceFire > rofDelay) {
			System.out.println("FIRE!");
			weapons.add(new BasicShot(x,y));
			timeSinceFire = 0.0f;
		}
	}
	
	public void alterPostion(float deltaX, float deltaY) {
		x += deltaX;
		y += deltaY;
	}
	
	public void setVelocity(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
	}
	
	public Mesh toMesh(float depth) {
		float l = x - size/2;
		float b = y - size/2;
		float r = x + size/2;
		float t = y + size/2;
		Mesh m = new Mesh(true,4,4,
				new VertexAttribute(Usage.Position,3,"a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		m.setVertices(new float[] {l, b, depth, color.toFloatBits(),
								   r, b, depth, color.toFloatBits(),
								   l, t, depth, color.toFloatBits(),
								   r, t, depth, color.toFloatBits() });
		m.setIndices(new short[] {0,1,2,3});
		return m;
	}
}

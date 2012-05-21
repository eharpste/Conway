package com.duckcult.conway.player.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.gol.FastBoard;

public class BasicShot extends Shot {
	public static final float DEFAULT_SPEED = .5f;
	
	/**
	 * Creates a new BasicShot that is half the size of its origin and moves up at the default speed.
	 * @param origin
	 */
	public BasicShot (Rectangle origin) {			
		this.rect = new Rectangle(origin.x+origin.width/4f, origin.y+origin.height, origin.width/2, origin.height/2);
		vx = 0.0f;
		vy = DEFAULT_SPEED;
		color = Color.WHITE;
	}
	
	/**
	 * Creates a new BasicShot that is half the size of its origin and moves at the given velocities.
	 * @param vx
	 * @param vy
	 * @param origin
	 */
	public BasicShot(float vx, float vy, Rectangle origin) {
		this.rect = new Rectangle(origin.x+origin.width/4f, origin.y+origin.height, origin.width/2, origin.height/2);
		this.vx = vx;
		this.vy = vy;
		color = Color.WHITE;
	}
	
	public BasicShot(float x, float y, float xv, float yv, Rectangle origin) {
		this.rect = new Rectangle (x, y, origin.width/2f, origin.height/2f);
		this.vx = xv;
		this.vy = yv;
		color = Color.WHITE;
	}
	
	@Override
	public	void update(float deltaTime) {
		rect.y+=vy*deltaTime;
		rect.x+=vx*deltaTime;
	}

	@Override
	public boolean hit(FastBoard board) {
		return board.killOverlapCell(rect);
	}

	@Override
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

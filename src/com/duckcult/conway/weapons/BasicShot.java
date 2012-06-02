package com.duckcult.conway.weapons;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.player.Ship;

public class BasicShot extends Shot {
	/**
	 * The default speed of BasicShots.
	 */
	public static final float DEFAULT_SPEED = .5f;
	/**
	 * A faster default speed for BasicShots.
	 */
	public static final float DEFAULT_FAST = .75f;
	
	/**
	 * Creates a new BasicShot that is half the size of its origin and moves up at the default speed.
	 * @param origin
	 */
	public BasicShot(Ship origin) {
		this.rect = new Rectangle(origin.getRect().x+origin.getRect().width/4f, origin.getRect().y+origin.getRect().height, origin.getRect().width/2, origin.getRect().height/2);
		this.velocity = new Vector2(0.0f,DEFAULT_SPEED);
		setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
	}
	
	/**
	 * Creates a new BasicShot that is half the size of its origin and moves at the given velocities.
	 * @param vx
	 * @param vy
	 * @param origin
	 */
	public BasicShot(float vx, float vy, Ship origin) {
		this.rect = new Rectangle(origin.getRect().x+origin.getRect().width/4f, origin.getRect().y+origin.getRect().height, origin.getRect().width/2, origin.getRect().height/2);
		this.velocity = new Vector2(vx,vy);
		setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
	}
	
	/**
	 * Creates a new BasicShot that is half the size of its origin, starts at the given position, and moves at the given velocities.
	 * @param x
	 * @param y
	 * @param vx
	 * @param vy
	 * @param origin
	 */
	public BasicShot(float x, float y, float vx, float vy, Ship origin) {
		this.rect = new Rectangle(x, y, origin.getRect().width/2, origin.getRect().height/2);
		this.velocity = new Vector2(vx,vy);
		setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
	}
	
	/**
	 * Creates a new BasicShot that is half the size of its origin, and moves at the given velocity.
	 * @param velocity
	 * @param origin
	 */
	public BasicShot(Vector2 velocity, Ship origin) {
		this.rect = new Rectangle(origin.getRect().x+origin.getRect().width/4f, origin.getRect().y+origin.getRect().height, origin.getRect().width/2, origin.getRect().height/2);
		this.velocity = velocity;
		setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
	}
	
	/**
	 * Creates a new BasicShot that is half the size of its origin, starts at the given position, and moves at the given velocity.
	 * @param startPosition
	 * @param velocity
	 * @param origin
	 */
	public BasicShot(Vector2 startPosition, Vector2 velocity, Ship origin) {
		this.rect = new Rectangle(startPosition.x, startPosition.y, origin.getRect().width/2, origin.getRect().height/2);
		this.velocity = velocity;
		setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
	}
	
	@Override
	/**
	 * Updates the Shot's position by its velocities multiplied by the deltaTime.
	 */
	public	void update(float deltaTime) {
		rect.y+=velocity.y*deltaTime;
		rect.x+=velocity.x*deltaTime;
	}

	@Override
	/**
	 * Calls killOverlapedCell in the FastBoard class and returns true is successful.
	 */
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

	@Override
	public boolean hit(Ship ship) {
		if(ship.getRect().overlaps(rect) && ship.getPlayerNumber() != this.originPlayer && ship.isAlive()) {
			ship.kill();
			return true;
		}
		return false;
	}
}

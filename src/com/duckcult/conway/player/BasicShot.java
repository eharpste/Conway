package com.duckcult.conway.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.duckcult.conway.gol.Board;

public class BasicShot extends Weapon {
	public BasicShot (float x, float y) {
		this.x = x;
		this.y = y;
		vx = 0.0f;
		vy = .5f;
		color = Color.WHITE;
		size = .04f;
	}
	
	@Override
	public	void update(float deltaTime) {
		y+=vy*deltaTime;
	}

	@Override
	public boolean hit(Board board) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
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

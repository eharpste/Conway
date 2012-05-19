package com.duckcult.conway.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.gol.Board;

public class BasicShot extends Weapon {
	public BasicShot (Rectangle origin) {			
		this.rect = new Rectangle(origin.x+origin.width/4f, origin.y+origin.height, origin.width/2, origin.height/2);
		vx = 0.0f;
		vy = .5f;
		color = Color.WHITE;
	}
	
	@Override
	public	void update(float deltaTime) {
		rect.y+=vy*deltaTime;
	}

	@Override
	public boolean hit(Board board) {
		// TODO Auto-generated method stub
		return false;
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

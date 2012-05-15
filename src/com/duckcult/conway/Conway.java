package com.duckcult.conway;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.duckcult.conway.gol.Board;
import com.duckcult.conway.gol.CellProfile;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.screens.ConwayScreen;

/**
 * The main game logic.
 * See documentation at: http://code.google.com/p/libgdx/ for how the Gdx library works.
 * @author eharpste
 *
 */
public class Conway extends Game {
	//private Mesh squareMesh;
	//private Texture texture;
	//private SpriteBatch spriteBatch;
	private Camera camera;
	//private Mesh nearSquare; 
	
	
	private FastBoard gol;
	private FastBoard background;
	
	@Override
	public void create() {
		gol = new FastBoard(25,21,CellProfile.NORMAL,0.1f);
		//gol = FastBoard.emptyBoard(25, 21);
		//background= new FastBoard(120,120,CellProfile.BACKGROUND,.8,.01f);
		//meshes = gol.toMeshes(-1);
		//System.out.println("meshes.length = "+meshes.size());
		System.out.println("Board height: "+gol.getHeight() + "Board width: "+gol.getWidth());
		//System.out.println("Board:\n"+gol.toString());
		/*
		if(squareMesh == null) {
			squareMesh = new Mesh(true,4,4,
					new VertexAttribute(Usage.Position,3,"a_position"),
					new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
			
			squareMesh.setVertices(new float[] {0, -.5f, -4, Color.toFloatBits(128, 0, 0, 255),
										   1, -.5f, -4, Color.toFloatBits(192, 0, 0, 255),
											 0,  .5f, -4, Color.toFloatBits(192, 0, 0, 255),
											 1,.5f,-4,Color.toFloatBits(255, 0, 0, 255)
											 });
			
			squareMesh.setIndices(new short[] {0,1,2,3});
		}
		
		if(nearSquare == null) {
			nearSquare = new Mesh(true,4,4,
					new VertexAttribute(Usage.Position, 3, "a_position"),
					new VertexAttribute(Usage.ColorPacked,4,"a_color"));
			
			nearSquare.setVertices(new float[] {
					-1,-.5f, -1.1f, Color.toFloatBits(0, 0, 128, 255),
					0,-.5f,-1.1f, Color.toFloatBits(0, 0, 192, 255),
					-1,.5f,-1.1f, Color.toFloatBits(0, 0, 192, 255),
					0,.5f,-1.1f,Color.toFloatBits(0, 0, 255, 255)
					});
			
			nearSquare.setIndices(new short[] {0,1,2,3});
		}*/
		
		//texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
		//spriteBatch = new SpriteBatch();
		//setScreen to current Main Menu
		//load up and play music?
	}

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
	}

	private float t2 = 0f;
	private float move = .5f;
	
	private float time = 0f;
	@Override
	public void render() {
		//System.out.println(Gdx.graphics.getDeltaTime());
		if(Gdx.input.isTouched()) {
		//	Gdx.graphics.
			gol.check(Gdx.input.getX(),Gdx.input.getY(),800,600);
		}
		gol.advanceBoard(Gdx.graphics.getDeltaTime());
		gol.update(Gdx.graphics.getDeltaTime());
		//background.advanceBoard(Gdx.graphics.getDeltaTime());
		if(time > 5f) {
	//		gol.update();
			time = 0f;
		}
		/*if(t2>4f){
			background.update();
			t2 = 0f;
		}*/
		camera.update();
		camera.apply(Gdx.gl10);
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	/*	for(Mesh m : background.toMeshes(-1.5f)){
			m.render(GL10.GL_TRIANGLE_STRIP,0,4);
			m.dispose();
		}*/
		for(Mesh m : gol.toMeshes(-1)) {
			m.render(GL10.GL_TRIANGLE_STRIP,0,4);
			m.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float)width/(float)height;
		camera = new PerspectiveCamera(67,2f * aspectRatio, 2f);
	}

	@Override
	public void resume() {
	}

	public ConwayScreen getScreen() {
		return (ConwayScreen)super.getScreen();
	}
}

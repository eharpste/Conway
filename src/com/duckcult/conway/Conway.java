package com.duckcult.conway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	private SpriteBatch batch;
	private Camera camera;
	//private Mesh nearSquare; 
	
	private World world;
	
	public static int screenWidth = 800;
	public static int screenHeight = 480;
	public static float boardScrollSpeed = 25;
	
	@Override
	public void create() {
		world = new World(new FastBoard(25,21,screenWidth,CellProfile.NORMAL,boardScrollSpeed));
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		((OrthographicCamera) camera).setToOrtho(false,screenWidth,screenHeight);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void render() {
		if(Gdx.input.isTouched()) {
			world.getBoard().checkScreenSpace(Gdx.input.getX(),Gdx.input.getY(),screenWidth,screenHeight);
		}
		world.update(Gdx.graphics.getDeltaTime());

		camera.update();
		camera.apply(Gdx.gl10);
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		world.draw(batch);
		batch.end();
		/*for(Mesh m : world.toMeshes(-1)) {
			m.render(GL10.GL_TRIANGLE_STRIP,0,4);
			m.dispose();
		}*/
	}

	@Override
	public void resize(int width, int height) {
		/*screenWidth = width;
		screenHeight = height;
		float aspectRatio = (float)width/(float)height;
		camera = new OrthographicCamera(2f * aspectRatio, 2f);
		//camera = new PerspectiveCamera(67,2f * aspectRatio, 2f);
*/	}

	@Override
	public void resume() {
	}

	public ConwayScreen getScreen() {
		return (ConwayScreen)super.getScreen();
	}
}

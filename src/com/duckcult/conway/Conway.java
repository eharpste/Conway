package com.duckcult.conway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.gol.CellProfile;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.gol.NeoBoard;
import com.duckcult.conway.player.Ship;
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
	
	private BitmapFont font;
	private float textHeight;
	private float textWidth;
	
	public static int screenWidth = 800;
	public static int screenHeight = 600;
	public static float boardScrollSpeed = 25;
	
	@Override
	public void create() {
		
		
		world = new World(new NeoBoard(25,21,new Rectangle(0,0,(float)screenWidth,(float)screenHeight),new Vector2(0,boardScrollSpeed),null,CellProfile.NORMAL,.5,true));
		batch = new SpriteBatch();
		font = new BitmapFont();
		textWidth = font.getBounds("0000").width;
		textHeight = font.getBounds("0000").height;
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
			world.getBoard().flipCell(Gdx.input.getX(),Gdx.input.getY());
		}
		world.update(Gdx.graphics.getDeltaTime());

		camera.update();
		camera.apply(Gdx.gl10);
		
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		world.draw(batch);
		font.draw(batch, formatScore(Ship.ships.get(0).getScore()), 0, screenHeight-textHeight);
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
	
	public String formatScore(int score) {
		if(score==0)
			return "0000";
		else if(score<10)
			return "000"+score;
		else if(score < 100)
			return "00"+score;
		else if(score < 1000)
			return score+"";
		else
			return "9999";
	}

}

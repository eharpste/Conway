package com.duckcult.conway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.player.KeyBindings;
import com.duckcult.conway.player.Ship;
import com.duckcult.conway.weapons.BasicShot;
import com.duckcult.conway.weapons.Shot;

public class World {
	private static final Color [] playerColors = {Color.RED, Color.BLUE, new Color(1,1,0,1), new Color (1,0,1,1), new Color(0,1,1,1)};
	//private static final KeyBindings [] bindings = {KeyBindings.WASD_QE_LSHIFT, KeyBindings.IJKL_UO_SLASH};
	private FastBoard board;
	private Array<Shot> shots;
	private Array<Ship> players;
	private Ship player1;
	//private Ship player2;
	private Rectangle checkRect = new Rectangle(0,0,Conway.screenWidth,Conway.screenHeight);
	
	private float respawnDelay = 1.5f;
	private float timeSinceRespawn = 0.0f;
	
	/**
	 * Creates a new World based around the supplied FastBoard.
	 * The player ship will be set to be 70% the size of the Board's squareSize.
	 * @param board
	 */
	public World (FastBoard board) {
		this.board = board;
		Texture defaultText = new Texture(Gdx.files.internal("assets/88White.png"));
		board.setCellTexture(defaultText);
		Ship.setGlobalTexture(defaultText);
		BasicShot.setGlobalTexture(defaultText);
		spawn2Players();
		//players.add(spawnPlayer(1,Color.RED));
		//players.add(spawnPlayer(2,Color.BLUE));
		shots = new Array<Shot>(false, 10);
	}
	
	/**
	 * Runs an update of the World for a frame tick.
	 * The Update propagates in this order: 
	 * 1 - Call update() on the FastBoard
	 * 2 - Call advanceBoard() on the FastBoard
	 * 3 - Check if the player overlaps a living cell and if so kill it.
	 * 4 - Call update() on the player
	 * 5 - For each shot:
	 * 	a - If the shot is outside the world it is removed.
	 * 	b - Call update() on the Shot.
	 *  c - Call hit() on the Shot.
	 *  d - If the shot is successful remove it.
	 * 6 - If the player is dead and 'R' is pressed, respawn the player. 
	 * @param deltaTime
	 */
	public void update(float deltaTime) {
		board.update(deltaTime);
		board.advanceBoard(deltaTime);
		if(player1.isAlive() && board.getOverlappedCell(player1.getRect()).alive)
			player1.kill();

		player1.update(deltaTime, shots);
		
		for(int i = 0; i<shots.size; i++) {
			if(!checkRect.contains(shots.get(i).getRect()))
				shots.removeIndex(i);
			else {
				shots.get(i).update(deltaTime);
				if(shots.get(i).hit(board)/* || shots.get(i).hit(player1) || shots.get(i).hit(player2)*/) {
					Ship.ships.get(shots.get(i).getOriginPlayer()).addScore(1);
					shots.removeIndex(i);
				}
			}
		}
		if(timeSinceRespawn < respawnDelay)
			timeSinceRespawn += deltaTime;
		else {
			if(Gdx.input.isKeyPressed(Input.Keys.R)){
				if(!player1.isAlive() && timeSinceRespawn >= respawnDelay) {
					reSpawnPlayer(player1);
					timeSinceRespawn = 0.0f;
				}
				
			}
		}
		
		/*player2.update(deltaTime, shots);
		if(player1.checkCrash(player2)) {
			player1.kill();
			player2.kill();
		}*/
		/*if(player1.isAlive() && board.getOverlappedCell(player1.getRect()).alive)
			player1.kill();
		player1.update(deltaTime,shots);
		*/
		/*for(Ship p : players) {
			if(p.isAlive() && board.getOverlappedCell(p.getRect()).alive)
				p.kill();
			p.update(deltaTime, shots);*/
			/*for(Ship p2: players) {
				if(p.getRect().overlaps(p2.getRect())) {
					p.kill();
					p2.kill();
				}
				/*if(!player2.isAlive() && timeSinceRespawn >= respawnDelay) {
					reSpawnPlayer(player2);
					timeSinceRespawn = 0.0f;
				}
					/*	if(player2.isAlive() && board.getOverlappedCell(player2.getRect()).alive)
			player2.kill();
			}
		}*/
		
	}

	private void reSpawnPlayer(Ship oldShip){
		oldShip.respawn(/*new Vector2(0,-.75f)*/);
		board.makeSafeZone(player1.getRect().y+player1.getRect().getHeight()+50);
	}
	
	public Array<Mesh> toMeshes(float depth) {
		Array<Mesh> ret = board.toMeshes(depth);
		ret.addAll(player1.toMeshes(depth));
	//	ret.addAll(player2.toMeshes(depth));
		for(Shot w : shots) {
			ret.add(w.toMesh(depth));
		}
		return ret;
	}
	
	public void draw(SpriteBatch batch) {
		board.draw(batch);
		for(Shot s : shots) {
			s.draw(batch);
		}
		player1.draw(batch);
	//	player2.draw(batch);
	}
	
	private void spawn2Players() {
		//ArrayList<Ship> ret = new ArrayList<Ship>(2);
		player1 = new Ship(board.getSquareSize()*.7f,playerColors[0],KeyBindings.WASD_QE_SPACE);
		//player2 = new Ship(board.getSquareSize()*.7f,2,playerColors[1],KeyBindings.OKLSEMICOLON_IP_N);
		//player1.alterPosition(-.5f, 0);
		//player2.alterPosition(.5f,0);
		board.makeSafeZone(player1.getRect().y+player1.getRect().getHeight()+50);
		//return ret;
	}
	
	public FastBoard getBoard() {return board;}
	
	//public Ship getShip() {return player;}
	
	public Array<Ship> getPlayers() {return players;}
	
	public Array<Shot> getShots() {return shots;}
}

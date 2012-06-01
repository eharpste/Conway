package com.duckcult.conway;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.player.KeyBindings;
import com.duckcult.conway.player.Ship;
import com.duckcult.conway.player.weapons.Shot;

public class World2P {
	private static final Color [] playerColors = {Color.RED, Color.BLUE, new Color(1,1,0,1), new Color (1,0,1,1), new Color(0,1,1,1)};
	private static final KeyBindings [] bindings = {KeyBindings.WASD_QE_LSHIFT, KeyBindings.IJKL_UO_SLASH};
	private FastBoard board;
	private ArrayList<Shot> shots;
	private ArrayList<Ship> players;
	private Ship player1;
	private Ship player2;
	
	private float respawnDelay = 1.5f;
	private float timeSinceRespawn = 0.0f;
	
	/**
	 * Creates a new World based around the supplied FastBoard.
	 * The player ship will be set to be 70% the size of the Board's squareSize.
	 * @param board
	 */
	public World2P (FastBoard board) {
		this.board = board;
		spawn2Players();
		//players.add(spawnPlayer(1,Color.RED));
		//players.add(spawnPlayer(2,Color.BLUE));
		shots = new ArrayList<Shot>();
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
		if(player2.isAlive() && board.getOverlappedCell(player2.getRect()).alive)
			player2.kill();
		player1.update(deltaTime, shots);
		player2.update(deltaTime, shots);
		if(player1.checkCrash(player2)) {
			player1.kill();
			player2.kill();
		}
		/*for(Ship p : players) {
			if(p.isAlive() && board.getOverlappedCell(p.getRect()).alive)
				p.kill();
			p.update(deltaTime, shots);
			for(Ship p2: players) {
				if(p.getRect().overlaps(p2.getRect())) {
					p.kill();
					p2.kill();
				}
			}
		}*/
		Rectangle checkRect = new Rectangle(-1.1f,-1.1f,2.1f,2.1f);
		for(int i = 0; i<shots.size(); i++) {
			if(!checkRect.contains(shots.get(i).getRect()))
				shots.remove(i);
			else {
				shots.get(i).update(deltaTime);
				if(shots.get(i).hit(board) || shots.get(i).hit(player1) || shots.get(i).hit(player2))
					shots.remove(i);
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
				if(!player2.isAlive() && timeSinceRespawn >= respawnDelay) {
					reSpawnPlayer(player2);
					timeSinceRespawn = 0.0f;
				}
			}
		}
	}
	
	private Ship spawnPlayer(int playerNumber, Color playerColor, KeyBindings bindings) {
		Ship player = new Ship(board.getSquareSize()*.7f, playerNumber, playerColor,bindings);
		board.makeSafeZone(player.getRect().y+player.getRect().getHeight()+.3f);
		return player;
	}
	
	private void reSpawnPlayer(Ship oldShip){
		oldShip.respawn(new Vector2(0,-.75f));
		board.makeSafeZone(oldShip.getRect().y+oldShip.getRect().getHeight()+.3f);
	}
	
	public ArrayList<Mesh> toMeshes(float depth) {
		ArrayList<Mesh> ret = board.toMeshes(depth);
		ret.addAll(player1.toMeshes(depth));
		ret.addAll(player2.toMeshes(depth));
		for(Shot w : shots) {
			ret.add(w.toMesh(depth));
		}
		return ret;
	}
	
	private void spawn2Players() {
		//ArrayList<Ship> ret = new ArrayList<Ship>(2);
		player1 = new Ship(board.getSquareSize()*.7f,1,playerColors[0],KeyBindings.WASD_QE_LSHIFT);
		player2 = new Ship(board.getSquareSize()*.7f,2,playerColors[1],KeyBindings.IJKL_UO_SLASH);
		player1.alterPosition(-.5f, 0);
		player2.alterPosition(.5f,0);
		board.makeSafeZone(player1.getRect().y+player1.getRect().getHeight()+.3f);
		//return ret;
	}
	
	public FastBoard getBoard() {return board;}
	
	//public Ship getShip() {return player;}
	
	public ArrayList<Ship> getPlayers() {return players;}
	
	public ArrayList<Shot> getShots() {return shots;}
}

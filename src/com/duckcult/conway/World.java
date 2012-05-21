package com.duckcult.conway;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.player.Ship;
import com.duckcult.conway.player.weapons.Shot;

public class World {
	private FastBoard board;
	private Ship player;
	private ArrayList<Shot> shots;
	
	/**
	 * Creates a new World based around the supplied FastBoard.
	 * The player ship will be set to be 70% the size of the Board's squareSize.
	 * @param board
	 */
	public World (FastBoard board) {
		this.board = board;
		spawnPlayer();
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
		if(player.isAlive() && board.getOverlappedCell(player.getRect()).alive)
			player.kill();
		player.update(deltaTime, shots);
		Rectangle checkRect = new Rectangle(-1.1f,-1.1f,2.1f,2.1f);
		for(int i = 0; i<shots.size(); i++) {
			if(!checkRect.contains(shots.get(i).getRect()))
				shots.remove(i);
			else {
				shots.get(i).update(deltaTime);
				if(shots.get(i).hit(board))
					shots.remove(i);
			}
		}
		if(!player.isAlive() && Gdx.input.isKeyPressed(Input.Keys.R))
			spawnPlayer();
	}
	
	private void spawnPlayer() {
		player = new Ship(board.getSquareSize()*.7f);
		board.makeSafeZone(player.getRect().y+player.getRect().getHeight()+.3f);
	}
	
	public ArrayList<Mesh> toMeshes(float depth) {
		ArrayList<Mesh> ret = board.toMeshes(depth);
		ret.addAll(player.toMeshes(depth));
		for(Shot w : shots) {
			ret.add(w.toMesh(depth));
		}
		return ret;
	}
	
	public FastBoard getBoard() {return board;}
	
	public Ship getShip() {return player;}
	
	public ArrayList<Shot> getShots() {return shots;}
}

package com.duckcult.conway;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Mesh;
import com.duckcult.conway.gol.FastBoard;
import com.duckcult.conway.player.Ship;
import com.duckcult.conway.player.Weapon;

public class World {
	private FastBoard board;
	private Ship player;
	private ArrayList<Weapon> shots;
	
	public World (FastBoard board) {
		this.board = board;
		player = new Ship(board.getSquareSize()*.7f);
		shots = new ArrayList<Weapon>();
		board.makeSafeZone(player.getRect().y+player.getRect().getHeight()+.3f);
	}
	
	public void update(float deltaTime) {
		board.update(deltaTime);
		board.advanceBoard(deltaTime);
		if(player.isAlive() && board.overlapsLiving(player.getRect()))
			player.kill();
		player.update(deltaTime, shots);
		board.overlapsLiving(player.getRect());
		for(int i = 0; i<shots.size(); i++) {
			if(shots.get(i).getRect().y>1.5)
				shots.remove(i);
			else {
				shots.get(i).update(deltaTime);
				if(board.killOverlapCell(shots.get(i).getRect()))
					shots.remove(i);
			}
		}
		
	}
	
	public ArrayList<Mesh> toMeshes(float depth) {
		ArrayList<Mesh> ret = board.toMeshes(depth);
		ret.addAll(player.toMeshes(depth));
		for(Weapon w : shots) {
			ret.add(w.toMesh(depth));
		}
		return ret;
	}
	
	public FastBoard getBoard() {return board;}
	
	public Ship getShip() {return player;}
	
	public ArrayList<Weapon> getShots() {return shots;}
}

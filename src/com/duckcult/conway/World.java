package com.duckcult.conway;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.duckcult.conway.gol.Board;
import com.duckcult.conway.player.Ship;
import com.duckcult.conway.player.Weapon;

public class World {
	private Board background;
	private Board level;
	private Board enemies;
	private Ship ship;
	private ArrayList<Weapon> weapons;
	
	private int viewPortY,viewPortX,viewPortHeight,viewPortWidth;
	
	/**
	 * Generates the world based on a predefined map.
	 * @param level
	 * @param enemies
	 * @param background
	 * @param viewPortHeight
	 * @param viewPortWidth
	 * @throws FileNotFoundException
	 */
	public World(File level, File enemies, File background,int viewPortHeight,int viewPortWidth) throws FileNotFoundException {
		this.background = new Board(background);
		this.level = new Board(level);
		this.enemies = new Board(enemies);
		viewPortY=0;
		viewPortX=this.level.getWidth()/2+this.level.getWidth()%2;
		this.viewPortHeight = viewPortHeight;
		this.viewPortWidth = viewPortWidth;
	//	this.ship = new Ship(viewPortX,Ship.DEFAULT_START_POSTION);
		weapons = new ArrayList<Weapon>();
	}
	
	/**
	 * Generates a random world based on the provided dimensions.
	 * @param height
	 * @param width
	 * @param viewPortHeight
	 * @param viewPortWidth
	 */
	public World(int height, int width, int viewPortHeight, int viewPortWidth) {
		this.background = new Board(height, width);
		this.level = new Board(height,width);
		this.enemies = new Board(height,width);
		viewPortY=0;
		viewPortX=this.level.getWidth()/2+this.level.getWidth()%2;
		this.viewPortHeight = viewPortHeight;
		this.viewPortWidth = viewPortWidth;
	//	this.ship = new Ship(viewPortX,Ship.DEFAULT_START_POSTION);
		weapons = new ArrayList<Weapon>();
	}

	/**
	 * returns the background GOL layer.
	 * @return
	 */
	public Board getBackground() {
		return background;
	}

	/**
	 * returns the GOL layer that has the main obstacles to fly through.
	 * @return
	 */
	public Board getLevel() {
		return level;
	}

	/**
	 * returns the GOL layer that contains the enemies.
	 * @return
	 */
	public Board getEnemies() {
		return enemies;
	}
	
	/**
	 * updates the GOL boards, moves the ship and updates weapons.
	 */
	public void update() {
		level.update();
		enemies.update();
		background.update();
	//	handleInput();
		for (Weapon w : weapons) {
			w.update();
		}
	}
	
	/**
	 * Updates the GOL logic without updating the background in case we ever need to catch-up on the system.
	 */
	public void updateLite() {
		level.update();
		enemies.update();
	//	handleInput();
		for (Weapon w : weapons) {
			w.update();
		}
	}
	
	/**
	 * Moves the viewport up.
	 */
	public void advance() {
		viewPortY++;
	}
	
	/**
	 * Collision Detection
	 */
	public void check() {
		//if(level.checkCells(ship.getX(), ship.getY(), ship.getShape()) || enemies.checkCells(ship.getX(), ship.getY(), ship.getShape()))
			//ship.kill();
		for(Weapon w : weapons) {
			if(level.checkCells(w.getX(), w.getY(), w.getShape())){
				if(w.hit(level))
					weapons.remove(w);
			}
			if(enemies.checkCells(w.getX(), w.getY(), w.getShape())) {
				if(w.hit(enemies))
					weapons.remove(w);
			}
		}
	}
	
	/**
	 * Flattens the three board layers to a single 2D array of Colors.
	 * @return
	 */
	public Color [][] getViewPortToRender(){
		Color[][] image = new Color[viewPortHeight][viewPortWidth];
		int xPos = viewPortX-viewPortWidth/2-viewPortWidth%2;
		int yPos = viewPortY;
		for(int i = 0; i < viewPortHeight; i++) {
			for(int j = 0; j<viewPortWidth; j++) {
				Color cell;
				if(enemies.getCell(xPos, yPos).isAlive()) {
					cell = enemies.getCell(xPos, yPos).getColor();
				}
				else if(level.getCell(xPos,yPos).isAlive()) {
					cell = level.getCell(xPos, yPos).getColor();
				}
				else
					cell = background.getCell(xPos, yPos).getColor();
				image[i][j] = cell;
				xPos++;
			}
			yPos++;
		}
		return image;
	}
	
	/**
	 * Process keyboard input. Currently supports WASD and arrows for movement. SPACE bar currently fires a shot.
	 */
/*	public void handleInput() {
		if((Gdx.input.isButtonPressed(Input.Keys.W) || Gdx.input.isButtonPressed(Input.Keys.UP)) && ship.y < viewPortHeight) 
			ship.y++;
		if((Gdx.input.isButtonPressed(Input.Keys.A) || Gdx.input.isButtonPressed(Input.Keys.LEFT)) && ship.x > 0)
			ship.x--;
		if((Gdx.input.isButtonPressed(Input.Keys.S) || Gdx.input.isButtonPressed(Input.Keys.DOWN)) &&  ship.y > 0)
			ship.y--;
		if((Gdx.input.isButtonPressed(Input.Keys.D) || Gdx.input.isButtonPressed(Input.Keys.RIGHT)) && ship.x < viewPortWidth)
			ship.x++;
		if(Gdx.input.isButtonPressed(Input.Keys.SPACE)) {
			weapons.add(ship.fireShot());
		}
	}*/
}

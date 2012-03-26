package com.duckcult.conway.gol;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

public abstract class Cell {
	protected boolean alive;
	protected CellProfile rules;

	public abstract int getType();
	public abstract Color getColor();
	public abstract boolean isAlive();
	public abstract boolean wasAlive();
	public abstract void check(ArrayList<Cell> neighbors);
	public abstract void kill();
	public abstract void birth();
}
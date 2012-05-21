package com.duckcult.conway.gol.old;


import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.duckcult.conway.gol.CellProfile;

public abstract class OldCell {
	protected boolean alive;
	protected CellProfile rules;

	public abstract int getType();
	public abstract Color getColor();
	public abstract boolean isAlive();
	public abstract boolean wasAlive();
	public abstract void check(ArrayList<OldCell> neighbors);
	public abstract void kill();
	public abstract void birth();
}
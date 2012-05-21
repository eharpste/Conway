package com.duckcult.conway.gol;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

/**
 * The main Cell that is used in the Board Class.
 * Its rules can be altered based upon its CellProfile.
 * @author eharpste
 *
 */
public class BasicCell extends OldCell {	
	int age;
	Color liveColor;
	Color deadColor;
	boolean wasAlive;

	/**
	 * Creates a new dead Cell with the NORMAL Profile.
	 */
	public BasicCell(){
		alive = false;
		rules = CellProfile.NORMAL;
		liveColor = rules.liveColor;
		deadColor = rules.deadColor;
	}

	/**
	 * Creates a new cell with the NORMAL profile.
	 * @param live
	 */
	public BasicCell(boolean live) {
		rules = CellProfile.NORMAL;
		liveColor = rules.liveColor;
		deadColor = rules.deadColor;
		if (live)
			birth();
		else
			kill();
	}

	/**
	 * Creates a new cell with the given profile.
	 * @param live
	 * @param profile
	 */
	public BasicCell(boolean live, CellProfile profile) {
		this.rules = profile;
		liveColor = profile.liveColor;
		deadColor = profile.deadColor;
		if (live)
			birth();
		else
			kill();
	}

	public int getType() {
		return	rules.type;
	}

	/**
	 * Returns the cell color based on whether it is alive or not.
	 */
	public Color getColor() {
		return (alive ? liveColor : deadColor);
	}

	private void setType(CellProfile newType) {
		rules = newType;
		liveColor = rules.liveColor;
		deadColor = rules.deadColor;
	}
	
	/**
	 * Checks if the cell is alive.
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Checks if the cell was alive in the previous tick.
	 */
	public boolean wasAlive() {
		return wasAlive;
	}

	/**
	 * Checks if the cell was born this tick.
	 * @return
	 */
	public boolean wasBorn() {
		return (!wasAlive&&alive);
	}

	/**
	 * Checks if the cell just died this tick.
	 * @return
	 */
	public boolean justDied() {
		return (wasAlive&&!alive);
	}
	
	/**
	 * Checks the number of living neighbors of the cell and processes the appropriate life and death states based on
	 * the CellProfile rules.
	 */
	public void check(ArrayList<OldCell> neighbors) {
		wasAlive = alive;
		int liveNeighbors = countLiveNeighbors(neighbors);
		if (alive) {		
			if((liveNeighbors<rules.low || liveNeighbors> rules.high))
				growOld();
			else
				growYoung();
		}
		else if(!alive && liveNeighbors>0) {
			int mostComType = mostCommonNeighborType(neighbors);
			setType(CellProfile.getCellRules(mostComType));
			if(liveNeighbors==rules.birthReq)
				birth();
		}
			
	}

	/**
	 * Lowers the age of the cell and raises the alpha of its color.
	 */
	private void growYoung() {
		if(age>0) {
			age--;
			liveColor.a = (liveColor.a+.25f > 1.0f ? 1.0f : liveColor.a+.25f);
		}
	}
	
	/**
	 * Raises the age of the cell and lowers the alpha of its color.
	 * If the age of the cell goes beyond the death age of its rules it kills the cell.
	 */
	private void growOld() {
		age++;
		if (age >=rules.deathAge)
			kill();
		liveColor.a = (liveColor.a-.25f < 0.0f ? 0.0f : liveColor.a-.25f);
	}
	
	/**
	 * this kills the cell.
	 */
	public void kill() {
		alive = false;
		liveColor = rules.liveColor;
	}

	/**
	 * brings the cell to life.
	 */
	public void birth() {
		alive = true;
		age =0;
	}

	/**
	 * counts the number of life neighbors.
	 * @param neighbors
	 * @return
	 */
	private int countLiveNeighbors(ArrayList<OldCell> neighbors) {
		int count=0;
		for(OldCell c : neighbors) {
			if(c.isAlive())
				count++;
		}
		return count;
	}

	/**
	 * Determines the most common type of cell that was alive neighboring the cell.
	 * This is used in configurations of boards with multiple species to a board which spawn new cells based on the most common
	 * species around them.
	 * @param neighbors
	 * @return
	 */
	private int mostCommonNeighborType(ArrayList<OldCell> neighbors) {
		int counts [] = new int [CellProfile.NUMBER_OF_TYPES];
		for (OldCell c : neighbors) {
			if(c.wasAlive())
				counts[c.getType()]++;
		}
		int max=0;
		for (int i=0;i<counts.length;i++)
			if (counts[i]>counts[max])
				max=i;
		return max;
	}

	/**
	 * returns a string representation of the cell 1 for alive 0 for dead. Currently does nothing to denote the species.
	 */
	public String toString() {
		return (alive ? "1" : "0");
	}
}


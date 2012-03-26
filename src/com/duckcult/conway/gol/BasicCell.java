package com.duckcult.conway.gol;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;


public class BasicCell extends Cell {	
	int age;
	Color liveColor;
	Color deadColor;
	boolean wasAlive;

	public BasicCell(){
		alive = false;
		rules = CellProfile.NORMAL;
		liveColor = rules.liveColor;
		deadColor = rules.deadColor;
	}

	public BasicCell(boolean live) {
		rules = CellProfile.NORMAL;
		liveColor = rules.liveColor;
		deadColor = rules.deadColor;
		if (live)
			birth();
		else
			kill();
	}

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

	public Color getColor() {
		return (alive ? liveColor : deadColor);
	}

	private void setType(CellProfile newType) {
		rules = newType;
		liveColor = rules.liveColor;
		deadColor = rules.deadColor;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public boolean wasAlive() {
		return wasAlive;
	}

	public boolean wasBorn() {
		return (!wasAlive&&alive);
	}

	public boolean justDied() {
		return (wasAlive&&!alive);
	}

	public void check(ArrayList<Cell> neighbors) {
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

	private void growYoung() {
		if(age>0) {
			age--;
			liveColor.a = (liveColor.a+.25f > 1.0f ? 1.0f : liveColor.a+.25f);
		}
	}
	
	private void growOld() {
		age++;
		if (age >=rules.deathAge)
			kill();
		liveColor.a = (liveColor.a-.25f < 0.0f ? 0.0f : liveColor.a-.25f);
	}
	
	public void kill() {
		alive = false;
		liveColor = rules.liveColor;
	}

	public void birth() {
		alive = true;
		age =0;
	}

	private int countLiveNeighbors(ArrayList<Cell> neighbors) {
		int count=0;
		for(Cell c : neighbors) {
			if(c.isAlive())
				count++;
		}
		return count;
	}

	private int mostCommonNeighborType(ArrayList<Cell> neighbors) {
		int counts [] = new int [CellProfile.NUMBER_OF_TYPES];
		for (Cell c : neighbors) {
			if(c.wasAlive())
				counts[c.getType()]++;
		}
		int max=0;
		for (int i=0;i<counts.length;i++)
			if (counts[i]>counts[max])
				max=i;
		return max;
	}

	public String toString() {
		return (alive ? "1" : "0");
	}
}


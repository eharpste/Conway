package com.duckcult.conway.gol;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

public class CellProfile {
	//defines a set of type probabilities {normal,super,crowd,breeder}
	public static final int [] DEFAULT_TYPE_DISTROBUTION 	= {70,10,10,10};
	public static final int [] VANILLA_TYPE_DISTROBUTION 	= {100,0,0,0};
	public static final int [] ALL_SUPER_DISTROBUTION 		= {0,100,0,0};
	public static final int [] ALL_CROWD_DISTROBUTION 		= {0,0,100,0};
	public static final int [] ALL_BREED_DISTROBUTION 		= {0,0,0,100};

	public static final int NUMBER_OF_TYPES = 6;
	
	public static final int BASIC_TYPE 		= 0;
	public static final int SUPER_TYPE 		= 1;
	public static final int CROWD_TYPE 		= 2;
	public static final int BREED_TYPE 		= 3;
	public static final int ENEMY_TYPE 		= 4;
	public static final int BACKGROUND_TYPE = 5;
	
	public static final CellProfile NORMAL 		= new CellProfile(Color.GREEN,Color.BLACK,0);
	public static final CellProfile ENEMY 		= new CellProfile(new Color(1,.5f,.25f,1),Color.BLACK,4);
	public static final CellProfile BACKGROUND 	= new CellProfile(new Color(.6f,.6f,.6f,1),Color.BLACK,5);
	public static final CellProfile SUPER 		= new CellProfile(Color.BLUE,Color.BLACK,3,2,3,3,1);
	public static final CellProfile CROWD 		= new CellProfile(new Color(1,0,1,1),Color.BLACK,3,2,6,1,2);
	public static final CellProfile BREED 		= new CellProfile(new Color(1,1,0,1),Color.BLACK,1,2,3,1,3);
	
	Color liveColor;
	Color deadColor;
	int birthReq;
	int low;
	int high;
	int deathAge;
	int type;
	
	/**
	 * Creates a new CellProfile for cells that want to use special rules.
	 * @param liveColor
	 * @param deadColor
	 * @param birthReq
	 * @param lowCap
	 * @param highCap
	 * @param deathAge
	 * @param type
	 */
	CellProfile(Color liveColor, 
			Color deadColor, 
			int birthReq, 
			int lowCap, 
			int highCap, 
			int deathAge, 
			int type) {

		this.liveColor = liveColor;
		this.deadColor = deadColor;
		this.birthReq = birthReq;
		this.low = lowCap;
		this.high = highCap;
		this.deathAge = deathAge;
		this.type = type;
	}
	
	/**
	 * Creates a CellProfile for cells that use standard Conway rules.
	 * @param liveColor
	 * @param deadColor
	 * @param type
	 */
	CellProfile(Color liveColor, Color deadColor, int type) {
		this.liveColor = liveColor;
		this.deadColor = deadColor;
		this.type = type;
		this.birthReq = 3;
		this.low = 2;
		this.high = 3;
		this.deathAge = 1;
	}

	/**
	 * Returns a CellRules based on its type identifier.
	 * @param type
	 * @return
	 */
	public static CellProfile getCellRules(int type) {
		switch (type) {
			case 0:
				return NORMAL;
			case 1:
				return SUPER;
			case 2:
				return CROWD;
			case 3:
				return BREED;
			case 4:
				return ENEMY;
			case 5:
				return BACKGROUND;
			default:
				return NORMAL;
		}
	}

	/**
	 * Returns a new CellProfile based on defined distribution.
	 * I've kind of changed how the profiles work so this isn't strictly accurate anymore.
	 * @param distrobution
	 * @return
	 * @deprecated
	 */
	public static CellProfile getRandomProfile(int [] distrobution) {
		Random fate = new Random();
		int rand = Math.abs(fate.nextInt()%100);
		int cutOff = 0;
		for (int i =0; i < distrobution.length; i++){
			cutOff = cutOff + distrobution[i];
			if (rand<cutOff) {
				return getCellRules(i);
			}
		}
		return getCellRules(0);
	}

	/**
	 * Checks if the type of 2 cells is the same. It is based entirely on the type parameter so separate types could equivalent properties and still fail this test.
	 * @param other
	 * @return
	 */
	public boolean equals(CellProfile other) {
		return type==other.type;
	}
}
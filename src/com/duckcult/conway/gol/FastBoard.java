package com.duckcult.conway.gol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class FastBoard {
		/**
		 * The percentage used to determine how many cells to make dead when using randomized boards.
		 */
		public static final double PERCENT_DEAD = 0.7;
		private ArrayList<ArrayList<Boolean>> grid;
		private CellProfile rules;
		
		private int height;
		private int width;
		/**
		 * File based constructor.
		 * assumes the file is a space delimited grid representation where alive is 1, dead is 0.
		 * @param f
		 * @throws FileNotFoundException 
		 */
		public FastBoard(File f, CellProfile rules) throws FileNotFoundException {
			this.rules = rules;
			Scanner fileScan = new Scanner(f);
			grid = new ArrayList<ArrayList<Boolean>>();
			String [] line;
			ArrayList<Boolean>temp;
			do {
				line = fileScan.nextLine().split(" ");
				temp = new ArrayList<Boolean>();
				temp.ensureCapacity(line.length);
				for (String s: line) {
					temp.add(s.equals("1"));
				}
				grid.add(temp);
			}
			while(fileScan.hasNext());
			height = grid.size();
			width = grid.get(0).size();
		}
		
		/**
		 * Creates a new Board of the specified dimensions populated with NORMAL Cells.
		 * The proportion of live to dead will be defined by the PERCENT_DEAD constant.
		 * @param height
		 * @param width
		 */
		public FastBoard(int height, int width) {
			this(height, width, 0, CellProfile.NORMAL);	
		}
		
		/**
		 * Creates a new Board of the specified dimensions populated with the specified type of Cell.
		 * The proportion of live to dead will be defined by the PERCENT_DEAD constant.
		 * @param height
		 * @param width
		 * @param profile
		 */
		public FastBoard(int height, int width, CellProfile profile) {
			this(height, width, 0, profile);
		}
		
		/**
		 * Creates a new Board of the specified dimensions populate with the type of Cell.
		 * The flag integer determines whether the board is populated, empty, or full.
		 * @param height
		 * @param width
		 * @param flag
		 * @param profile
		 */
		private FastBoard(int height, int width, int flag, CellProfile profile) {
			this.rules = profile;
			initBoard(height,width,flag);
		}
		
		/**
		 * Returns a Board of the specified dimensions populated entirely with dead NORMAL cells.
		 * @param height
		 * @param width
		 * @return
		 */
		public static FastBoard emptyBoard(int height, int width) {
			return new FastBoard(height,width,1,CellProfile.NORMAL);
		}
		
		/**
		 * Returns a Board of the specified dimensions populated entirely with live NORMAL cells. 
		 * @param height
		 * @param width
		 * @param profile
		 * @return
		 */
		public static FastBoard allLive(int height, int width, CellProfile profile) {
			return new FastBoard(height,width,2,profile);
		}

	//simple mutator methods	
		//private void setWrap(int type){wrap = type;}
		
		/**
		 * Returns the width of the Board
		 * @return
		 */
		public int getWidth(){return width;}
		/**
		 * Returns the height of the Board
		 * @return
		 */
		public int getHeight(){return height;}
		/**
		 * Returns the Cell at (x,y).
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean getCell(int x, int y){return grid.get(y).get(x);}
		
		private void setCell(int x, int y, boolean state){grid.get(y).set(x, state);}
		
		public CellProfile getRules() {return this.rules;}
		
		/**
		 * Initialized the Board with the the given dimensions and CellProfile.
		 * The flag int defines the living proportion.
		 * flag == 0 PERCENT_DEAD percent of dead cells
		 * flag == 1 full board of 100% dead cells
		 * flag == 2 full board of 100% live cells
		 * @param height
		 * @param width
		 * @param flag
		 * @param profile
		 */
		private void initBoard(int height, int width, int flag) {
			grid = new ArrayList<ArrayList<Boolean>>(height);
			for (int i=0; i<height;i++){
				switch(flag) {
				case 1:
					grid.add(emptyRow(width));
				case 2:
					grid.add(fullRow(width));
				default:
					grid.add(randomRow(width));	
				}
			}
			this.height = height;
			this.width = width;
		}
		
		/**
		 * Generates a new row of size length populated with all live profile type cells.
		 * @param length
		 * @param profile
		 * @return
		 */
		private ArrayList<Boolean> fullRow (int length) {
			ArrayList<Boolean> temp = new ArrayList<Boolean>(length);
			for (int i=0;i<length;i++)
				temp.add(true);
			return temp;
		}
		
		/**
		 * Generates a new row of size length populated with PERCENT_DEAD profile type cells. 
		 * @param length
		 * @param profile
		 * @return
		 */
		private ArrayList<Boolean> randomRow(int length) {
			ArrayList<Boolean> temp = new ArrayList<Boolean>(length);
			for (int i=0; i<length;i++){
				temp.add(Math.random()>PERCENT_DEAD);
			}
			return temp;
		}
		
		/**
		 * Generates a new row of size length populated with all dead profile type cells.
		 * @param length
		 * @param profile
		 * @return
		 */
		private ArrayList<Boolean> emptyRow(int length) {
			ArrayList<Boolean> temp = new ArrayList<Boolean>(length);
			for (int i=0;i<length;i++)
				temp.add(false);
			return temp;
		}
	 	
		/**
		 * Moves every row down one grid point and generates a new random row at the top of the board.
		 */
		public void advanceBoard() {
			for (int i = 0; i < grid.size()-1; i++) {
				grid.set(i, grid.get(i+1));
			}
			grid.set(grid.size()-1, randomRow(width));
		}
		
		/**
		 * Runs a single tick of the game  of life through the entire grid.
		 */
		public void update() {
			for(int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int count = countNeighbors(j,i);
					if(getCell(j,i)) {
						if (count < rules.low || count > rules.high)
							setCell(j,i,false);
					}
					else {
						if (count == rules.birthReq)
							setCell(j,i,true);
					}
				}
			}
		}
		
		/**
		 * Returns the neighbors of a cell collapsed into a single array.
		 * Their positions map to:
		 * [[0 1 2]
		 *  [3 _ 4]
		 *  [5 6 7]]
		 * @param x
		 * @param y
		 * @return
		 */
		private int countNeighbors(int x, int y) {
			int tot = 0;
			if(x>0 && y>0 && getCell(x-1,y-1)) tot++;
			if(x<width-1 && y<height-1 && getCell(x+1,y+1)) tot++;
			if(x>0 && getCell(x-1,y)) tot++;
			if(y>0 && getCell(x,y-1)) tot++;
			if(x<width-1 && y>0 && getCell(x+1,y-1)) tot++; 
			if(x<width-1 && y<height-1 && getCell(x+1,y+1)) tot++;
			if(x<width-1 && getCell(x+1,y)) tot++;
			if(y<height-1 && getCell(x,y+1)) tot++;
			return tot;
		}
		
		/**
		 * Returns a 1D arrayList of meshes of the board for rendering.
		 * The returned ArrayList only contains Meshes for live cells and is ordered left to right and up.
		 * @param depth
		 * @return
		 */
		public ArrayList<Mesh> toMeshes(float depth) {
			ArrayList<Mesh> ret = new ArrayList<Mesh>(height*width);
			float squareSize = 2f/(float)width;
			float l = -1;
			float r = squareSize-1;
			float t = 2f/(float)height-1;
			float b = -1;
			Color color= rules.liveColor;
			for (int i = 0; i < height; i++) {
				l = -1;
				r=squareSize-1;
				for (int j = 0; j < width; j++) {
					if(getCell(j,i)) {
						Mesh m = new Mesh(true,4,4,
							new VertexAttribute(Usage.Position,3,"a_position"),
							new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
					//System.out.println("l="+l+" r="+r+" t="+t+" b="+b);
						m.setVertices(new float[] {l, b, depth, color.toFloatBits(),
												   r, b, depth, color.toFloatBits(),
												   l, t, depth, color.toFloatBits(),
												   r, t, depth, color.toFloatBits() });
						m.setIndices(new short[] {0,1,2,3});
						ret.add(m);
					}
					l = r;
					r += squareSize;
				}
				b = t;
				t+=squareSize;
				//t += ((float)2)/(float)height;
			}
			return ret;
		}
		
		
		/**
		 * rotates a coordinate bout it's axis. Used to be used in the Klien Bottle wrapping mode.  
		 * @param coord
		 * @param max
		 * @return
		 */
			private int rotateOnAxis (int coord, int max) {
				return (max-1)-coord;
			}

		/**
		 * Returns a String representation of the board.
		 * This might not work, I have not checked it since converting the Board over to being based on Objects instead of booleans.
		 * It should probably output the same string representation that the File reading constructor requires.
		 */
			public String toString(){
				String ret = "";
				for (int i = 0; i < height; i++){
					for (int j = 0; j < width; j++){
						ret = ret + (getCell(j,i) ? "1" : "0");
					}
					ret = ret + "\n";
				}
				return ret;
			}
			
		/**
		 * Checks if all cells on the board are dead, if so it returns true.
		 * @return
		 */
			public boolean extinct(){
				for (int i = 0; i < height; i++){
					for (int j =0; j < width; j++){
						if (getCell(j,i))
							return false;
					}
				}
				return true;
			}
			
			/**
			 * Inserts a pattern of live cells beginning at the specified x,y coordinates.
			 * If an element of the pattern is < 0 the cell at its corresponding location will be left as it is
			 * If an element in the pattern == 0 the corresponding cell will be marked as dead.
			 * If an element in the pattern > 0 the corresponding cell will be marked as alive. 
			 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.  
			 * @param x
			 * @param y
			 * @param cells
			 */
			public void setCells(int x, int y, int [][] pattern) {
				for (int i = 0; i < pattern.length; i++) {
					for (int j = 0; j < pattern[0].length; j++) {
						if(pattern[i][j] >= 0){
							setCell(x,y,pattern[i][j]>0);
						}
						x++;
					}
					y++;
				}
			}
			
			/**
			 * Checks a pattern of cells at position (x,y) and returns true if any of them are alive.
			 * It essentially functions like a regional call to extinct() except returns true if a Cell if alive.
			 * If an element of the pattern is < 0 the cell in that location will be ignored.
			 * Any element >= 0 will be checked.
			 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.
			 * @param x
			 * @param y
			 * @param cells
			 * @return
			 */
			public boolean checkCells(int x, int y, int [][]cells) {
				for (int i = 0; i < cells.length; i++) {
					for (int j = 0; j < cells[0].length; j++) {
						if(cells[i][j] >= 0 && getCell(x,y))
							return true;
						x++;
					}
					y++;
				}
				return false;
			}
			
			/**
			 * Returns a subset of the main grid starting at (x,y) with the provided height and width.
			 * Currently there is no special checking for index out of bounds exceptions.
			 * @param x
			 * @param y
			 * @param width
			 * @param height
			 * @return
			 */
			public ArrayList<ArrayList<Boolean>> getSubgrid(int x, int y, int width, int height) {
				ArrayList<ArrayList<Boolean>> ret = new ArrayList<ArrayList<Boolean>> (height);
				for (int i = y; i < height; i++) {
					ret.add((ArrayList<Boolean>) grid.get(i).subList(x, x+width));
				}
				return ret;
			}
}

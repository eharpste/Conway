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
		public static final int BUFFER_OFF = 0;
		public static final int PATTERN_BUFFER = 1;
		public static final int RANDOM_BUFFER = 2;
		public static final int PATTERN_TO_RANDOM_BUFFER = 3;
		
		private static final int DEFAULT_EMPTY = 0;
		private static final int DEFAULT_FULL = 1;
		private static final int DEFAULT_RANDOM = 2;
		private static final double DEFAULT_PERCENT_DEAD = .5;
		private ArrayList<ArrayList<Boolean>> buffer;
		private boolean [][] grid;
		private CellProfile rules;
		//the mode the buffer behaves in
		private int bufferMode;

		private float squareSize = 0.0f;
		//the current y value of the lower left corner of the board in OpenGL render space
		private float bottom = -1.0f;
		//the ammount to increase bottom by every update cycle
		private float timeStep = .1f;
		
		private double percentDead = .5;
		/**
		 * File based constructor.
		 * assumes the file is a space delimited grid representation where alive is 1, dead is 0.
		 * @param f
		 * @throws FileNotFoundException 
		 * @deprecated
		 */
		/*public FastBoard(File f, CellProfile rules) throws FileNotFoundException {
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
		}*/
		
		/**
		 * Returns a Board of the specified dimensions populated entirely with live NORMAL cells. 
		 * @param height
		 * @param width
		 * @param profile
		 * @return
		 */
		public static FastBoard allLive(int height, int width, CellProfile profile) {
			return new FastBoard(height,width,DEFAULT_FULL,profile,BUFFER_OFF,0.0,0f,null);
		}
		
		/**
		 * Returns an initially empty board loaded with the specified buffer
		 * @param height
		 * @param width
		 * @param buffer
		 * @return
		 */
		public static FastBoard bufferedEmprtBoard(int height, int width, ArrayList<ArrayList<Boolean>> buffer) {
			return new FastBoard(height,width,DEFAULT_EMPTY,CellProfile.NORMAL,PATTERN_BUFFER,DEFAULT_PERCENT_DEAD,0f,buffer);
		}
		
		/**
		 * Returns a Board of the specified dimensions populated entirely with dead NORMAL cells.
		 * @param height
		 * @param width
		 * @return
		 */
		public static FastBoard emptyBoard(int height, int width) {
			return new FastBoard(height,width,DEFAULT_EMPTY,CellProfile.NORMAL,BUFFER_OFF,1.0,0f,null);
		}
		
		/**
		 * Creates a new Board of the specified dimensions populated with NORMAL Cells.
		 * The proportion of live to dead will be defined by the PERCENT_DEAD constant.
		 * @param height
		 * @param width
		 */
		public FastBoard(int height, int width) {
			this(height, width, DEFAULT_RANDOM, CellProfile.NORMAL,BUFFER_OFF,DEFAULT_PERCENT_DEAD,0f,null);	
		}
		
		/**
		 * Creates a new Board of the specified dimensions populated with the specified type of Cell.
		 * The proportion of live to dead will be defined by the PERCENT_DEAD constant.
		 * @param height
		 * @param width
		 * @param profile
		 */
		public FastBoard(int height, int width, CellProfile profile) {
			this(height, width, DEFAULT_RANDOM, profile,BUFFER_OFF,DEFAULT_PERCENT_DEAD,0f,null);
		}
		
		/**
		 * Creates a new random board with a pre-loaded buffer.
		 * @param height
		 * @param width
		 * @param profile
		 * @param bufferMode
		 * @param buffer
		 * @param timeStep
		 */
		public FastBoard(int height, int width, CellProfile profile, ArrayList<ArrayList<Boolean>> buffer,float timeStep) {
			this(height,width,DEFAULT_RANDOM,profile,PATTERN_BUFFER,DEFAULT_PERCENT_DEAD,timeStep,buffer);
		}
		
		public FastBoard(int height, int width,CellProfile profile, double percentDead, float timeStep) {
			this(height, width,DEFAULT_RANDOM,profile,RANDOM_BUFFER,percentDead,timeStep,null);
		}
		
		public FastBoard(int height, int width, CellProfile profile, float timeStep) {
			this(height,width,DEFAULT_RANDOM,profile,RANDOM_BUFFER,DEFAULT_PERCENT_DEAD,timeStep,null);
		}
		
		/**
		 * Creates a new randomly populated board, with all the specified parameters.
		 * @param height
		 * @param width
		 * @param profile
		 * @param bufferMode
		 * @param percentDead
		 * @param timeStep
		 */
		public FastBoard(int height, int width, CellProfile profile,int bufferMode, double percentDead, float timeStep) {
			this(height,width,DEFAULT_RANDOM,profile,bufferMode,percentDead,timeStep,null);
		}
		
		/**
		 * OVERLOADED CONSTRUCTOR
		 * Creates a new Board of the specified dimensions populate with the type of Cell.
		 * The flag integer determines whether the board is populated, empty, or full.
		 * @param height
		 * @param width
		 * @param flag
		 * @param profile
		 */
		private FastBoard(int height, int width, int flag, CellProfile profile, int bufferMode, double percentDead, float timeStep, ArrayList<ArrayList<Boolean>> buff) {
			this.rules = profile;
			this.bufferMode = bufferMode;
			this.percentDead = percentDead;
			this.timeStep = timeStep;
			squareSize = 2f/width;
			//bottom -= 2*squareSize;
			if(buff == null) 
				buffer = new ArrayList<ArrayList<Boolean>>();
			else
				buffer = buff;
			initBoard(height,width,flag);
		}
		
		/**
		 * Adds the pattern to the current buffer.
		 * @param pattern
		 */
		public void addBuffer(ArrayList<ArrayList<Boolean>> pattern) {
			buffer.addAll(pattern);
		}
		/**
		 * Moves every row down one grid point and generates a new random row at the top of the board.
		 */
		public void advanceBoard(float deltaTime) {
			switch(bufferMode) {
				//pattern case	
				case PATTERN_BUFFER:
				case PATTERN_TO_RANDOM_BUFFER:
					if(buffer.size()>0 &&buffer.get(0)!=null) {
						bottom -= deltaTime*timeStep;
						if(bottom < (-1-2*squareSize)) {
							for(int i =0; i<grid.length-1;i++) {
								grid[i]=grid[i+1];
							}
							ArrayList<Boolean> line= buffer.remove(0);
							grid[grid.length-1] = new boolean[grid[0].length];
							for(int i = 0; i < grid[0].length; i++) {
								grid[grid.length-1][i] = line.get(i);
							}
							bottom += squareSize;
						}
					}
					else if(bufferMode == PATTERN_TO_RANDOM_BUFFER){
						bufferMode = RANDOM_BUFFER;
					}
					break;
				//random case
				case RANDOM_BUFFER:
					bottom -= deltaTime*timeStep;
					if(bottom < (-1-2*squareSize)) {
						for(int i =0; i<grid.length-1;i++) {
							grid[i]=grid[i+1];
						}
						grid[grid.length-1] = randomRow(grid[0].length);
						bottom += squareSize;
					}
					break;
				default :
					return;
			}
		}
		
		/**
		 * Check the status of a cell in render space
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean check(float x, float y) {
			int j = (int)((y-bottom)/squareSize);
			int i = (int)(x*squareSize+1);
			return getCell(i,j);
		}
		
		/**
		 * Checks the status of a cell in screen pixel space
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean check(int x, int y, int screenWidth, int screenHeight) {
			int pixPerSquare = screenWidth/grid[0].length;
			//y = rotateOnAxis(y, screenHeight);
			int xo = x/pixPerSquare;
			int yo = (y/pixPerSquare)+6;
			yo = rotateOnAxis(yo,grid.length);
			System.out.println("pixPerSquare="+pixPerSquare+" xo="+xo+" yo="+yo);
			//int xo = (int)(2*(float)x/(float)screenWidth/squareSize); 
			//int yo = (int)(2*(float)y/(float)screenHeight/squareSize);
		//	yo = grid.length-yo;
			setCell(xo,yo,!getCell(xo,yo));
			return getCell(xo,yo);
		}
		
		/**
		 * Checks a pattern of cells at position (x,y) and returns true if any of them are alive.
		 * It essentially functions like a regional call to extinct() except returns true if a Cell is alive.
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
		 * Checks if all cells on the board are dead, if so it returns true.
		 * @return
		 */
			public boolean extinct(){
				for (int i = 0; i < grid.length; i++){
					for (int j =0; j < grid[i].length; j++){
						if (getCell(j,i))
							return false;
					}
				}
				return true;
			}
		
		/**
		 * returns the height*width of the grid
		 * @return
		 */
		public int getArea(){return grid.length*grid[0].length;}
		
		/**
		 * Returns the Cell at (x,y).
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean getCell(int x, int y){
			if(x > grid[0].length || y > grid.length || x < 0 || y < 0)
				return false;
			return grid[y][x];
		}
		
		/**
		 * Returns the height of the Board
		 * @return
		 */
		public int getHeight(){return grid.length;}
		
		public double getPercentDead(){return percentDead;}
		
		public CellProfile getRules() {return this.rules;}
		
		public float getTimeStemp() {return timeStep;}
		
		/**
		 * Returns the width of the Board
		 * @return
		 */
		public int getWidth(){return grid[0].length;}
		
		/**
		 * Replaces the current buffer with a new pattern.
		 * @param pattern
		 */
		public void replaceBuffer(ArrayList<ArrayList<Boolean>> pattern) {
			buffer = pattern;
		}
		
		public void setBufferMode(int mode) {bufferMode = mode;}
		
		/**
		 * Inserts a pattern of live cells beginning at the specified x,y coordinates.
		 * If an element of the pattern is < 0 the cell at its corresponding location will be left as it is
		 * If an element in the pattern == 0 the corresponding cell will be marked as dead.
		 * If an element in the pattern > 0 the corresponding cell will be marked as alive. 
		 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.  
		 * @param x
		 * @param y
		 * @param pattern
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
	 	
		public void setPercentDead(double percent) {percentDead = percent;}
		
		public void setTimeStep(float step){timeStep = step;}
		
		/**
		 * Returns a 1D arrayList of meshes of the board for rendering.
		 * The returned ArrayList only contains Meshes for live cells and is ordered left to right and up.
		 * @param depth
		 * @return
		 */
		public ArrayList<Mesh> toMeshes(float depth) {
			ArrayList<Mesh> ret = new ArrayList<Mesh>(); 
			float l = -1;
			float r = squareSize-1;
			float b = bottom;
			float t = b+squareSize;
			Color color= rules.liveColor;
			for (int i = 0; i < grid.length; i++) {
				l = -1;
				r=squareSize-1;
				for (int j = 0; j < grid[i].length; j++) {
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
				if(t-bottom > 2f + 4*(squareSize))
					return ret;
			}
			return ret;
		}
		
		/**
		 * Returns a String representation of the board.
		 * This might not work, I have not checked it since converting the Board over to being based on Objects instead of booleans.
		 * It should probably output the same string representation that the File reading constructor requires.
		 */
			public String toString(){
				String ret = "";
				for (int i = 0; i < grid.length; i++){
					for (int j = 0; j < grid[i].length; j++){
						ret = ret + (getCell(j,i) ? "1" : "0");
					}
					ret = ret + "\n";
				}
				return ret;
			}
		
		/**
		 * Runs a single tick of the game  of life through the entire grid.
		 */
		public void update() {
			for(int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[i].length; j++) {
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
		//	advanceBoard(deltaTime);
		}
		
		/**
		 * Returns the number neighbors of a cell.
		 * @param x
		 * @param y
		 * @return
		 */
		private int countNeighbors(int x, int y) {
			int tot = 0;
			//lower left
			if(x>0 && y>0 && grid[y-1][x-1]) tot++;
			//upper right
			if(x<grid[y].length-1 && y<grid.length-1 && grid[y+1][x+1]) tot++;
			//straight left
			if(x>0 && grid[y][x-1]) tot++;
			//straight down
			if(y>0 && grid[y-1][x]) tot++;
			//lower right
			if(x<grid[y].length-1 && y>0 && grid[y-1][x+1]) tot++; 
			//upper left
			if(x>0 && y<grid.length-1 && grid[y+1][x-1]) tot++;
			//straight right
			if(x<grid[y].length-1 && grid[y][x+1]) tot++;
			//straight up
			if(y<grid.length-1 && grid[y+1][x]) tot++;
			return tot;
		}
		
		/**
		 * Generates a new row of size length populated with all dead profile type cells.
		 * @param length
		 * @param profile
		 * @return
		 */
		private boolean [] emptyRow(int length) {
			return new boolean[length];
		}
		
		
		/**
		 * Generates a new row of size length populated with all live profile type cells.
		 * @param length
		 * @param profile
		 * @return
		 */
		private boolean [] fullRow (int length) {
			boolean [] temp = new boolean [length];
			for (int i=0;i<length;i++)
				temp[i]=true;
			return temp;
		}

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
			grid = new boolean[height][width];
			for (int i=0; i<height;i++){
				switch(flag) {
				case DEFAULT_EMPTY:
					grid[i]=emptyRow(width);
				case DEFAULT_FULL:
					grid[i]=fullRow(width);
				default:
					grid[i]=randomRow(width);	
				}
			}
		}
			
		/**
		 * Generates a new row of size length populated with PERCENT_DEAD profile type cells. 
		 * @param length
		 * @param profile
		 * @return
		 */
		private boolean [] randomRow(int length) {
			boolean [] temp = new boolean [length];
			for (int i=0; i<length;i++){
				temp[i]=(Math.random()>percentDead);
			}
			return temp;
		}
			
			/**
			 * rotates a coordinate about it's axis. Used to be used in the Klien Bottle wrapping mode.  
			 * @param coord
			 * @param max
			 * @return
			 */
				private int rotateOnAxis (int coord, int max) {
					return (max-1)-coord;
				}
			
			private void setCell(int x, int y, boolean state){
				if(x > grid[0].length || y > grid.length || x<0 || y<0)
					return;
				grid[y][x]=state;
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
			public boolean [][] getSubgrid(int x, int y, int width, int height) {
				boolean [][] ret = new boolean[height][width];
				for (int i = y; i < height && y < grid.length; i++) {
					for(int j = x; j < width && x < grid[x].length; j++) {
						ret[i][j] = grid[y][x];
						x++;
					}
					y++;
				}
				return ret;
			}
}

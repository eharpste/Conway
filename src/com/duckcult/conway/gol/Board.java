package com.duckcult.conway.gol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

/**
 * The Board class runs all of the Game of Life code.
 * It provides various constructors and accessor methods for getting at different subsets of the grid.
 * @author eharpste
 *
 */
public class Board {
	/**
	 * The percentage used to determine how many cells to make dead when using randomized boards.
	 */
	public static final double PERCENT_DEAD = 0.7;
	private ArrayList<ArrayList<Cell>> grid;
	
	private int height;
	private int width;
	/**
	 * File based constructor.
	 * assumes the file is a space delimited grid representation where alive is 1, dead is 0.
	 * @param f
	 * @throws FileNotFoundException 
	 */
	public Board(File f) throws FileNotFoundException {
		Scanner fileScan = new Scanner(f);
		grid = new ArrayList<ArrayList<Cell>>();
		String [] line;
		ArrayList<Cell>temp;
		do {
			line = fileScan.nextLine().split(" ");
			temp = new ArrayList<Cell>();
			temp.ensureCapacity(line.length);
			for (String s: line) {
				temp.add(new BasicCell(s.equals("1")));
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
	public Board(int height, int width) {
		this(height, width, 0, CellProfile.NORMAL);	
	}
	
	/**
	 * Creates a new Board of the specified dimensions populated with the specified type of Cell.
	 * The proportion of live to dead will be defined by the PERCENT_DEAD constant.
	 * @param height
	 * @param width
	 * @param profile
	 */
	public Board(int height, int width, CellProfile profile) {
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
	private Board(int height, int width, int flag, CellProfile profile) {
		initBoard(height,width,flag,profile);
	}
	
	/**
	 * Returns a Board of the specified dimensions populated entirely with dead NORMAL cells.
	 * @param height
	 * @param width
	 * @return
	 */
	public static Board emptyBoard(int height, int width) {
		return new Board(height,width,1,CellProfile.NORMAL);
	}
	
	/**
	 * Returns a Board of the specified dimensions populated entirely with live NORMAL cells. 
	 * @param height
	 * @param width
	 * @param profile
	 * @return
	 */
	public static Board allLive(int height, int width, CellProfile profile) {
		return new Board(height,width,2,profile);
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
	public Cell getCell(int x, int y){return grid.get(y).get(x);}
	
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
	private void initBoard(int height, int width, int flag, CellProfile profile) {
		grid = new ArrayList<ArrayList<Cell>>(height);
		for (int i=0; i<height;i++){
			switch(flag) {
			case 1:
				grid.add(emptyRow(width,profile));
			case 2:
				grid.add(fullRow(width,profile));
			default:
				grid.add(randomRow(width,profile));	
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
	private ArrayList<Cell> fullRow (int length, CellProfile profile) {
		ArrayList<Cell> temp = new ArrayList<Cell>(length);
		for (int i=0;i<length;i++)
			temp.add(new BasicCell(true, profile));
		return temp;
	}
	
	/**
	 * Generates a new row of size length populated with PERCENT_DEAD profile type cells. 
	 * @param length
	 * @param profile
	 * @return
	 */
	private ArrayList<Cell> randomRow(int length, CellProfile profile) {
		ArrayList<Cell> temp = new ArrayList<Cell>(length);
		for (int i=0; i<length;i++){
			temp.add(new BasicCell(Math.random()>PERCENT_DEAD, profile));
		}
		return temp;
	}
	
	/**
	 * Generates a new row of size length populated with all dead profile type cells.
	 * @param length
	 * @param profile
	 * @return
	 */
	private ArrayList<Cell> emptyRow(int length, CellProfile profile) {
		ArrayList<Cell> temp = new ArrayList<Cell>(length);
		for (int i=0;i<length;i++)
			temp.add(new BasicCell(false, profile));
		return temp;
	}
 	
	/**
	 * Moves every row down one grid point and generates a new random row at the top of the board.
	 */
	public void advanceBoard() {
		for (int i = 0; i < grid.size()-1; i++) {
			grid.set(i, grid.get(i+1));
		}
		grid.set(grid.size()-1, randomRow(width,CellProfile.NORMAL));
	}
	
	/**
	 * Runs a single tick of the game  of life through the entire grid.
	 */
	public void update() {
		for(int i = 0; i < height; i++) {
			ArrayList<Cell> temp = grid.get(i);
			for (int j = 0; j < width; j++) {
				temp.get(j).check(getNeighbors(j,i));
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
	private ArrayList<Cell> getNeighbors(int x, int y) {
		ArrayList<Cell> temp = new ArrayList<Cell>();
		if(x>0 && y>0) temp.add(getCell(x-1,y-1));
		if(x<width-1 && y<height-1)temp.add(getCell(x+1,y+1));
		if(x>0)	temp.add(getCell(x-1,y));
		if(y>0) temp.add(getCell(x,y-1));
		if(x<width-1 && y>0) temp.add(getCell(x+1,y-1)); 
		if(x<width-1 && y<height-1) temp.add(getCell(x+1,y+1));
		if(x<width-1) temp.add(getCell(x+1,y));
		if(y<height-1) temp.add(getCell(x,y+1));
		return temp;
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
		for (int i = 0; i < height; i++) {
			l = -1;
			r=squareSize-1;
			for (int j = 0; j < width; j++) {
				if(getCell(j,i).isAlive()) {
					Mesh m = new Mesh(true,4,4,
						new VertexAttribute(Usage.Position,3,"a_position"),
						new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
				//System.out.println("l="+l+" r="+r+" t="+t+" b="+b);
					m.setVertices(new float[] {l, b, depth, getCell(j,i).getColor().toFloatBits(),
											   r, b, depth, getCell(j,i).getColor().toFloatBits(),
											   l, t, depth, getCell(j,i).getColor().toFloatBits(),
											   r, t, depth, getCell(j,i).getColor().toFloatBits() });
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
			for (int i = 0; i < grid.size(); i++){
				ArrayList<Cell> temp = grid.get(i);
				for (int j = 0; j < temp.size(); j++){
					ret = ret + temp.get(j);
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
			for (int i = 0; i < grid.size(); i++){
				ArrayList<Cell> temp = grid.get(i);
				for (int j =0; j < temp.size(); j++){
					if (temp.get(j).isAlive())
						return false;
				}
			}
			return true;
		}
		
		/**
		 * Inserts a grid of cells beginning at the specified x,y coordinates.
		 * If an element in the provided grid is null then the Cell at its corresponding coordinates will be left alone.
		 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.  
		 * @param x
		 * @param y
		 * @param cells
		 */
		public void setCells(int x, int y, Cell[][] cells) {
			for (int i = 0; i < cells.length; i++) {
				for (int j = 0; j < cells[0].length; j++) {
					if(cells[i][j]!=null){
						grid.get(y).set(x, cells[i][j]);
					}
					x++;
				}
				y++;
			}
		}
		
		/**
		 * Checks a pattern of cells at position (x,y) and returns true if any of them are alive.
		 * It essentially functions like a regional call to extinct() except returns true if a Cell if alive.
		 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.
		 * @param x
		 * @param y
		 * @param cells
		 * @return
		 */
		public boolean checkCells(int x, int y, Cell[][]cells) {
			for (int i = 0; i < cells.length; i++) {
				for (int j = 0; j < cells[0].length; j++) {
					if(cells[i][j]!=null && grid.get(x).get(y).isAlive())
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
		public ArrayList<ArrayList<Cell>> getSubgrid(int x, int y, int width, int height) {
			ArrayList<ArrayList<Cell>> ret = new ArrayList<ArrayList<Cell>> (height);
			for (int i = y; i < height; i++) {
				ret.add((ArrayList<Cell>) grid.get(i).subList(x, x+width));
			}
			return ret;
		}
	
/*-----------------------------------------------------------------------------------------------------------------//
		Everything bellow this line is fragments of the former implementation of the game of life code.
//-----------------------------------------------------------------------------------------------------------------*/
//sets up the board for the closed box setting.
	/*private void initBoardBox(int pRow, int pCol, int ratio) {
		int temp;
		this.row = pRow+2;
		this.col = pCol+2;
		grid = new Cell [row][col];
		for (int i =0; i < row; i++){
			for (int j = 0; j < col; j++){
				if (j==0 || j == col-1 || i==0 || i==row-1)
					grid[i][j] = new WallCell();
				else{
					temp = Math.abs(fate.nextInt()%100);
					if (temp < ratio)	
						grid[i][j] = new BasicCell (true, CellProfile.getRandomProfile());
					else
						grid[i][j] = new BasicCell (false, CellProfile.getRandomProfile());
				}
			}
		}
	}

	private void initBoardGlobe(int pRow, int pCol, int ratio) {
		int temp;
		this.row = pRow;
		this.col = pCol+2;
		grid = new Cell [row][col];
		for (int i =0; i < row; i++){
			for (int j = 0; j < col; j++){
				if (i==0 || i==row-1)
					grid[i][j] = new WallCell();
				else{
					temp = Math.abs(fate.nextInt()%100);
					if (temp < ratio)	
						grid[i][j] = new BasicCell (true, CellProfile.getRandomProfile());
					else
						grid[i][j] = new BasicCell (false, CellProfile.getRandomProfile());
				}
			}
		}
	}

	private void initBoardNoWalls(int pRow, int pCol, int ratio) {
		int temp;
		this.row = pRow;
		this.col = pCol;
		grid = new Cell [row][col];
		for (int i =0; i < row; i++){
			for (int j = 0; j < col; j++){
				temp = Math.abs(fate.nextInt()%100);
				if (temp < ratio)	
					grid[i][j] = new BasicCell (true, CellProfile.getRandomProfile());
				else
					grid[i][j] = new BasicCell (false, CellProfile.getRandomProfile());
			}
		}
	}*/
//
/*controls a single turn iteration	
	public void turn(){
		switch (wrap){
			case 0:
				for (int i = 1; i < row-1; i++){
					for (int j = 1; j < col-1;j++){
						grid[i][j].check(getNeighbors(i,j));
					}
				}
				break;
			case 1:
				for (int i = 1; i < row-1; i++){
					for (int j = 0; j < col;j++){
						grid[i][j].check(getNeighbors(i,j));
					}
				}
				break;
			case 2:
			case 3:
				for (int i = 1; i < row; i++) {
					for(int j = 0; j < col; j++) {
						grid[i][j].check(getNeighbors(i,j));
					}
				}
			default:
				for (int i = 1; i < row-1; i++){
					for (int j = 1; j < col-1;j++){
						grid[i][j].check(getNeighbors(i,j));
					}
				}
				break;
		}
		return;
	}*/

/*collects a 2D array of neighbor Cells to pass to the check method
	private Cell[][] getNeighbors(int cRow,int cCol) {
		Cell[][] neighbors = new Cell[3][3];
		switch (wrap){
			case 0:
				neighbors = getNeighborsBox(cRow, cCol,neighbors);
				break;
			case 1:
				neighbors = getNeighborsGlobe(cRow, cCol, neighbors);
				break;
			case 2:
				neighbors = getNeighborsTorus(cRow, cCol, neighbors);
				break;
			case 3:
				neighbors = getNeighborsKlein(cRow, cCol, neighbors);
				break;
			default:
				neighbors = getNeighborsBox(cRow,cCol,neighbors);
		}
		return neighbors;
	}*/
/*
//collects the neighbors of a cell in the closed box wrap setting, assumes a dead cell buffer
	private Cell[][] getNeighborsBox(int cRow,int cCol, Cell[][] neighbors) {
		neighbors[0][0] = grid[cRow-1][cCol-1];
		neighbors[0][1] = grid[cRow-1][cCol];
		neighbors[0][2] = grid[cRow-1][cCol+1];
		neighbors[1][0] = grid[cRow][cCol-1];
		neighbors[1][2] = grid[cRow][cCol+1];
		neighbors[2][0] = grid[cRow+1][cCol-1];
		neighbors[2][1] = grid[cRow+1][cCol];
		neighbors[2][2] = grid[cRow+1][cCol+1];
		return neighbors;
	}

//collects the neighbors of a cell for the globe wrap setting
	private Cell[][] getNeighborsGlobe(int cRow,int cCol, Cell[][] neighbors) {
		int preRow = cRow-1;
		int nexRow = cRow+1;
		int preCol = (cCol==0 ? col-1 : cCol-1);
		int nexCol = (cCol==col-1 ? 0 : cCol+1);
		neighbors[0][0] = grid[preRow][preCol];
		neighbors[0][1] = grid[preRow][cCol];
		neighbors[0][2] = grid[preRow][nexCol];   
		neighbors[1][0] = grid[cRow][preCol];   
		neighbors[1][2] = grid[cRow][nexCol];  
		neighbors[2][0] = grid[nexRow][preCol];  
		neighbors[2][1] = grid[nexRow][cCol]; 
		neighbors[2][2] = grid[nexRow][nexCol]; 
		return neighbors;
	}

//collects the neighbors of a cell for the torus wrap setting
	private Cell[][] getNeighborsTorus(int cRow, int cCol, Cell[][] neighbors) {
		int preRow = (cRow==0 ? row-1 : cRow-1);
		int preCol = (cCol==0 ? col-1 : cCol-1);
		int nexRow = (cRow==row-1 ? 0 : cRow+1);
		int nexCol = (cCol==col-1 ? 0 : cCol+1);
		neighbors[0][0] = grid[preRow][preCol];
		neighbors[0][1] = grid[preRow][cCol];
		neighbors[0][2] = grid[preRow][nexCol];   
		neighbors[1][0] = grid[cRow][preCol];   
		neighbors[1][2] = grid[cRow][nexCol];  
		neighbors[2][0] = grid[nexRow][preCol];  
		neighbors[2][1] = grid[nexRow][cCol]; 
		neighbors[2][2] = grid[nexRow][nexCol]; 
		return neighbors;
	}                         

//collects the neighbors of a cell for the klein bottle wrap setting
	private Cell[][] getNeighborsKlein(int cRow, int cCol, Cell[][] neighbors) {
		int nexRow = (cRow==row-1 ? 0 : cRow+1);
		int preRow = (cRow==0 ? row-1 : cRow-1);
		if (nexRow==0 || preRow==row-1)
			cCol = rotateOnAxis(cCol, col);
		int preCol = (cCol==0 ? col-1 : cCol-1);
		int nexCol = (cCol==col-1 ? 0 : cCol+1);
		neighbors[0][0] = grid[preRow][preCol];
		neighbors[0][1] = grid[preRow][cCol];
		neighbors[0][2] = grid[preRow][nexCol];   
		neighbors[1][0] = grid[cRow][preCol];   
		neighbors[1][2] = grid[cRow][nexCol];  
		neighbors[2][0] = grid[nexRow][preCol];  
		neighbors[2][1] = grid[nexRow][cCol]; 
		neighbors[2][2] = grid[nexRow][nexCol]; 
		return neighbors;
	}
*/


}

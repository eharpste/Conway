package com.duckcult.conway.gol;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

/**
 * FastBoard is the main entity that runs all of the Game of Life code.
 * The Fast prefix is because I have made a number of improvements over the original Board class to keep it from taking 
 * 	egregious amounts of RAM or processing time to run within the render loop.
 * In general things are mostly self contained and documented, the category of misc methods at the end are random 
 * 	artifacts or toys that I felt like leaving in. 
 * The general architecture is 2D array of booleans that hold the cells.
 * Update runs the game of life on the grid, advanceBoard visually moves the grid down the screen.
 * There are various options to buffer the grid to keep adding to it as it moves off screen, there are also more 
 * 	efficient options for updating in batches.
 * 
 * Things to note:
 * 
 * Most processing should be done on currGrid not nextGrid. Next grid is for the update method to use.
 * 
 * currGrid[0][0] is the BOTTOM LEFT element on screen currGrid[length][0] is the BOTTOM RIGHT, currGrid[0][length] is the UPPER LEFT 
 * 	and currGrid[length][length] is the UPPER RIGHT
 * 
 * Throughout the documentation I make reference to 3 different spaces: board space, render space, and screen space.
 * Board space is the literal currGrid[y][x].
 * Render space is defined by OpenGL defining the center of the screen as (0,0) and going from -1 to 1 for the extends of the screen.
 * 	the values of bottom, and movementPerTick are in render space units
 * Screen space is defined by LibGDX to handle mouse and touch input and corresponds to the number of pixels from the top left of the screen.
 * 	no parameters current use this but I am working on a mapping function.
 * @author eharpste
 *
 */
public class FastBoard {
		
//----------------------------------- static properties ---------------------------------------------------\\
		/**
		 * Buffer mode for turning off the buffer. 
		 * If advanceBoard() is called with the buffer off it will return immediately with no effect.
		 */
		public static final int BUFFER_OFF = 0;
		/**
		 * Buffer mode for turning on pattern buffering.
		 * If advanceBoard() is called with the pattern buffer it will advance the board and load in new rows from the buffer when old ones leave the screen.
		 * When the buffer is empty advancement will stop until more is added to the buffer. 
		 */
		public static final int PATTERN_BUFFER = 1;
		/**
		 * Buffer mode for turning on the random buffer.
		 * If advanceBoard() is called with the random buffer it will advance the board and generate a new random row when old ones leave the screen.
		 */
		public static final int RANDOM_BUFFER = 2;
		/**
		 * Buffer mode for turning on pattern buffering with a random backup.
		 * If advanceBoard() is called with the pattern buffer it will advance the board and load in new rows from the buffer when old ones leave the screen.
		 * When the buffer is empty the setting will switch to RANDOM_BUFFER. 
		 */
		public static final int PATTERN_TO_RANDOM_BUFFER = 3;
		/**
		 * Default value used for the percentage of cells dead in a randomized row.
		 */
		public static final double DEFAULT_PERCENT_DEAD = .5;
		/**
		 * Default value for the amount of render space the board moves per call to advanceBoard().
		 */
		public static final float DEFAULT_MOVEMENT_PER_TICK = 0f;
	
		/**
		 * A default flag for initBoard() corresponds to an empty board.
		 */
		private static final int DEFAULT_EMPTY = 0;
		/**
		 * A default flag for initBoard() corresponds to a full board.
		 */
		private static final int DEFAULT_FULL = 1;
		/**
		 * A default flag for initBoard() corresponds to a randomized board.
		 */
		private static final int DEFAULT_RANDOM = 2;	
//----------------------------------- end static properties -----------------------------------------------\\

//---------------------------------------  properties -----------------------------------------------------\\
		/**
		 * A buffer for patterns to load in when bufferMode is set to PATTERN_BUFFER or PATTERN_TO_RANDOM_BUFFER.
		 */
		private ArrayList<ArrayList<Boolean>> buffer;	
		/**
		 * The current game grid. This is where all external logic will happen.
		 */
		private boolean [][] currGrid;
		/**
		 * A grid to hold the temporary progress of update() running as a coroutine.
		 * When update is finished running on all rows and as waited more than secondsPerUpdate this board will be committed to currGrid.
		 */
		private boolean [][] nextGrid;
		/**
		 * The rule set that the board will abide by while running updates.
		 */
		private CellProfile rules;
		/**
		 * Defines the behavior of the board when advanceBoard() is called.
		 */
		private int bufferMode;
		/**
		 * The size of a single cell in render space.
		 */
		private float squareSize = 0.0f;
		/**
		 * the current y value of the lower left corner of the board in render space.
		 */
		private float bottom = -1.0f;
		/**
		 * The amount of render space the board should move down (decrease bottom) per call to advanceBoard().
		 * NOTE: advance board multiples this number by deltaTime
		 */
		private float movementPerTick = .1f;
		/**
		 * the amount of time since the last committed update of currGrid.
		 */
		private float timeSinceUpdate = 0.0f;
		/**
		 * Which row the coroutine version of update will process on the next call to update.
		 */
		private int updateIterator = 0;
		/**
		 * The current percentage to use when generating new random rows.
		 */
		private double percentDead = .5;
		/**
		 * The number of seconds the coroutine version of update should wait before committing nextGrid to currGrid if the process finished faster.
		 */
		private float secondsPerUpdate = 1.0f;
//------------------------------------- end  properties ---------------------------------------------------\\		

//------------------------------------- constructors ------------------------------------------------------\\
		/**
		 * Returns a new FastBoard of the specified dimensions populated entirely with live cells.
		 * The board's rule set follows the provided profile rule set. (Note: if the standard Conway Game of Life rules are used all the cells will die on the first call to update)
		 * The board will not advance down the screen or add new rows.
		 * Any calls to advanceBoard will return with no effect.
		 * @param height 			the height of the board.
		 * @param width 			the width of the board.
		 * @param profile 			the rule set the board will follow
		 * @return A new FastBoard with the specified dimensions populated with all living cells that follow the specified rule set.
		 */
		public static FastBoard fullBoard(int height, int width, CellProfile profile) {
			return new FastBoard(height,width,DEFAULT_FULL,profile,BUFFER_OFF,0.0,0f,null);
		}
		
		/**
		 * Returns new FastBoard of the specified dimensions that contains all dead cells and a buffer loaded with the specified values.
		 * The board's rule set follows the standard Conway Game of Life rules.
		 * The board will advance down the screen and load in rows from the buffer as rows disappear off the screen.
		 * @param height 			the height of the board.
		 * @param width 			the width of the board.
		 * @param buffer 			a pre-loaded buffer pattern.
		 * @return A new FastBoard with all dead cells and a pre-loaded buffer.
		 */
		public static FastBoard bufferedEmptyBoard(int height, int width, ArrayList<ArrayList<Boolean>> buffer) {
			return new FastBoard(height,width,DEFAULT_EMPTY,CellProfile.NORMAL,PATTERN_BUFFER,DEFAULT_PERCENT_DEAD,0f,buffer);
		}
		
		/**
		 * Returns a new  FastBoard of the specified dimensions that contains all dead cells.
		 * The board's rule set follows the standard Conway Game of Life rules.
		 * The board will not advance down the screen and will not add new rows.
		 * Any calls to advanceBoard will return with no effect.
		 * @param height			the height of the board
		 * @param width 			the width of the board
		 * @return a new FastBoard with the given height and width that is populated with all dead NORMAL cells.
		 */
		public static FastBoard emptyBoard(int height, int width) {
			return new FastBoard(height,width,DEFAULT_EMPTY,CellProfile.NORMAL,BUFFER_OFF,1.0,0f,null);
		}
		
		/**
		 * Creates a new FastBoard of the specified dimensions.
		 * This board will follow the standard rules of Conway's Game of Life.
		 * It will NOT advance down the screen or add new cells.
		 * Calls to advanceBoard() will return immediately with no effect.
		 * The proportion of live to dead will be defined by the DEFAULT_PERCENT_DEAD constant.
		 * @param height 			the height of the board.
		 * @param width 			the width of the board.
		 */
		public FastBoard(int height, int width) {
			this(height, width, DEFAULT_RANDOM, CellProfile.NORMAL,BUFFER_OFF,DEFAULT_PERCENT_DEAD,0f,null);	
		}
	
		/**
		 * Creates a new randomly populated FastBoard with the specified dimensions, pre-loaded with the given buffer.
		 * The board will follow the rule set in the provided CellProfile.
		 * The board will advance down the screen when advanceBoard() is called by loading in lines from the provided buffer.
		 * The distance the board advances movementPerTick multiplied by deltaTime with every call to advanceBoard().
		 * @param height			the height of the board
		 * @param width 			the width of the board
		 * @param profile 			the rule set the board will follow
		 * @param buffer 			the buffer the board will use to add new rows as it advances
		 * @param movementPerTick 	the distance the board will move, in render space units, with each call to advanceBoard
		 */
		public FastBoard(int height, int width, CellProfile profile, ArrayList<ArrayList<Boolean>> buffer,float movementPerTick) {
			this(height,width,DEFAULT_RANDOM,profile,PATTERN_BUFFER,DEFAULT_PERCENT_DEAD,movementPerTick,buffer);
		}
		
		/**
		 * Creates a new randomly populated FastBoard with the specified dimensions.
		 * The board will follow the rule set of the provided CellProfile.
		 * The board will advance down the screen when advanceBoard() is called by generating new random rows.
		 * The distance the board advances movementPerTick multiplied by deltaTime with every call to advanceBoard().
		 * @param height 			the height of the board.
		 * @param width 			the width of the board.
		 * @param profile			the rule set the board will follow.
		 * @param movementPerTick 	the distance the board will move, in render space units, with each call to advanceBoard
		 */
		public FastBoard(int height, int width, CellProfile profile, float movementPerTick) {
			this(height,width,DEFAULT_RANDOM,profile,RANDOM_BUFFER,DEFAULT_PERCENT_DEAD,movementPerTick,null);
		}
		
		/**
		 * Creates a new randomly populated FastBoard with the specified dimensions.
		 * The board will follow the rule set of the provided CellProfile.
		 * The board will advance down the screen when advanceBoard() following the policy of the defined bufferMode
		 * The board will be randomly populated by perecentDead cells.
		 * The distance the board advances movementPerTick multiplied by deltaTime with every call to advanceBoard().
		 * @param height 			the height of the board
		 * @param width 			the width of the board
		 * @param profile 			the rule set the board will follow
		 * @param bufferMode 		the buffering policy the board will use
		 * @param percentDead 		the percentage of dead cells to be generated by random row calls
		 * @param movementPerTick 	the distance the board will move, in render space units, with each call to advanceBoard
		 */
		public FastBoard(int height, int width, CellProfile profile,int bufferMode, double percentDead, float movementPerTick) {
			this(height,width,DEFAULT_RANDOM,profile,bufferMode,percentDead,movementPerTick,null);
		}
		
		/**
		 * **OVERLOADED CONSTRUCTOR**
		 * All other public constructors and static build methods fall through to this constructor.
		 * @param height			the height of the board (in number of cells)	
		 * @param width				the width of the board (in number of cells)
		 * @param flag				a flag for initBoard, translates to empty, full, or random initialization @see initiBoard()
		 * @param profile			the rule set the board will follow
		 * @param bufferMode		the bufferMode the board will use @see advanceBoard()
		 * @param percentDead		the percentage dead to be used when generating random rows
		 * @param movementPerTick	the distance * deltaTime that the board will move with every call to advanceBoard in render space units @see advanceBoard()		
		 * @param buff				a buffer to use if there is one.
		 */
		private FastBoard(int height, int width, int flag, CellProfile profile, int bufferMode, double percentDead, float movementPerTick, ArrayList<ArrayList<Boolean>> buff) {
			this.rules = profile;
			this.bufferMode = bufferMode;
			this.percentDead = percentDead;
			this.movementPerTick = movementPerTick;
			squareSize = 2f/width;
			//bottom -= 2*squareSize;
			if(buff == null) 
				buffer = new ArrayList<ArrayList<Boolean>>();
			else
				buffer = buff;
			initBoard(height,width,flag);
		}
//----------------------------------- end constructors ----------------------------------------------------\\
		
//------------------------------------- initializers ------------------------------------------------------\\ 
		/**
		 * Initialized the Board with the the given dimensions and CellProfile.
		 * The flag int defines the living proportion.
		 * flag == 0 full board of 100% dead cells
		 * flag == 1 full board of 100% live cells
		 * otherwise PERCENT_DEAD percent of dead cells
		 * @param height	the height of the board in number of cells
		 * @param width		the width of the board in number of cells
		 * @param flag		the flag for empty/full/random
		 */
		private void initBoard(int height, int width, int flag) {
			currGrid = new boolean[height][width];
			nextGrid = new boolean[height][width];
			for (int i=0; i<height;i++){
				switch(flag) {
				case DEFAULT_EMPTY:
					currGrid[i]=emptyRow(width);
				case DEFAULT_FULL:
					currGrid[i]=fullRow(width);
				default:
					currGrid[i]=randomRow(width);	
				}
				nextGrid[i]=emptyRow(width);
			}
		}
		
		/**
		 * Generates a new row of size length populated with all dead profile type cells.
		 * @param length	the length of the row
		 * @return a row of all dead cells of length length.
		 */
		private boolean [] emptyRow(int length) {
			return new boolean[length];
		}		
		
		/**
		 * Generates a new row of size length populated with all live profile type cells.
		 * @param length the length of the row
		 * @return a row of all live cells of length length.
		 */
		private boolean [] fullRow (int length) {
			boolean [] temp = new boolean [length];
			for (int i=0;i<length;i++)
				temp[i]=true;
			return temp;
		}
			
		/**
		 * Generates a new row of size length populated with PERCENT_DEAD profile type cells. 
		 * @param length the length of the row
		 * @return a new row containing a random number of living cells of length length.
		 */
		private boolean [] randomRow(int length) {
			boolean [] temp = new boolean [length];
			for (int i=0; i<length;i++){
				temp[i]=(Math.random()>percentDead);
			}
			return temp;
		}		
//----------------------------------- end initializers ----------------------------------------------------\\ 		
		
//---------------------------------------- getters --------------------------------------------------------\\		
		/**
		 * Returns the area of the board.
		 * @return the area of the board.
		 */
		public int getArea(){return currGrid.length*currGrid[0].length;}
		
		/**
		 * Returns the current buffer mode used by the board.
		 * @return the current buffer mode used by the board.
		 */
		public int getBufferMode() {return bufferMode;}
		
		/**
		 * Returns the status of the cell at position (x,y) in board space.
		 * NOTE: (0,0) is the lower left of the board (width,0) is the lower right.
		 * @param x		The x-coordinate of the cell in board space.
		 * @param y		The y-coordinate of the cell in board space.
		 * @return The status of the cell at position (x,y) in board space.
		 */
		public boolean getCell(int x, int y){
			if(x >= currGrid[0].length || y >= currGrid.length || x < 0 || y < 0)
				return false;
			return currGrid[y][x];
		}
		
		/**
		 * Returns the height of the board in number of cells.
		 * @return the height of the board in number of cells.
		 */
		public int getHeight(){return currGrid.length;}
		
		/**
		 * Returns the amount of distance the board moves in render space with every call to advanceBoard().
		 * NOTE: advanceBoard() multiples this number by deltaTime.
		 * @return
		 */
		public float getMovementPerTick() {return movementPerTick;}
		
		/**
		 * Returns the percentage currently be used to generate the populations of random rows.
		 * NOTE: The default value is .5
		 * @return the percentage currently be used to generate the populations of random rows.
		 */
		public double getPercentDead(){return percentDead;}
		
		/**
		 * Returns the rule set profile currently being used by the board during updates.
		 * @return the rule set profile currently being used by the board during updates.
		 */
		public CellProfile getRules() {return this.rules;}
				
		/**
		 * Returns the number of seconds the board waits to commit an update.
		 * NOTE: updates are run as a coroutine, updating a row per tick so this is the amount of time before committing the update.
		 * @return the number of seconds the board waits to commit an update.
		 */
		public float getSecondsPerUpdate() {return secondsPerUpdate;}
		
		/**
		 * Returns the width of the board in number of cells
		 * @return the width of the board in number of cells
		 */
		public int getWidth(){return currGrid[0].length;}
//---------------------------------------- end getters ----------------------------------------------------\\	

		
//---------------------------------------- setters --------------------------------------------------------\\
		/**
		 * Sets the buffer mode to be used by the board
		 * @param mode 		The desired mode (see the integer constants for options).
		 */
		public void setBufferMode(int mode) {bufferMode = mode;}
		
		/**
		 * Sets the cell at (x,y) to the given state
		 * NOTE: (0,0) is the bottom left corner (width,0) is the bottom right.
		 * @param x			The x-coordinate of the target cell.
		 * @param y			The y-coordinate of the target cell.
		 * @param state		The desired state of the target cell.
		 */
		private void setCell(int x, int y, boolean state){
			if(x >= currGrid[0].length || y >= currGrid.length || x<0 || y<0)
				return;
			currGrid[y][x]=state;
		}
		
		/**
		 * Sets the amount of distance the board moves in render space with every call to advanceBoard().
		 * NOTE: advanceBoard() multiples this number by deltaTime.
		 * @param step		The amount of distance the board moves in render space with every call to advanceBoard().
		 */
		public void setMovementPerTick(float step){movementPerTick = step;}
		
		/**
		 * Sets the percentage to be used to generate the populations of random rows.
		 * NOTE: The default value is .5
		 * @param percent	The percentage to be used to generate the populations of random rows.
		 */
		public void setPercentDead(double percent) {percentDead = percent;}
		
		/**
		 * Set the rule set profile to used by the board during updates.
		 * @param rules		The rule set profile to used by the board during updates.
		 */
		public void setRules (CellProfile rules) {this.rules = rules;}
		
		/**
		 * Returns the number of seconds the board waits to commit an update.
		 * NOTE: updates are run as a coroutine, updating a row per tick so this is the amount of time before committing the update.
		 * @param secs 		The number of seconds the board waits to commit an update.
		 */
		public void setSecondsPerUpdate(float secs) {	secondsPerUpdate = secs;}
	
		/**
		 * WARNING: this method might break shit as I don't actively test it.
		 * Inserts a pattern of live cells beginning at the specified x,y coordinates.
		 * If an element of the pattern is < 0 the cell at its corresponding location will be left as it is
		 * If an element in the pattern == 0 the corresponding cell will be marked as dead.
		 * If an element in the pattern > 0 the corresponding cell will be marked as alive. 
		 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.
		 * @param x			The x-coordinate to start inserting the pattern at, corresponds to the lower left corner of the pattern.
		 * @param y			The y-coordinate to start inserting the pattern at, corresponds to the lower left corner of the pattern.
		 * @param pattern	The pattern to insert.
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
//-------------------------------------- end setters ------------------------------------------------------\\
		
		
//------------------------------------- major methods -----------------------------------------------------\\
		/**
		 * Adds the specified pattern to the current buffer.
		 * @param pattern	The desired addition to the current buffer.
		 */
		public void addBuffer(ArrayList<ArrayList<Boolean>> pattern) {
			buffer.addAll(pattern);
		}
		
		/**
		 * Moves the board down movementPerTick * deltaTime units in render space.
		 * Behavior will depend on the setting of the buffer mode.
		 * bufferMode == PATTERN_BUFFER		The board will advance movementPerTick * deltaTime units down in render space.
		 * 		When the bottom row is off the screen all rows will be shifted down one and the next row in the buffer will be moved to the top of the active board.
		 * 		When the buffer is empty the board will not move or add rows.
		 * bufferMode == PATTERN_TO_RANDOM_BUFFER	The board will advance movementPerTick * deltaTime units down in render space.
		 * 		When the bottom row is off the screen all rows will be shifted down one and the next row in the buffer will be moved to the top of the active board.
		 * 		When the buffer is empty the board will revert to RANDOM_BUFFER mode.
		 * bufferMode == RANDOM_BUFFER		The board will advance movementPerTick * deltaTime units down in render space.
		 * 		When the bottom row is off the screen all rows will be shifted down one and a new random row will be generated at the top of the active board.
		 * bufferMode == NO_BUFFER This method will return with no effect.
		 * @param deltaTime
		 */
		public void advanceBoard(float deltaTime) {
			switch(bufferMode) {
				//pattern case	
				case PATTERN_BUFFER:
				case PATTERN_TO_RANDOM_BUFFER:
					if(buffer.size()>0 &&buffer.get(0)!=null) {
						bottom -= deltaTime*movementPerTick;
						if(bottom < (-1-2*squareSize)) {
							for(int i =0; i<currGrid.length-1;i++) {
								currGrid[i]=currGrid[i+1];
								nextGrid[i]=nextGrid[i+1];
							}
							ArrayList<Boolean> line= buffer.remove(0);
							currGrid[currGrid.length-1] = new boolean[currGrid[0].length];
							for(int i = 0; i < currGrid[0].length; i++) {
								currGrid[currGrid.length-1][i] = line.get(i);
							}
							nextGrid[nextGrid.length-1] = currGrid[currGrid.length-1];
							bottom += squareSize;
						}
					}
					else if(bufferMode == PATTERN_TO_RANDOM_BUFFER){
						bufferMode = RANDOM_BUFFER;
					}
					break;
				//random case
				case RANDOM_BUFFER:
					bottom -= deltaTime*movementPerTick;
					if(bottom < (-1-2*squareSize)) {
						for(int i =0; i<currGrid.length-1;i++) {
							currGrid[i]=currGrid[i+1];
							nextGrid[i]=nextGrid[i+1];
						}
						currGrid[currGrid.length-1] = randomRow(currGrid[0].length);
						nextGrid[nextGrid.length-1] = currGrid[currGrid.length-1];
						bottom += squareSize;
					}
					break;
				default :
					return;
			}
		}
		
		/**
		 * Counts the number of living neighbors surrounding the cell at (x,y).
		 * The method will account for cells that are on the edge of the board.
		 * NOTE: (0,0) is the lower left of the board (width,0) is the lower right.
		 * @param x		The x-coordinate of the target cell
		 * @param y		The y-coordinate of the target cell
		 * @return		The number of living cells around the target cell.
		 */
		private int countNeighbors(int x, int y) {
			int tot = 0;
			if(x>0 && y>0 && currGrid[y-1][x-1]) tot++;										//lower left
			if(x<currGrid[y].length-1 && y<currGrid.length-1 && currGrid[y+1][x+1]) tot++;	//upper right
			if(x>0 && currGrid[y][x-1]) tot++;												//straight left
			if(y>0 && currGrid[y-1][x]) tot++;												//straight down
			if(x<currGrid[y].length-1 && y>0 && currGrid[y-1][x+1]) tot++;					//lower right 
			if(x>0 && y<currGrid.length-1 && currGrid[y+1][x-1]) tot++;						//upper left
			if(x<currGrid[y].length-1 && currGrid[y][x+1]) tot++;							//straight right
			if(y<currGrid.length-1 && currGrid[y+1][x]) tot++;								//straight up
			return tot;
		}
		
		/**
		 * Replaces the current buffer with a new pattern.
		 * @param pattern The pattern to replace the current buffer with.
		 */
		public void replaceBuffer(ArrayList<ArrayList<Boolean>> pattern) {
			buffer = pattern;
		}
		
		/**
		 * Returns a 1D ArrayList of meshes representing the living cells of the board.
		 * The returned ArrayList only contains Meshes for live cells and is ordered left to right and up.
		 * @param depth 	The desired z-value to render the board at.
		 * @return	A 1D ArrayList of meshes representing the living cells of the board.
		 */
		public ArrayList<Mesh> toMeshes(float depth) {
			ArrayList<Mesh> ret = new ArrayList<Mesh>(); 
			float l = -1;
			float r = squareSize-1;
			float b = bottom;
			float t = b+squareSize;
			Color color= rules.liveColor;
			for (int i = 0; i < currGrid.length; i++) {
				l = -1;
				r=squareSize-1;
				for (int j = 0; j < currGrid[i].length; j++) {
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
		 * Runs a single tick of the Game of Life on the entire board following the rule set in rules.
		 */
		public void update() {
			for(int i = 0; i < currGrid.length; i++) {
				for (int j = 0; j < currGrid[i].length; j++) {
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
		 * Runs the Game of Life on a single row of the current active board following the rule set in rules.
		 * Once every row as been run and totalTime is greater than the defined secondsPerUpdate it will commit the changes to the activeBoard.
		 * This method is intended to run update as a coroutine with a render cycle and should be called with delta time for each tick of a render loop.
		 * @param deltaTime		The time since the last frame.
		 */
		public void update(float deltaTime) {
			timeSinceUpdate += deltaTime;
			if(updateIterator < currGrid.length) {
				int i = updateIterator;
				for(int j = 0; j < currGrid[i].length;j++) {
					int count = countNeighbors(j,i);
					if(getCell(j,i)) {
						if (count < rules.low || count > rules.high)
							nextGrid[i][j] = false;
					}
					else {
						if (count == rules.birthReq)
							nextGrid[i][j] = true;
					}
				}
				updateIterator++;
			}
			if(timeSinceUpdate > secondsPerUpdate && updateIterator >= currGrid.length) {
				currGrid = nextGrid;
				timeSinceUpdate = 0.0f;
				updateIterator = 0;
			}
		}	
//----------------------------------- end major methods ---------------------------------------------------\\


//------------------------------------- misc methods ------------------------------------------------------\\
		/**
		 * WARNING: This method is not fully tested yet.
		 * Returns the status of the cell at (x,y) in render space.
		 * Render space is defined by OpenGL as being -1 to 1 left to right and up and down.
		 * This method maps render space back into board space to find a specific cell.
		 * @param x		The x-coordinate of the target cell in render space
		 * @param y		The y-coordinate of the target cell in render space
		 * @return	The status of the target cell.
		 */
		public boolean checkRenderSpace(float x, float y) {
			int j = (int)((y-bottom)/squareSize);
			int i = (int)(x*squareSize+1);
			return getCell(i,j);
		}
		
		/**
		 * WARNING: This method is not fully tested yet.
		 * Returns the status of the cell at (x,y) in screen space.
		 * LibGDX uses screen space for touch and mouse input and it is defined as the number of pixels from the upper left corner of the screen.
		 * This method maps screen space back into board space to find a specific cell.
		 * @param x				The x-coordinate of the target cell.
		 * @param y				The y-coordinate of the target cell.
		 * @param screenWidth	The pixel width of the screen.
		 * @param screenHeight	The pixel height of the screen.
		 * @return	The status of the target cell.
		 */
		public boolean checkScreenSpace(int x, int y, int screenWidth, int screenHeight) {
			int pixPerSquare = screenWidth/currGrid[0].length;
			//y = rotateOnAxis(y, screenHeight);
			int xo = x/pixPerSquare;
			int yo = (y/pixPerSquare)+6;
			yo = rotateOnAxis(yo,currGrid.length);
			System.out.println("pixPerSquare="+pixPerSquare+" xo="+xo+" yo="+yo);
			//int xo = (int)(2*(float)x/(float)screenWidth/squareSize); 
			//int yo = (int)(2*(float)y/(float)screenHeight/squareSize);
		//	yo = grid.length-yo;
			setCell(xo,yo,!getCell(xo,yo));
			return getCell(xo,yo);
		}
		
		/**
		 * Checks a pattern of cells at position (x,y) and returns true if any of them are alive.
		 * If an element of the pattern is < 0 the cell in that location will be ignored.
		 * Any element >= 0 will be checked.
		 * NOTE: [0][0] is the bottom left corner of the grid [0][length] is the bottom right.
		 * @param x			The x-coordinate to start checking at, corresponds to the bottom left of the pattern.
		 * @param y			The y-coordinate to start checking at, corresponds to the bottom left of the pattern.
		 * @param cells		The pattern to check within the grid.
		 * @return	Whether there are ANY cells under the pattern that are alive.
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
		 * @return whether or not all cells on the board are dead.
		 */
		public boolean extinct(){
			for (int i = 0; i < currGrid.length; i++){
				for (int j =0; j < currGrid[i].length; j++){
					if (getCell(j,i))
						return false;
				}
			}
			return true;
		}
		
		/**
		 * WARNING: This might work oddly, I haven't tested it in a while.
		 * Returns a String representation of the board.
		 * Living cells are represented with '1' and dead ones with '0'. 
		 * Each row will be printed on its own line.
		 * @return a String representation of the board.
		 */
		public String toString(){
			String ret = "";
			for (int i = 0; i < currGrid.length; i++){
				for (int j = 0; j < currGrid[i].length; j++){
					ret = ret + (getCell(j,i) ? "1" : "0");
				}
				ret = ret + "\n";
			}
			return ret;
		}
		
		/**
		 * Rotates a coordinate about it's axis. 
		 * This board is derived from a much more complicated object oriented version of the Game of Life that I made a while back.
		 * This method used to be used in surface wrapping mode for a Klien Bottle.
		 * Currently it isn't used but might be for space mapping.  
		 * @param coord		The coordinate
		 * @param max		The maximum value of its axis
		 * @return			The coordinate rotate over the center of the axis.
		 */
		private int rotateOnAxis (int coord, int max) {
			return (max-1)-coord;
		}
		
		/**
		 * Returns a subset of the main grid starting at (x,y) with the provided height and width.
		 * I don't really know what I would use this for but its here.
		 * If the subgrid would overflow the normal bounds the cells will show up as dead.
		 * @param x			The x-coordinate to start the subGrid at.
		 * @param y			The y-coordinate to start the subGrid at.
		 * @param width		The width of the subgrid.
		 * @param height	The height of the subgrid.
		 * @return	a subset of the main grid starting at (x,y) with the provided height and width.
		 */
		public boolean [][] getSubgrid(int x, int y, int width, int height) {
			boolean [][] ret = new boolean[height][width];
			for (int i = y; i < height && y < currGrid.length; i++) {
				for(int j = x; j < width && x < currGrid[x].length; j++) {
					ret[i][j] = getCell(x,y);
					x++;
				}
				y++;
			}
			return ret;
		}
//----------------------------------- end misc methods -----------------------------------------------------\\
}

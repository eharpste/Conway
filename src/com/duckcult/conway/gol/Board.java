package com.duckcult.conway.gol;


import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Board {
	/**
	 * Returns the area of the board.
	 * @return the area of the board.
	 */
	public abstract int getArea();
	
	/**
	 * Returns the current buffer mode used by the board.
	 * @return the current buffer mode used by the board.
	 */
	public abstract int getBufferMode();
	
	/**
	 * Returns the status of the cell at position (x,y) in board space.
	 * NOTE: (0,0) is the lower left of the board (width,0) is the lower right.
	 * @param x		The x-coordinate of the cell in board space.
	 * @param y		The y-coordinate of the cell in board space.
	 * @return The status of the cell at position (x,y) in board space.
	 */
	public abstract boolean getCell(int x, int y);
	
	/**
	 * Returns the height of the board in number of cells.
	 * @return the height of the board in number of cells.
	 */
	public abstract int getHeight();
	
	/**
	 * Returns the amount of distance the board moves in render space with every call to advanceBoard().
	 * NOTE: advanceBoard() multiples this number by deltaTime.
	 * @return
	 */
	public abstract float getMovementPerTick();
	
	/**
	 * Returns the percentage currently be used to generate the populations of random rows.
	 * NOTE: The default value is .5
	 * @return the percentage currently be used to generate the populations of random rows.
	 */
	public abstract double getPercentDead();
	
	/**
	 * Returns the rule set profile currently being used by the board during updates.
	 * @return the rule set profile currently being used by the board during updates.
	 */
	public abstract CellProfile getRules();
			
	/**
	 * Returns the number of seconds the board waits to commit an update.
	 * NOTE: updates are run as a coroutine, updating a row per tick so this is the amount of time before committing the update.
	 * @return the number of seconds the board waits to commit an update.
	 */
	public abstract float getSecondsPerUpdate();
	
	/**
	 * Returns the width of the board in number of cells
	 * @return the width of the board in number of cells
	 */
	public abstract int getWidth();
	
	/**
	 * Returns the render size of the grid squares. 
	 * @return The render size of the grid suares.
	 */
	public abstract float getSquareSize();
	
	/**
	 * Sets the buffer mode to be used by the board
	 * @param mode 		The desired mode (see the integer constants for options).
	 */
	public abstract void setBufferMode(int mode);
	
	/**
	 * Sets the cell at (x,y) to the given state
	 * NOTE: (0,0) is the bottom left corner (width,0) is the bottom right.
	 * @param x			The x-coordinate of the target cell.
	 * @param y			The y-coordinate of the target cell.
	 * @param state		The desired state of the target cell.
	 */
	protected abstract void setCell(int x, int y, boolean state);
	
	/**
	 * Sets the amount of distance the board moves in render space with every call to advanceBoard().
	 * NOTE: advanceBoard() multiples this number by deltaTime.
	 * @param step		The amount of distance the board moves in render space with every call to advanceBoard().
	 */
	public abstract void setMovementPerTick(float step);
	
	/**
	 * Sets the percentage to be used to generate the populations of random rows.
	 * NOTE: The default value is .5
	 * @param percent	The percentage to be used to generate the populations of random rows.
	 */
	public abstract void setPercentDead(double percent);
	
	/**
	 * Set the rule set profile to used by the board during updates.
	 * @param rules		The rule set profile to used by the board during updates.
	 */
	public abstract void setRules (CellProfile rules);
	
	/**
	 * Returns the number of seconds the board waits to commit an update.
	 * NOTE: updates are run as a coroutine, updating a row per tick so this is the amount of time before committing the update.
	 * @param secs 		The number of seconds the board waits to commit an update.
	 */
	public abstract void setSecondsPerUpdate(float secs);

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
	public abstract void setCells(int x, int y, int [][] pattern);
	
	/**
	 * Adds the specified pattern to the current buffer.
	 * @param pattern	The desired addition to the current buffer.
	 */
	public abstract void addBuffer(Array<Array<Boolean>> pattern);
	
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
	public abstract void advanceBoard(float deltaTime);
	
	/**
	 * Counts the number of living neighbors surrounding the cell at (x,y).
	 * The method will account for cells that are on the edge of the board.
	 * NOTE: (0,0) is the lower left of the board (width,0) is the lower right.
	 * @param x		The x-coordinate of the target cell
	 * @param y		The y-coordinate of the target cell
	 * @return		The number of living cells around the target cell.
	 */
	protected abstract int countNeighbors(int x, int y);
	
	/**
	 * Replaces the current buffer with a new pattern.
	 * @param pattern The pattern to replace the current buffer with.
	 */
	public abstract void replaceBuffer(Array<Array<Boolean>> pattern);
	
	/**
	 * Returns a 1D ArrayList of meshes representing the living cells of the board.
	 * The returned ArrayList only contains Meshes for live cells and is ordered left to right and up.
	 * This version of toMeshes direct accesses the underlying grid instead of going through toCellObs so it is potentially faster.
	 * It is not guaranteed that this method will return output identical to the other version.
	 * @param depth 	The desired z-value to render the board at.
	 * @return	A 1D ArrayList of meshes representing the living cells of the board.
	 */
	public abstract Array<Mesh> toMeshesDirect(float depth);
	
	/**
	 * Returns the Cell that is overlapped by the provided Rectangle.
	 * @param rect	A Rectangle that describes something in render space.
	 * @return	The Cell that overlaps the given Rectangle in render space.
	 */
	public abstract Cell getOverlappedCell(Rectangle rect);
	
	/**
	 * Kills the cell described by the provided cell and returns true. 
	 * If the provided cell is outside the board or already dead it returns false.
	 * @param c	A Cell object representing a position in the grid.
	 * @return	true if the cell was killed, false if the cell was outside the board or already dead.
	 */
	public abstract boolean killCell(Cell c);
	
	/**
	 * Returns a 1D ArrayList of meshes representing the living cells of the board.
	 * The returned ArrayList only contains Meshes for live cells and is ordered left to right and up.
	 * This version of the method is potentially less efficient than the other one but I've never noticed a difference.
	 * @param depth 	The desired z-value to render the board at.
	 * @return	A 1D ArrayList of meshes representing the living cells of the board.
	 */
	public abstract Array<Mesh> toMeshes(float depth);
	
	
	/**
	 * Runs a single tick of the Game of Life on the entire board following the rule set in rules.
	 * This does not take into account deltaTime and will run the entire board in a single call.
	 */
	public abstract void update();
	
	/**
	 * Runs the Game of Life on a single row of the current active board following the rule set in rules.
	 * Once every row as been run and totalTime is greater than the defined secondsPerUpdate it will commit the changes to the activeBoard.
	 * This method is intended to run update as a coroutine with a render cycle and should be called with delta time for each tick of a render loop.
	 * @param deltaTime		The time since the last frame.
	 */
	public abstract void update(float deltaTime);
	
	/**
	 * Kills the cell that is overlapped by the given Rectangle in render space and returns true if it succeeded.
	 * If the Rectangle does not overlap a living cell or doesn't overlap any cell it will return false;
	 * @param rect	A Rectangle that describes something in render space.
	 * @return	true if the kill was successful, false if the cell is already dead or the the rectangle is outside the board.
	 */
	public abstract boolean killOverlapCell(Rectangle rect);
	
	/**
	 * Kills the given number of rows.
	 * This is used to allow the player's ship to have a safe spawn area instead of instantly dying.
	 * @param rows	The number of rows to kill.
	 */
	public abstract void makeSafeZone(int rows);
	
	/**
	 * Kills all cells that fall bellow the given height in render space.
	 * This is used to allow the player ship to have a safe spawn area instead of instantly dying.
	 * Technically this version is better because its based on the render space used in collision detection.
	 * @param renderHeight	The height in render space that you want to clear the board to.
	 */
	public abstract void makeSafeZone(float renderHeight);
}

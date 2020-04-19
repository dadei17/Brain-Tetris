import java.util.Arrays;

// Board.java

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you: 
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	private int[] heights; 
	private int[] widths;
	private int maxHeight;
	
	private boolean[][] undoGrid;
	private int[] undoHeights;
	private int[] undoWidths;
	private int undoMaxHeight;
	
	// Here a few trivial methods are provided:
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		heights = new int[width];
		widths = new int[height];
		maxHeight = 0;
		
		undoGrid = new boolean[width][height];
		undoHeights = new int[width];
		undoWidths = new int[height];
		undoMaxHeight = 0;
	}


	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		
		return maxHeight; // YOUR CODE HERE
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			int[] checkWidths = new int[height];
			int[] checkHeights = new int[width]; 
			for(int i =0; i<width; i++) {
				for(int j=height-1; j>=0; j--) {
					if(grid[i][j] == true) {
						checkWidths[j]++;
						if(checkHeights[i] == 0) {
							checkHeights[i] = j+1;  
						}
					}
				}
			}
			assert (Arrays.equals(checkWidths, widths));
			assert (Arrays.equals(checkHeights, heights));
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skrt = piece.getSkirt(); 
		if(x + skrt.length > width) return -1;
		int firstIndx = x;
		for(int i=1; i<skrt.length; i++) {
			int diff = heights[firstIndx] - heights[x+i];
			if(diff < (skrt[firstIndx-x] - skrt[i])) {
				firstIndx = x + i;
			}
		} 
		return heights[firstIndx] - skrt[firstIndx - x]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		if(x<0 || x>= width) return -1;
		return heights[x]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		if(y<0 || y>= height) return -1;
		 return widths[y]; // YOUR CODE HERE
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if(x<0 || x>= width || y<0 || y>= height) return false;
		return grid[x][y]; // YOUR CODE HERE
	}
	
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem"); 
		undoHelper(grid, undoGrid, heights, undoHeights, widths, undoWidths, false);
		undoMaxHeight = maxHeight;
		 
		int result = PLACE_OK;
	 
		TPoint[] points = piece.getBody();
		for(int i =0; i<points.length; i++) { 
			TPoint p = points[i];
			int px = x + p.x; 
			int py = y + p.y;  
			if(px <0 || px >width-1 || py <0 || py >height-1) return PLACE_OUT_BOUNDS; 
			if(grid[px][py] == true) result = PLACE_BAD;
			grid[px][py] = true;
			if(heights[px]-1 < py) heights[px] = py+1;
			widths[py]++;
			if(widths[py] == width) {
				if(result != PLACE_BAD)result = PLACE_ROW_FILLED;
			}
			if(py > maxHeight-1) maxHeight = py + 1;
		}
		return result;
	}
	
	private void clear(int y) {
		if(y == maxHeight-1) {
			maxHeight--;
			for(int i =0; i<width; i++) {
				int curY = y;
				grid[i][y] = false;
				while(curY >= 0 && grid[i][curY] != true) {
					heights[i] = curY;
					curY--;
				}
			} 
			widths[y] = 0;
			return;
		} 
		for(int i=0; i<width; i++) {
			grid[i][y] = grid[i][y+1];
		}
		widths[y] = widths[y+1];
		clear(y+1);
	}
 
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		committed = false; 
		int rowsCleared = 0; 
		sanityCheck();
		for(int i=maxHeight-1; i>=0; i--) {
			if(widths[i] == width) {
				clear(i);
				rowsCleared++;
			}
		}
		return rowsCleared;
	}
	
	
	private void undoHelper(boolean[][] fromG, boolean[][] toG, int[] fromH, int[] toH, int[] fromW, int[] toW, boolean b) {
		for(int i =0; i<width; i++) {
			for(int j = 0; j<height; j++) {
				toG[i][j] = fromG[i][j];
			}
		}
		for(int i =0; i<width; i++) {
			toH[i] = fromH[i];
		}
		for(int i =0; i<height; i++) {
			toW[i] = fromW[i];
		}
		committed = b;
	}

	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(!committed) {
			undoHelper(undoGrid, grid, undoHeights, heights, undoWidths, widths, true);
			maxHeight = undoMaxHeight;
		}
	}
	
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}



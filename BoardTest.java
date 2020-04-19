import junit.framework.TestCase;


public class BoardTest extends TestCase {
	Board b,c,d,mini;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;
	private Piece stick1, stick2;
	private Piece l11, l12, l13, l14;
	private Piece l21, l22, l23, l24; 
	private Piece square;

	// This shows how to build things in setUp() to re-use
	// across tests.
	
	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	
	protected void setUp() throws Exception { 
		b = new Board(3, 6);
		c = new Board(4,8);
		d = new Board(5,10);
		mini = new Board(3,4);
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		stick1 = new Piece(Piece.STICK_STR);
		l11 = new Piece(Piece.L1_STR);
		l21 = new Piece(Piece.L2_STR);
		square = new Piece(Piece.SQUARE_STR);
		
		pyr2 = pyr1.computeNextRotation();
		stick2 = stick1.computeNextRotation();
		l12 = l11.computeNextRotation();
		l22 = l21.computeNextRotation();
		
		pyr3 = pyr2.computeNextRotation();
		l13 = l12.computeNextRotation();
		l23 = l22.computeNextRotation();
		
		pyr4 = pyr3.computeNextRotation();
		l14 = l13.computeNextRotation();
		l24 = l23.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
		
		b.place(pyr1, 0, 0);
	}
	
	// Check the basic width/height/max after the one placement
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	
	// Place sRotated into the board, then check some measures
	public void testSample2() {
		b.commit(); 
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	} 
	
	public void testPlaces() {
		//Place row filled.
		c.commit();
		assertEquals(Board.PLACE_ROW_FILLED, c.place(stick2, 0, 0));
		c.undo();
		
		//Place bad.
		c.place(stick1, 0, 0);
		c.commit();
		assertEquals(Board.PLACE_BAD, c.place(stick2, 0, 3)); 
		c.undo();
		
		// Place out of bounds
		b.undo();
		assertEquals(Board.PLACE_OUT_BOUNDS, b.place(l21, 6, 0));
		b.undo();
		assertEquals(Board.PLACE_OUT_BOUNDS, b.place(l21, -1, 0));
		b.undo();
		assertEquals(Board.PLACE_OUT_BOUNDS, b.place(s, 0, -1));
		b.undo();
		assertEquals(Board.PLACE_OUT_BOUNDS, b.place(l11, 2, 6));
		
		//placeOk.
		d.commit();
		assertEquals(Board.PLACE_OK, d.place(l11, 0, 0));
		d.undo();
		
		// drop height;
		assertEquals(Board.PLACE_OK, d.place(l11, 0, d.dropHeight(l11, 0)));
		d.undo();
		d.place(stick1, 4, 0);
		d.commit();
		d.place(pyr3, 2, d.dropHeight(pyr3, 2)); 
		d.commit();
		assertEquals(-1,mini.dropHeight(stick2, 0));
		assertEquals(Board.PLACE_ROW_FILLED, mini.place(pyr1, 0, mini.dropHeight(pyr1, 0)));
		mini.undo();
		
		
		c.place(square, 2, c.dropHeight(square, 2));
		c.commit();
		c.place(l14, 0, c.dropHeight(l14, 0));
		c.commit();
		c.place(l24, 1, c.dropHeight(l24, 0));
		assertEquals(c.getMaxHeight(),c.getColumnHeight(1));
		c.commit();
		System.out.println(c.toString());  
		assertEquals(c.getMaxHeight(),c.getColumnHeight(1));
		
		System.out.println(b.toString());  
		b.commit();
		b.place(square, 0, 0);
		boolean runExept = false;
		try {
			b.place(stick1, 2 , 0);
		}catch(RuntimeException r) {
			runExept = true;
		}
		assertEquals(true,runExept);
		
	}
	
	public void testClearRows() {
		c.commit();
		assertEquals(Board.PLACE_ROW_FILLED, c.place(stick2, 0, 0));
		c.clearRows();
		c.commit();
		assertEquals(Board.PLACE_OK, c.place(stick1, 0, 0));
		c.commit();
		c.place(stick2, 0, 4);
		c.clearRows();
		c.commit();
		assertEquals(Board.PLACE_OK, c.place(pyr1, 0, 4));
		
		mini.place(pyr3, 0, 0);
		mini.commit();
		mini.clearRows();
		mini.commit();
		assertEquals(Board.PLACE_BAD, mini.place(pyr1, 0, 0));
		
		b.commit();
		b.undo();
		
		 
	}
	
	public void testGets() {
		// getWidth and getHeight
		assertEquals(5,d.getWidth());
		assertEquals(10,d.getHeight());
		
		//getColumnHeight.
		assertEquals(2,b.getColumnHeight(1));
		assertEquals(-1,b.getColumnHeight(-1));
		assertEquals(-1,b.getColumnHeight(4));
		assertEquals(d.getMaxHeight(),d.getColumnHeight(4));
		c.place(stick1, 2, c.dropHeight(stick1, 0));
		c.commit();
		c.place(pyr1, 1, c.dropHeight(pyr1, 1));
		assertEquals(6,c.getColumnHeight(2));
		
		//getRowWidth.
		assertEquals(3,b.getRowWidth(0));
		assertEquals(-1,b.getRowWidth(-1));
		assertEquals(-1,b.getRowWidth(6));
		
		//getGrid.
		assertEquals(true,b.getGrid(1, 1));
		assertEquals(false,b.getGrid(-1,4));
		assertEquals(false,b.getGrid(6,3));
		assertEquals(false,b.getGrid(2,-1));
		assertEquals(false,b.getGrid(2,7));
		d.place(l13, 0, 0);
		assertEquals(true,d.getGrid(1,0));
		assertEquals(true,d.getGrid(0,2));
		
		b.clearRows();
		assertEquals(false,b.getGrid(1, 1));
		b.commit();
		b.place(stick1, 1, b.dropHeight(stick1, 1));
		b.commit();
		assertEquals(5,b.getColumnHeight(1));
		
	} 
	
	// Makre  more tests, by putting together longer series of 
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.
	
	
}

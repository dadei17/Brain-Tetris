import junit.framework.TestCase;

import java.util.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest extends TestCase {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;
	private Piece stick1, stick2;
	private Piece l11, l12, l13, l14;
	private Piece l21, l22, l23, l24;
	private Piece square;
 
	protected void setUp() throws Exception {
		super.setUp();
		
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
	}
	
	// Here are some sample tests to get you started
	
	public void testSampleSize() {
		// Check size of pyramid piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
		
		assertEquals(2, l11.getWidth());
		assertEquals(3, l11.getHeight());
		assertEquals(3, l12.getWidth());
		assertEquals(2, l12.getHeight());
		
		assertEquals(square.getHeight(), square.getWidth());
		assertEquals(2, square.getWidth());
	}
	
	
	// Test the skirt returned by a few pieces
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0}, l11.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, l12.getSkirt()));
		assertTrue(Arrays.equals(new int[] {2, 0}, l13.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1, 1}, l14.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, l24.getSkirt()));
	}
	
	public void testRotations() {
		// pyramid
		assertTrue(pyr2.equals(new Piece("1 0  1 1  1 2  0 1")));
		assertTrue(pyr3.equals(new Piece("0 1  1 1	2 1  1 0")));
		assertTrue(pyr4.equals(new Piece("0 0  0 1	0 2  1 1")));
		assertTrue(pyr4.computeNextRotation().equals(new Piece("0 0  1 0  1 1  2 0")));
		//stick
		assertTrue(stick1.equals(new Piece("0 0  0 1  0 2  0 3")));
		assertTrue(stick2.equals(new Piece("0 0  1 0  2 0  3 0")));
		assertTrue(stick2.computeNextRotation().equals(stick1));
		//l11
		assertTrue(l11.computeNextRotation().equals(l12));
		assertTrue(l12.computeNextRotation().equals(l13));
		assertTrue(l13.computeNextRotation().equals(l14));
		assertTrue(l14.computeNextRotation().equals(l11));
		
		//fast Rotation
		Piece[] pieces =  Piece.getPieces();
		assertTrue(pieces[Piece.PYRAMID].fastRotation().equals(pyr2));
		assertTrue(pieces[Piece.L1].fastRotation().equals(l12));
		assertTrue(pieces[Piece.L2].fastRotation().equals(l22));
		assertTrue(pieces[Piece.S1].fastRotation().equals(sRotated));
		assertTrue(pieces[Piece.SQUARE].fastRotation().equals(square));
		
		assertTrue(square.equals(square));
		assertFalse(square.equals(new HashMap<Integer,Integer>() ));
		
		Piece.getPieces();
		boolean runExept = false;
		
		try {
			new Piece("f");
		}catch(RuntimeException r) {
			runExept = true;
		}
		assertTrue(runExept);
		
	}
}

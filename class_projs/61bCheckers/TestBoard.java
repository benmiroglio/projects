import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class TestBoard {
	Board b;

	@Test
	public void testPlacePieceAtRemoved() {
		b = new Board(true);
		Piece p = new Piece(true, b, 0, 0, "shield");
		Piece p1 = new Piece(false, b, 7, 7, "pawn");
		b.place(p, 0, 0);
		assertTrue(b.pieceAt(0, 0).isShield());
		assertTrue(b.pieceAt(0, 0).isFire());
		b.place(p1, 7, 7);
		assertFalse(b.pieceAt(7, 7).isFire());
		Board b1 = new Board(true);
		assertNull(b1.pieceAt(0, 0));
		b1.place(p, 0, 0);
		assertTrue(b1.pieceAt(0, 0).isFire());
		b1.remove(0, 0);
		assertNull(b1.pieceAt(0, 0));
		b.remove(7, 7);
		assertNull(b.pieceAt(7, 7));

	}

	@Test
	public void testCanSelect() {
		b = new Board(false);
		assertTrue(b.canSelect(0, 0));
		assertTrue(b.canSelect(0, 2));
		assertFalse(b.canSelect(7, 7));
		assertFalse(b.canSelect(7, 5));
		Board b1 = new Board(true);
		Piece pFire = new Piece(true, b1, 0, 0, "pawn");
		Piece pWater = new Piece(false, b1, 1, 1, "pawn");
		b1.place(pFire, 0, 0);
		b1.place(pWater, 1, 1);
		
	}

	@Test
	public void testBombexplosions() {
		b = new Board(true);
		Piece bomb = new Piece(true, b, 0, 0, "bomb");
		Piece shield = new Piece(true, b, 2, 0, "shield");
		Piece pawn1 = new Piece(true, b, 0, 2, "pawn");
		Piece pawn2 = new Piece(false, b, 1, 1, "pawn");
		b.place(bomb, 0, 0);
		b.place(shield, 2, 0);
		b.place(pawn1, 0, 2);
		b.place(pawn2, 1, 1);
		assertTrue(b.canSelect(0, 0));
		b.select(0, 0);
		assertTrue(b.canSelect(2, 2));
		b.pieceAt(0, 0).move(2, 2);
		assertNull(b.pieceAt(0, 0));
		assertNull(b.pieceAt(1, 1));
		assertNull(b.pieceAt(0, 2));
		assertNull(b.pieceAt(2, 2));
		assertTrue(b.pieceAt(2, 0).isShield());
		//assertTrue(b.canEndTurn());
	}

    @Test 

    public void testWinner() {
        b = new Board(true);
        Piece pawn1 = new Piece(true, b, 0, 2, "pawn");
        Piece pawn2 = new Piece(false, b, 1, 1, "pawn");
        b.place(pawn1, 0, 2);
        b.place(pawn2, 1, 1);
        assertEquals(null, b.winner());
        b.remove(0, 2);
        assertEquals("Water", b.winner());
        b.place(pawn1, 0, 2);
        b.remove(1, 1);
        assertEquals("Fire", b.winner());
        b.remove(0, 2);
        assertEquals("No one", b.winner());


    }
    


/*/////////////////////////////////////////////////////////////////////////*/
/**
 * @MISCONCEPTIONS_TESTS
 */
/*////////////////////////////////////////////////////////////////////////////////////////*/
	 @Test
    public void testCoreFunctionality() {
        //System.out.println("Test 1: See comments in MisconceptionTests.java for description.");
        Board b = new Board(true);

        // Place a shield at position 0, 0.
        Piece shield = new Piece(true, b, 0, 0, "shield");
        b.place(shield, 0, 0);

        // Verify that it can be selected.
        assertTrue(b.canSelect(0, 0));
        b.select(0, 0);

        // Verify that the blank square to the top right of it can be selected.
        assertTrue(b.canSelect(1, 1));
        b.select(1, 1); 

        // Ensure that we can end turn after movement.
        assertTrue(b.canEndTurn());        
    }


    @Test
    public void testThatSelectAndCanSelectDontCallEachOther() {
        //System.out.println("Test 2: See comments in MisconceptionTests.java for description.");

        SpyBoard b = new SpyBoard(true);

        // Place a shield at position 0, 0.
        Piece shield = new SpyPiece(true, b, 0, 0, "shield");
        b.place(shield, 0, 0);
        
        assertTrue(b.canSelect(0, 0));

        // Assert that canSelect has been called once, but
        // select has not been called.
        assertEquals(0, b.selectCount);
        assertEquals(1, b.canSelectCount);

        b.select(0, 0);

        // Assert that select and canSelect have been
        // called exactly once.
        assertEquals(1, b.selectCount);
        assertEquals(1, b.canSelectCount);

        assertTrue(b.canSelect(1, 1));

        assertEquals(1, b.selectCount);
        assertEquals(2, b.canSelectCount);

        b.select(1, 1); 

        assertEquals(2, b.selectCount);
        assertEquals(2, b.canSelectCount); 
    }


    @Test
    public void testThatSelectCallsMove() {
        //System.out.println("Test 3: See comments in MisconceptionTests.java for description.");

        Board b = new Board(true);

        // Place a shield at position 0, 0.
        SpyPiece shield = new SpyPiece(true, b, 0, 0, "shield");
        b.place(shield, 0, 0);
        

        b.select(0, 0);
        assertEquals(0, shield.moveCount);
        b.select(1, 1); 
        assertNull(b.pieceAt(0, 0));
        assertEquals(1, shield.moveCount);
    }

 	public static class SpyBoard extends Board {
        public static int selectCount = 0;
        public static int canSelectCount = 0;

        public SpyBoard(boolean blank) {
            super(blank);
        }

        @Override
        public void select(int x, int y) {
            selectCount += 1;
            super.select(x, y);
        }

        @Override
        public boolean canSelect(int x, int y) {
            canSelectCount += 1;
            return super.canSelect(x, y);
        }
    }

    /* Special class that spies on your game. */
    public static class SpyPiece extends Piece {
        public static int moveCount = 0;

        public SpyPiece(boolean isFire, Board b, int x, int y, String type) {
            super(isFire, b, x, y, type);
        }

        @Override
        public void move(int x, int y) {
            moveCount += 1;
            super.move(x, y);
        }
    }


	public static void main(String... args) {
        jh61b.junit.textui.runClasses(TestBoard.class);
    }
}
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TestPiece {
	Board b;
	@Test
	public void testConstructer() {
		b = new Board(false);
		Piece p = new Piece(true, b, 0, 0, "pawn");
		assertEquals(true, p.isFire());
		assertEquals(0, p.side());
		assertEquals(false, p.isKing());
		assertEquals(false, p.isBomb());
		assertEquals(false, p.isShield());

	}

	@Test
	public void testIsKing() {
		b = new Board(true);
		Piece p = new Piece(true, b, 0, 6, "pawn");
		b.place(p, 0, 6);
		b.pieceAt(0, 6).move(1, 7);
		assertEquals(true, b.pieceAt(1, 7).isKing());

	}

	@Test
	public void testMove() {
		b = new Board(false);
		b.pieceAt(0, 2).move(1, 3);
		assertEquals(null, b.pieceAt(0, 2));
		assertEquals(true, b.pieceAt(1, 3).isFire());
		b.endTurn();
		b.pieceAt(1, 5).move(0, 4);
		assertEquals(null, b.pieceAt(1, 5));
		assertEquals(false, b.pieceAt(0, 4).isFire());
		b.endTurn();
		b.pieceAt(1, 3).move(2, 4);
		//assertTrue(b.canEndTurn());
		assertEquals(true, b.pieceAt(2, 4).isFire());
		assertEquals(null, b.pieceAt(1, 3));
		b.pieceAt(2, 4).move(3, 5);
		assertTrue(b.pieceAt(3, 5).isFire());
		assertNull(b.pieceAt(2, 4));

	}













	 public static void main(String... args) {
        jh61b.junit.textui.runClasses(TestPiece.class);
        

    }   
}
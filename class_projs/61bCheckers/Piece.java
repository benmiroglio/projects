public class Piece {
	private boolean isFire;
 	private Board board;
 	private int xpos, ypos;
 	private String type;
 	private boolean crowned = false;
 	private boolean captured = false;
//GRADE

	public Piece(boolean isFire, Board b, int x, int y, String type){
		this.isFire = isFire;
		this.board = b;
		this.xpos = x;
		this.ypos = y;
		this.type = type; 
	}

	public boolean isFire() {
		return isFire;
	}

	public int side() {
		if (isFire()) {
			return 0;
		}
		return 1;
	}


	//later
	public boolean isKing() {
		for(int i=0;i<8;i++) {
		if ((isFire() && board.pieceAt(i, 7) == this)) {
			crowned = true;
		}
		if ((!isFire() && board.pieceAt(i, 0) == this)) {
			crowned = true;
		}
	}
	return crowned;
}

	public boolean isBomb() {
		if (type == "bomb") {
			return true;
		}
		return false;
	}

	public boolean isShield() {
		if (type == "shield") {
			return true;
		}
		return false;
	}



	//later
	public void move(int x, int y) {
		if (xpos + 2 == x || ypos + 2 == y || xpos - 2 == x || ypos - 2 == y) {
			int middleX = (xpos+x)/2;
			int middleY = (ypos+y)/2;
			if (type == "bomb") {
				for(int i=x-1; i <= x+1; i++) {
					for(int j=y-1;j <= y+1;j++) {
					  if ((i+j) %2 == 0) {
						if(board.pieceAt(i, j) != null) {
								if (!board.pieceAt(i, j).isShield()) {
									board.remove(i, j);
								}
						}
					}
					}
				}
				board.remove(xpos, ypos);
			}
			board.remove(middleX, middleY);
			captured = true;
		}
		//board.select(x, y);
		board.place(board.pieceAt(xpos, ypos), x, y);
		int a = xpos, b = ypos;
		board.remove(a, b);
		xpos = x;
		ypos = y;

	
		/*b.place(b.pieceAt(1, 3), 2, 4);
		b.remove(1, 3);

		/*Piece s = stored[X][Y];
        place(s, (int) StdDrawPlus.mouseX(), (int) StdDrawPlus.mouseY());
        remove(X, Y);*/



		
	}

	//later
	public boolean hasCaptured() {
		return captured;
	}

	//later
	public void doneCapturing() {
		captured = false;
	}




}
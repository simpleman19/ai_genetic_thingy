package ai_genetic_thingy;

public class ai_genetic_thingy {
	
	public class Board {
		// Blank is *
		// Black is B
		// Red is R
		
		char[][] board = new char[6][7];
		
		public Board() {
			for (int i = 0; i < 6;  i++) {
				for (int j = 0; j < 7; j++) {
					board[i][j] = '*';
				}
			}
		}
		
		public boolean isWinner() {
			return false;
		}
		
		public boolean isDraw() {
			return false;
		}
		public char getWinner() {
			
			return 'b';
		}
		
		public int heuristic() {
			return 0;
		}
	}
	
	public void main(String [] args) {
		
		Board gameBoard = new Board();
		
		
		
	}
}

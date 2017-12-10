package ai_genetic_thingy;

import java.util.Random;
import java.util.Scanner;

public class ai_genetic_thingy {
	
	public char humanColor = 'B';
	
	private class Board {
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
		
		public boolean humanMove(int col) {			
			return move(col, humanColor);
		}
		
		public boolean computerMove(int col) {
			if (humanColor == 'B') {
				return move(col, 'R');
			} else {
				return move(col, 'B');
			}
		}
		
		private boolean move(int col, char player) {
			boolean inserted = false;
			
			for (int i = board.length - 1; i >= 0 && !inserted; i--) {
				if (this.board[i][col] == '*' ) {
					inserted = true;
					this.board[i][col] = player;
				}
			}
			
			return inserted;
		}
		
		public boolean isWinner() {
			return false;
		}
		
		public boolean isDraw() {
			return !this.isWinner() && this.isFull();
		}
		
		private boolean isFull() {
			boolean full = true;
			
			for (int i = 0; i < board.length; i++) {
				if (board[i][0] == '*') {
					full = false;
				}
			}
			
			return full;
		}
		
		public char getWinner() {
			
			return 'b';
		}
		
		public int heuristic() {
			return 0;
		}
		
		public char[][] toArray() {
			return this.board;
		}
	}
	
	public static void main(String [] args) {
		ai_genetic_thingy connect4 = new ai_genetic_thingy();
		connect4.playGame();
	}
	
	public void playGame() {
		
		Board gameBoard = new Board();
		int columnChoice = 0;
		Scanner s = new Scanner(System.in);
		
		System.out.print("Pick your color (R or B): ");
		String choice = s.nextLine();
		
		if (choice.toUpperCase().charAt(0) == 'R') {
			this.humanColor = 'R';
		} else if (choice.toUpperCase().charAt(0) == 'B') {
			this.humanColor = 'B';
		} else {
			System.out.println("You input a invalid character so you are using black");
		}
		
		while (!gameBoard.isWinner() && !gameBoard.isDraw()) {
			displayBoard(gameBoard);
			System.out.print("Please input the column you wish to play in: ");
			columnChoice = Integer.parseInt(s.nextLine()) - 1;
			
			if (columnChoice >= 0 && columnChoice < 7) {
				while(!gameBoard.humanMove(columnChoice)) {
					System.out.print("Could not move in column " + columnChoice + " input the column you wish to play in: ");
					columnChoice = Integer.parseInt(s.nextLine()) - 1;
				}
				columnChoice = this.generateComputerMove();
				while (!gameBoard.computerMove(columnChoice)) {
					columnChoice = this.generateComputerMove();
				}
			} else {
				System.out.println("Invalid Column");
			}
		}
		
		char winner = gameBoard.getWinner();
		
		if (winner == 'B') {
			System.out.println("Congratulations Black Player");
		} else if (winner == 'R') {
			System.out.println("Congratulations Red Player");
		}
		
	}
	
	public int generateComputerMove() {
		Random rand = new Random(System.currentTimeMillis());
		
		return rand.nextInt(7);
	}
	
	public static void displayBoard(Board board) {
		char[][] boardArray = board.toArray();
		
		for (int j = 0; j < boardArray[0].length; j++) {
			System.out.print(" " + (j+1) + " ");
			if (j != boardArray[0].length - 1) {
				System.out.print("|");
			}
		}
		
		System.out.println("");
		
		for (int i = 0; i < boardArray.length; i++) {
			for (int j = 0; j < boardArray[i].length; j++) {
				System.out.print(" " + boardArray[i][j] + " ");
				if (j != boardArray[i].length - 1) {
					System.out.print("|");
				}
			}
			System.out.println("");
		}
	}
}

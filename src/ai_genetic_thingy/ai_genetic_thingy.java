package ai_genetic_thingy;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ai_genetic_thingy {
	
	public char humanColor = 'B';
	
	private class Board {
		// Blank is *
		// Black is B
		// Red is R
		
		char[][] board = new char[6][7];
		char winner;
		
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
			boolean isWin = false;
			
			// Check for horizontal wins
			for (int row = 0; row < 6; row++) {
				for (int col = 0; col < 4; col++) {
					if (board[row][col] != '*' && board[row][col] == board[row][col+1] &&
							board[row][col] == board[row][col+2] && board[row][col] == board[row][col+3]) {
						isWin = true;
						setWinner(board[row][col]);
						return isWin;
					}
				}
			}
			
			// Check for vertical wins
			for (int col = 0; col < 7; col++) {
				for (int row = 0; row < 3; row++) {
					if (board[row][col] != '*' && board[row][col] == board[row+1][col] && 
							board[row][col] == board[row+2][col] && board[row][col] == board[row+3][col]) {
						isWin = true;
						setWinner(board[row][col]);
						return isWin;
					}
				}
			}
			
			// Check for descending diagonal wins
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 4; col++) {
					if (board[row][col] != '*' && board[row][col] == board[row+1][col+1] && 
							board[row][col] == board[row+2][col+2] && board[row][col] == board[row+3][col+3]) {
						isWin = true;
						setWinner(board[row][col]);
						return isWin;
					}
				}
			}
			
			// Check for ascending diagonal wins
			for (int col= 0; col < 4; col++) {
				for (int row = 5; row > 2; row--) {
					if (board[row][col] != '*' && board[row][col] == board[row-1][col+1] &&
							board[row][col] == board[row-2][col+2] && board[row][col] == board[row-3][col+3]) {
						isWin = true;
						setWinner(board[row][col]);
						return isWin;
					}
				}
			}
			
			return isWin;
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
			
			return winner;
		}
		
		public void setWinner(char winner) {
			this.winner = winner;
		}
		
		public int heuristic() {
			return 0;
		}
		
		public char[][] toArray() {
			return this.board;
		}
		
		public Board deepClone() {
			Board newBoard = new Board();
			for(int i = 0; i < board.length; i++) {
				newBoard.board[i] = board[i].clone();
			}
			return newBoard;
		}
	}
	
	private class Genetic {
		ArrayList<Board> rCurrentBest;
		ArrayList<Board> bCurrentBest;
		
		public Genetic() {
			rCurrentBest = new ArrayList<Board>();
		}
		
		public Board[] generateComputer() {
			return null;
		}
		
		public Board[] generateHuman() {
			return null;
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
				gameBoard = generateComputerMove(gameBoard);
			} else {
				System.out.println("Invalid Column");
			}
		}
		
		char winner = gameBoard.getWinner();
		
		displayBoard(gameBoard);
		
		if (winner == 'B') {
			System.out.println("Congratulations Black Player");
		} else if (winner == 'R') {
			System.out.println("Congratulations Red Player");
		}
		
	}
	
	public Board generateComputerMove(Board gameBoard) {
		Random rand = new Random(System.currentTimeMillis());
		
		int columnChoice = rand.nextInt(7);
		while (!gameBoard.computerMove(columnChoice)) {
			columnChoice = rand.nextInt(7);
		}
		
		return gameBoard.deepClone();
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

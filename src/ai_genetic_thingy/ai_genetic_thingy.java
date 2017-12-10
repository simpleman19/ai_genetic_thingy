package ai_genetic_thingy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class ai_genetic_thingy {
	
	public char humanColor = 'B';
	
	private class Board {
		// Blank is *
		// Black is B
		// Red is R
		// Human +
		// Computer -
		
		char[][] board = new char[6][7];
		char winner;
		int heuristic;
		
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
		
		public int recalculateHeuristic() {
			// Adds 5 points for two-in-a-row
			// Three-in-a-row is two two-in-a-rows
			// Adds Integer.MAX_VALUE for win
			int sum = 0;
			
			if (this.isWinner()) {
				if (this.isWinner()) {
					return Integer.MAX_VALUE;
				}
				else {
					// Check for horizontal two-in-a-rows
					for (int row = 0; row < 6; row++) {
						for (int col = 0; col < 6; col++) {
							if (board[row][col] != '*' && board[row][col] == board[row][col+1]) {
								sum += 5;
							}
						}
					}

					// Check for vertical two-in-a-rows
					for (int col = 0; col < 7; col++) {
						for (int row = 0; row < 5; row++) {
							if (board[row][col] != '*' && board[row][col] == board[row+1][col]) {
								sum += 5;
							}
						}
					}
					
					// Check for descending diagonal two-in-a-rows
					for (int row = 0; row < 5; row++) {
						for (int col = 0; col < 6; col++) {
							if (board[row][col] != '*' && board[row][col] == board[row+1][col+1]) {
								sum += 5;
							}
						}
					}
					
					// Check for ascending diagonal two-in-a-rows
					for (int col= 0; col < 6; col++) {
						for (int row = 5; row > 0; row--) {
							if (board[row][col] != '*' && board[row][col] == board[row-1][col+1]) {
								sum += 5;
							}
						}
					}
				}
			}
			this.heuristic = sum;
			
			return sum;
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
		ArrayList<Integer> hCurrentBest;
		ArrayList<Integer> cCurrentBest;
		
		public Genetic() {
			hCurrentBest = new ArrayList<Integer>();
			cCurrentBest = new ArrayList<Integer>();
		}
		
		public Board[] generateComputer(Board gameBoard, int keep, int count) {
			ArrayList<Board> boards = generate(gameBoard, cCurrentBest, keep, count, -1);
			
			return boards.toArray(new Board[cCurrentBest.size()]);
		}
		
		public Board[] generateHuman(Board gameBoard, int keep, int count) {
			ArrayList<Board> boards = generate(gameBoard, hCurrentBest, keep, count, 1);
			
			return boards.toArray(new Board[hCurrentBest.size()]);
		}
		
		private ArrayList<Board> generate(Board gameBoard, ArrayList<Integer> moves, int keep, int count, int playerMove) {
			
			PriorityQueue<Board> generated = new PriorityQueue<Board>();
			
			Board tempBoard;
			
			for (int move: moves) {
				tempBoard = gameBoard.deepClone();
				if (playerMove > 0) {
					tempBoard.humanMove(move);
				} else {
					tempBoard.computerMove(move);
				}
				generated.add(tempBoard);
			}
			
			
			return null;
		}
		
	}
	
	private class BoardComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Board b1 = (Board) o1;
			Board b2 = (Board) o2;
			
			if (b1.heuristic > b2.heuristic) {
				return 1;
			} else if (b1.heuristic < b2.heuristic) {
				return -1;
			} else {
				return 0;
			}
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

		s.close();
	}
	
	public Board generateComputerMove(Board gameBoard) {
		Random rand = new Random(System.currentTimeMillis());
		
<<<<<<< HEAD
<<<<<<< HEAD
		int columnChoice = rand.nextInt(7);
		while (!gameBoard.computerMove(columnChoice)) {
			columnChoice = rand.nextInt(7);
		}
		
		return gameBoard.deepClone();
=======
=======
		// Human: +
		// AI: -
		
>>>>>>> Updated heuristic
		// minimax
		
		return rand.nextInt(7);
>>>>>>> added comment
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

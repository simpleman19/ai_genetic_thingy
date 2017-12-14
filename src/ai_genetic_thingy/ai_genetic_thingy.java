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
		int previousMove;
		Genetic genetic;
		
		public Board() {
			for (int i = 0; i < 6;  i++) {
				for (int j = 0; j < 7; j++) {
					board[i][j] = '*';
				}
			}
			genetic = new Genetic();
		}
		
		public boolean humanMove(int col) {	
			previousMove = col;
			return move(col, humanColor);
		}
		
		public boolean computerMove(int col) {
			previousMove = col;
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
			
			this.recalculateHeuristic();
			
			return inserted;
		}
		
		public Board[] generate(int keep, int count, boolean humanMove) {
			if (humanMove) {
				return genetic.generateHuman(this, keep, count);
			} else {
				return genetic.generateComputer(this, keep, count);
			}
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
			
			newBoard.recalculateHeuristic();
			newBoard.previousMove = this.previousMove;
			
			return newBoard;
		}
	}
	
	private class Node {
		private Board board;
		private Node parent;
		private Node children[];
		int minMaxValue;
//		PriorityQueue<Node> children = new PriorityQueue<Node>(11, new Comparator<Node>() {
//			public int compare(Node i, Node j) {
//				if(i.board.heuristic > j.board.heuristic) {
//					return 1;
//				}
//				else if (i.board.heuristic < j.board.heuristic) {
//					return -1;
//				}
//				else {
//					return 0;
//				}
//			}
//		});
		
		public Node() {
			parent = null;
		}
		
		public Node(Board board) {
			this.board = board;
		}

		public Board getBoard() {
			return this.board;
		}
		
		public void setBoard(Board board) {
			this.board = board;
		}
		
		public Node getParent() {
			return this.parent;
		}
		
		public void setParent(Node parent) {
			this.parent = parent;
		}
	}
	
	private class Genetic {
		public ArrayList<Integer> hCurrentBest;
		public ArrayList<Integer> cCurrentBest;
		Random rand;
		
		public Genetic() {
			hCurrentBest = new ArrayList<Integer>();
			cCurrentBest = new ArrayList<Integer>();
			rand = new Random(System.currentTimeMillis());
		}
		
		public Board[] generateComputer(Board gameBoard, int keep, int count) {
			PriorityQueue<Board> boards = generate(gameBoard, cCurrentBest, count, -1);
			ArrayList<Board> culledBoards = cullList(boards, keep);
			ArrayList<Integer> currentBest = getMoves(culledBoards);
			
			for (Board board : culledBoards) {
				board.genetic = new Genetic();
				board.genetic.hCurrentBest = currentBest;
				board.genetic.cCurrentBest = this.cCurrentBest;
			}
			
			return culledBoards.toArray(new Board[culledBoards.size()]);
		}
		
		public Board[] generateHuman(Board gameBoard, int keep, int count) {
			PriorityQueue<Board> boards = generate(gameBoard, hCurrentBest, count, 1);
			ArrayList<Board> culledBoards = cullList(boards, keep);
			ArrayList<Integer> currentBest = getMoves(culledBoards);
			
			for (Board board : culledBoards) {
				board.genetic = new Genetic();
				board.genetic.cCurrentBest = currentBest;
				board.genetic.hCurrentBest = this.hCurrentBest;
			}
			
			return culledBoards.toArray(new Board[culledBoards.size()]);
		}
		
		private PriorityQueue<Board> generate(Board gameBoard, ArrayList<Integer> moves, int count, int playerMove) {
			
			PriorityQueue<Board> generated = new PriorityQueue<Board>(count + 5, new BoardComparator((playerMove > 0) ? false : true));
			
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
			
			for (int i = 0; i < (count - moves.size()); i++) {
				tempBoard = gameBoard.deepClone();
				if (playerMove > 0) {					
					tempBoard.humanMove(genetic(moves));
				} else {
					tempBoard.computerMove(genetic(moves));
				}
				generated.add(tempBoard);
			}
			
			return generated;
		}
		
		private int genetic(ArrayList<Integer> moves) {
			int decision = rand.nextInt(3);
			int move = 0;
			
			switch (decision) {
				case 0:
					// Get center between 2 moves
					move = center(moves.get(rand.nextInt(moves.size())), moves.get(rand.nextInt(moves.size())));
					break;
				case 1:
					move = rand.nextInt(7);
					break;
				case 2:
					move = moves.get(rand.nextInt(moves.size())) + rand.nextInt(7) % 7;
					break;
			}
			
			return move;
		}
		
		private int center(int num1, int num2) {
			return (int) Math.round((num2 - num1) / 2.0 + num1);
		}
		
		private ArrayList<Integer> getMoves(ArrayList<Board> boards) {
			ArrayList<Integer> moves = new ArrayList<Integer>();
			
			for (Board board: boards) {
				moves.add(board.previousMove);
			}
			
			return moves;
		}
		
		private ArrayList<Board> cullList(PriorityQueue<Board> boards, int number) {
			ArrayList<Board> list = new ArrayList<Board>();
			
			for (int i = 0; i < number; i++) {
				list.add(boards.remove());
			}
			
			return list;
		}
	}
	
	private class BoardComparator implements Comparator<Object> {
		boolean reverse = false;
		
		public BoardComparator() {
			reverse = false;
		}
		
		public BoardComparator(boolean reverse) {
			this.reverse = reverse;
		}
		
		public int compare(Object o1, Object o2) {
			Board b1 = (Board) o1;
			Board b2 = (Board) o2;
			
			if (b1.heuristic > b2.heuristic) {
				return (reverse) ? -1 : 1;
			} else if (b1.heuristic < b2.heuristic) {
				return (reverse) ? 1 : -1;
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
	
	public Board generateComputerMove(Board board) {
		int count = 100;
		int keep = 10;
		
		Board computerMove = null;
		Node curNode = new Node();
		int maxDepth = 4;
		int curDepth = 0;
		Genetic genetic = new Genetic();
		Board temp1[], temp2[], temp3[], temp4[];
		
		Node rootNode = new Node(board);
		curNode = rootNode;
		temp1 = genetic.generateComputer(board, keep, count);	// store boards in temp board array
		curNode.children = new Node[temp1.length];	// set node array size to temp array size
		for (int i = 0; i < temp1.length; i++) {		// generate depth 1 children nodes
			curNode.children[i] = new Node(temp1[i]);
			curNode.children[i].parent = curNode;
			temp2 = genetic.generateHuman(curNode.children[i].getBoard(), keep, count);
			
			for (int j = 0; j < temp2.length; j++) {	// generate depth 2 children
				curNode.children[i].children[j] = new Node(temp2[j]);
				curNode.children[i].children[j].parent = curNode.children[i];
				temp3 = genetic.generateComputer(curNode.children[i].children[j].getBoard(), keep, count);
				
				for (int k = 0; k < temp3.length; k++) {	// generate depth 3 children
					curNode.children[i].children[j].children[k] = new Node(temp3[k]);
					curNode.children[i].children[j].children[k].parent = curNode.children[i].children[j];
					temp4 = genetic.generateHuman(curNode.children[i].children[j].children[k].getBoard(), keep, count);
					
					for (int l = 0; l < temp4.length; l++) {	// generate depth 4 children
						curNode.children[i].children[j].children[k].children[l] = new Node(temp4[l]);
						curNode.children[i].children[j].children[k].children[l].parent = curNode.children[i].children[j].children[k];
						temp4 = genetic.generateComputer(curNode.children[i].children[j].children[k].children[l].getBoard(), keep, count);
					}
				}
			}
		}
		
		// heuristic calculation of leaves
//		for (int i = 0; i < rootNode.children.length; i++) {
//			for (int j = 0; j < rootNode.children[i].children.length; j++) {
//				for (int k = 0; k < rootNode.children[i].children[j].children.length; k++) {
//					for (int l = 0; l < rootNode.children[i].children[j].children[k].children.length; l++) {
//						rootNode.children[i].children[j].children[k].children[l].board.recalculateHeuristic();
//					}	
//				}	
//			}
//		}
		
		// min/max
		int max = 0;
		int min = rootNode.children[0].getBoard().heuristic;
		for (int i = 0; i < rootNode.children.length; i++) {
			max = rootNode.children[i].children[0].getBoard().heuristic;
			for (int j = 0; j < rootNode.children[i].children.length; j++) {
				min = rootNode.children[i].children[j].children[0].getBoard().heuristic;
				for (int k = 0; k < rootNode.children[i].children[j].children.length; k++) {
					max = rootNode.children[i].children[j].children[k].children[0].getBoard().heuristic;
					for (int l = 0; l < rootNode.children[i].children[j].children[k].children.length; l++) {
						if (rootNode.children[i].children[j].children[k].children[l].getBoard().heuristic > max) {
							max = rootNode.children[i].children[j].children[k].children[l].getBoard().heuristic;
						}
					}
					rootNode.children[i].children[j].children[k].minMaxValue = max;
					if (rootNode.children[i].children[j].children[k].getBoard().heuristic < min) {
						min = rootNode.children[i].children[j].children[k].getBoard().heuristic;
					}
				}
				rootNode.children[i].children[j].minMaxValue = min;
				if (rootNode.children[i].children[j].getBoard().heuristic > max) {
					max = rootNode.children[i].children[j].getBoard().heuristic;
				}
			}
			rootNode.children[i].minMaxValue = max;
			if (rootNode.children[i].getBoard().heuristic < min) {
				min = rootNode.children[i].getBoard().heuristic;
				computerMove = rootNode.children[i].getBoard();
			}
		}
		
		
//		// if winning AI move is available
//		if (rootNode.children.peek().board.heuristic == Integer.MIN_VALUE) {
//			computerMove = rootNode.children.poll().board;
//		}
//		else {
//			for (int i = 0; i < rootNode.children.size(); i++) {
//				
//				for (int j = 0; j < rootNode.children.)
//			}
//		}
//		
//		if (curDepth == maxDepth) {
//			
//		}
//		
		return computerMove;
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

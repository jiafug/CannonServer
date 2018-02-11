package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.HashSet;

public class Board implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8109492181730182687L;

	private int[][] board = new int[10][10];
	private CannonGame game;
	private final String letterIndex = "abcdefghij";
	private HashSet<String> possibleMoves;

	public Board(CannonGame game) {
		this.game = game;
		this.possibleMoves = new HashSet<>();
	}

	// [0][0] ist das Feld oben links, also a9, [9][9] ist j0 um mit dem
	// FenString konform zu sein.
	// [0][1] ist demnach b9 und so weiter.
	// 0 ist freies Feld, 1 ist Soldat weiss, 2 ist Stadt weiss, fuer Schwarz
	// negative Zahlen.

	public String getBoard() {
		String result = "";
		for (int i = 0; i < 10; i++) {
			String tmp = "";
			for (int j = 0; j < 10; j++) {
				if (board[i][j] == 0)
					tmp = tmp + "1";
				if (board[i][j] == 1)
					tmp = tmp + "w";
				if (board[i][j] == 2)
					tmp = tmp + "W";
				if (board[i][j] == -1)
					tmp = tmp + "b";
				if (board[i][j] == -2)
					tmp = tmp + "B";
			}
			if (tmp.contentEquals("1111111111"))
				tmp = "/";
			else
				tmp = tmp + "/";
			result = result + tmp;
		}
		return trimFenString(result.substring(0, result.length() - 1));
	}

	/**
	 * adds all sequences of ones to get a shorter FEN string, in preparation
	 * for the jUnit tests
	 */
	private String trimFenString(String toTrim) {
		String result = "";
		String[] seq = toTrim.split("/");
		for (String row : seq) {
			char[] rowArray = row.toCharArray();
			int counter = 0;
			for (int i = 0; i < rowArray.length; i++) {
				if (rowArray[i] == '1')
					counter++;
				else if (counter != 0) {
					result += counter;
					result += rowArray[i];
					counter = 0;
				} else
					result += rowArray[i];
			}
			if (rowArray.length != 0 && rowArray[rowArray.length - 1] == '1')
				result += counter;
			result = result + "/";
		}
		if (seq.length == 10)
			return result.substring(0, result.length() - 1);
		else
			return result;
	}

	public void setBoard(String fen) {
		this.board = new int[10][10];
		this.possibleMoves = new HashSet<>();
		int iCount = 0;
		int jCount = 0;
		for (int f = 0; f < fen.length(); f++) {
			char c = fen.charAt(f);
			int space = Character.getNumericValue(fen.charAt(f));
			// eine Reihe Null setzen
			if (c == '/' && jCount == 0) setRowNull(iCount, jCount, board);
			// eine Reihe weiter am Ende
			if (c == '/' && jCount != 0) oneFurther(iCount, jCount, board);
			// Null Feld
			if (space > 0 && space < 10) nullField(space, iCount, jCount, board);
			// Schwarz Soldat
			if (c == 'b') blackSoldier(iCount, jCount, board);
			// Schwarz Stadt
			if (c == 'B') blackCity(iCount, jCount, board);
			// Weiss Soldat
			if (c == 'w') whiteSoldier(iCount, jCount, board);
			// Weiss Stadt
			if (c == 'W') {
				board[iCount][jCount] = 2;
				jCount++;
			}
		}
	}
	
	public void setRowNull(int iCount, int jCount, int[][] board){
		for (int j = 0; j < 10; j++) {
			board[iCount][jCount] = 0;
		}
		iCount++;
	}
	
	public void oneFurther(int iCount, int jCount, int[][] board){
		iCount++;
		jCount = 0;
	}
	
	public void nullField(int space, int iCount, int jCount, int [][] board){
		for (int i = 0; i < space; i++) {
			board[iCount][jCount] = 0;
			jCount++;
		}}
	public void blackSoldier(int iCount, int jCount, int[][] board){
		board[iCount][jCount] = -1;
		jCount++;
	}

	public void blackCity(int iCount, int jCount,int[][] board) {
		board[iCount][jCount] = -2;
		jCount++;
	}
	
	public void whiteSoldier(int iCount, int jCount,int[][] board){
		board[iCount][jCount] = 1;
		jCount++;
	}
	/**
	 * sets the town by changing the 2D board array
	 */
	public void setTown(String move) {
		int xAxis = letterIndex.indexOf(move.charAt(0));
		int yAxis = Math.abs(Character.getNumericValue(move.charAt(1)) - 9);
		if (game.isWhiteNext())
			board[yAxis][xAxis] = 2;
		else
			board[yAxis][xAxis] = -2;
	}

	/**
	 * checks and performs a move by changing the 2D board array
	 */
	public boolean performMove(String move) {
		// Berechnet die Position im board-Array
		if (this.possibleMoves.contains(move)) {
			this.possibleMoves.clear();
			int startX = letterIndex.indexOf(move.charAt(0));
			int startY = Math.abs(Character.getNumericValue(move.charAt(1)) - 9);
			int destX = letterIndex.indexOf(move.charAt(3));
			int destY = Math.abs(Character.getNumericValue(move.charAt(4)) - 9);
			// Ueberprueft ob die zu bewegene Figur die richtige Farbe hat und
			// welche Zuege moeglich sind
			if (cannonAttack(startX, startY, destX, destY)) {
				board[destY][destX] = 0;
				return true;
			} else if (game.isWhiteNext()) {
				board[startY][startX] = 0;
				board[destY][destX] = 1;
				return true;
			} else {
				board[startY][startX] = 0;
				board[destY][destX] = -1;
				return true;
			}
		} else
			return false;
	}

	/**
	 * finds and saves all possible moves in a hash map
	 */
	public HashSet<String> allPossibleMoves() {
		String move = "";
		for (char c : letterIndex.toCharArray()) {
			for (int i = 0; i < 10; i++) {
				move = String.valueOf(c) + i + "-";
				allPossibleMovesHelper(move);
				move = "";
			}
		}
		return this.possibleMoves;
	}

	/**
	 * method to reduce the nested block depth of anyMovePossible()
	 */
	private void allPossibleMovesHelper(String move) {
		for (char c : letterIndex.toCharArray()) {
			for (int i = 0; i < 10; i++) {
				move += String.valueOf(c) + i;
				if (checkMove(move))
					possibleMoves.add(move);
				move = move.substring(0, 3);
			}
		}
	}

	/**
	 * checks weather a move is possible
	 */
	private boolean checkMove(String move) {
		int startX = letterIndex.indexOf(move.charAt(0));
		int startY = Math.abs(Character.getNumericValue(move.charAt(1)) - 9);
		int destX = letterIndex.indexOf(move.charAt(3));
		int destY = Math.abs(Character.getNumericValue(move.charAt(4)) - 9);
		if (checkWhite(startX, startY, destX, destY)) {
			return true;
		} else if (checkBlack(startX, startY, destX, destY)) {
			return true;
		} else if (cannonAttack(startX, startY, destX, destY)) {
			return true;
		} else
			return false;
	}

	/**
	 * method to reduce the cyclomatic complexity of performMove(String move)
	 */
	private boolean checkWhite(int startX, int startY, int destX, int destY) {
		return (game.isWhiteNext() && (board[startY][startX] == 1)
				&& (simpleMove(startX, startY, destX, destY) || attackMove(startX, startY, destX, destY)
						|| retreatMove(startX, startY, destX, destY) || cannonMove(startX, startY, destX, destY)));
	}

	/**
	 * method to reduce the cyclomatic complexity of performMove(String move)
	 */
	private boolean checkBlack(int startX, int startY, int destX, int destY) {
		return (!game.isWhiteNext() && (board[startY][startX] == -1)
				&& (simpleMove(startX, startY, destX, destY) || attackMove(startX, startY, destX, destY)
						|| retreatMove(startX, startY, destX, destY) || cannonMove(startX, startY, destX, destY)));
	}

	/**
	 * checks if a simple move is possible
	 */
	private boolean simpleMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob auf dem Zielfeld sich Feind befindet und ob der
		// Schritt ein
		// Feld geradeaus oder diagonal verlaeuft
		int var;
		if (game.isWhiteNext())
			var = -1;
		else
			var = 1;
		return ((board[destY][destX] == 0 || ((board[destY][destX] == var) || board[destY][destX] == var * 2))
				&& (destX == startX + 1 || destX == startX - 1 || destX == startX) && (destY == startY + (-1 * var)));
	}

	/**
	 * checks if a attack move is possible
	 */
	private boolean attackMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob sich auf dem Zielfeld ein Feind befindet und ob das
		// Zielfeld ein Feld rechts oder links ist
		int var;
		if (game.isWhiteNext())
			var = -1;
		else
			var = 1;
		if (board[destY][destX] == var * 2)
			var = var * 2;
		return (destX == startX - 1 || destX == startX + 1) && (destY == startY) && (board[destY][destX] == var);
	}

	/**
	 * checks if a retreat move is possible
	 */
	private boolean retreatMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob das Zielfeld leer ist und keine Figur im Weg steht und
		// ob der Schritt genau zwei Felder nach hinten gerade oder diagonal
		// verlaeuft
		int var;
		if (game.isWhiteNext())
			var = -1;
		else
			var = 1;
		return (board[destY][destX] == 0
				&& (destY == startY + (2 * var) && (index(startX, startY + 1) == var || index(startX - 1, startY) == var
						|| index(startX - 1, startY + 1) == var || index(startX + 1, startY) == var
						|| index(startX + 1, startY + 1) == var || index(startX, startY - 1) == var
						|| index(startX - 1, startY - 1) == var || index(startX + 1, startY - 1) == var))
				&& (destX == startX - 2 || destX == startX + 2 || destX == startX)
				&& board[(destY + startY) / 2][(destX + startX) / 2] == 0);
	}

	private int index(int x, int y) {
		if (x < 0 || x > 9 || y < 0 || y > 9)
			return 3;
		else
			return board[y][x];
	}

	/**
	 * checks if a cannon move is possible
	 */
	private boolean cannonMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob auf den naechsten zwei Feldern eigene Soladaten stehen
		// und das Zielfeld leer ist.
		// Rotiert im Uhrzeigersinn um alle moeglichen Richtungen abzudecken.
		int var;
		if (game.isWhiteNext())
			var = 1;
		else
			var = -1;
		return (board[destY][destX] == 0 && (((destY - startY == 3) && (destX == startX)
				&& board[startY + 1][startX] == var && board[startY + 2][startX] == var
				|| (destY - startY == 3) && (destX - startX == 3) && board[startY + 1][startX + 1] == var
						&& board[startY + 2][startX + 2] == var
				|| (destY == startY) && (destX - startX == 3) && board[startY][startX + 1] == var
						&& board[startY][startX + 2] == var
				|| (destY - startY == -3) && (destX - startX == 3) && board[startY - 1][startX + 1] == var
						&& board[startY - 2][startX + 2] == var
				|| (destY - startY == -3) && (destX == startX) && board[startY - 1][startX] == var
						&& board[startY - 2][startX] == var
				|| (destY - startY == -3) && (destX - startX == -3) && board[startY - 1][startX - 1] == var
						&& board[startY - 2][startX - 2] == var
				|| (destY == startY) && (destX - startX == -3) && board[startY][startX - 1] == var
						&& board[startY][startX - 2] == var
				|| (destY - startY == 3) && (destX - startX == -3) && board[startY + 1][startX - 1] == var
						&& board[startY + 2][startX - 2] == var)));
	}

	/**
	 * checks if a cannon attack is possible
	 */
	private boolean cannonAttack(int startX, int startY, int destX, int destY) {
		int var;
		if (game.isWhiteNext())
			var = -1;
		else
			var = 1;
		return (((board[destY][destX] == var || board[destY][destX] == var * 2)
				&& (((destY == startY - 4 || destY == startY - 5) && destX == startX
						&& board[startY - 1][startX] == -1 * var && board[startY - 2][startX] == -1 * var
						&& board[startY - 3][startX] == 0)
						|| (((destY == startY - 4 && destX == startX + 4)
								|| (destY == startY - 5 && destX == startX + 5))
								&& board[startY - 1][startX + 1] == -1 * var
								&& board[startY - 2][startX + 2] == -1 * var && board[startY - 3][startX + 3] == 0)
						|| (destY == startY
								&& (destX == startX + 4 || destX == startX + 5) && board[startY][startX + 1] == -1 * var
								&& board[startY][startX + 2] == -1 * var && board[startY][startX + 3] == 0)
						|| (((destY == startY + 4 && destX == startX + 4)
								|| (destY == startY + 5 && destX == startX + 5))
								&& board[startY + 1][startX + 1] == -1 * var
								&& board[startY + 2][startX + 2] == -1 * var && board[startY + 3][startX + 3] == 0)
						|| ((destY == startY + 4 || destY == startY + 5)
								&& destX == startX && board[startY + 1][startX] == -1 * var
								&& board[startY + 2][startX] == -1 * var && board[startY + 3][startX] == 0)
						|| (((destY == startY + 4 && destX == startX - 4)
								|| (destY == startY + 5 && destX == startX - 5))
								&& board[startY + 1][startX - 1] == -1 * var
								&& board[startY + 2][startX - 2] == -1 * var && board[startY + 3][startX - 3] == 0)
						|| (destY == startY && (destX == startX - 4 || destX == startX - 5)
								&& board[startY][startX - 1] == -1 * var && board[startY][startX - 2] == -1 * var
								&& board[startY][startX - 3] == 0)
						|| (((destY == startY - 4 && destX == startX - 4)
								|| (destY == startY - 5 && destX == startX - 5))
								&& board[startY - 1][startX - 1] == -1 * var
								&& board[startY - 2][startX - 2] == -1 * var && board[startY - 3][startX - 3] == 0)))
				&& board[startY][startX] == -1 * var);
	}

}

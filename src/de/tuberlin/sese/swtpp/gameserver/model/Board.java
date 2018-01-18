package de.tuberlin.sese.swtpp.gameserver.model;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;

public class Board implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8109492181730182687L;

	protected int[][] board = new int[10][10];
	private CannonGame game;
	private final String letterIndex = "abcdefghij";

	public Board(CannonGame game) {
		this.game = game;
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
				else if (board[i][j] == 1)
					tmp = tmp + "w";
				else if (board[i][j] == 2)
					tmp = tmp + "W";
				else if (board[i][j] == -1)
					tmp = tmp + "b";
				else if (board[i][j] == -2)
					tmp = tmp + "B";
			}
			if (tmp.contentEquals("1111111111"))
				tmp = "/";
			else
				tmp = tmp + "/";
			result = result + tmp;
		}
		return result.substring(0, result.length() - 1);
	}

	public void setBoard(String fen) {
		int iCount = 0;
		int jCount = 0;
		for (int f = 0; f < fen.length(); f++) {
			char c = fen.charAt(f);
			// eine Reihe Null setzen
			if (c == '/' && jCount == 0) {
				for (int j = 0; j < 10; j++) {
					board[iCount][jCount] = 0;
				}
				iCount++;
			}
			// eine Reihe weiter am Ende
			else if (c == '/' && jCount != 0) {
				iCount++;
				jCount = 0;
			}
			// Null Feld
			else if (c == '1') {
				board[iCount][jCount] = 0;
				jCount++;
			}
			// Schwarz Soldat
			else if (c == 'b') {
				board[iCount][jCount] = -1;
				jCount++;
			}
			// Schwarz Stadt
			else if (c == 'B') {
				board[iCount][jCount] = -2;
				jCount++;
			}
			// Weiss Soldat
			else if (c == 'w') {
				board[iCount][jCount] = 1;
				jCount++;
			}
			// Weiss Stadt
			else if (c == 'W') {
				board[iCount][jCount] = 2;
				jCount++;
			}
		}
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
		int startX = letterIndex.indexOf(move.charAt(0));
		int startY = Math.abs(Character.getNumericValue(move.charAt(1)) - 9);
		int destX = letterIndex.indexOf(move.charAt(3));
		int destY = Math.abs(Character.getNumericValue(move.charAt(4)) - 9);
		// Ueberprueft ob die zu bewegene Figur die richtige Farbe hat und
		// welche Zuege moeglich sind
		if (checkWhite(startX, startY, destX, destY)) {
			board[startY][startX] = 0;
			board[destY][destX] = 1;
			return true;
		} else if (checkBlack(startX, startY, destX, destY)) {
			board[startY][startX] = 0;
			board[destY][destX] = -1;
			return true;
		} else
			return false;
	}

	/**
	 * method to reduce the cyclomatic complexity of performMove(String move)
	 */
	private boolean checkWhite(int startX, int startY, int destX, int destY) {
		return (game.isWhiteNext() && (board[startY][startX] == 1) && (simpleMove(startX, startY, destX, destY)
				|| attackMove(startX, startY, destX, destY) || retreatMove(startX, startY, destX, destY)));
	}

	/**
	 * method to reduce the cyclomatic complexity of performMove(String move)
	 */
	private boolean checkBlack(int startX, int startY, int destX, int destY) {
		return (!game.isWhiteNext() && (board[startY][startX] == -1) && (simpleMove(startX, startY, destX, destY)
				|| attackMove(startX, startY, destX, destY) || retreatMove(startX, startY, destX, destY)));
	}

	/**
	 * checks if a simple move is possible
	 */
	private boolean simpleMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob auf dem Zielfeld sich Feind befindet und ob der
		// Schritt ein
		// Feld geradeaus oder diagonal verlaeuft
		return ((board[destY][destX] == 0
				|| (game.isWhiteNext() && (board[destY][destX] == -1) || board[destY][destX] == -2)
				|| (!game.isWhiteNext() && (board[destY][destX] == 1 || board[destY][destX] == 2)))
				&& (destX == startX + 1 || destX == startX - 1 || destX == startX)
				&& ((game.isWhiteNext() && destY == startY + 1) || (!game.isWhiteNext() && destY == startY - 1)));
	}

	/**
	 * checks if a attack move is possible
	 */
	private boolean attackMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob sich auf dem Zielfeld ein Feind befindet und ob das
		// Zielfeld ein Feld rechts oder links ist
		return (destX == startX - 1 || destX == startX + 1) && (destY == startY)
				&& ((game.isWhiteNext() && board[destY][destX] == -1 || board[destY][destX] == -2)
						|| (!game.isWhiteNext() && (board[destY][destX] == 1 || board[destY][destX] == 2)));
	}

	/**
	 * checks if a retreat move is possible
	 */
	private boolean retreatMove(int startX, int startY, int destX, int destY) {
		// Ueberprueft ob das Zielfeld leer ist und keine Figur im Weg steht und
		// ob der Schritt genau zwei Felder nach hinten gerade oder diagonal
		// verlaeuft
		return (board[destY][destX] == 0
				&& ((game.isWhiteNext() && destY == startY - 2
						&& (board[startY + 1][startX] == -1 || board[startY][startX - 1] == -1
								|| board[startY + 1][startX - 1] == -1 || board[startY][startX + 1] == -1
								|| board[startY + 1][startX + 1] == -1 || board[startY - 1][startX] == -1
								|| board[startY - 1][startX - 1] == -1 || board[startY - 1][startX + 1] == -1))
						|| (!game.isWhiteNext() && destY == startY + 2
								&& (board[startY - 1][startX] == 1 || board[startY][startX + 1] == 1
										|| board[startY - 1][startX + 1] == 1 || board[startY][startX - 1] == 1
										|| board[startY - 1][startX - 1] == 1 || board[startY + 1][startX] == -1
										|| board[startY + 1][startX - 1] == -1 || board[startY + 1][startX + 1] == -1)))
				&& (destX == startX - 2 || destX == startX + 2 || destX == startX)
				&& board[(destY + startY) / 2][(destX + startX) / 2] == 0);
	}

}

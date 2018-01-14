package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance
 * that allows to play Lasca (http://www.lasca.org/).
 *
 */
public class CannonGame extends Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign white and black
	// player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	private String boardStatus;
	private boolean whiteTownSet;
	private boolean blackTownSet;
	private ArrayList<String> whiteTownPos;
	private ArrayList<String> blackTownPos;

	/************************
	 * constructors
	 ***********************/

	public CannonGame() {
		super();
		// set start position of all pieces
		boardStatus = "/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/";
		whiteTownSet = false;
		blackTownSet = false;
		// defines all possible positions of the towns
		whiteTownPos = new ArrayList<>();
		blackTownPos = new ArrayList<>();
		String[] whitePosArray = new String[] { "b9-b9", "c9-c9", "d9-d9", "e9-e9", "f9-f9", "g9-g9", "h9-h9",
				"i9-i9" };
		String[] blackPosArray = new String[] { "b0-b0", "c0-c0", "d0-d0", "e0-e0", "f0-f0", "g0-g0", "h0-h0",
				"i0-i0" };
		whiteTownPos.addAll(Arrays.asList(whitePosArray));
		blackTownPos.addAll(Arrays.asList(blackPosArray));
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = this.whitePlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (whiteGaveUp())
				gameInfo = "white gave up";
			else if (didWhiteDraw() && !didBlackDraw())
				gameInfo = "white called draw";
			else if (!didWhiteDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "white won";
		}

		return gameInfo;
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) {
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();

			return true;
		}

		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/

	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}

	/**
	 * Switch next player
	 */
	private void updateNext() {
		if (nextPlayer == whitePlayer)
			nextPlayer = blackPlayer;
		else
			nextPlayer = whitePlayer;
	}

	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();

			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		System.out.println("state: " + state);
	}

	@Override
	public String getBoard() {
		return boardStatus;
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		System.out.println("moveString: " + moveString);
		System.out.println("Player: " + player.getName());
		if (!whiteTownSet || !blackTownSet) {
			if (player.equals(whitePlayer) && whiteTownPos.contains(moveString)) {
				boardStatus = "W9" + boardStatus;
				whiteTownSet = true;
			} else if (player.equals(blackPlayer) && blackTownPos.contains(moveString)) {
				boardStatus = boardStatus + "3B6";
				blackTownSet = true;
			}
		}
		updateNext();
		return false;
	}

}

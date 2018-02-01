package de.tuberlin.sese.swtpp.gameserver.test.cannon;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");

	Player whitePlayer = null;
	Player blackPlayer = null;
	CannonGame game = null;
	GameController controller;

	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();

		int gameID = controller.startGame(user1, "");

		game = (CannonGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}

	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);
		blackPlayer = game.getPlayer(user2);

		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext ? whitePlayer : blackPlayer);
	}

	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else
			assertEquals(expectedResult, game.tryMove(move, blackPlayer));
	}

	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(expectedBoard, game.getBoard().replaceAll("e", ""));
		assertEquals(whiteNext, game.isWhiteNext());

		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(whiteNext, game.isWhiteNext());
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/

	@Test
	public void exampleTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("h6-h5", true, true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w3w/2w4w2/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false, false,
				false);
	}

	// TODO: implement test cases of same kind as example here

	@Test
	public void setTownTest() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", true);
		assertMove("a8-a8", true, false);
		assertMove("c9-c9", true, true);
		assertMove("d3-d3", false, false);
		assertMove("f0-f0", false, true);
		assertGameState("2W7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/5B4", true, false,
				false);
	}

	@Test
	public void moveTest1() {
		startGame("2W7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/5B4", true);
		assertMove("d8-d5", true, true);
		assertMove("g1-f2", false, true);
		assertMove("d7-d6", true, false);
		assertMove("f6-e5", true, true);
		assertMove("e1-e5", false, true);
		assertGameState("2W7/1w3w1w1w/1w1w1w1w1w/1w1w3w1w/3w6//b1b1b1b1b1/b1b1bbb1b1/b1b1b3b1/5B4", true, false, false);
	}

	@Test
	public void moveTest2() {
		startGame("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5", true);
		assertMove("f7-e6", true, true);
		assertMove("e3-f4", false, true);
		assertMove("f6-f5", true, true);
		assertMove("g1-f2", false, true);
		assertMove("f5-e4", true, true);
		assertMove("f4-e5", false, true);
		assertMove("d6-d5", true, true);
		assertMove("c1-d2", false, true);
		assertMove("f8-f7", true, true);
		assertMove("c3-c4", false, true);
		assertGameState("4W5/1w1w3w1w/1w1w1w1w1w/1w2w2w1w/3wb5/2b1w5/b5b1b1/b1bbbbb1b1/b3b3b1/4B5", true, false, false);
	}

	@Test
	public void moveTest3() {
		startGame("4W5/1w1w3w1w/1w1w3w1w/1w3w1w1w/3bwww3/4b5/b1b1b1b1b1/b1b1b1b1b1/b5b1b1/4B5", false);
		assertMove("c2-d3", false, true);
		assertMove("f5-e4", true, true);
		assertMove("c3-d4", false, true);
		assertMove("d7-e6", true, true);
		assertMove("d5-e6", false, true);
		assertMove("d8-e7", true, true);
		assertMove("e6-e7", false, true);
		assertMove("e4-d4", true, true);
		assertMove("e7-d8", false, true);
		assertMove("e5-e4", true, true);
		assertMove("d8-e9", false, true);
		assertGameState("4b5/1w5w1w/1w5w1w/1w3w1w1w/6w3/3ww5/b2bb1b1b1/b3b1b1b1/b5b1b1/4B5", true, true, false);
	}

	@Test
	public void moveTest4() {
		startGame("4W5/1w3w1w1w/1w1ww2w1w/1w1w2ww1w/6w3/7b2/b1b1b3b1/b1b1bbb1bb/b1b3b3/3B6", true);
		assertMove("g5-f4", true, true);
		assertMove("j2-g5", false, true);
		assertMove("g6-g5", true, true);
		assertMove("c1-d2", false, true);
		assertMove("f4-i7", true, true);
		assertMove("e3-e4", false, true);
		assertMove("h8-g7", true, true);
		assertMove("d2-d3", false, true);
		assertMove("h7-g6", true, true);
		assertMove("c2-f5", false, true);
		assertMove("e7-e6", true, true);
		assertMove("h4-i5", false, true);
		assertMove("g6-f5", true, true);
		assertMove("g2-g3", false, true);
		assertMove("g5-g4", true, true);
		assertMove("e2-e3", false, true);
		assertMove("f5-f4", true, true);
		assertMove("g3-g4", false, true);
		assertMove("f4-f3", true, true);
		assertMove("c3-f3", false, false);
		assertMove("f2-f3", false, true);
		assertMove("b8-b5", true, true);
		assertMove("f3-f4", false, true);
		assertMove("b5-b4", true, true);
		assertMove("i3-i4", false, true);
		assertMove("b4-b3", true, true);
		assertGameState("4W5/5w3w/1w1w2w1ww/1w1ww2w1w/8b1/4bbb1b1/bwbbb5/b7b1/b5b3/3B6", false, false, false);
	}

	@Test
	public void stateTest1() {
		startGame("/1w3w1w1w/1w1ww2w1w/1w1w2ww1w/6w3/7b2/b1b1b3b1/b1b1bbb1bb/b1b3b3/3B6", true);
		assertMove("b8-b5", true, false);
		assertGameState("/1w3w1w1w/1w1ww2w1w/1w1w2ww1w/6w3/7b2/b1b1b3b1/b1b1bbb1bb/b1b3b3/3B6", true, false, false);
	}

	@Test
	public void stateTest2() {
		startGame("3W6/1w3w1w1w/1w1ww2w1w/1w1w2ww1w/6w3/7b2/b1b1b3b1/b1b1bbb1bb/b1b3b3/", false);
		assertMove("b8-b5", true, false);
		assertGameState("3W6/1w3w1w1w/1w1ww2w1w/1w1w2ww1w/6w3/7b2/b1b1b3b1/b1b1bbb1bb/b1b3b3/", false, false, false);
	}

	@Test
	public void stateTest3() {
		startGame("4W5/1w1w3w1w/1w1w3w1w/1w1w3w1w/2b7/2b2w4/b1b1bwb1b1/b3b1b1b1/b3w1b1b1/3B6", false);
		assertMove("g1-g4", false, true);
		assertMove("e1-d0", true, true);
		assertGameState("4W5/1w1w3w1w/1w1w3w1w/1w1w3w1w/2b7/2b2wb3/b1b1bwb1b1/b3b1b1b1/b7b1/3w6", false, true, true);
	}

	@Test
	public void stateTest4() {
		startGame("b1b1W5/9b/////5w4/7ww1//w2B6", true);
		assertMove("f3-e2", true, true);
		assertMove("j8-j9", false, true);
		assertMove("i2-i1", true, true);
		assertGameState("b1b1W4b///////4w2w2/8w1/w2B6", false, true, true);
	}

	@Test
	public void stateTest5() {
		startGame("b1b1W5/9b/////5b4/7bb1/9w/w2B6", true);
		assertMove("j1-i0", true, true);
		assertMove("h2-h3", false, true);
		assertGameState("b1b1W5/9b/////5b1b2/8b1//w2B4w1", true, true, false);
	}
}

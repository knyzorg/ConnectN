package vknyazev_ConnectN;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConnectNGame {
	enum GameState {
		Playable, EndedTie, Ended, Invalid
	};

	private int lastTurn[];
	private char boardState[][];
	private File saveFile;
	private int currentPlayerIndex;
	private Player players[];
	private int n;

	/**
	 * @return the lastTurn
	 */
	public int[] getLastTurn() {
		return lastTurn;
	}

	/**
	 * @param lastTurn the lastTurn to set
	 */
	public void setLastTurn(int[] lastTurn) {
		this.lastTurn = lastTurn;
	}

	/**
	 * @return the boardState
	 */
	public char[][] getBoardState() {
		return boardState;
	}

	/**
	 * @param boardState the boardState to set
	 */
	public void setBoardState(char[][] boardState) {
		this.boardState = boardState;
	}

	/**
	 * @return the saveFile
	 */
	public File getSaveFile() {
		return saveFile;
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

	/**
	 * @return the currentPlayerIndex
	 */
	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	/**
	 * @param currentPlayerIndex the currentPlayerIndex to set
	 */
	public void setCurrentPlayerIndex(int currentPlayerIndex) {
		this.currentPlayerIndex = currentPlayerIndex;
	}

	/**
	 * @return the players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	}

	/**
	 * @param n the n to set
	 */
	public void setN(int n) {
		this.n = n;
	}

	/**
	 * Create a new playable board
	 * @param rows The number of rows to create
	 * @param cols The number of columns to create
	 * @param n The sequence length required for a victory
	 */
	public ConnectNGame(int rows, int cols, int n) {
		this.boardState = new char[rows][cols];
		this.n = n;
	}

	/**
	 * Load game from a file
	 * @param loadFile file to load from
	 */
	public ConnectNGame(File loadFile) {
		// TODO: Generate board from file
		this.saveFile = loadFile;
	}

	enum PlayResult { Okay, OutOfBoundsColumn, OutOfBoundsRow, CheckerAlreadyPlaced, Illegal };

	/**
	 * Marks a column and row with the color character of the current player rotates the turn if play was valid
	 * @param row the row to mark
	 * @param col the column to mark
	 * @return If the play was valid
	 */
	public PlayResult play(int row, int col) {

		if (row > this.boardState.length || row < 1) {
			return PlayResult.OutOfBoundsRow;
		}
		if (col > this.boardState[0].length || col < 1) {
			return PlayResult.OutOfBoundsColumn;
		}
		
		if (this.boardState[row-1][col-1] != '\u0000') {
			return PlayResult.CheckerAlreadyPlaced;
		}

		
		this.boardState[row-1][col-1] = this.getCurrentPlayer().getColor();
		this.lastTurn = new int[]{row-1, col-1};
		this.alternateTurn();


        // Check if game state is still valid
        if (this.getGameState() == GameState.Invalid) {
			// Move was bad. Undo it.
			this.undo();
			// Report it as such
            return PlayResult.Illegal;
		}
		
		return PlayResult.Okay;
	}

	/**
	 * Checks if the entire board has been filled up
	 * @return True if the board is full
	 */
	private boolean isBoardFull() {
		for (int row = 0; row < boardState.length; row++) {
			for (int col = 0; col < boardState[0].length; col++) {
				// TODO: Refactor with accessor
				if (boardState[row][col] == '\u0000')
					// Empty cell found. Is not full.
					return false;
			}
		}

		return true;
	}

	public GameState getGameState() {
		// TODO: Add verification for end game
		// TODO: Add verification for invalid setup

		if (isBoardFull())
			return GameState.EndedTie;

		// Assume playable if not anything else
		return GameState.Playable;
	}

	/**
	 * Retrieves the current player based on the current player index
	 * @return The current player
	 */
	public Player getCurrentPlayer() {
		return this.players[this.currentPlayerIndex];
	}

	/**
	 * Saves the game to whatever file it was setup to use
	 */
	public void save() throws IOException {
		FileWriter fw = new FileWriter(this.saveFile);

		// Write rows
		fw.write(String.valueOf(this.getBoardDimensions()[0]));
		fw.write("\n");

		// Write columns
		fw.write(String.valueOf(this.getBoardDimensions()[1]));
		fw.write("\n");


		// Write 'N'
		fw.write(String.valueOf(this.n));
		fw.write("\n");

		// Write player names
		fw.write(this.players[0].getName());
		fw.write("\n");
		fw.write(this.players[1].getName());
		fw.write("\n");

		// Save current player index
		fw.write(String.valueOf(this.currentPlayerIndex));
		fw.write("\n");

		// Save the board
        for (int row = this.getBoardDimensions()[0] - 1; row >= 0; row--) {
            for (int col = 0; col < this.getBoardDimensions()[1]; col++) {
                char checker = this.boardState[row][col];
                // Leave 'E' if there is no checker. Otherwise, print checker. End line with either \n or ~ depending if it's the last element
                fw.write((checker == '\u0000' ? "E" : checker) + (col == this.getBoardDimensions()[1] - 1 ? "\n" : "~"));
			}
        }

		// Done!
		fw.close();
	}

	/**
	 * Changes currently player by flipping the index between 0 and 1
	 */
	private void alternateTurn() {
		this.currentPlayerIndex = (this.currentPlayerIndex+1) % 2;
	}

	public int[] getBoardDimensions() {
		return new int[]{this.boardState.length, this.boardState[0].length};
	}

	/**
	 * Undoes the previous turn
	 * @return True if there was a turn to undo
	 */
	public boolean undo() {
		if (this.lastTurn != null) {
			// Set last turn's checker to null
			this.boardState[lastTurn[0]][lastTurn[1]] = '\u0000';
			// Go to other player
			this.alternateTurn();
			// Report success
			return true;
		} else {
			// No turn to undo. Report failure.
			return false;
		}
	}
}

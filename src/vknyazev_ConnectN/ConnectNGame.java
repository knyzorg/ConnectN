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
	} // getLastTurn

	/**
	 * @param lastTurn the lastTurn to set
	 */
	public void setLastTurn(int[] lastTurn) {
		this.lastTurn = lastTurn;
	} // setLastTurn

	/**
	 * @return the boardState
	 */
	public char[][] getBoardState() {
		return boardState;
	} // getBoardState

	/**
	 * @param boardState the boardState to set
	 */
	public void setBoardState(char[][] boardState) {
		this.boardState = boardState;
	} // setBoardState

	/**
	 * @return the saveFile
	 */
	public File getSaveFile() {
		return saveFile;
	} // getSaveFile

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	} // setSaveFile

	/**
	 * This method is mostly present for internal use. Avoid using.
	 * @deprecated Should be avoided in favor of getCurrentPlayer()
	 * @return the currentPlayerIndex
	 */
	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	} // getCurrentPlayerIndex

	/**
	 * This method is mostly present for internal use. Avoid using.
	 * @param currentPlayerIndex the currentPlayerIndex to set
	 */
	public void setCurrentPlayerIndex(int currentPlayerIndex) {
		this.currentPlayerIndex = currentPlayerIndex;
	} // setCurrentPlayerIndex

	/**
	 * @return the players
	 */
	public Player[] getPlayers() {
		return players;
	} // getPlayers

	/**
	 * @param players the players to set
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	} // setPlayers

	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	} // getN

	/**
	 * @param n the n to set
	 */
	public void setN(int n) {
		this.n = n;
	} // setN

	/**
	 * Create a new playable board
	 * @param rows The number of rows to create
	 * @param cols The number of columns to create
	 * @param n The sequence length required for a victory
	 */
	public ConnectNGame(int rows, int cols, int n) {
		this.boardState = new char[rows][cols];
		this.n = n;
	} // ConnectNGame

	/**
	 * Load game from a file
	 * @param loadFile file to load from
	 */
	public ConnectNGame(File loadFile) {
		// TODO: Generate board from file
		this.saveFile = loadFile;
	} // ConnectNGame

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
		} // if
		if (col > this.boardState[0].length || col < 1) {
			return PlayResult.OutOfBoundsColumn;
		} // if
		
		if (this.boardState[row-1][col-1] != '\u0000') {
			return PlayResult.CheckerAlreadyPlaced;
		} // if

		
		this.boardState[row-1][col-1] = this.getCurrentPlayer().getColor();
		this.lastTurn = new int[]{row-1, col-1};
		this.alternateTurn();


        // Check if game state is still valid
        if (this.getGameState() == GameState.Invalid) {
			// Move was bad. Undo it.
			this.undo();
			// Report it as such
            return PlayResult.Illegal;
		} // if
		
		return PlayResult.Okay;
	} // play

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
			} // for
		} // for

		return true;
	} // isBoardFull

	/**
	 * Determines the game state based on the board. It Determines if it is valid and playable.
	 * @return the state of the game
	 */
	public GameState getGameState() {
		// TODO: Add verification for end game
		// TODO: Add verification for invalid setup

		if (isBoardFull())
			return GameState.EndedTie;

		// Assume playable if not anything else
		return GameState.Playable;
	} // getGameState

	/**
	 * Retrieves the current player based on the current player index
	 * @return The current player
	 */
	public Player getCurrentPlayer() {
		return this.players[this.currentPlayerIndex];
	} // getCurrentPlayer

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
			} // for
        } // for

		// Done!
		fw.close();
	} // save

	/**
	 * Changes currently player by flipping the index between 0 and 1
	 */
	private void alternateTurn() {
		this.currentPlayerIndex = (this.currentPlayerIndex+1) % 2;
	} // alternateTurn

	/**
	 * Retrieves the dimensions of the playing board
	 * @return A tuple of the dimensions of the board in the format (row size, column size)
	 */
	public int[] getBoardDimensions() {
		return new int[]{this.boardState.length, this.boardState[0].length};
	} // getBoardDimensions

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
	} // undo
}

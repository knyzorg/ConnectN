package vknyazev_ConnectN;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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
	public ConnectNGame(File loadFile) throws IOException {
		Scanner gameLoader = new Scanner(loadFile);
		int rows = gameLoader.nextInt();
		int cols = gameLoader.nextInt();
		int n = gameLoader.nextInt();
		this.boardState = new char[rows][cols];
		this.n = n;

		// flush trailing new line
		gameLoader.nextLine();

		Player player1 = new Player(gameLoader.nextLine(), 'Y');
		Player player2 = new Player(gameLoader.nextLine(), 'R');
		this.players = new Player[] { player1, player2 };

		this.currentPlayerIndex = gameLoader.nextInt();
		// flush trailing new line
		gameLoader.nextLine();

		// Read the board into a buffer
		StringBuffer textBoardBuffer = new StringBuffer();
		while (gameLoader.hasNextLine())
			textBoardBuffer.append(gameLoader.nextLine() + "\n");

		String[] textBoardRows = textBoardBuffer.toString().split("\n");
		for (int row = 0; row < rows; row++) {
			String[] cells = textBoardRows[row].split("~");
			for (int col = 0; col < cols; col++) {
				char cell = cells[col].charAt(0);

				// Leave 'E' as null
				if (cell != 'E')
					setCell(rows - row , col + 1, cell);
			}
		}

		this.saveFile = loadFile;
	} // ConnectNGame

	enum PlayResult {
		Okay, OutOfBoundsColumn, OutOfBoundsRow, CheckerAlreadyPlaced, Illegal
	};

	/**
	 * Marks a column and row with the color character of the current player rotates the turn if play was valid
	 * @param row the row to mark
	 * @param col the column to mark
	 * @return If the play was valid
	 */
	public PlayResult play(int row, int col) {

		int[] dimensions = getBoardDimensions();

		if (row > dimensions[0] || row < 1)
			return PlayResult.OutOfBoundsRow;

		if (col > dimensions[1] || col < 1)
			return PlayResult.OutOfBoundsColumn;

		if (getCell(row, col) != '\u0000')
			return PlayResult.CheckerAlreadyPlaced;

		setCell(row, col, this.getCurrentPlayer().getColor());

		// backup last turn in case it needs to be restored
		int[] lastTurnBackup = this.lastTurn;

		this.lastTurn = new int[] { row, col };
		this.alternateTurn();

		// Check if game state is still valid
		if (this.getGameState() == GameState.Invalid) {
			// Move was bad. Undo it.
			this.undo();

			// Restore last turn
			this.lastTurn = lastTurnBackup;

			// Report it as such
			return PlayResult.Illegal;
		} // if

		// If the move caused a victory, rotate the turn to make acc
		if (this.getGameState() == GameState.Ended)
			this.alternateTurn();

		return PlayResult.Okay;
	} // play

	/**
	 * Checks if there are n checkers in a row in a diagonal from the upper left to the bottom right
	 */
	private boolean checkDiagLR() {
		for (int col = this.n; col <= this.getBoardDimensions()[1]; col++) {
			for (int row = 1; row <= this.getBoardDimensions()[0] - this.n; row++) {
				char search = getCell(row, col);
				boolean okay = true;

				if (row == 1 && col == 2)
					System.out.println("");

				// It is pointless to continue if it is null
				if (search != '\u0000') {
					// Check the next (n-1) instances if they match the char
					for (int cursor = 1; cursor < this.n; cursor++)
						if (getCell(row + cursor, col - cursor) != search)
							// At least one does not
							okay = false;

					// Everything was in order. Good!
					if (okay)
						return true;
				}
			} // for
		} // for

		return false;
	}

	/**
	 * Checks if there are n checkers in a row in a diagonal from the upper right to the bottom left
	 */
	private boolean checkDiagRL() {
		for (int col = 1; col <= this.getBoardDimensions()[1] - this.n; col++) {
			for (int row = 1; row <= this.getBoardDimensions()[0] - this.n; row++) {
				char search = getCell(row, col);
				boolean okay = true;

				// It is pointless to continue if it is null
				if (search != '\u0000') {
					// Check the next (n-1) instances if they match the char
					for (int cursor = 1; cursor < this.n; cursor++)
						if (getCell(row + cursor, col + cursor) != search)
							// At least one does not
							okay = false;

					// Everything was in order. Good!
					if (okay)
						return true;
				}
			} // for
		} // for

		return false;
	}

	/**
	 * Checks if there are n checkers in a row in a column (Vertical)
	 */
	private boolean checkColumns() {
		for (int col = 1; col <= this.getBoardDimensions()[1]; col++) {
			for (int row = 1; row <= this.getBoardDimensions()[0] - this.n; row++) {
				char search = getCell(row, col);
				boolean okay = true;

				// It is pointless to continue if it is null
				if (search != '\u0000') {
					// Check the next (n-1) instances if they match the char
					for (int cursor = 1; cursor < this.n; cursor++)
						if (getCell(row + cursor, col) != search)
							// At least one does not
							okay = false;

					// Everything was in order. Good!
					if (okay)
						return true;
				}
			} // for
		} // for

		return false;
	}

	/**
	 * Checks if there are n checkers in a row in a column (Vertical)
	 * @return True if a non-null character is repeated n times in a single row
	 */
	private boolean checkRows() {
		for (int row = 1; row <= this.getBoardDimensions()[0]; row++) {
			for (int col = 1; col <= this.getBoardDimensions()[1] - this.n; col++) {
				char search = getCell(row, col);
				boolean okay = true;

				// It is pointless to continue if it is null
				if (search != '\u0000') {
					// Check the next (n-1) instances if they match the char
					for (int cursor = 1; cursor < this.n; cursor++)
						if (getCell(row, col + cursor) != search)
							// At least one does not
							okay = false;

					// Everything was in order. Good!
					if (okay)
						return true;
				}
			} // for
		} // for

		return false;
	}

	/**
	 * Determines the game state based on the board. It Determines if it is valid and playable.
	 * @return the state of the game
	 */
	public GameState getGameState() {

		// Check for floating checkers
		for (int col = 1; col <= this.getBoardDimensions()[1]; col++) {
			boolean hasStanding = true;
			for (int row = 1; row <= this.getBoardDimensions()[0]; row++) {
				if (getCell(row, col) == '\u0000')
					// Nothing to stand on anymore
					hasStanding = false;
				else if (!hasStanding)
					// Nothing to stand on and attempt to
					return GameState.Invalid;
			} // for
		} // for

		// n < dimensions
		if (this.getBoardDimensions()[1] < n || this.getBoardDimensions()[0] < n)
			return GameState.Invalid;

		// 8 <= n >= 3
		if (3 > n || 8 < n)
			return GameState.Invalid;

		// 4 <= dimensions <= 12
		if (this.getBoardDimensions()[1] > 12 || this.getBoardDimensions()[0] > 12 || this.getBoardDimensions()[1] < 4
				|| this.getBoardDimensions()[0] < 4)
			return GameState.Invalid;

		// Check each victory condition (Separated for simplified debugging)

		if (checkRows())
			return GameState.Ended;

		if (checkColumns())
			return GameState.Ended;

		if (checkDiagRL())
			return GameState.Ended;

		if (checkDiagLR())
			return GameState.Ended;

		// Check for fullness
		boolean isFull = true;
		for (int row = this.getBoardDimensions()[0]; row > 0; row--) {
			for (int col = 1; col <= this.getBoardDimensions()[1]; col++) {
				if (getCell(row, col) == '\u0000')
					// Empty cell found. Is not full.
					isFull = false;
			} // for
		} // for

		// Since is full and not victory condition, declare tie
		if (isFull)
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
		int rows = this.getBoardDimensions()[0];
		int cols = this.getBoardDimensions()[1];

		fw.write(String.valueOf(rows));
		fw.write("\n");

		// Write columns
		fw.write(String.valueOf(cols));
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
		for (int row = rows; row > 0; row--) {
			for (int col = 1; col <= cols; col++) {
				char checker = getCell(row, col);
				// Leave 'E' if there is no checker. Otherwise, print checker. End line with either \n or ~ depending if it's the last element
				String checkerValue = (checker == '\u0000' ? "E" : String.valueOf(checker));
				boolean isLastCellInRow = col == cols;
				fw.write(checkerValue + (isLastCellInRow ? "\n" : "~"));
			} // for
		} // for

		// Done!
		fw.close();
	} // save

	/**
	 * Changes currently player by flipping the index between 0 and 1
	 */
	private void alternateTurn() {
		this.currentPlayerIndex = (this.currentPlayerIndex + 1) % 2;
	} // alternateTurn

	/**
	 * Retrieves the dimensions of the playing board
	 * @return A tuple of the dimensions of the board in the format (row size, column size)
	 */
	public int[] getBoardDimensions() {
		return new int[] { this.boardState.length, this.boardState[0].length };
	} // getBoardDimensions

	/**
	 * Undoes the previous turn
	 * @return True if there was a turn to undo
	 */
	public boolean undo() {
		if (this.lastTurn != null) {
			// Set last turn's checker to null
			setCell(lastTurn[0], lastTurn[1], '\u0000');
			// Go to other player
			this.alternateTurn();
			// Report success
			return true;
		} else {
			// No turn to undo. Report failure.
			return false;
		}
	} // undo

	/**
	 * Recommended method to retrieve the value of specific point
	 * @param row Index of the row between 1 and the height of the board
	 * @param col Index of the column between 1 and the width of the board
	 * @return Either color char that was played or the null character \u0000
	 */
	public char getCell(int row, int col) {
		try {
			return boardState[row - 1][col - 1];
		} catch (Exception e) {
			return '\u0000';
		} // try
	} // getCell

	/**
	 * Recommended method to set the value of specific point
	 * @param row Index of the row between 1 and the height of the board
	 * @param col Index of the column between 1 and the width of the board
	 */
	public void setCell(int row, int col, char value) {
		boardState[row - 1][col - 1] = value;
	} // getCell
}

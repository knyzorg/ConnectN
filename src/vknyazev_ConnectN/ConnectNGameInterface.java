package vknyazev_ConnectN;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import vknyazev_ConnectN.ConnectNGame.GameState;
import vknyazev_ConnectN.ConnectNGame.PlayResult;

/**
 * ConnectNGameInterface
 */
public class ConnectNGameInterface {
    ConnectNGame game;
    Scanner keyboard = new Scanner(System.in);

    /**
     * Attempts to play a checker in a defined position
     * @return Returns true f the play was successful
     */
    private boolean play(int row, int col) {
        PlayResult played = game.play(row, col);
        switch (played) {
        case CheckerAlreadyPlaced:
            System.err.println("Invalid move.  That location already has a checker.  Please try again.");
            return false;
        case OutOfBoundsColumn:
            System.err.println("Invalid column number.  Please try again.");
            return false;
        case OutOfBoundsRow:
            System.err.println("Invalid row number.  Please try again.");
            return false;
        case Illegal:
            System.err
                    .println("Invalid move.  You must place the checker on top of another checker.  Please try again.");
            return false;
        case Okay:
            // Nothing to do here fam, left for clarity
        } // switch

        // The play was successful
        return true;
    } // play

    private void renderBoard() {

        int[] dimensions = this.game.getBoardDimensions();

        // Print bulk of the board
        for (int row = dimensions[0]; row > 0; row--) {
            for (int col = 1; col <= dimensions[1]; col++) {
                char checker = this.game.getCell(row, col);
                // Leave 'E' space if there is no checker. Otherwise, print checker.
                System.out.print(" " + (checker == '\u0000' ? "E" : checker));
            } // for
              // Number label the rows
            System.out.println("| " + (row));
        } // for

        // Print footer labels for columns
        for (int col = 1; col <= dimensions[1]; col++) {
            System.out.print(" " + col);
        } // for

        // Add line break
        System.out.println("");
    } // renderBoard

    /**
     * Checks if the game has ended by checking the game state against Ended and EndedTie GameStates
     */
    private boolean hasGameEnded() {
        switch (this.game.getGameState()) {
        case Ended:
        case EndedTie:
            return true;
        default:
            return false;
        } // switch
    } // hasGameEnded

    /**
     * Sets up a ConnectN game interactively by either loading it from file or by creating an new one
     * @return Returns a functional game object
     */
    private ConnectNGame createGameInteractively() {
        // Start new or resume game

        char option;
        ConnectNGame game = null;

        while (game == null) {
            System.out.println("Please enter N to start a new game or R to resume the game stored in currentGame.txt");
            option = keyboard.next().charAt(0);
            switch (Character.toUpperCase(option)) {
            case 'R':
                try {
                    game = new ConnectNGame(new File("currentGame.txt"));
                } catch (Exception e) {
                    System.out.println(e.toString());
                    e.getStackTrace();
                    System.err.println("Failed to open currentGame.txt. It is either missing or corrupted.");
                }

                break;
            case 'N':
                System.out.print("Enter the number of rows on the game board: ");
                int numRows = keyboard.nextInt();
                while (numRows < 4 || numRows > 12) {
                    System.err.println("Please enter a row count between 4 and 12");
                    numRows = keyboard.nextInt();
                }

                System.out.print("Enter the number of columns on the game board: ");
                int numCols = keyboard.nextInt();
                while (numCols < 4 || numCols > 12) {
                    System.err.println("Please enter a column count between 4 and 12");
                    numCols = keyboard.nextInt();
                }

                System.out.print("Enter the value for N, the number of checkers in a row for a win: ");
                int nValue = keyboard.nextInt();
                while (3 > nValue || 8 < nValue) {
                    System.err.println("Please enter an N-value between 3 and 8");
                    numCols = keyboard.nextInt();
                }

                // Burn left-over line-break
                keyboard.nextLine();

                // Setup players
                System.out.print("Enter name of Player 1, yellow: ");
                Player player1 = new Player(keyboard.nextLine(), 'Y');

                System.out.print("Enter name of Player 2, red: ");
                Player player2 = new Player(keyboard.nextLine(), 'R');

                game = new ConnectNGame(numRows, numCols, nValue);
                game.setPlayers(new Player[] { player1, player2 });
                break;
            default:
                System.out.println("Invalid input");
            } // switch
        } // while

        return game;
    } // createGameInteractively

    public ConnectNGameInterface() {

        System.out.println("Welcome to Heritage Connect-N");

        this.game = createGameInteractively();
        this.game.setSaveFile(new File("currentGame.txt"));
        System.out.println("Type Q at any time to exit the game, S to save the game or U to undo the game");

        while (!this.hasGameEnded()) {

            // Display the board
            renderBoard();

            System.out.print(
                    this.game.getCurrentPlayer().getName() + ", enter square number (row, column) of your move -> ");
            String input = keyboard.nextLine();
            // Checking for Q, S, and U
            switch (Character.toUpperCase(input.charAt(0))) {
            case 'Q':
                System.out.println("Goodbye.");
                System.exit(0);
                break;
            case 'S':
                // Save the game
                try {
                    this.game.save();
                    System.out.println("Game state has been saved to the file.");
                } catch (Exception e) {
                    e.getStackTrace();
                }
                break;
            case 'U':
                // Undo last move
                if (this.game.undo())
                    System.out.println(this.game.getCurrentPlayer().getName() + ", your last move has been undone.");
                else
                    System.out.println("There is no move to undo.");
                break;
            default:
                try {
                    StringTokenizer str = new StringTokenizer(input, ",");
                    int row = Integer.parseInt(str.nextToken());
                    int col = Integer.parseInt(str.nextToken());
                    this.play(row, col);
                } catch (Exception e) {
                    System.err.println("Please enter a valid option or row,col");
                }
            } // switch
        } // while

        System.out.println("Game has ended!");
        // Display the board for the last time
        renderBoard();
        switch (game.getGameState()) {
        case Ended:
            System.out.println(this.game.getCurrentPlayer().getName() + " has won!");
            break;
        case EndedTie:
            System.out.println("The game is a tie!");
            break;
        default:
            System.err.println("The game appears to be in an invalid state.");
        }

        keyboard.close();

    } // ConnectNGameInterface

    public static void main(String[] args) {
        new ConnectNGameInterface();
    } // main
}
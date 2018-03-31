package vknyazev_ConnectN;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import vknyazev_ConnectN.ConnectNGame.GameState;

/**
 * ConnectNGameCLI
 */
public class ConnectNGameCLI {
    ConnectNGame game;

    /**
     * Saves the game to a file
     * @param fileName File to use to save the game
     */
    private void saveToFile(String fileName) {
        File saveFile = new File(fileName);
        game.setSaveFile(saveFile);
    }

    private boolean play(int row, int col) {
        boolean played = game.play(row, col);
        if (!played) {
            // The play did not work due to an internal exception
            return false;
        }

        // Check if game state is still valid
        if (game.getGameState() == GameState.Invalid) {
            game.undo();
            return false;
        }

        // The play was successful
        return true;
    }

    private void renderTable() {
        // TODO: Refactor direct array access into accessors

        int[] dimensions = this.game.getBoardDimensions();

        // Print bulk of the board
        for (int row = dimensions[0] - 1; row >= 0; row--) {
            for (int col = 0; col < dimensions[1]; col++) {
                char checker = this.game.getBoardState()[row][col];
                // Leave empty space if there is no checker. Otherwise, print checker.
                System.out.print("|" + (checker == '\u0000' ? " " : checker));
            }
            // Number label the rows
            System.out.println("| " + (row + 1));
        }

        // Print footer labels for columns
        for (int col = 0; col < dimensions[1]; col++) {
            System.out.print(" " + (col + 1));
        }
    }

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
        }
    }

    /**
     * Sets up a ConnectN game interactively by either loading it from file or by creating an new one
     * @return Returns a functional game object
     */
    private ConnectNGame createGameInteractively() {
        // Start new or resume game

        Scanner keyboard = new Scanner(System.in);
        char option;
        ConnectNGame game = null;

        while (game == null) {
            System.out.println("Please enter N to start a new game or R to resume the game stored in currentGame.txt");
            option = keyboard.next().charAt(0);
            switch (Character.toUpperCase(option)) {
            case 'R':
                game = new ConnectNGame(new File("currentGame.txt"));
                break;
            case 'N':
                System.out.print("Enter the number of rows on the game board: ");
                int numRows = keyboard.nextInt();
                System.out.print("Enter the number of columns on the game board: ");
                int numCols = keyboard.nextInt();

                System.out.print("Enter the value for N, the number of checkers in a row for a win: ");
                int nValue = keyboard.nextInt();

                // Setup players
                System.out.print("Enter name of Player 1, yellow: ");
                Player player1 = new Player(keyboard.next(), 'Y');

                System.out.print("Enter name of Player 2, red: ");
                Player player2 = new Player(keyboard.next(), 'R');

                game = new ConnectNGame(numRows, numCols, nValue);
                game.setPlayers(new Player[] { player1, player2 });
                break;
            default:
                System.out.println("Invalid input");
            }
        }

        return game;
    }

    public ConnectNGameCLI() {

        System.out.println("Welcome to Heritage Connect-N");

        this.game = createGameInteractively();
        System.out.println("Type Q at any time to exit the game, S to save the game or U to undo the game");

        Scanner keyboard = new Scanner(System.in);
        while (!this.hasGameEnded()) {
            // Display the board
            renderTable();

            String input = keyboard.next();
            // Checking for Q, S, and U
            switch (Character.toUpperCase(input.charAt(0))) {
            case 'Q':
                break;
            case 'S':
                break;
            case 'U':
                break;
            default:
                StringTokenizer str = new StringTokenizer(input, ",");
                int row = Integer.parseInt(str.nextToken());
                int col = Integer.parseInt(str.nextToken());
                this.play(row, col);
            }
        }

        keyboard.close();

    }

    public static void main(String[] args) {
        ConnectNGameCLI cli = new ConnectNGameCLI();
    }
}
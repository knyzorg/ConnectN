package vknyazev_ConnectN;

import java.awt.*;
import vknyazev_ConnectN.ConnectNGame.GameState;
import javax.swing.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;;

public class ConnectNGameFrame extends JFrame {
	enum CheckerState {
		Player1, Player2, Disabled, Enabled
	}

	private JPanel contentPane;
	private ConnectNGame game;
	private Player players[];

	JPanel boardPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConnectNGameFrame frame = new ConnectNGameFrame();
					//frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConnectNGameFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 691, 412);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_file_new();
			}
		});
		mnFile.add(mntmNew);

		JMenuItem mntmRestore = new JMenuItem("Restore");
		mntmRestore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_file_restore();
			}
		});
		mnFile.add(mntmRestore);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_file_save();
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menu_file_quit();
			}
		});
		mnFile.add(mntmQuit);

		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);

		JMenuItem mntmUndoLastMove = new JMenuItem("Undo last move");
		mntmUndoLastMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_game_undo();
			}
		});
		mnGame.add(mntmUndoLastMove);

		JMenu mnNewMenu = new JMenu("Information");
		menuBar.add(mnNewMenu);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_information_help();
			}
		});
		mnNewMenu.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_information_about();
			}
		});
		mnNewMenu.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		loadGame(new File("currentGame.txt"));
		this.setVisible(true);

		JPanel newGameDialog = new JPanel();
		newGameDialog.setVisible(true);
		displayButtons();
	}

	// Handler methods for menu
	private void menu_file_quit() {
		System.exit(0);
	}

	private void menu_file_save() {
		try {
			this.game.save();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not save to file.");
		}
		
	}

	private void menu_file_restore() {
		try {
			this.game.restore();
			displayButtons();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "There is no available game file.");
		}
		
	}

	private void menu_file_new() {
		// TODO
	}

	private void menu_game_undo() {
		if (this.game.undo())
			displayButtons();
		else
			JOptionPane.showMessageDialog(this, "There is no available undo move.");
	}

	private void menu_information_help() {
		// TODO
	}

	private void menu_information_about() {
		// TODO
	}

	private void loadGame(File loadFile) {
		try {
			this.game = new ConnectNGame(loadFile);
			this.players = game.getPlayers();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "There is no available game file.");
			e.printStackTrace();
		}
	}

	private void createGame(int cols, int rows, int n, String p1Name, String p2Name) {
		ConnectNGame game = new ConnectNGame(rows, cols, n);

		Player players[] = new Player[2];

		players[0] = new Player(p1Name, 'Y');
		players[1] = new Player(p2Name, 'R');

		this.players = players;
		game.setPlayers(players);

		this.game = game;
	}

	private void displayButtons() {
		boardPanel.removeAll();

		JButton btnTable[][] = getButtonTableFromState(getCheckerStateTable());

		boardPanel.setLayout(new GridLayout(btnTable.length, btnTable[0].length, 0, 0));

		for (int row = btnTable.length - 1; row >= 0; row--)
			for (int col = 0; col < btnTable[row].length; col++) {
				final int playRow = row + 1;
				final int playCol = col + 1;

				this.getContentPane().add(boardPanel);
				JButton btn = btnTable[row][col];
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ConnectNGame.PlayResult p = game.play(playRow, playCol);
						System.out.println(p);

						// Re-display board
						displayButtons();

					}
				});

				boardPanel.add(btn);
			}

		// Seems to be required to update visuals
		revalidate();
	}

	private JButton[][] getButtonTableFromState(CheckerState state[][]) {
		JButton btnTable[][] = new JButton[state.length][state[0].length];

		for (int row = 0; row < state.length; row++)
			for (int col = 0; col < state[row].length; col++) {
				JButton btn = new JButton();
				btn.setText(row + " " + col + state[row][col]);
				btn.setEnabled(false);
				switch (state[row][col]) {
				case Enabled:
					btn.setEnabled(true);
					break;
				case Player1:
					btn.setBackground(Color.YELLOW);
					break;
				case Player2:
					btn.setBackground(Color.RED);
					break;
				case Disabled:
					break;
				}
				btn.putClientProperty("row", row + 1);
				btn.putClientProperty("col", col + 1);
				btnTable[row][col] = btn;
			}

		return btnTable;
	}

	private CheckerState[][] getCheckerStateTable() {

		int rowCount = game.getBoardDimensions()[0];
		int colCount = game.getBoardDimensions()[1];

		CheckerState[][] checkerState = new CheckerState[rowCount][colCount];

		char player1Color = players[0].getColor();
		char player2Color = players[1].getColor();

		// Build locked map
		for (int row = 0; row < rowCount; row++)
			for (int col = 0; col < colCount; col++) {
				char cellValue = game.getCell(row + 1, col + 1);
				CheckerState cellState = CheckerState.Disabled;

				// Cell cell to players
				if (cellValue == player1Color)
					cellState = CheckerState.Player1;

				if (cellValue == player2Color)
					cellState = CheckerState.Player2;

				checkerState[row][col] = cellState;
			}

		// Unlock playable cells
		if (this.game.getGameState() == GameState.Playable)
			for (int col = 0; col < colCount; col++) {

				boolean done = false;

				for (int row = 0; row < rowCount && !done; row++) {
					if (checkerState[row][col] == CheckerState.Disabled) {
						done = true;
						checkerState[row][col] = CheckerState.Enabled;
					}
				} // for
			} // for

		return checkerState;
	}

}

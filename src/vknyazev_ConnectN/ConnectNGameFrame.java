package vknyazev_ConnectN;

import java.awt.*;
import vknyazev_ConnectN.ConnectNGame.GameState;
import javax.swing.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectNGameFrame extends JFrame {
	enum CheckerState {
		Player1, Player2, Disabled, Enabled
	}

	private JTextField player1name;
	private JTextField player2name;
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

		this.setVisible(true);
		showNewGameDialog();
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

	private void showNewGameDialog() {

		JPanel newGamePanelRoot = new JPanel();
		newGamePanelRoot.setLayout(new BorderLayout(0, 0));

		JPanel newGamePanel = new JPanel();
		newGamePanelRoot.add(newGamePanel, BorderLayout.NORTH);

		JRadioButton rdbtnLoadFromFile = new JRadioButton("Load from file");
		newGamePanel.add(rdbtnLoadFromFile);
		rdbtnLoadFromFile.setSelected(true);

		JRadioButton rdbtnNewGame = new JRadioButton("New Game");
		newGamePanel.add(rdbtnNewGame);

		ButtonGroup gameSelect = new ButtonGroup();
		gameSelect.add(rdbtnNewGame);
		gameSelect.add(rdbtnLoadFromFile);

		JPanel newGamePanel_1 = new JPanel();
		newGamePanelRoot.add(newGamePanel_1, BorderLayout.CENTER);
		newGamePanel_1.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel newGamePanel_6 = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) newGamePanel_6.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		newGamePanel_1.add(newGamePanel_6);

		JPanel newGamePanel_7 = new JPanel();
		newGamePanel_6.add(newGamePanel_7);

		JLabel lblPlayerName = new JLabel("Player 1 name:");
		newGamePanel_7.add(lblPlayerName);

		player1name = new JTextField();
		newGamePanel_7.add(player1name);
		player1name.setColumns(10);

		JPanel newGamePanel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) newGamePanel_4.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		newGamePanel_1.add(newGamePanel_4);

		JPanel newGamePanel_10 = new JPanel();
		newGamePanel_4.add(newGamePanel_10);

		JLabel lblRows = new JLabel("Rows:");
		newGamePanel_10.add(lblRows);

		JSpinner spinner = new JSpinner();
		newGamePanel_10.add(spinner);
		spinner.setModel(new SpinnerNumberModel(5, 4, 8, 1));

		JPanel newGamePanel_9 = new JPanel();
		newGamePanel_4.add(newGamePanel_9);

		JLabel lblColumns = new JLabel("Columns:");
		newGamePanel_9.add(lblColumns);

		JSpinner spinner_1 = new JSpinner();
		newGamePanel_9.add(spinner_1);

		JPanel newGamePanel_3 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) newGamePanel_3.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		newGamePanel_1.add(newGamePanel_3);

		JPanel newGamePanel_5 = new JPanel();
		newGamePanel_3.add(newGamePanel_5);
		FlowLayout flowLayout_3 = (FlowLayout) newGamePanel_5.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);

		JLabel lblPlayerName_1 = new JLabel("Player 2 name:");
		newGamePanel_5.add(lblPlayerName_1);

		player2name = new JTextField();
		newGamePanel_5.add(player2name);
		player2name.setColumns(10);

		JPanel newGamePanel_2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) newGamePanel_2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		newGamePanel_1.add(newGamePanel_2);

		JPanel newGamePanel_8 = new JPanel();
		newGamePanel_2.add(newGamePanel_8);

		JLabel lblWinningSequence = new JLabel("Winning sequence:");
		newGamePanel_8.add(lblWinningSequence);

		JSpinner spinner_2 = new JSpinner();
		newGamePanel_8.add(spinner_2);

		int reply = JOptionPane.showConfirmDialog(this, newGamePanelRoot, "New Game", JOptionPane.PLAIN_MESSAGE);
		System.out.println(reply);
		if (gameSelect.getSelection() == rdbtnNewGame.getModel()) {
			System.out.println("New custom game");
		} else {
			System.out.println("Loaded game");
			loadGame(new File("currentGame.txt"));
		}
		System.out.println(player1name.getText());
	}

	private void menu_file_new() {
		showNewGameDialog();
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
			displayButtons();
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

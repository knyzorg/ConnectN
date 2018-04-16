package vknyazev_ConnectN;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.BoxLayout;
import java.awt.GridLayout;

public class ConnectNGameNewGameDialog extends JPanel {
	private JTextField player1name;
	private JTextField player2name;

	/**
	 * Create the panel.
	 */
	public ConnectNGameNewGameDialog() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JRadioButton rdbtnLoadFromFile = new JRadioButton("Load from file");
		panel.add(rdbtnLoadFromFile);
		
		JRadioButton rdbtnNewGame = new JRadioButton("New Game");
		panel.add(rdbtnNewGame);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) panel_6.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_6);
		
		JPanel panel_7 = new JPanel();
		panel_6.add(panel_7);
		
		JLabel lblPlayerName = new JLabel("Player 1 name:");
		panel_7.add(lblPlayerName);
		
		player1name = new JTextField();
		panel_7.add(player1name);
		player1name.setColumns(10);
		
		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_4);
		
		JPanel panel_10 = new JPanel();
		panel_4.add(panel_10);
		
		JLabel lblRows = new JLabel("Rows:");
		panel_10.add(lblRows);
		
		JSpinner spinner = new JSpinner();
		panel_10.add(spinner);
		spinner.setModel(new SpinnerNumberModel(5, 4, 8, 1));
		
		JPanel panel_9 = new JPanel();
		panel_4.add(panel_9);
		
		JLabel lblColumns = new JLabel("Columns:");
		panel_9.add(lblColumns);
		
		JSpinner spinner_1 = new JSpinner();
		panel_9.add(spinner_1);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_3.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_3);
		
		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5);
		FlowLayout flowLayout_3 = (FlowLayout) panel_5.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		
		JLabel lblPlayerName_1 = new JLabel("Player 2 name:");
		panel_5.add(lblPlayerName_1);
		
		player2name = new JTextField();
		panel_5.add(player2name);
		player2name.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_2);
		
		JPanel panel_8 = new JPanel();
		panel_2.add(panel_8);
		
		JLabel lblWinningSequence = new JLabel("Winning sequence:");
		panel_8.add(lblWinningSequence);
		
		JSpinner spinner_2 = new JSpinner();
		panel_8.add(spinner_2);

	}
}

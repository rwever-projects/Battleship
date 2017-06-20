import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.SwingConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 * Copyright (c) 2015 Rudi Wever, Software Engineering, Arizona State University
 * at on-line campus
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 * <p/>
 * This program is distributed for the purpose of grading for required course
 * work.
 * </p>
 * This program is distributed in the hope that it will be useful, but without
 * any warranty or fitness for a particular purpose.
 * <p/>
 * Please review the GNU General Public License at:
 * http://www.gnu.org/licenses/gpl-2.0.html see also:
 * https://www.gnu.org/licenses/gpl-faq.html so you are aware of the terms and
 * your rights with regard to this software. Or, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,USA
 * <p/>
 * Purpose: To play the game of Battleship
 *<p/>
 * Ser215 Software Enterprise I - Fall 2015 - B
 * 
 * @author Rudi Wever (rwever@asu.edu) - Software Engineering Ira Fulton Schools
 *         of Engineering, ASU Polytechnic
 * @file Final Project
 * @date November-December, 2015
 * @license See above
 */
public class BattleShipClient extends JFrame implements ActionListener {
	static BattleShipClient frame;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6469746952773279967L;
	protected int userNumber = 0;
	private JPanel contentPane;
	private JPanel pnlLeftButtons;
	private JPanel pnlRightButtons;
	private JPanel pnlProgressBars;
	private JPanel pnlOpponent;
	private JPanel pnlConfigure;
	private JPanel pnlServerLocation;
	private JTextField txtName;
	private JComboBox cmbLevel;
	private JLabel lblMyName;
	private JLabel lblLevel;
	private JLabel lblMyWinner;
	private JLabel lblOpponentWinner;
	private JLabel lblOpponentsBoard;
	private JLabel lblErrorMsg;
	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnTestConfigure;
	private JButton btnTestPlay;
	private JButton btnTestMyTurn;
	private ButtonGroup group;
	private JLabel lblMyTurn;
	private Map<Integer, JComboBox> cfgOrientationMap;
	private javax.swing.Timer winnerTimer;
	private Color defaultBtnBackgroundColor;
	private DisplayBoard myBoard;
	private DisplayBoard opponentBoard;
	private boolean setConfigureMode;
	private boolean processShipPlacement;
	private final int HORIZONTAL = 0;
	private final static int WINNER_TIME = 500;
	private final int[] shipLengths = { 2, 3, 3, 4, 5 }; // lengths of
															// the(shipTypes-1)
	private int lastX;
	private int lastY;
	private int lastOrientation;
	private boolean myTurn;
	private String serverLocation;
	private Thread myThread;
	// IO streams
	protected boolean connected = false; // true if connected to server
	protected DataOutputStream toServer;
	protected DataInputStream fromServer;
	private JTextField txtServerLocation;
	private boolean debugOn = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new BattleShipClient();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BattleShipClient() {
		myBoard = new DisplayBoard();
		opponentBoard = new DisplayBoard();
		setConfigureMode = true;
		group = new ButtonGroup();
		// Create the hashmap for the Orientation JComboBoxes
		cfgOrientationMap = new LinkedHashMap<Integer, JComboBox>();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setBounds(100, 100, 1060, 472);
		setBounds(100, 100, 2060, 472);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(128, 128, 128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setTitle("Battleship Client");

		pnlLeftButtons = new JPanel();
		pnlLeftButtons.setBackground(new Color(255, 255, 255));
		pnlLeftButtons.setBounds(10, 46, 483, 272);
		pnlLeftButtons.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));

		pnlRightButtons = new JPanel();
		pnlRightButtons.setBackground(new Color(255, 255, 255));
		pnlRightButtons.setBounds(522, 46, 483, 272);
		// panel_2.setBounds(2000, 46, 483, 272);
		pnlRightButtons.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagLayout gbl_pnlRightButtons = new GridBagLayout();
		gbl_pnlRightButtons.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0 };
		gbl_pnlRightButtons.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0 };
		gbl_pnlRightButtons.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_pnlRightButtons.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pnlRightButtons.setLayout(gbl_pnlRightButtons);

		pnlProgressBars = new JPanel();
		pnlProgressBars.setBackground(new Color(128, 128, 128));
		pnlProgressBars.setBounds(522, 329, 483, 93);

		pnlOpponent = new JPanel();
		pnlOpponent.setBackground(new Color(128, 128, 128));
		pnlOpponent.setForeground(new Color(255, 255, 255));
		pnlOpponent.setBounds(622, 16, 300, 24);

		JPanel pnlMyBoard = new JPanel();
		pnlMyBoard.setForeground(new Color(255, 255, 255));
		pnlMyBoard.setBackground(new Color(128, 128, 128));
		pnlMyBoard.setBounds(100, 16, 300, 24);

		lblOpponentsBoard = new JLabel("Opponent's Board");
		lblOpponentsBoard.setForeground(new Color(255, 255, 255));
		pnlOpponent.add(lblOpponentsBoard);

		lblMyName = new JLabel("My Board");
		lblMyName.setForeground(new Color(255, 255, 255));
		pnlMyBoard.add(lblMyName);
		pnlProgressBars.setLayout(null);

		JProgressBar pgbCarrier = new JProgressBar();
		pgbCarrier.setStringPainted(true);
		pgbCarrier.setMaximum(5);
		pgbCarrier.setForeground(Color.RED);
		pgbCarrier.setBounds(24, 79, 67, 14);
		pnlProgressBars.add(pgbCarrier);
		opponentBoard.addControl(5, pgbCarrier);

		JProgressBar pgbBattleship = new JProgressBar();
		pgbBattleship.setStringPainted(true);
		pgbBattleship.setMaximum(4);
		pgbBattleship.setForeground(Color.RED);
		pgbBattleship.setBounds(115, 79, 67, 14);
		pnlProgressBars.add(pgbBattleship);
		opponentBoard.addControl(4, pgbBattleship);

		JProgressBar pgbDestroyer = new JProgressBar();
		pgbDestroyer.setStringPainted(true);
		pgbDestroyer.setMaximum(3);
		pgbDestroyer.setForeground(Color.RED);
		pgbDestroyer.setBounds(206, 79, 67, 14);
		pnlProgressBars.add(pgbDestroyer);
		opponentBoard.addControl(3, pgbDestroyer);

		JProgressBar pgbSubmarine = new JProgressBar();
		pgbSubmarine.setStringPainted(true);
		pgbSubmarine.setMaximum(3);
		pgbSubmarine.setForeground(Color.RED);
		pgbSubmarine.setBounds(297, 79, 67, 14);
		pnlProgressBars.add(pgbSubmarine);
		opponentBoard.addControl(2, pgbSubmarine);

		JProgressBar pgbPatrolboat = new JProgressBar();
		pgbPatrolboat.setStringPainted(true);
		pgbPatrolboat.setMaximum(2);
		pgbPatrolboat.setForeground(Color.RED);
		pgbPatrolboat.setBounds(388, 79, 67, 14);
		pnlProgressBars.add(pgbPatrolboat);
		opponentBoard.addControl(1, pgbPatrolboat);

		JLabel lblNewLabel = new JLabel("Carrier");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(22, 44, 66, 14);
		pnlProgressBars.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Battleship");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(116, 44, 66, 14);
		pnlProgressBars.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Destroyer");
		lblNewLabel_2.setForeground(new Color(255, 255, 255));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(207, 44, 66, 14);
		pnlProgressBars.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Submarine");
		lblNewLabel_3.setForeground(new Color(255, 255, 255));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(298, 44, 66, 14);
		pnlProgressBars.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Patrol Boat");
		lblNewLabel_4.setForeground(new Color(255, 255, 255));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(389, 44, 66, 14);
		pnlProgressBars.add(lblNewLabel_4);

		GridBagLayout gbl_pnlLeftButtons = new GridBagLayout();
		gbl_pnlLeftButtons.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0 };
		gbl_pnlLeftButtons.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0 };
		gbl_pnlLeftButtons.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_pnlLeftButtons.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pnlLeftButtons.setLayout(gbl_pnlLeftButtons);

		createPlayerBoard(pnlLeftButtons);

		pnlConfigure = new JPanel();
		pnlConfigure.setBackground(new Color(169, 169, 169));
		pnlConfigure.setBounds(1014, 46, 338, 272);
		pnlConfigure.setLayout(null);

		JLabel lblName = new JLabel("Name:");
		lblName.setForeground(new Color(255, 255, 255));
		lblName.setBounds(58, 17, 49, 14);
		pnlConfigure.add(lblName);

		txtName = new JTextField();
		txtName.addFocusListener(new FocusAdapter() {
			// @Override
			public void focusLost(FocusEvent arg0) {
				String newName = txtName.getText();
				if (!newName.equals(myBoard.getUserName())) {
					myBoard.setUserName(newName);
					displayMyName();
					String line = "1," + userNumber + ',' + newName;
					xmitToServer(line);
				}
			}
		});

		txtName.setActionCommand("txtName");
		txtName.addActionListener(this);
		txtName.setBounds(101, 14, 170, 20);
		pnlConfigure.add(txtName);
		txtName.setColumns(10);

		lblLevel = new JLabel("Level");
		lblLevel.setBounds(178, 14, 45, 14);
		lblLevel.setVisible(false);
		pnlConfigure.add(lblLevel);

		cmbLevel = new JComboBox<Object>();
		cmbLevel.setVisible(false);
		cmbLevel.setActionCommand("cmbLevel");
		cmbLevel.addActionListener(this);

		cmbLevel.setMaximumRowCount(2);
		cmbLevel.setModel(new DefaultComboBoxModel(new String[] { "Easy",
				"Advanced" }));
		cmbLevel.setBounds(233, 11, 73, 20);
		pnlConfigure.add(cmbLevel);

		final JRadioButton rdbtnCarrier = new JRadioButton("Carrier (5 Locations)");
		rdbtnCarrier.setForeground(new Color(0, 0, 0));
		rdbtnCarrier.setBackground(new Color(245, 245, 245));
		rdbtnCarrier.setSelected(true);
		rdbtnCarrier.setBounds(58, 64, 170, 23);
		rdbtnCarrier.setActionCommand("5");
		pnlConfigure.add(rdbtnCarrier);
		group.add(rdbtnCarrier);

		final JComboBox cmbCarrier = new JComboBox();
		cmbCarrier.setModel(new DefaultComboBoxModel(new String[] {
				"Horizontal", "Vertical" }));
		cmbCarrier.setBounds(247, 64, 91, 20);
		pnlConfigure.add(cmbCarrier);
		cmbCarrier.setActionCommand("5");
		cfgOrientationMap.put(5, cmbCarrier);

		JComboBox cmbBattleship = new JComboBox();
		cmbBattleship.setEnabled(false);
		cmbBattleship.setModel(new DefaultComboBoxModel(new String[] {
				"Horizontal", "Vertical" }));
		cmbBattleship.setBounds(247, 90, 91, 20);
		pnlConfigure.add(cmbBattleship);
		cmbBattleship.setActionCommand("4");
		cfgOrientationMap.put(4, cmbBattleship);

		final JRadioButton rdbtnBattleship = new JRadioButton("Battleship (4 Locations)");
		rdbtnBattleship.setForeground(new Color(0, 0, 0));
		rdbtnBattleship.setBackground(new Color(245, 245, 245));
		rdbtnBattleship.setEnabled(false);
		rdbtnBattleship.setBounds(58, 90, 170, 23);
		rdbtnBattleship.setActionCommand("4");
		pnlConfigure.add(rdbtnBattleship);
		group.add(rdbtnBattleship);

		JComboBox cmbDestroyer = new JComboBox();
		cmbDestroyer.setEnabled(false);
		cmbDestroyer.setModel(new DefaultComboBoxModel(new String[] {
				"Horizontal", "Vertical" }));
		cmbDestroyer.setBounds(247, 116, 91, 20);
		pnlConfigure.add(cmbDestroyer);
		cmbDestroyer.setActionCommand("3");
		cfgOrientationMap.put(3, cmbDestroyer);

		final JRadioButton rdbtnDestroyer = new JRadioButton("Destroyer (3 Locations)");
		rdbtnDestroyer.setForeground(new Color(0, 0, 0));
		rdbtnDestroyer.setBackground(new Color(245, 245, 245));
		rdbtnDestroyer.setEnabled(false);
		rdbtnDestroyer.setBounds(58, 116, 170, 23);
		rdbtnDestroyer.setActionCommand("3");
		pnlConfigure.add(rdbtnDestroyer);
		group.add(rdbtnDestroyer);

		JComboBox cmbSubmarine = new JComboBox();
		cmbSubmarine.setEnabled(false);
		cmbSubmarine.setModel(new DefaultComboBoxModel(new String[] {
				"Horizontal", "Vertical" }));
		cmbSubmarine.setBounds(247, 142, 91, 20);
		pnlConfigure.add(cmbSubmarine);
		cmbSubmarine.setActionCommand("2");
		cfgOrientationMap.put(2, cmbSubmarine);

		final JRadioButton rdbtnSubmarine = new JRadioButton("Submarine (3 Locations)");
		rdbtnSubmarine.setForeground(new Color(0, 0, 0));
		rdbtnSubmarine.setBackground(new Color(245, 245, 245));
		rdbtnSubmarine.setEnabled(false);
		rdbtnSubmarine.setBounds(58, 142, 170, 23);
		rdbtnSubmarine.setActionCommand("2");
		pnlConfigure.add(rdbtnSubmarine);
		group.add(rdbtnSubmarine);

		JComboBox cmbPatrolboat = new JComboBox();
		cmbPatrolboat.setEnabled(false);
		cmbPatrolboat.setModel(new DefaultComboBoxModel(new String[] {
				"Horizontal", "Vertical" }));
		cmbPatrolboat.setBounds(247, 168, 91, 20);
		pnlConfigure.add(cmbPatrolboat);
		cmbPatrolboat.setActionCommand("1");
		cfgOrientationMap.put(1, cmbPatrolboat);

		final JRadioButton rdbtnPatrolBoat = new JRadioButton("Patrol Boat (2 Locations)");
		rdbtnPatrolBoat.setForeground(new Color(0, 0, 0));
		rdbtnPatrolBoat.setBackground(new Color(245, 245, 245));
		rdbtnPatrolBoat.setEnabled(false);
		rdbtnPatrolBoat.setBounds(58, 168, 170, 23);
		rdbtnPatrolBoat.setActionCommand("1");
		pnlConfigure.add(rdbtnPatrolBoat);
		group.add(rdbtnPatrolBoat);

		btnSave = new JButton("Save");
		btnSave.setEnabled(false);
		btnSave.addMouseListener(new MouseAdapter() {
			// @Override
			public void mouseReleased(MouseEvent arg0) {

				int shipType = getShipTypeSelected();
				int orientation = getShipOrientation(shipType);

				debug("x=" + lastX + " y=" + lastY);
				debug("ShipType=" + shipType);

				debug("Orientation=" + orientation);
				btnSave.setEnabled(false);
				btnCancel.setEnabled(false);
				int currentShipType = getShipTypeSelected();
				if (currentShipType > 0)
					setShipTypeSelected(currentShipType - 1);
				if (currentShipType > 1)
					processShipPlacement = true;
				// build the command setShiptAt(int userNumber, int x, int y,
				// int shipType, boolean verticalOrientation)
				// 3 int int int int boolean
				String bVerticalOrientation = "T";
				if (lastOrientation == 0)
					bVerticalOrientation = "F";
				String line = "3," + userNumber + "," + lastX + "," + lastY
						+ "," + shipType + "," + bVerticalOrientation;
				debug(line);
				xmitToServer(line);

			}
		});

		btnSave.setBounds(100, 241, 68, 23);
		pnlConfigure.add(btnSave);

		btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			// @Override
			public void mouseReleased(MouseEvent arg0) {

				int shipType = getShipTypeSelected();
				debug("ShipType=" + shipType + " Orientation="
						+ lastOrientation + " x=" + lastX + " y=" + lastY);
				JButton button = new JButton();
				btnSave.setEnabled(false);
				btnCancel.setEnabled(false);
				processShipPlacement = true;

				if (lastOrientation == HORIZONTAL) {
					for (int col = 0; col < shipLengths[shipType - 1]; col++) {
						if (lastX + col < 10) {
							button = myBoard.getControl(new Point(lastX + col,
									lastY));
							button.setBackground(defaultBtnBackgroundColor);
							button.setEnabled(true);
						}
					}
				} else {
					for (int row = 0; row < shipLengths[shipType - 1]; row++) {
						if (lastY + row < 10) {
							button = myBoard.getControl(new Point(lastX, lastY
									+ row));
							button.setBackground(defaultBtnBackgroundColor);
							button.setEnabled(true);
						}
					}
				}

			}
		});

		btnCancel.setEnabled(false);
		btnCancel.setBounds(248, 241, 73, 23);
		pnlConfigure.add(btnCancel);

		createOpponentBoard(pnlRightButtons);
		contentPane.setLayout(null);

		contentPane.add(pnlMyBoard);
		contentPane.add(pnlLeftButtons);
		contentPane.add(pnlOpponent);
		contentPane.add(pnlProgressBars);

		lblMyTurn = new JLabel("MY TURN");
		lblMyTurn.setForeground(new Color(255, 255, 255));
		lblMyTurn.setBackground(new Color(128, 128, 128));
		lblMyTurn.setBounds(205, 9, 72, 27);
		pnlProgressBars.add(lblMyTurn);
		lblMyTurn.setHorizontalAlignment(SwingConstants.CENTER);
		lblMyTurn.setFont(new Font("Tahoma", Font.BOLD, 14));

		lblMyWinner = new JLabel("");
		lblMyWinner.setForeground(new Color(255, 255, 255));
		lblMyWinner.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMyWinner.setBounds(10, 9, 195, 19);
		pnlProgressBars.add(lblMyWinner);

		lblOpponentWinner = new JLabel("");
		lblOpponentWinner.setForeground(new Color(255, 255, 255));
		lblOpponentWinner.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblOpponentWinner.setBounds(283, 9, 200, 19);
		pnlProgressBars.add(lblOpponentWinner);
		
		JLabel lblLocations = new JLabel("(5 Locations)");
		lblLocations.setForeground(new Color(255, 255, 255));
		lblLocations.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblLocations.setHorizontalAlignment(SwingConstants.CENTER);
		lblLocations.setBounds(18, 59, 74, 14);
		pnlProgressBars.add(lblLocations);
		
		JLabel lblLocations_1 = new JLabel("(4 Locations)");
		lblLocations_1.setForeground(new Color(255, 255, 255));
		lblLocations_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblLocations_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLocations_1.setBounds(110, 59, 74, 14);
		pnlProgressBars.add(lblLocations_1);
		
		JLabel lblLocations_2 = new JLabel("(3 Locations)");
		lblLocations_2.setForeground(new Color(255, 255, 255));
		lblLocations_2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblLocations_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblLocations_2.setBounds(202, 59, 74, 14);
		pnlProgressBars.add(lblLocations_2);
		
		JLabel lblLocations_3 = new JLabel("(3 Locations)");
		lblLocations_3.setForeground(new Color(255, 255, 255));
		lblLocations_3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblLocations_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblLocations_3.setBounds(294, 59, 74, 14);
		pnlProgressBars.add(lblLocations_3);
		
		JLabel lblLocations_4 = new JLabel("(2 Locations)");
		lblLocations_4.setForeground(new Color(255, 255, 255));
		lblLocations_4.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblLocations_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblLocations_4.setBounds(386, 59, 74, 14);
		pnlProgressBars.add(lblLocations_4);
		contentPane.add(pnlRightButtons);
		contentPane.add(pnlConfigure);
		
		JLabel lblNewLabel_7 = new JLabel("I'm satisfied with");
		lblNewLabel_7.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_7.setBounds(94, 213, 104, 14);
		pnlConfigure.add(lblNewLabel_7);
		
		JLabel lblNewLabel_8 = new JLabel(" my ship placement...");
		lblNewLabel_8.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_8.setBounds(89, 222, 106, 14);
		pnlConfigure.add(lblNewLabel_8);
		
		JLabel lblIWantTo = new JLabel("I want to place");
		lblIWantTo.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblIWantTo.setBounds(254, 212, 77, 14);
		pnlConfigure.add(lblIWantTo);
		
		JLabel lblThisShipAgain = new JLabel("this ship again...");
		lblThisShipAgain.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblThisShipAgain.setBounds(251, 222, 75, 14);
		pnlConfigure.add(lblThisShipAgain);
		
		JLabel lblClickOnA = new JLabel("Click on a board position to place");
		lblClickOnA.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblClickOnA.setBounds(58, 38, 250, 14);
		pnlConfigure.add(lblClickOnA);
		
		JLabel lblNewLabel_9 = new JLabel(" the following ship...");
		lblNewLabel_9.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_9.setBounds(58, 48, 120, 14);
		pnlConfigure.add(lblNewLabel_9);

		JPanel pnlTest = new JPanel();
		pnlTest.setBackground(new Color(128, 128, 128));
		pnlTest.setBounds(10, 329, 483, 93);
		contentPane.add(pnlTest);
		pnlTest.setLayout(null);

		btnTestConfigure = new JButton("Configure");
		btnTestConfigure.addMouseListener(new MouseAdapter() {
			// @Override
			public void mouseReleased(MouseEvent arg0) {
				setBoardsToConfigureMode();
			}
		});

		btnTestConfigure.setBounds(43, 59, 89, 23);
		pnlTest.add(btnTestConfigure);

		btnTestPlay = new JButton("Play Mode");
		btnTestPlay.addMouseListener(new MouseAdapter() {
			// @Override
			public void mouseReleased(MouseEvent arg0) {
				setBoardsToPlayMode();
				lblMyTurn.setVisible(false);
				myTurn = false;
			}
		});

		btnTestPlay.setBounds(175, 59, 104, 23);
		pnlTest.add(btnTestPlay);

		lblErrorMsg = new JLabel("Invalid Ship Location");
		lblErrorMsg.setForeground(new Color(255, 255, 255));
		lblErrorMsg.setBackground(new Color(128, 128, 128));
		lblErrorMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lblErrorMsg.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblErrorMsg.setBounds(43, 11, 397, 23);
		lblErrorMsg.setVisible(false);
		pnlTest.add(lblErrorMsg);

		btnTestMyTurn = new JButton("Set My Turn");
		btnTestMyTurn.addMouseListener(new MouseAdapter() {
			// @Override
			public void mouseReleased(MouseEvent arg0) {
				if (!myTurn) {
					myTurn = true;
					lblMyTurn.setVisible(true);
				}
			}
		});
		btnTestMyTurn.setBounds(307, 59, 133, 23);
		pnlTest.add(btnTestMyTurn);

		pnlServerLocation = new JPanel();
		pnlServerLocation.setBounds(1015, 318, 337, 104);
		contentPane.add(pnlServerLocation);
		pnlServerLocation.setLayout(null);

		JLabel lblServerLocation = new JLabel("Server Internet Address");
		lblServerLocation.setBounds(31, 11, 140, 14);
		pnlServerLocation.add(lblServerLocation);

		txtServerLocation = new JTextField();
		txtServerLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblErrorMsg.setVisible(false);
				setServerLocation();
			}
		});
		txtServerLocation.setText("localhost");
		txtServerLocation.setBounds(31, 26, 160, 20);
		pnlServerLocation.add(txtServerLocation);
		txtServerLocation.setColumns(10);

		JButton btnNewButton = new JButton("Ok");
		btnNewButton.addMouseListener(new MouseAdapter() {
			// @Override
			public void mouseReleased(MouseEvent arg0) {
				lblErrorMsg.setVisible(false);
				setServerLocation();
			}
		});

		btnNewButton.setBounds(223, 24, 89, 23);
		pnlServerLocation.add(btnNewButton);

		JLabel lblGameName = new JLabel("If connecting to a server on your computer,");
		lblGameName.setHorizontalAlignment(SwingConstants.LEFT);
		lblGameName.setFont(new Font("Arial Narrow", Font.PLAIN, 10));
		lblGameName.setBounds(31, 43, 281, 20);
		pnlServerLocation.add(lblGameName);
		
		JLabel lblNewLabel_6 = new JLabel("use the default address : 'localhost'.");
		lblNewLabel_6.setFont(new Font("Arial Narrow", Font.PLAIN, 10));
		lblNewLabel_6.setBounds(31, 58, 160, 14);
		pnlServerLocation.add(lblNewLabel_6);
		
		// Import ImageIcon     
		ImageIcon iconLogo = new ImageIcon("images/battleshiptitle.png");
		// In init() method write this code
		JLabel lblNewLabel_5 = new JLabel("New label");
		lblNewLabel_5.setBounds(415, 2, 186, 42);
		contentPane.add(lblNewLabel_5);
		lblNewLabel_5.setIcon(iconLogo);
		displayMyName();
		if (setConfigureMode)
			getServerLocation();
		// setBounds(100, 100, 1031, 472);

		//
		/*
		 * Create winnerTimer. Causes the bell to beep once to indicate that
		 * there is a winner
		 */
		winnerTimer = new javax.swing.Timer(WINNER_TIME, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Toolkit.getDefaultToolkit().beep();
				winnerTimer.stop();
			}
		});

	}

	private void debug(String line) {
		if (debugOn)
			System.out.println(line);
	}

	private void setServerLocation() {
		serverLocation = txtServerLocation.getText();

		startClient(serverLocation);
//		if (connected)
//			setBoardsToConfigureMode();
	}

	private void startClient(String serverLocation) {
		try {
			// Create a socket to connect to the server
			// Socket socket = new Socket("localhost", 8000);
			Socket socket = new Socket(serverLocation, 8000);
			debug("Connection established");
			connected = true;

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

		} catch (IOException ex) {
			connected = false;
			displayErrorMsg("Unable to connect to server at " + serverLocation);
			processShipPlacement = false;
			// System.err.println(ex);
		}

		if (connected) {
			myThread = new Thread(
					new clientListener(this, toServer, fromServer));
			myThread.start();
		}
	}

	/*
	 * Get the Server location
	 */
	private void getServerLocation() {
		setBounds(100, 100, 1031, 472);
		pnlServerLocation.setBounds(522, 46, 433, 272);
		pnlRightButtons.setVisible(false);
		pnlConfigure.setVisible(false);
		pnlProgressBars.setVisible(false);
		pnlOpponent.setVisible(false);
		// enableMyButtons();
		// btnSave.setEnabled(false);
		// btnCancel.setEnabled(false);
		lblErrorMsg.setVisible(false);
		btnTestConfigure.setVisible(false);
		btnTestPlay.setVisible(false);
		btnTestMyTurn.setVisible(false);
		processShipPlacement = false;
		// setBoardsToConfigureMode();
	}

	/*
	 * Sets MyBoard with all buttons enabled plus the configure panel to allow
	 * the placement of all my ships
	 */
	protected void setBoardsToConfigureMode() {
		setBounds(100, 100, 1031, 472);
		pnlConfigure.setBounds(522, 46, 433, 272);
		setConfigureMode = true;
		pnlRightButtons.setVisible(false);
		pnlConfigure.setVisible(true);
		pnlProgressBars.setVisible(false);
		pnlOpponent.setVisible(false);
		pnlServerLocation.setVisible(false);
		enableMyButtons();
		btnSave.setEnabled(false);
		btnCancel.setEnabled(false);
		if (connected) {
			lblErrorMsg.setVisible(false);
			processShipPlacement = true;
		}
	}

	/*
	 * Display the msg
	 */
	protected void displayErrorMsg(String msg) {
		lblErrorMsg.setText(msg);
		lblErrorMsg.setVisible(true);
	}

	/*
	 * Sets MyBoard with all buttons disabled and the OpponentBoard with all
	 * buttons enabled. Display the progress bars of the battleships.
	 */
	protected void setBoardsToPlayMode() {
		setBounds(100, 100, 1031, 472);
		setConfigureMode = false;
		pnlConfigure.setVisible(false);
		pnlServerLocation.setVisible(false);
		myTurn = false;
		lblMyTurn.setVisible(false);
		pnlRightButtons.setVisible(true);
		pnlProgressBars.setVisible(true);
		pnlOpponent.setVisible(true);
		disableMyButtons();
	}

	/*
	 * Disable all the buttons of myBoard.
	 */
	private void disableMyButtons() {
		int SIZE = 100;
		int x = 0, y = -1;

		JButton button = new JButton();
		for (int i = 0; i < SIZE; i++) {
			if (i % 10 == 0) {
				x = 0;
				y = y + 1;
			}
			button = myBoard.getControl(new Point(x, y));
			button.setEnabled(false);
			x++;
		}
	}

	/*
	 * Enable all the buttons of myBoard.
	 */
	private void enableMyButtons() {
		int SIZE = 100;
		int x = 0, y = -1;

		JButton button = new JButton();
		for (int i = 0; i < SIZE; i++) {
			if (i % 10 == 0) {
				x = 0;
				y = y + 1;
			}
			button = myBoard.getControl(new Point(x, y));
			button.setEnabled(true);
			x++;
		}
	}

	/*
	 * Returns the currently selected shipType
	 */
	private int getShipTypeSelected() {
		int shipType = 0;
		int count = group.getButtonCount();
		if (count > 0) {
			boolean done = false;
			Enumeration<AbstractButton> buttons = group.getElements();
			while (!done) {
				count = group.getButtonCount();
				AbstractButton button = buttons.nextElement();
				if (button.isSelected()) {
					String command = button.getActionCommand();
					shipType = Integer.parseInt(command);
					debug("ShipType=" + shipType);
					done = true;
				}
			}
		}
		return shipType;
	}

	/*
	 * Set the requested radio button to enable all others to disable If
	 * shipType is 0 then all radio button are disabled
	 */
	private void setShipTypeSelected(int shipType) {
		int count = group.getButtonCount();
		if (count > 0) {
			Enumeration<AbstractButton> buttons = group.getElements();
			count = group.getButtonCount();
			for (int i = 0; i < count; i++) {
				JComboBox cmbBox = new JComboBox();
				AbstractButton button = buttons.nextElement();
				String command = button.getActionCommand();
				int sType = Integer.parseInt(command);
				if (sType == shipType) {
					button.setEnabled(true);
					button.setSelected(true);
					cmbBox = cfgOrientationMap.get(sType);
					cmbBox.setEnabled(true);
				} else {
					button.setEnabled(false);
					cmbBox = cfgOrientationMap.get(sType);
					cmbBox.setEnabled(false);
				}
			}
		}
	}

	/*
	 * Returns the selected orientation of the given shipType
	 */
	private int getShipOrientation(int shipType) {
		int cmbOrientation = cfgOrientationMap.get(shipType).getSelectedIndex();
		return cmbOrientation;
	}

	/*
	 * Displays my current User name if any else displays "My Board"
	 */
	private void displayMyName() {
		String name = myBoard.getUserName();
		if (name == "")
			lblMyName.setText("My Board");
		else
			lblMyName.setText(name + "'s Board");
	}

	/*
	 * Player Board Contains the Player ships All the buttons are disabled,
	 * since the Computer fires at the x,y coordinates The target ships are
	 * initially Green
	 */
	public void createPlayerBoard(JPanel gb) {
		GridBagConstraints gbc;

		int SIZE = 100;
		int x = 0, y = -1;
		int xOffset = 0, yOffset = 1;

		gbc = new GridBagConstraints();

		JButton[] button = new JButton[SIZE];
		for (int i = 0; i < SIZE; i++) {
			if (i < 9) {
				button[i] = new JButton("0" + String.valueOf(i + 1));
				if (i == 0) {
					defaultBtnBackgroundColor = button[i].getBackground();
				}
			} else if (i == SIZE - 1)
				button[i] = new JButton("00");
			else
				button[i] = new JButton(String.valueOf(i + 1));
			// button[i] = new JButton("   ");
			button[i].setFont(new Font("Tahoma", Font.PLAIN, 10));
			gbc.insets = new Insets(2, 2, 2, 2);
			if (i % 10 == 0) {
				x = 0;
				y = y + 1;
			}
			gbc.gridx = x + xOffset;
			gbc.gridy = y + yOffset;
			button[i].setEnabled(true);
			gb.add(button[i], gbc);
			myBoard.addControl(new Point(x, y), button[i]);
			button[i].setActionCommand(x + "," + y);
			button[i].addActionListener(this);
			x++;
		}
	}

	/*
	 * Computer Board Contains the Computer player ships All buttons are
	 * initially enabled. The target ships are hidden from the Player
	 */
	public void createOpponentBoard(JPanel gb) {
		GridBagConstraints gbc;

		int SIZE = 100;
		int x = 0, y = -1;
		int xOffset = 0, yOffset = 1;

		gbc = new GridBagConstraints();

		JButton[] button = new JButton[SIZE];
		for (int i = 0; i < SIZE; i++) {
			if (i < 9)
				button[i] = new JButton("0" + String.valueOf(i + 1));
			else if (i == SIZE - 1)
				button[i] = new JButton("00");
			else
				button[i] = new JButton(String.valueOf(i + 1));
			button[i].setFont(new Font("Tahoma", Font.PLAIN, 10));
			gbc.insets = new Insets(2, 2, 2, 2);
			if (i % 10 == 0) {
				x = 0;
				y = y + 1;
			}
			gbc.gridx = x + xOffset;
			gbc.gridy = y + yOffset;
			button[i].setEnabled(true);
			gb.add(button[i], gbc);
			opponentBoard.addControl(new Point(x, y), button[i]);
			button[i].setActionCommand(x + "," + y);
			button[i].addActionListener(this);
			x++;
		}
	}

	/*
	 * Update the battleships progress bars.
	 */
	private void updateProgressBars() {
		JProgressBar progressBar = new JProgressBar();
		for (int index = 1; index <= 5; index++) {
			int value = opponentBoard.getNumberOfShotsFiredAt(index);
			progressBar = opponentBoard.getControl(index);
			progressBar.setValue(value);
		}
	}

	/*
	 * Add a representation of the shipType on my board only Does not send any
	 * data to the server.
	 */
	private void addMyShip(int shipType, int orientation, int x, int y) {

		if (processShipPlacement) {
			boolean valid = isValidShipPosition(shipType, orientation, x, y);
			debug("Location valid" + (valid ? " succesfull" : " failed"));
			if (valid) {
				lblErrorMsg.setVisible(false);
				if (shipType > 0 && shipType <= 5) {
					JButton button = new JButton();
					if (orientation == HORIZONTAL) {
						for (int col = 0; col < shipLengths[shipType - 1]; col++) {
							if (x + col < 10) {
								button = myBoard.getControl(new Point(x + col,
										y));
								button.setBackground(Color.GREEN);
								button.setEnabled(false);
							}
						}
					} else {
						for (int row = 0; row < shipLengths[shipType - 1]; row++) {
							if (y + row < 10) {
								button = myBoard.getControl(new Point(x, y
										+ row));
								button.setBackground(Color.GREEN);
								button.setEnabled(false);
							}
						}
					}
					lastX = x;
					lastY = y;
					lastOrientation = orientation;
					btnSave.setEnabled(true);
					btnCancel.setEnabled(true);
					processShipPlacement = false;
				}
			} else
				displayErrorMsg("Invalid Ship Location");
		}
	}

	/*
	 * Returns true if the new shipType placement is not conflicting with
	 * another ship or not out of bound.
	 */
	private boolean isValidShipPosition(int shipType, int orientation, int x,
			int y) {

		if (shipType > 0 && shipType <= 5) {
			boolean valid = true;
			JButton button = new JButton();
			if (orientation == HORIZONTAL) {
				for (int col = 0; col < shipLengths[shipType - 1]; col++) {
					if (x + col < 10) {
						button = myBoard.getControl(new Point(x + col, y));
						if (!button.isEnabled()) {
							valid = false;
						}
					} else
						valid = false;
				}
			} else {
				for (int row = 0; row < shipLengths[shipType - 1]; row++) {
					if (y + row < 10) {
						button = myBoard.getControl(new Point(x, y + row));
						if (!button.isEnabled()) {
							valid = false;
						}
					} else
						valid = false;
				}
			}
			return valid;
		}
		return false;
	}

	/*
	 * Sets the user number
	 */
	protected void setUserNumber(int userNumber) {
		this.userNumber = userNumber;

		if (userNumber == 1) {
			lblLevel.setVisible(false);
			cmbLevel.setVisible(false);

		}
	}

	/*
	 * Sets this user turn to play (fire at the opponent's board.
	 */
	protected void setMyTurn(boolean myTurn) {
		if (myTurn) {
			debug("My turn set");
			this.myTurn = true;
			lblMyTurn.setVisible(true);
		} else {
			this.myTurn = false;
			lblMyTurn.setVisible(false);
			debug("Sorry not your turn");
		}
	}

	/*
	 * Sets the opponent's name
	 */
	protected void setOpponentName(String name) {
		opponentBoard.setUserName(name);
		lblOpponentsBoard.setText(name + "'s Board");
	}

	/*
	 * setBoardPositionState Updates the board if the following manner: if
	 * targetUserNumber = myUserNumber then it is myBoard that is being updated
	 * else it is the opponentBoard if updating myBoard then update the button
	 * at x,y to one of the following colors according to the state 0-
	 * defaultBtnBackgroundColor, 1- Green, 2- Grey, 3- Red shipType is ignored
	 * (button is already disabled, only Grey and Red needs implementing) If
	 * updating the opponentBoard then update the button at x,y to one of the
	 * following colors according to the state 0- defaultBtnBackgroundColor, 1-
	 * Green, 2- Grey, 3- Red, if state 3 then indicate
	 * opponentBoard.firedAt(shipType) and update all progress bars
	 */
	protected void setBoardPositionState(int targetUserNumber, int x, int y,
			int shipType, int state) {
		if (targetUserNumber == userNumber) {
			JButton button = new JButton();
			button = myBoard.getControl(new Point(x, y));
			switch (state) {
			case 0:
				button.setBackground(defaultBtnBackgroundColor);
				break;
			case 1:
				button.setBackground(Color.GREEN);
				break;
			case 2:
				button.setBackground(Color.LIGHT_GRAY);
				break;
			case 3:
				button.setBackground(Color.RED);
				winnerTimer.start(); // Produce a beep to indicate a hit
				break;
			default:
				break;
			}
		} else {
			JButton button = new JButton();
			button = opponentBoard.getControl(new Point(x, y));
			switch (state) {
			case 0:
				button.setBackground(defaultBtnBackgroundColor);
				break;
			case 1:
				button.setBackground(Color.GREEN);
				break;
			case 2:
				button.setBackground(Color.LIGHT_GRAY);
				break;
			case 3:
				button.setBackground(Color.RED);
				opponentBoard.firedAt(shipType);
				winnerTimer.start(); // Produce a beep to indicate a hit
				updateProgressBars();
				break;
			default:
				break;

			}
		}
	}

	/*
	 * Indicate who WON the game
	 */
	protected void setWinner(int uNumber) {
		if (userNumber == uNumber) { // I won
			String uName = myBoard.getUserName();
			if (uName.length() > 0)
				lblMyWinner.setText(uName + " WON");
			else
				lblMyWinner.setText("I WON");
		} else { // opponent won
			String uName = opponentBoard.getUserName();
			if (uName.length() > 0)
				lblOpponentWinner.setText(uName + " WON");
			else
				lblOpponentWinner.setText("YOUR OPPONENT WON");
		}
		winnerTimer.start(); // Produce a beep to indicate a winner

	}

	protected void opponentDisconnect() {
		processShipPlacement = false;
		String opponentName = opponentBoard.getUserName();
		if (opponentName == "")
			displayErrorMsg("Opponent disconnected");
		else
			displayErrorMsg(opponentName + " disconnected.");
	}

	/*
	 * Sends the data to the server
	 */
	private void xmitToServer(String data) {
		try {
			if (connected) {
				toServer.writeUTF(data);
				toServer.flush();
				debug("User " + userNumber + " sending:" + data);
			}
		} catch (IOException ex) {
			connected = false;
			displayErrorMsg("Battleship Server disconnected");
			processShipPlacement = false;
			setConfigureMode=false;
			setMyTurn(false);
			// System.err.println(ex);
		}
	}

	// @Override
	/*
	 * Handles the actions of the following: cmbLevel, txtName, buttons from
	 * myBoard ( in Configuration mode), buttons from opponentBoard (in play
	 * mode).
	 */
	public void actionPerformed(ActionEvent arg0) {
		String actionCommand = arg0.getActionCommand();
		debug(actionCommand);
		if (actionCommand.equals("cmbLevel")) {
			int selection = cmbLevel.getSelectedIndex();
			String line = "2," + userNumber + "," + selection;
			xmitToServer(line);
		} else if (actionCommand.equals("txtName")) {
			String name = txtName.getText();
			myBoard.setUserName(name);
			displayMyName();
			String line = "1," + userNumber + ',' + name;
			xmitToServer(line);
		} else {
			String[] actions = actionCommand.split(",");
			int x = Integer.parseInt(actions[0]);
			int y = Integer.parseInt(actions[1]);
			if (setConfigureMode) {
				if (processShipPlacement) {
					int shipType = getShipTypeSelected();
					int orientation = getShipOrientation(shipType);
					debug("Configure the ships at x=" + x + " y=" + y);
					debug("shipType=" + shipType + " orientation="
							+ orientation);
					addMyShip(shipType, orientation, x, y);
				}
			} else {
				if (myTurn) {
					JButton button = new JButton();
					button = opponentBoard.getControl(new Point(x, y));
					button.setEnabled(false);
					debug("Fired at the opponent at x=" + x + " y=" + y);
					String line = "4," + userNumber + "," + x + "," + y;
					xmitToServer(line);

					myTurn = false;
					lblMyTurn.setVisible(false);
				} else
					debug("Sorry not your turn");

			}
		}
	}
}

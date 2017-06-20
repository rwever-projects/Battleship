import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//import sun.audio.AudioPlayer;
//import sun.audio.AudioStream;
import sun.audio.*;
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
public class BattleshipServer extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2229337623017619159L;
	/*
	 * 
	 */
	// Text area for displaying contents
	private JTextArea jta = new JTextArea();
	private JTextField txtClient1;
	private JTextField txtClient2;
	private DataOutputStream outputToClient1;
	private DataOutputStream outputToClient2;
	private static Battlefield user1BattleField;
	private static Battlefield user2BattleField;
	private int playLevel;
	private static boolean client1Connected;
	private static boolean client2Connected;
	private final int[] shipLengths = { 2, 3, 3, 4, 5 }; // lengths of
													// the(shipTypes-1)
	private static Vector<String> client1Cmds;
	private static Vector<String> client2Cmds;
	private int client1NumberOfTurns;
	private int client2NumberOfTurns;


	public static void main(String[] args) {
		user1BattleField = new Battlefield();
		user2BattleField = new Battlefield();
		client1Cmds = new Vector<String>();
		client2Cmds = new Vector<String>();
		client1Connected = false;
		client2Connected = false;
		new BattleshipServer();
	}

	public BattleshipServer() {
		getContentPane().setLayout(null);
		JScrollPane scrollPane = new JScrollPane(jta);
		scrollPane.setBounds(0, 0, 484, 100);
		getContentPane().add(scrollPane);

		txtClient1 = new JTextField();
		txtClient1.setBounds(95, 111, 309, 20);
		getContentPane().add(txtClient1);
		txtClient1.setActionCommand("Client1");
		txtClient1.addActionListener(this);
		txtClient1.setColumns(10);

		JLabel lblNewLabel = new JLabel("To Client 1");
		lblNewLabel.setBounds(21, 111, 64, 14);
		getContentPane().add(lblNewLabel);

		txtClient2 = new JTextField();
		txtClient2.setBounds(95, 161, 309, 20);
		getContentPane().add(txtClient2);
		txtClient2.setActionCommand("Client2");
		txtClient2.addActionListener(this);
		txtClient2.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("To Client 2");
		lblNewLabel_1.setBounds(20, 164, 65, 14);
		getContentPane().add(lblNewLabel_1);
		setTitle("Battleship Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		try {

			// Create a server socket
			ServerSocket serverSocket = new ServerSocket(8000);
			jta.append("MultiThreadServer started at " + new Date() + "\n");

			// Number a client
			int clientNo = 1;

			while (true) {

				// Listen for a new connection request
				Socket socket = serverSocket.accept();

				// Display the client number
				jta.append("Starting thread for client " + clientNo + " at "
						+ new Date() + "\n");

				// Find the client's host name, and IP address
				InetAddress inetAddress = socket.getInetAddress();
				jta.append("Client " + clientNo + "'s host name is "
						+ inetAddress.getHostName() + "\n");
				jta.append("Client " + clientNo + "'s IP Address is "
						+ inetAddress.getHostAddress() + "\n");

				if (clientNo == 1) {
					outputToClient1 = new DataOutputStream(
							socket.getOutputStream());
					outputToClient1.writeUTF("1," + clientNo);
					outputToClient1.flush();
					outputToClient1.writeUTF("3"); //setBoardsToConfigureMode
					outputToClient1.flush();
					client1Connected = true;
				}
				if (clientNo == 2) {
					outputToClient2 = new DataOutputStream(
							socket.getOutputStream());
					outputToClient2.writeUTF("1," + clientNo);
					outputToClient2.flush();
					outputToClient2.writeUTF("3"); //setBoardsToConfigureMode
					outputToClient2.flush();
					client2Connected = true;
					// if user 1 has set name already then let user 2 know
					String user1Name = user1BattleField.getUserName();
					if (user1Name != "") {
						outputToClient2.writeUTF("2," + user1Name);
						outputToClient2.flush();
					}
				}

				// Create a new thread for the connection
				HandleAClient task = new HandleAClient(socket, clientNo);

				// Start the new thread
				new Thread(task).start();
				if (clientNo == 2) {
					while (true) {
						// Do nothing
					}
				}
				// Increment clientNo
				clientNo++;
			}
		} catch (IOException ex) {
			// System.err.println(ex);
			jta.append(ex.toString() + "\n");
		}
	}

	// }

	private void setShipAt(int userNumber, int x, int y, int shipType,
			boolean selectedOrientation) {
//		final int[] shipLengths = { 2, 3, 3, 4, 5 }; // lengths of the
														// (shipTypes-1)

		if ((userNumber == 1) && (user1BattleField.getNumberOfShips() < 5)) {
			int shipLength = shipLengths[shipType - 1];
			if (selectedOrientation == false) {
				for (int offset = 0; offset < shipLength; offset++) {
					user1BattleField.setShipState(new Point(x + offset, y), 1);
					user1BattleField.setShipType(new Point(x + offset, y),
							shipType);
				}
			} else { // Orientation is Vertical
				for (int offset = 0; offset < shipLength; offset++) {
					user1BattleField.setShipState(new Point(x, y + offset), 1);
					user1BattleField.setShipType(new Point(x, y + offset),
							shipType);
				}
			}
			user1BattleField.incrementNumberOfShips(1);
			if (user1BattleField.getNumberOfShips() >= 5) {
				// Set user1 board to play mode
				String cmdLine = "4";
				sendToClient1(cmdLine);
			}

		} else if ((userNumber == 2)
				&& (user2BattleField.getNumberOfShips() < 5)) {
			int shipLength = shipLengths[shipType - 1];
			if (selectedOrientation == false) {
				for (int offset = 0; offset < shipLength; offset++) {
					user2BattleField.setShipState(new Point(x + offset, y), 1);
					user2BattleField.setShipType(new Point(x + offset, y),
							shipType);
				}
			} else { // Orientation is Vertical
				for (int offset = 0; offset < shipLength; offset++) {
					user2BattleField.setShipState(new Point(x, y + offset), 1);
					user2BattleField.setShipType(new Point(x, y + offset),
							shipType);
				}
			}
			user2BattleField.incrementNumberOfShips(1);
			if (user2BattleField.getNumberOfShips() >= 5) {
				// Set user2 board to play mode
				String cmdLine = "4";
				sendToClient2(cmdLine);
			}

		}
		if (user1BattleField.getNumberOfShips() >= 5
				&& user2BattleField.getNumberOfShips() >= 5) {
			// Start the play by assigning a turn
			int uNumber = 0; // make this random
			int min = 1;
			int max = 2;
			uNumber = ThreadLocalRandom.current().nextInt(min, max + 1);
			String cmdLine = "";
			switch (uNumber) {
			case 1:
				if (playLevel==1)
					client1NumberOfTurns=5;
				cmdLine = "5,T";
				sendToClient1(cmdLine);
				break;
			case 2:
				if (playLevel==1)
					client2NumberOfTurns=5;
				cmdLine = "5,T";
				sendToClient2(cmdLine);
				break;
			default:
				jta.append("Failure: No user initially assigned\n");
				break;
			}

		}

	}

	private void fire(int uNumber, int x, int y) {
		int state;
		int shipType;
		String cmdLine = "";
		int winner = 0;
		if (playLevel == 0) {
			if (uNumber == 1) {
				state = user2BattleField.getShipState(new Point(x, y));
				if (state <= 1) {
					state = state + 2;
					user2BattleField.setShipState(new Point(x, y), state);
				}
				shipType = user2BattleField.getShipType(new Point(x, y));
				state = user2BattleField.getShipState(new Point(x, y));
				cmdLine = ("7,2," + x + "," + y + "," + shipType + "," + state);
				sendToClient1(cmdLine);
				sendToClient2(cmdLine);
				winner = isThereAWinner(uNumber);
			} else if (uNumber == 2) {
				state = user1BattleField.getShipState(new Point(x, y));
				if (state <= 1) {
					state = state + 2;
					user1BattleField.setShipState(new Point(x, y), state);
				}
				shipType = user1BattleField.getShipType(new Point(x, y));
				state = user1BattleField.getShipState(new Point(x, y));
				// setBoardPositionState(int userNumber, int x, int y, int
				// shipType, int state)
				cmdLine = ("7,1," + x + "," + y + "," + shipType + "," + state);
				sendToClient1(cmdLine);
				sendToClient2(cmdLine);
				winner = isThereAWinner(uNumber);
			}
			// determine if there is a winner
			switch (winner) {
			case 0: // Determine next turn
				if (uNumber == 1) {
					cmdLine = "5,T";
					sendToClient2(cmdLine);
				}
				if (uNumber == 2) {
					cmdLine = "5,T";
					sendToClient1(cmdLine);
				}
				break;
			case 1: // User1 won
				cmdLine = "6,1";
				sendToClient1(cmdLine);
				sendToClient2(cmdLine);
				break;
			case 2: // User2 won
				cmdLine = "6,2";
				sendToClient1(cmdLine);
				sendToClient2(cmdLine);
				break;
			default:
				break;
			}
		}
		if (playLevel==1){
			if (uNumber == 1) {
				state = user2BattleField.getShipState(new Point(x, y));
				if (state <= 1) {
					state = state + 2;
					user2BattleField.setShipState(new Point(x, y), state);
				}
				shipType = user2BattleField.getShipType(new Point(x, y));
				state = user2BattleField.getShipState(new Point(x, y));
				cmdLine = ("7,2," + x + "," + y + "," + shipType + "," + state);
//				sendToClient1(cmdLine);
				client1Cmds.add(cmdLine);
				sendToClient2(cmdLine);
//				winner = isThereAWinner(uNumber);
			} else if (uNumber == 2) {
				state = user1BattleField.getShipState(new Point(x, y));
				if (state <= 1) {
					state = state + 2;
					user1BattleField.setShipState(new Point(x, y), state);
				}
				shipType = user1BattleField.getShipType(new Point(x, y));
				state = user1BattleField.getShipState(new Point(x, y));
				cmdLine = ("7,1," + x + "," + y + "," + shipType + "," + state);
				sendToClient1(cmdLine);
//				sendToClient2(cmdLine);
				client2Cmds.add(cmdLine);
//				winner = isThereAWinner(uNumber);
			}
			if (uNumber ==1){
				if (client1NumberOfTurns==1){
					int cmdsPending = client1Cmds.size();
					for (int i=0; i<cmdsPending; i++){
						cmdLine = client1Cmds.get(i);
						sendToClient1(cmdLine);
					}
						
					client2NumberOfTurns=5;
					cmdLine = "5,T";
					sendToClient2(cmdLine);
				}
				else{
					client1NumberOfTurns--;
					cmdLine = "5,T";
					sendToClient1(cmdLine);
				}
			}
			if (uNumber ==2){
				if (client2NumberOfTurns==1){
					int cmdsPending = client2Cmds.size();
					for (int i=0; i<cmdsPending; i++){
						cmdLine = client1Cmds.get(i);
						sendToClient2(cmdLine);
					}
					client1NumberOfTurns=5;
					cmdLine = "5,T";
					sendToClient1(cmdLine);
				}
				else{
					client2NumberOfTurns--;
					cmdLine = "5,T";
					sendToClient1(cmdLine);
				}
			}
			
/*
			// determine if there is a winner
			switch (winner) {
			case 0: // Determine next turn
				if (uNumber == 1) {
					cmdLine = "5,T";
					sendToClient2(cmdLine);
				}
				if (uNumber == 2) {
					cmdLine = "5,T";
					sendToClient1(cmdLine);
				}
				break;
			case 1: // User1 won
				cmdLine = "6,1";
				sendToClient1(cmdLine);
				sendToClient2(cmdLine);
				break;
			case 2: // User2 won
				cmdLine = "6,2";
				sendToClient1(cmdLine);
				sendToClient2(cmdLine);
				break;
			default:
				break;
			}
*/			
		}
	}

	private int isThereAWinner(int userNumber) {
		int winner = 0;
		
		if (userNumber==1){
		if (!user2BattleField.isThereStandingShips())
			winner = 1;
		}
		if (userNumber==2){
		if (!user1BattleField.isThereStandingShips())
			winner = 2;
		}
		return winner;
	}

	private void sendToClient1(String line) {
		try {
			if (client1Connected) {
				outputToClient1.writeUTF(line);
				outputToClient1.flush();
			} else
				jta.append("Client 1 has disconnected.\n");
			jta.append("To Client 1:" + line + "\n");
		} catch (IOException ex) {
			// System.err.println(ex);
			//jta.append(ex.toString());
		}
	}

	private void sendToClient2(String line) {
		try {
			if (client1Connected) {
				outputToClient2.writeUTF(line);
				outputToClient2.flush();
			} else
				jta.append("Client 1 has disconnected.\n");
			jta.append("To Client 2:" + line + "\n");
		} catch (IOException ex) {
			// System.err.println(ex);
			//jta.append(ex.toString());
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		String actionCommand = arg0.getActionCommand();
		try {
			if (actionCommand.equals("Client1")) {
				String line = txtClient1.getText();
				jta.append("To Client 1:" + line + "\n");
				outputToClient1.writeUTF(line);
				outputToClient1.flush();

			} else if (actionCommand.equals("Client2")) {
				String line = txtClient2.getText();
				jta.append("To Client 2:" + line + "\n");
				outputToClient2.writeUTF(line);
				outputToClient2.flush();
			}
		} catch (IOException ex) {
			// System.err.println(ex);
			jta.append(ex.toString() + "\n");
		}
	}

	// Inner class
	// Define the thread class for handling new connection
	class HandleAClient implements Runnable {

		private Socket socket; // A connected socket
		// private DataOutputStream outputToClient;

		private int userNumber;

		/** Construct a thread */
		public HandleAClient(Socket socket, int userNumber) {
			this.socket = socket;
			this.userNumber = userNumber;
		}

		/** Run a thread */
		public void run() {
			try {
				while (true) {
					// Create data input streams
					DataInputStream inputFromClient = new DataInputStream(
							socket.getInputStream());

					String line = inputFromClient.readUTF();

					jta.append("From User: " + userNumber + "\n");
					jta.append(line + "\n");

					String[] cmds = line.split(",");
					int cmd = Integer.parseInt(cmds[0]);
					switch (cmd) {
					case 0: // Response
						break;
					case 1: // setMyName(int userNumber, string name)
						if (cmds.length > 2) {
							int uNumber = Integer.parseInt(cmds[1]);
							jta.append("Set user name: " + cmds[2] + "\n");
							if (uNumber == 1) {
								user1BattleField.setUserName(cmds[2]);
								if (client2Connected) // setoponentName
									sendToClient2("2," + cmds[2]);
							}
							if (uNumber == 2) {
								user2BattleField.setUserName(cmds[2]);
								if (client1Connected) // setoponentName
									sendToClient1("2," + cmds[2]);
							}
						}
						break;
					case 2: // setPlayLevel(int userNumber, int level)
						if (cmds.length > 2) {
							int uNumber = Integer.parseInt(cmds[1]);
							jta.append("Set play level: " + cmds[2]+ "\n");
							jta.append("Limited to level 0, for now\n");
							if (uNumber == 1) {
								int newLevel = Integer.parseInt(cmds[2]);
								 playLevel = newLevel;
								//playLevel = 0;
							}
						}
						break;
					case 3: // setShiptAt(int userNumber, int x, int y, int
							// shipType, boolean verticalOrientation)
						if (cmds.length > 5) {
							boolean VerticalOrientation = false;
							int uNumber = Integer.parseInt(cmds[1]);
							int x = Integer.parseInt(cmds[2]);
							int y = Integer.parseInt(cmds[3]);
							int shipType = Integer.parseInt(cmds[4]);
							if (cmds[5].equalsIgnoreCase("T"))
								VerticalOrientation = true;
							jta.append("Set ship at: X:" + cmds[2] + " Y:"
									+ cmds[3] + " ShipType:" + cmds[4]
									+ " Vertical:" + cmds[5] + "\n");
							setShipAt(uNumber, x, y, shipType,
									VerticalOrientation);
						}
						break;
					case 4: // fire(userNumber, int x, int y)
						if (cmds.length > 2) {
							int uNumber = Integer.parseInt(cmds[1]);
							int x = Integer.parseInt(cmds[2]);
							int y = Integer.parseInt(cmds[3]);
						//	jta.append("Fire: user " + cmds[1] + " x=" + x
						//			+ " y=" + y + "\n");
							fire(uNumber, x, y);
						}

						break;
					default:
						jta.append("Unrecognized command:" + line + "\n");
						break;
					}
				}
			} catch (IOException e) {
				// System.err.println(e);
				String cmdLine = "13"; // setOpponentDisconnected()
				try {
					if (userNumber == 1) {
						client1Connected = false;
						if (client2Connected) // setoponentName
						{
							outputToClient2.writeUTF(cmdLine);
							outputToClient2.flush();
							jta.append("To Client 2:13\n");
							cmdLine ="5,F";	//Loose your turn
							outputToClient2.writeUTF(cmdLine);
							outputToClient2.flush();
							jta.append("To Client 2:5,F\n");
						}
					} else {
						client2Connected = false;
						if (client1Connected) // setoponentName
						{
							outputToClient1.writeUTF(cmdLine);
							outputToClient1.flush();
							jta.append("To Client 1:13\n");
							cmdLine ="5,F";	//Loose your turn
							outputToClient1.writeUTF(cmdLine);
							outputToClient1.flush();
							jta.append("To Client 1:5,F\n");
						}
					}
				} catch (IOException e1) {
					// e1.printStackTrace();
				}
				jta.append("Client " + userNumber + " Connection reset\n");
			}
		}
	}
}

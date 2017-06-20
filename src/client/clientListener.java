import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
public class clientListener implements Runnable {
	protected DataOutputStream toServer;
	protected DataInputStream fromServer;
	BattleShipClient frame;
	boolean debugOn=false;
	
	public clientListener(BattleShipClient jframe, DataOutputStream toSrvr,
			DataInputStream fromSrvr) {
		frame = jframe;
		toServer = toSrvr;
		fromServer = fromSrvr;
	}

	public void xmit(String data) {
		try {
			toServer.writeUTF(data);
			toServer.flush();
		} catch (IOException ex) {
			System.err.println(ex);
		}

	}
	
	private void debug(String line){
		if (debugOn){
			System.out.println(line);
		}
	}

	public void run() {
		debug("clientListener running");
		try {
			/*
			 * // Create a socket to connect to the server Socket socket = new
			 * Socket("localhost", 8000);
			 * System.out.println("Connection established");
			 * 
			 * // Create an input stream to receive data from the server
			 * fromServer = new DataInputStream(socket.getInputStream());
			 * 
			 * // Create an output stream to send data to the server toServer =
			 * new DataOutputStream(socket.getOutputStream());
			 */
			while (true) {
				// Get input from the server
				String response = fromServer.readUTF();

				// toServer.writeUTF(response);
				// toServer.flush();

				// Display to the text area
				
				debug("Received " + response );

				String[] cmds = response.split(",");
				int cmd = Integer.parseInt(cmds[0]);
				switch (cmd) {
				case 0: // Response
					break;
				case 1: // youAreUserNumber
					if (cmds.length > 1) {
						int uNumber = Integer.parseInt(cmds[1]);
						debug("My user number is: " + uNumber);
						frame.setUserNumber(uNumber);
					}
					break;
				case 2: // setOpponentName
					if (cmds.length > 1) {
					debug("My opponent's name is: " + cmds[1]
							+ '\n');
					frame.setOpponentName(cmds[1]);
					}
					break;
				case 3: // Set boards to Configure mode
					frame.setBoardsToConfigureMode();
					break;
				case 4: // Set boards to play mode
					frame.setBoardsToPlayMode();
					break;
				case 5: // setYourTurn
					if (cmds.length > 1) {
					boolean success = false;
					if (cmds[1].equalsIgnoreCase("T"))
						success = true;
					debug("My turn is: " + success + '\n');

					frame.setMyTurn(success);
					}
					break;
				case 6: // set winner
					if (cmds.length > 1) {
					int wNumber = Integer.parseInt(cmds[1]);
					frame.setWinner(wNumber);
					}
					break;
				case 7: // setBoardPositionState
					if (cmds.length > 5) {
					int uNumber = Integer.parseInt(cmds[1]);
					int x = Integer.parseInt(cmds[2]);
					int y = Integer.parseInt(cmds[3]);
					int shipType = Integer.parseInt(cmds[4]);
					int state = Integer.parseInt(cmds[5]);
					frame.setBoardPositionState(uNumber, x, y, shipType, state);
					}
					break;
				case 13:	//opponentDisconnected
					frame.opponentDisconnect();
					break;
				default:
					debug("Unrecognized command:" + response);
					break;
				}
			}

		} catch (IOException ex) {
			// connected=false;
			frame.displayErrorMsg("Battleship Server disconnected");

			//System.err.println(ex);
		}
	}
}
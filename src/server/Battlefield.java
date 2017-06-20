import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
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
/*
 * Battlefield class
 * Maintains information about a battlefield.
 * The information are:
 * userName - corresponds to the name of the user of this battlefield.
 * shipTypeMap - is a hash map of key=Point xy and value=shipType
 * shipStateMap - is a hash map of key=Point xy and value=state of a ship
 * 					states are 0=no ship, 1=ship present but not fired at,
 *                  2=no ship but fired at, 3=ship present and fired at.
 * numberOfShips - number of ships that has been added
 * numberOfAllowedShotsRemaining - is initialized to the sum of all ships length
 * 					and decremented every time the ship state changes from 1 to 3
 * 					When the value is 0 it easy to report a winner
 */
public class Battlefield {
	private String userName; // Stores to user name
	private Map<Point, Integer> shipTypeMap; // Contains the shipType at the x,y
												// point.
	private Map<Point, Integer> shipStateMap; // Contains the state of the ships
												// at the x,y point.
	private int numberOfShips; // Stores the number of ship added
	private int numberOfAllowedShotsRemaining; // Used to determine an winner
												// when value reaches 0
	final int[] shipLengths = { 2, 3, 3, 4, 5 }; // lengths of the
													// (shipTypes-1)

	/*
	 * Battlefield() Constructor Create an empty map of a 10X10 space with no
	 * shipType and state=0
	 */
	public Battlefield() {
		shipTypeMap = new LinkedHashMap<Point, Integer>();
		shipStateMap = new LinkedHashMap<Point, Integer>();
		createMaps();
		numberOfShips = 0;
		numberOfAllowedShotsRemaining = 0;
		for (int shipType = 0; shipType < 5; shipType++) {
			numberOfAllowedShotsRemaining = numberOfAllowedShotsRemaining
					+ shipLengths[shipType];
		}
		userName = "";
	}

	/*
	 * void createMaps() Creates an empty state and type map
	 */
	private void createMaps() {
		int SIZE = 100;
		int x = 0, y = -1;
		for (int i = 0; i < SIZE; i++) {
			if (i % 10 == 0) {
				x = 0;
				y = y + 1;
			}
			shipStateMap.put(new Point(x, y), 0);
			shipTypeMap.put(new Point(x, y), 0);
			x++;
		}
	}

	/*
	 * String getUserName() Returns the user name
	 */
	public String getUserName() {
		return userName;
	}

	/*
	 * void setUserName (String name) set the user name to name
	 */
	public void setUserName(String name) {
		this.userName = name;
	}

	/*
	 * int getNumberOfShips() Returns the number of ships
	 */
	public int getNumberOfShips() {
		return numberOfShips;
	}

	/*
	 * void setNumberOfShips (int numberOfShips) sets the number of ships to
	 * numberOfShips
	 */
	private void setNumberOfShips(int numberOfShips) {
		this.numberOfShips = numberOfShips;
	}

	/*
	 * int getShipState (Point xy) returns the state of the ship at Point xy
	 */
	public int getShipState(Point xy) {
		return shipStateMap.get(xy);
	}

	/*
	 * void setShipState (Point xy, int state) sets the state of the ship at
	 * Point xy to state
	 */
	public void setShipState(Point xy, int state) {
		shipStateMap.put(xy, state);
		if (state==3)
			numberOfAllowedShotsRemaining --;
	}

	/*
	 * int getShipType (Point xy) returns the shipType of the ship at Point xy
	 */
	public int getShipType(Point xy) {
		return shipTypeMap.get(xy);
	}

	/*
	 * void setShipType (Point xy, int value) sets the shipType of the ship at
	 * Point xy to value
	 */
	public void setShipType(Point xy, int value) {
		shipTypeMap.put(xy, value);
	}

	/*
	 * void setShipInfo (Point xy, int state, int shipType) sets the shipType
	 * and the state of the ship at Point xy
	 */
	public void setShipInfo(Point xy, int state, int shipType) {
		shipTypeMap.put(xy, shipType);
		shipStateMap.put(xy, state);
	}

	/*
	 * void incrementNumberOfShips (int value) increments the number of ship by
	 * value
	 */
	public void incrementNumberOfShips(int value) {
		numberOfShips = numberOfShips + value;
	}

	/*
	 * boolean isThereStandingShips() returns true if there are any part of any
	 * ship that has not been fired at.
	 */
	public boolean isThereStandingShips() {
	
	/*	
		int SIZE = 100;
		int state = 0;
		boolean shipPresent = false;
		int x = 0, y = -1;
		for (int i = 0; i < SIZE; i++) {
			if (i % 10 == 0) {
				x = 0;
				y = y + 1;
			}
			state = shipStateMap.get(new Point(x, y));
			if (state == 1)
				shipPresent = true;
			x++;
		}
		return shipPresent;
	*/

		return (numberOfAllowedShotsRemaining!=0);
	}
}

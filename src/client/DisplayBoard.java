import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class DisplayBoard {
	private String userName;
	private int[] shotsAtShips;
	private Map<Point, JButton> boardControlsMap; // Collection of buttons on board
	private Map<Integer, JProgressBar> progressBarsMap;	//Collection of progress bars showing how many times they have been fired at.
	
	public DisplayBoard(){
		userName = "";
		boardControlsMap = new LinkedHashMap<Point, JButton>();
		progressBarsMap  = new LinkedHashMap<Integer, JProgressBar>();
		shotsAtShips = new int[5];
	}
	
	/*
	 * Stores the user name
	 */
	public void setUserName( String name){
		userName = name;
	}
	
	/*
	 * Returns the user name;
	 */
	public String getUserName(){
		return userName;
	}
	
	/*
	 * Keeps track of the button object at point xy
	 */
	public void addControl(Point xy, JButton button){
		boardControlsMap.put(xy, button);
	}
	
	/*
	 * Returns the button object associated wit the xy point.
	 */
	public JButton getControl (Point xy){
		JButton button = new JButton();
		button=boardControlsMap.get(xy);
		return button;
	}
	
	/*
	 * Keeps track of the Progress Bar associated with each ship type.
	 */
	public void addControl (int shipType, JProgressBar progressBar){
		JProgressBar pBar = new JProgressBar();
		progressBarsMap.put(shipType, progressBar);
	}
	
	/*
	 * Returns the Progress Bar associated with the specific ship type.
	 */
	public JProgressBar getControl(int shipType){
		JProgressBar progressBar = new JProgressBar();
		progressBar = progressBarsMap.get(shipType);
		return progressBar;
	}
	
	/*
	 * Keeps score of hpw many times each ship has been fired at.
	 */
	public void firedAt( int shipType){
		if (shipType>0 && shipType<=5)
			shotsAtShips[shipType-1] ++;
	}
	
	/*
	 * Returns the number of times a ship has been fired at.
	 */
	public int getNumberOfShotsFiredAt( int shipType){
		if (shipType>0 && shipType<=5)
			return shotsAtShips[shipType-1];
		else
			return 0;
	}

}

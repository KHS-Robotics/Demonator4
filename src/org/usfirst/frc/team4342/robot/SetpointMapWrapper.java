package org.usfirst.frc.team4342.robot;

import java.util.Hashtable;
import java.util.Map;

/**
 * 
 * @author khsrobotics
 * 
 * Wraps the java.util.Hashtable class for the robot's elevator setpoints.
 * The setpoints are used for automatic elevator movement when a specific
 * button is pressed on the elevator joystick
 * 
 * About encoders: http://www.dynapar.com/Technology/Encoder_Basics/Motor_Encoders/
 * 
 */
public class SetpointMapWrapper
{
	private static final long serialVersionUID = -5792610831329435703L;
	
	private Map<Integer, Integer> map;
	
	/**
	 * Main constructor for this class
	 * @param setpoints the button and setpoints for the elevator
	 * 
	 * @throws IllegalArgumentException if setpoints is null
	 */
	public SetpointMapWrapper(Setpoint[] setpoints) {
		if(setpoints == null) {
			throw new IllegalArgumentException("setpoints cannot be null");
		}
		
		map = new Hashtable<Integer, Integer>();
		
		init(setpoints);
	}
	
	/**
	 * Adds a setpoint for the elevator
	 * @param button the button corresponding to the setpoint
	 * @param encValue the encoder value for the setpoint
	 */
	public void add(int button, int encValue) {
		map.put(button, encValue);
	}
	
	/**
	 * Gets the encoder value/setpoint for the specific button
	 * @param button the button on the joystick
	 * @return the setpoint corresponding to the button
	 */
	public int get(int button) {
		return map.get(button);
	}
	
	/**
	 * Removes a setpoint for the elevator
	 * @param button the setpoint to remove
	 */
	public void remove(int button) {
		map.remove(button);
	}
	
	/**
	 * Checks to see if a button is specified
	 * @param button the button for the setpoint
	 * @return true if the button is specified, false otherwise
	 */
	public boolean containsButton(int button) {
		return map.containsKey(button);
	}
	
	/**
	 * Initializes the Hashtable
	 * @param setpoints the button and setpoints for the elevator
	 */
	private void init(Setpoint[] setpoints) {
		for(int i = 0; i < setpoints.length; i++) {
			map.put(setpoints[i].getButton(), setpoints[i].getEncoderCounts());
		}
	}
	
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
}
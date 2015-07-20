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
	 * @param buttonValues the button values corresponding to the encoder values
	 * @param encoderValues the encoder values corresponding to the buttons values
	 * 
	 * @throws IllegalArgumentException if the two arrays are not equal in length
	 */
	public SetpointMapWrapper(int[] buttonValues, int[] encoderValues) {
		if(buttonValues == null) {
			throw new IllegalArgumentException("Button values cannot be null");
		}
		if(encoderValues == null) {
			throw new IllegalArgumentException("Encoder values cannot be null");
		}
		
		if(buttonValues.length != encoderValues.length) {
			throw new IllegalArgumentException("Button and encoder values must be the same length");
		}
		
		map = new Hashtable<Integer, Integer>();
		
		init(buttonValues, encoderValues);
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
	 * @param buttonValues the button values corresponding to the encoder values
	 * @param encoderValues the encoder values corresponding to the buttons
	 */
	private void init(int[] buttonValues, int[] encoderValues) {
		for(int i = 0; i < buttonValues.length; i++) {
			map.put(buttonValues[i], encoderValues[i]);
		}
	}
	
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
}
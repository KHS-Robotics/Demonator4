package org.usfirst.frc.team4342.robot.elevator.setpoints;

/**
 * This class is used for automatic elevator movement. The user can press
 * a button on the joystick in order to move to a certain height of the
 * elevator. This class is used for the SetpointMapWrapper
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class Setpoint {
	
	private final int button;
	private final int encoderCounts;
	
	/**
	 * Constructs an elevator setpoint
	 * @param button the button associated with the setpoint
	 * @param encoderCounts the encoder counts for the setpoint
	 */
	public Setpoint(int button, int encoderCounts) {
		this.button = button;
		this.encoderCounts = encoderCounts;
	}
	
	public int getButton() {
		return button;
	}
	
	public int getEncoderCounts() {
		return encoderCounts;
	}
}

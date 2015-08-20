package org.usfirst.frc.team4342.robot.elevator;

/**
 * 
 * @author khsrobotics
 *
 * This class simply holds PID values for the elevator.
 * 
 * PID = Proportional Integral Derivative (three values needed
 * for the equation)
 * 
 * Equation: (p * error) + (i * accumulatedError) + ((error - previousError) * d)
 * where P = P in PID, I = I in PID, and D = D in PID
 * 
 * About PID: https://en.wikipedia.org/wiki/PID_controller
 */
public final class ElevatorPID {
	
	private ElevatorPID() {
		
	}
	
	// Values for going up (higher to fight gravity)
	public static final double kP = 0.007;
	public static final double kI = 0.0;
	public static final double kD = 0.10;
	
	// Values for going down (lower because of gravity)
	public static final double kPd = 0.001;
	public static final double kId = 0.0;
	public static final double kDd = 0.008;
}

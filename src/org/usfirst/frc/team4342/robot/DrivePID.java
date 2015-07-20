package org.usfirst.frc.team4342.robot;

/**
 * 
 * @author khsrobotics
 *
 * This class simply holds PID values for the drive train.
 * 
 * PID = Proportional Integral Derivative (three values needed
 * for the equation)
 * 
 * Equation: (p * error) + (i * accumulatedError) + ((error - previousError) * d)
 * where P = P in PID, I = I in PID, and D = D in PID
 * 
 * About PID: https://en.wikipedia.org/wiki/PID_controller
 */
public class DrivePID {
	
	/**
	 * Front right motor controller
	 */
	static class FrontRight {
		static final double kP = 0.45;
		static final double kI = 0.005;
		static final double kD = 0.001;
	}
	
	/**
	 * Front left motor controller
	 */
	static class FrontLeft {
		static final double kP = 0.45;
		static final double kI = 0.003;
		static final double kD = 0.002;
	}
	
	/**
	 * Rear right motor controller
	 */
	static class RearRight {
		static final double kP = 0.45;
		static final double kI = 0.009;
		static final double kD = 0.001;
	}
	
	/**
	 * Rear left motor controller
	 */
	static class RearLeft {
		static final double kP = 0.45;
		static final double kI = 0.009;
		static final double kD = 0.001;
	}
	
	/** Number of revolutions for one encoder count */
	static final int kCodesPerRev = 512;
}
package org.usfirst.frc.team4342.robot.drive;

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
public final class DrivePID {
	
	private DrivePID() {
		
	}
	
	/**
	 * Front right motor controller
	 */
	public static class FrontRight {
		public static final double kP = 0.45;
		public static final double kI = 0.005;
		public static final double kD = 0.001;
	}
	
	/**
	 * Front left motor controller
	 */
	public static class FrontLeft {
		public static final double kP = 0.45;
		public static final double kI = 0.003;
		public static final double kD = 0.002;
	}
	
	/**
	 * Rear right motor controller
	 */
	public static class RearRight {
		public static final double kP = 0.45;
		public static final double kI = 0.009;
		public static final double kD = 0.001;
	}
	
	/**
	 * Rear left motor controller
	 */
	public static class RearLeft {
		public static final double kP = 0.45;
		public static final double kI = 0.009;
		public static final double kD = 0.001;
	}
	
	/**
	 * Values for autonomous
	 */
	public static class Autonomous {
		public static final double kP = 0.01;
	}
	
	/** Number of revolutions for one encoder count */
	public static final int kCodesPerRev = 512;
}
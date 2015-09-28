package org.usfirst.frc.team4342.robot.components;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is for getting sensors and motor controllers used by the drive train
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class DriveTrain {
	
	private DriveTrain() {
		
	}
	
	private static Joystick driveStick;
	private static CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private static Gyro pivotGyro, pitchGyro;
	
	/**
	 * The joystick used to control the robot drive
	 */
	public static class Stick {
		
		/**
		 * Gets the joystick used for controlling the robot drive
		 * @return the drive stick
		 */
		public static Joystick getInstance() {
			if(driveStick == null) {
				driveStick = new Joystick(0);
			}
			
			return driveStick;
		}
	}
	
	/**
	 * Front right motor controller
	 */
	public static class FrontRight {
		
		/**
		 * Gets the front right motor controller
		 * @return the front right motor controller
		 */
		public static CANJaguar getInstance() {
			if(frontRight == null) {
				frontRight = new CANJaguar(22);
			}
			
			return frontRight;
		}
	}
	
	/**
	 * The front left motor controller
	 */
	public static class FrontLeft {
		
		/**
		 * Gets the front left motor controller
		 * @return the front left motor controller
		 */
		public static CANJaguar getInstance() {
			if(frontLeft == null) {
				frontLeft = new CANJaguar(21);
			}
			
			return frontLeft;
		}
	}
	
	/**
	 * The rear right motor controller
	 */
	public static class RearRight {
		
		/**
		 * Gets the rear right motor controller
		 * @return the rear right motor controller
		 */
		public static CANJaguar getInstance() {
			if(rearRight == null) {
				rearRight = new CANJaguar(23);
			}
			
			return rearRight;
		}
	}
	
	/**
	 * The rear left motor controller
	 */
	public static class RearLeft {
		
		/**
		 * Gets the rear left motor controller
		 * @return the rear left motor controller
		 */
		public static CANJaguar getInstance() {
			if(rearLeft == null) {
				rearLeft = new CANJaguar(20);
			}
			
			return rearLeft;
		}
	}
	
	/**
	 * The gyro used to track the z-axis
	 */
	public static class PivotGyro {
		
		/**
		 * Gets the z-axis gyro
		 * @return the z-axis gyro
		 */
		public static Gyro getInstance() {
			if(pivotGyro == null) {
				pivotGyro = new Gyro(0);
				pivotGyro.setSensitivity(0.007);
			}
			
			return pivotGyro;
		}
	}
	
	/**
	 * The gyro used to track pitch
	 */
	public static class PitchGyro {
		
		/**
		 * Gets the pitch gyro
		 * @return the pitch gyro
		 */
		public static Gyro getInstance() {
			if(pitchGyro == null) {
				pitchGyro = new Gyro(1);
				pitchGyro.setSensitivity(0.007);
			}
			
			return pitchGyro;
		}
	}
}

package org.usfirst.frc.team4342.robot.components;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * This class is used for getting sensors used in autonomous
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class Autonomous {
	
	private Autonomous() {
		
	}
	
	private static DigitalInput rightPhotoSensor, leftPhotoSensor;
	private static edu.wpi.first.wpilibj.Ultrasonic ultra;
	
	/**
	 * The right photo sensor used for tote alignment
	 */
	public static class RightPhotoSensor {
		
		/**
		 * Gets the right photo sensor
		 * @return the right photo sensor
		 */
		public static DigitalInput getInstance() {
			if(rightPhotoSensor == null) {
				rightPhotoSensor = new DigitalInput(0);
			}
			
			return rightPhotoSensor;
		}
	}
	
	/**
	 * The left photo sensor used for tote alignment
	 */
	public static class LeftPhotoSensor {
		
		/**
		 * Gets the left photo sensor
		 * @return the left photo sensor
		 */
		public static DigitalInput getInstance() {
			if(leftPhotoSensor == null) {
				leftPhotoSensor = new DigitalInput(1);
			}
			
			return leftPhotoSensor;
		}
	}
	
	/**
	 * The ultrasonic sensor for aligning the robot at an optimal distance
	 */
	public static class Ultrasonic {
		
		/**
		 * Gets the ultrasonic sensor used for aligning the robot at an optimal distance
		 * @return the ultrasonic sensor
		 */
		public static edu.wpi.first.wpilibj.Ultrasonic getInstance() {
			if(ultra == null) {
				ultra = new edu.wpi.first.wpilibj.Ultrasonic(2, 3, edu.wpi.first.wpilibj.Ultrasonic.Unit.kInches);
				ultra.setAutomaticMode(true);
			}
			
			return ultra;
		}
	}
}

package org.usfirst.frc.team4342.robot.components;

import org.usfirst.frc.team4342.robot.elevator.setpoints.Setpoint;
import org.usfirst.frc.team4342.robot.elevator.setpoints.SetpointMapWrapper;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

/**
 * This class is used for getting sensors used on the elevator
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class Elevator {
	
	private Elevator() {
		
	}
	
	private static Joystick elevatorStick;
	private static Talon rightMotor, leftMotor;
	private static edu.wpi.first.wpilibj.Encoder encoder;
	private static DigitalInput topLS, botLS;
	private static SetpointMapWrapper setpoints;
	
	/**
	 * The joystick used to control the elevator
	 */
	public static class Stick {
		
		/**
		 * Gets the joystick used to control the elevator
		 * @return the joystick to control the elevator
		 */
		public static Joystick getInstance() {
			if(elevatorStick == null) {
				elevatorStick = new Joystick(1);
			}
			
			return elevatorStick;
		}
	}
	
	/**
	 * The right motor of the elevator
	 */
	public static class RightMotor {
		
		/**
		 * Gets the right motor of the elevator
		 * @return the right motor of the elevator
		 */
		public static Talon getInstance() {
			if(rightMotor == null) {
				rightMotor = new Talon(0);
			}
			
			return rightMotor;
		}
	}
	
	/**
	 * The left motor of the elevator
	 */
	public static class LeftMotor {
		
		/**
		 * Gets the left motor of the elevator
		 * @return the left motor of the elevator
		 */
		public static Talon getInstance() {
			if(leftMotor == null) {
				leftMotor = new Talon(1);
			}
			
			return leftMotor;
		}
	}
	
	/**
	 * The encoder used to track location of the elevator
	 */
	public static class Encoder {
		
		/**
		 * Gets the encoder used to track location of the elevator
		 * @return the encoder for the elevator
		 */
		public static edu.wpi.first.wpilibj.Encoder getInstance() {
			if(encoder == null) {
				encoder = new edu.wpi.first.wpilibj.Encoder(8, 9, false, EncodingType.k1X);
			}
			
			return encoder;
		}
	}
	
	/**
	 * The top limit switch used to stop the elevator from going too high
	 */
	public static class TopLimitSwitch {
		
		/**
		 * Gets the top limit switch used to stop the elevator from going too high
		 * @return the top limit switch
		 */
		public static DigitalInput getInstance() {
			if(topLS == null) {
				topLS = new DigitalInput(7);
			}
			
			return topLS;
		}
	}
	
	/**
	 * The bottom limit switch used to stop the elevator from going too low
	 */
	public static class BottomLimitSwitch {
		
		/**
		 * Gets the bottom limit switch used to stop the elevator from going too low
		 * @return the bottom limit switch
		 */
		public static DigitalInput getInstance() {
			if(botLS == null) {
				botLS = new DigitalInput(4);
			}
			
			return botLS;
		}
	}
	
	public static class Setpoints {
		
		public static SetpointMapWrapper getInstance() {
			if(setpoints == null) {
				setpoints = new SetpointMapWrapper(
					new Setpoint[] {
						new Setpoint(2, 0),
						new Setpoint(4, 325),
						new Setpoint(3, 750),
						new Setpoint(5, 1475),
						new Setpoint(8, 1200),
						new Setpoint(9, 2800)
					}
				);
			}
			
			return setpoints;
		}
	}
}

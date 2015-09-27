package org.usfirst.frc.team4342.robot.drive;

import edu.wpi.first.wpilibj.CANJaguar;

/**
 * 
 * This class initializes and/or resets the CANJaguars (motor controllers) 
 * for the drive train.
 * 
 * About CAN bus: https://en.wikipedia.org/wiki/CAN_bus
 * About Jaguars: http://www.vexrobotics.com/217-3367.html
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public final class CANJaguarLoader {
	
	private CANJaguarLoader() {
		
	}
	
	/**
	 * Initializes the CANJaguars for the drive train. Each controller
	 * has an encoder that operates over CAN bus
	 * @param jaguar the jaguar to initialize
	 * @param resetEncoders true to reset the encoder, false otherwise
	 */
	public static void init(CANJaguar jaguar, boolean resetEncoders) {
		jaguar.configNeutralMode(CANJaguar.NeutralMode.Brake);
		
		if(resetEncoders) {
			jaguar.setPositionMode(CANJaguar.kQuadEncoder, 512, 0.0, 0.0, 0.0);
			jaguar.enableControl(0);
		}
		
		jaguar.setSpeedMode(
			CANJaguar.kQuadEncoder, 
			DrivePID.kCodesPerRev, 
			DrivePID.FrontRight.kP, 
			DrivePID.FrontRight.kI,
			DrivePID.FrontRight.kD
		);
		
		jaguar.enableControl();
	}
	
	/**
	 * Sets the CANJaguar's neutral mode to coast
	 * @param jaguars the jaguars to set
	 */
	public static void setCoast(CANJaguar[] jaguars) {
		for(CANJaguar jaguar : jaguars) {
			jaguar.configNeutralMode(CANJaguar.NeutralMode.Coast);
		}
	}
	
	/**
	 * Sets the jaguars to speed mode and sets their PID values with the Encoders
	 * @param jaguars the jaguars to initialize
	 */
	public static void init(CANJaguar[] jaguars) {
		init(jaguars, false);
	}
	
	/**
	 * Sets the jaguars to speed mode and sets their PID vales with the Encoders
	 * @param jaguars the jaguars to initialize
	 * @param resetEncoders true to reset the encoders, false to not reset them
	 */
	public static void init(CANJaguar[] jaguars, boolean resetEncoders) {
		for(CANJaguar jag : jaguars) {
			init(jag, resetEncoders);
		}
	}
}

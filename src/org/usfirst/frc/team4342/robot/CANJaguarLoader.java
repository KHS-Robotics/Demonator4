package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.CANJaguar;

public class CANJaguarLoader {
	
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
	
	public static void init(CANJaguar[] jaguars, boolean resetEncoders) {
		for(CANJaguar jag : jaguars) {
			init(jag, resetEncoders);
		}
	}
}

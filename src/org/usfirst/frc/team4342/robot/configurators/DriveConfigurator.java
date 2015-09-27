package org.usfirst.frc.team4342.robot.configurators;

import org.usfirst.frc.team4342.robot.drive.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.drive.DriveHealthMonitor;
import org.usfirst.frc.team4342.robot.drive.MecanumDrive;

import ernie.logging.loggers.MultiLog;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is for initializing the drive train
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class DriveConfigurator {
	
	private DriveConfigurator() {
		
	}
	
	private static MecanumDrive drive;
	
	/**
	 * Gets the mecanum drive
	 * @return the mecanum drive
	 */
	public static MecanumDrive getMecanumDrive() {
		return drive;
	}
	
	/**
	 * Initialized the drive train
	 * @param jaguars the jagaurs associated with the drive  train
	 * @param driveStick the joystick to track
	 * @param pivotGyro the gyro to get data from
	 * @param multiLog the log to log to
	 */
	public static void configure(CANJaguar[] jaguars, Joystick driveStick, Gyro pivotGyro, MultiLog multiLog) {
		
		CANJaguarLoader.init(jaguars, false);
		
		MecanumDrive mecDrive = new MecanumDrive(
			jaguars[0],
			jaguars[1],
			jaguars[2], 
			jaguars[3], 
			driveStick,
			pivotGyro
		);
		
		drive = mecDrive;
		
		DriveHealthMonitor dhm = new DriveHealthMonitor(
			driveStick, 
			jaguars[0], 
			jaguars[1], 
			jaguars[2], 
			jaguars[3],
			multiLog
		);
		
		dhm.startMonitoring();
	}
}

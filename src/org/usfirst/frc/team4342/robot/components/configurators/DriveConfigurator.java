package org.usfirst.frc.team4342.robot.components.configurators;

import org.usfirst.frc.team4342.robot.components.DriveTrain;
import org.usfirst.frc.team4342.robot.drive.DriveHealthMonitor;
import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.drive.configurators.CANJaguarLoader;

import ernie.logging.loggers.MultiLog;

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
	 * Initializes the drive train
	 * @param jaguars the jagaurs associated with the drive  train
	 * @param driveStick the joystick to track
	 * @param pivotGyro the gyro to get data from
	 * @param multiLog the log to log to
	 */
	public static void configure(MultiLog multiLog) {
		try {
			CANJaguarLoader.init(DriveTrain.FrontRight.getInstance(), false);
			CANJaguarLoader.init(DriveTrain.FrontLeft.getInstance(), false);
			CANJaguarLoader.init(DriveTrain.RearRight.getInstance(), false);
			CANJaguarLoader.init(DriveTrain.RearLeft.getInstance(), false);
			
			MecanumDrive mecDrive = new MecanumDrive(
				DriveTrain.FrontRight.getInstance(),
				DriveTrain.FrontLeft.getInstance(),
				DriveTrain.RearRight.getInstance(), 
				DriveTrain.RearLeft.getInstance(), 
				DriveTrain.Stick.getInstance(),
				DriveTrain.PivotGyro.getInstance()
			);
			
			drive = mecDrive;
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the drive train", ex);
		}
		
		try {
			DriveHealthMonitor dhm = new DriveHealthMonitor(multiLog);
			
			dhm.startMonitoring();
		} catch(Exception ex) {
			multiLog.warning("Failed to start DHM");
		}
	}
}

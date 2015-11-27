package org.usfirst.frc.team4342.robot.components.configurators;

import org.usfirst.frc.team4342.robot.components.repository.RobotRepository;
import org.usfirst.frc.team4342.robot.drive.DriveHealthMonitor;
import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.drive.configurators.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;

import ernie.logging.loggers.ILogger;

/**
 * This class is for initializing the drive train
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class DriveConfigurator 
{
	
	private DriveConfigurator() {}
	
	private static MecanumDrive drive;
	
	/**
	 * Gets the mecanum drive
	 * @return the mecanum drive
	 */
	public static MecanumDrive getMecanumDrive() 
	{
		return drive;
	}
	
	/**
	 * Initializes the drive train
	 * @param jaguars the jagaurs associated with the drive  train
	 * @param driveStick the joystick to track
	 * @param pivotGyro the gyro to get data from
	 * @param multiLog the log to log to
	 */
	public static void configure(ILogger log, RobotConsoleLogger consoleLog) {
		try 
		{
			CANJaguarLoader.init(RobotRepository.FrontRight, false);
			CANJaguarLoader.init(RobotRepository.FrontLeft, false);
			CANJaguarLoader.init(RobotRepository.RearRight, false);
			CANJaguarLoader.init(RobotRepository.RearLeft, false);
			
			MecanumDrive mecDrive = new MecanumDrive();
			
			drive = mecDrive;
		} 
		catch(Exception ex) 
		{
			consoleLog.error("Unexpected error while initializing the drive train", ex);
			log.error("Unexpected error while initializing the drive train", ex);
		}
		
		try 
		{
			DriveHealthMonitor dhm = new DriveHealthMonitor(log, consoleLog);
			
			dhm.startMonitoring();
		} 
		catch(Exception ex) 
		{
			consoleLog.warning("Failed to start DHM");
			log.error("Failed to start DHM", ex);
		}
	}
}

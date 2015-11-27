package org.usfirst.frc.team4342.robot.components.configurators;

import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.elevator.ElevatorHealthMonitor;
import org.usfirst.frc.team4342.robot.elevator.setpoints.SetpointMapWrapper;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;

import ernie.logging.loggers.ILogger;

/**
 * This class is for initializing the elevator
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class ElevatorConfigurator 
{	
	private ElevatorConfigurator() {}
	
	private static ElevatorController elevatorController;
	
	/**
	 * Gets the elevator controller
	 * @return the elevator controller
	 */
	public static ElevatorController getElevatorController() 
	{
		return elevatorController;
	}
	
	/**
	 * Initializes the elevator controls
	 * @param elevatorMotors the elevator motors
	 * @param elevStick the joystick to track
	 * @param elevEnc the elevator encoder
	 * @param limitSwitches the elevator limit switches
	 * @param setpoints the elevator presets
	 * @param multiLog the log to log to
	 */
	public static void configure(SetpointMapWrapper setpoints, ILogger log, RobotConsoleLogger consoleLog) 
	{
		try 
		{
		
			ElevatorController elevController = new ElevatorController(setpoints);
			
			elevatorController = elevController;
		} 
		catch(Exception ex)
		{
			consoleLog.error("Unexpected error while initializing the elevator controls", ex);
			log.error("Unexpected error while initializing the elevator controls", ex);
		}
		
		try 
		{
			ElevatorHealthMonitor ehm = new ElevatorHealthMonitor(log, consoleLog);
			
			ehm.startMonitoring();
		} 
		catch(Exception ex) 
		{
			consoleLog.warning("Failed to start EHM");
			log.error("Failed to start EHM", ex);
		}
	}
}

package org.usfirst.frc.team4342.robot;

import org.usfirst.frc.team4342.robot.autonomous.AutoRoutines;
import org.usfirst.frc.team4342.robot.autonomous.configurators.AutoRoutine;
import org.usfirst.frc.team4342.robot.autonomous.configurators.AutoRoutineLoader;
import org.usfirst.frc.team4342.robot.components.DriveTrain;
import org.usfirst.frc.team4342.robot.components.Elevator;
import org.usfirst.frc.team4342.robot.components.configurators.CameraConfigurator;
import org.usfirst.frc.team4342.robot.components.configurators.DriveConfigurator;
import org.usfirst.frc.team4342.robot.components.configurators.ElevatorConfigurator;
import org.usfirst.frc.team4342.robot.drive.configurators.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.logging.LoggingMonitor;

import ernie.logging.loggers.ActiveLogger;
import ernie.logging.loggers.ILogger;
import ernie.logging.loggers.LoggerAsync;
import ernie.logging.loggers.MultiLogger;

import org.usfirst.frc.team4342.robot.logging.factories.RobotLogFactory;
import org.usfirst.frc.team4342.robot.logging.loggers.PDPLogger;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;
import org.usfirst.frc.team4342.robot.logging.loggers.SmartDashboardUpdater;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.shared.FileHelper;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Main class for the entire robot!
 * 
 * Code goes in here: 
 * 		http://www.andymark.com/product-p/am-3000.htm
 * 
 * Pictures of Robot: 
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336234
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336362
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336356
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336354
 * 
 * Logger is from:
 * 		https://github.com/Ernie3/Logger
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class Robot extends IterativeRobot {
	
	private AutoRoutines autos;
	private AutoRoutine autoRoutine;
	
	private LoggerAsync log;
	private RobotConsoleLogger consoleLog;
	private MultiLogger multiLog;
	private PDPLogger pdpLogger;
	
	/**
	 * Initialization code for when the robot is first powered on
	 */
	@Override
    public void robotInit() {
		
		ActiveLogger.info(FileHelper.ACTIVE_LOG_PATH, "D4-main", "Robot turned on");
		
		consoleLog = RobotLogFactory.createRobotConsoleLogger();
		
		try {
			log = RobotLogFactory.createAsyncLogger();
			multiLog = new MultiLogger(new ILogger[] { log, consoleLog });
		} catch(Exception ex) {
			multiLog = new MultiLogger(new ILogger[] { consoleLog });
			consoleLog.warning("Robot log failed to initalize :: " + ExceptionInfo.getType(ex));
		}
		
		try {
			pdpLogger = new PDPLogger(new PowerDistributionPanel(), multiLog);
			pdpLogger.start();
		} catch(Exception ex) {
			multiLog.warning("Failed to start PDPMonitor");
		}
		
		ElevatorConfigurator.configure(
			Elevator.Setpoints.getInstance(), 
			log,
			consoleLog
		);
		
		DriveConfigurator.configure(log, consoleLog);
		
		try {
			autos = new AutoRoutines(
				DriveConfigurator.getMecanumDrive(), 
				ElevatorConfigurator.getElevatorController(),
				multiLog, 
				consoleLog
			);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing autonomous settings", ex);
		}
		
		CameraConfigurator.configure(log, consoleLog);
		
		SmartDashboardUpdater.startUpdating(multiLog);
		
		LoggingMonitor.startMonitoring();
    }
    
	/**
	 * Initialization code for autonomous
	 */
	@Override
    public void autonomousInit() {
		try {
			DriveTrain.PivotGyro.getInstance().reset();
			DriveTrain.PitchGyro.getInstance().reset();
			
			autoRoutine = AutoRoutineLoader.getAutoRoutine();
			
		} catch(Exception ex) {
			multiLog.error(ExceptionInfo.getType(ex) + " in autonomousInit()", ex);
		}
    }
	
	/**
	 * Code to run autonomous. This method runs 50 times per second
	 * while the robot is in auto
	 */
	@Override
    public void autonomousPeriodic() {
		try {
			autos.executeAutonomous(autoRoutine);
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in autonomousPeriodic()", ex);
		}
    }
    
	/**
	 * Initialization code for operator control
	 */
	@Override
    public void teleopInit() {
		try {
			CANJaguarLoader.init(DriveTrain.FrontRight.getInstance(), false);
			CANJaguarLoader.init(DriveTrain.FrontLeft.getInstance(), false);
			CANJaguarLoader.init(DriveTrain.RearRight.getInstance(), false);
			CANJaguarLoader.init(DriveTrain.RearLeft.getInstance(), false);
		} catch(Exception ex) {
			multiLog.error(ExceptionInfo.getType(ex) + " in teleopInit()", ex);
		}
    }
    
	/**
	 * Code to run operator control. This method runs 50 times per second
	 * while the robot is in auto
	 */
	@Override
    public void teleopPeriodic() {
		try {
			DriveConfigurator.getMecanumDrive().drive();
		} catch(Exception ex) {
			tryLogError("Error in teleopPeriodic()", ex);
		}
    }
	
	/**
	 * Initialization code for disabled
	 */
	@Override
	public void disabledInit() {
		try {
			CANJaguarLoader.setCoast(DriveTrain.FrontRight.getInstance());
			CANJaguarLoader.setCoast(DriveTrain.FrontLeft.getInstance());
			CANJaguarLoader.setCoast(DriveTrain.RearRight.getInstance());
			CANJaguarLoader.setCoast(DriveTrain.RearRight.getInstance());
		} catch(Exception ex) {
			multiLog.error(ExceptionInfo.getType(ex) + " in disabledInit()", ex);
		}
	}
	
	/**
	 * Code to run disabled. This method runs 50 times per second
	 * while in disabled
	 */
	@Override
	public void disabledPeriodic() {
		// No need to put code in here,
		// everything that needs to run
		// is on separate threads
	}
	
	/**
	 * Tries to log to the specified text file and Driver Station.
	 * @param message the message to write and display
	 * @param ex the exception associated with the error
	 */
	private void tryLogError(String message, Exception ex) {
		if(!LoggingMonitor.hasLogged()) {
			multiLog.error(message, ex);
		}
		
		LoggingMonitor.logged();
	}
}

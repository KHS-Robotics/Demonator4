package org.usfirst.frc.team4342.robot;

import org.usfirst.frc.team4342.robot.autonomous.AutoRoutine;
import org.usfirst.frc.team4342.robot.autonomous.configurators.AutoRoutineLoader;
import org.usfirst.frc.team4342.robot.autonomous.configurators.AutoRoutines;
import org.usfirst.frc.team4342.robot.components.DriveTrain;
import org.usfirst.frc.team4342.robot.components.configurators.CameraConfigurator;
import org.usfirst.frc.team4342.robot.components.configurators.DriveConfigurator;
import org.usfirst.frc.team4342.robot.components.configurators.ElevatorConfigurator;
import org.usfirst.frc.team4342.robot.drive.configurators.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.elevator.setpoints.Setpoint;
import org.usfirst.frc.team4342.robot.elevator.setpoints.SetpointMapWrapper;
import org.usfirst.frc.team4342.robot.logging.LoggingMonitor;

import ernie.logging.loggers.ILog;
import ernie.logging.loggers.LoggerAsync;
import ernie.logging.loggers.MultiLog;
import ernie.logging.loggers.ActiveLog;

import org.usfirst.frc.team4342.robot.logging.factories.RobotLogFactory;
import org.usfirst.frc.team4342.robot.logging.loggers.PDPLogger;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLog;
import org.usfirst.frc.team4342.robot.logging.loggers.SmartDashboardUpdater;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;

import edu.wpi.first.wpilibj.CameraServer;
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
	
	public static final String ACTIVE_LOG_PATH = "/home/lvuser/ActiveLog.txt";
	
	private CameraServer camera;
	
	private AutoRoutines autos;
	private AutoRoutine autoRoutine;
	
	private Setpoint[] setpoints = {
		new Setpoint(2, 0),
		new Setpoint(4, 325),
		new Setpoint(3, 750),
		new Setpoint(5, 1475),
		new Setpoint(8, 1200),
		new Setpoint(9, 2800)
	};
	
	private static LoggerAsync log;
	private static RobotConsoleLog consoleLog;
	private static MultiLog multiLog;
	private static PDPLogger pdpLogger;
	
	/**
	 * Initialization code for when the robot is first powered on
	 */
	@Override
    public void robotInit() {
		
		ActiveLog.info(ACTIVE_LOG_PATH, "D4-main", "Robot turned on");
		
		consoleLog = RobotLogFactory.createRobotConsoleLog();
		
		try {
			log = RobotLogFactory.createAsyncLog();
			multiLog = new MultiLog(new ILog[] { log, consoleLog });
		} catch(Exception ex) {
			multiLog = new MultiLog(new ILog[] { consoleLog });
			consoleLog.warning("Robot log failed to initalize :: " + ExceptionInfo.getType(ex));
		}
		
		try {
			pdpLogger = new PDPLogger(new PowerDistributionPanel(), multiLog);
			pdpLogger.start();
		} catch(Exception ex) {
			multiLog.warning("Failed to start PDPMonitor");
		}
		
		try {
			ElevatorConfigurator.configure(
				new SetpointMapWrapper(setpoints), 
				multiLog
			);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the elevator controls", ex);
		}
		
		try {
			DriveConfigurator.configure(multiLog);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the drive train", ex);
		}
		
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
		
		try {
			CameraConfigurator.configure(camera);
		} catch(Exception ex) {
			multiLog.warning("Failed to initialize the camera");
		}
		
		try {
			SmartDashboardUpdater.startUpdating(multiLog);
		} catch(Exception ex) {
			multiLog.warning("Failed to start updating SDB");
		}
		
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
		// No need to put code in here,
		// everything that needs to run
		// is on separate threads
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

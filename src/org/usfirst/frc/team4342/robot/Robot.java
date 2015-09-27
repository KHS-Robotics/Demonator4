package org.usfirst.frc.team4342.robot;

import org.usfirst.frc.team4342.configurators.CameraConfigurator;
import org.usfirst.frc.team4342.configurators.DriveConfigurator;
import org.usfirst.frc.team4342.configurators.ElevatorConfigurator;
import org.usfirst.frc.team4342.robot.autonomous.AutoRoutine;
import org.usfirst.frc.team4342.robot.autonomous.AutoRoutineLoader;
import org.usfirst.frc.team4342.robot.autonomous.AutoRoutines;
import org.usfirst.frc.team4342.robot.drive.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.elevator.Setpoint;
import org.usfirst.frc.team4342.robot.elevator.SetpointMapWrapper;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.PDPLogger;
import org.usfirst.frc.team4342.robot.logging.RobotLogFactory;
import org.usfirst.frc.team4342.robot.logging.SmartDashboardUpdater;

import Logging.*;

import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Ultrasonic;

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
	
	private Joystick driveStick = new Joystick(0), elevatorStick = new Joystick(1);
	
	private CANJaguar frontRight = new CANJaguar(22),
					  frontLeft = new CANJaguar(21), 
					  rearRight = new CANJaguar(23), 
					  rearLeft = new CANJaguar(20);
	private CANJaguar[] jaguars = { frontLeft, frontRight, rearLeft, rearRight };
	
	private Talon rightElev = new Talon(0), leftElev = new Talon(1);
	private Talon[] talons = { rightElev, leftElev };
	
	private Encoder elevatorEnc = new Encoder(8, 9, false, EncodingType.k1X);;
	
	private DigitalInput topElevLS = new DigitalInput(7), 
						 botElevLS = new DigitalInput(4), 
						 rightPhotoSensor = new DigitalInput(0), 
						 leftPhotoSensor = new DigitalInput(1);
	private DigitalInput[] limitSwitches = { topElevLS, botElevLS };
	
	private Ultrasonic ultra = new Ultrasonic(2, 3, Ultrasonic.Unit.kInches);
	
	private Gyro pivotGyro = new Gyro(0), pitchGyro = new Gyro(1);;
	
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
		
		pivotGyro.setSensitivity(0.007);
		pitchGyro.setSensitivity(0.007);
		
		ultra.setAutomaticMode(true);
		
		SmartDashboardUpdater.addJoystick("Joy-Drive", driveStick);
		SmartDashboardUpdater.addJoystick("Joy-Elev", elevatorStick);
		
		SmartDashboardUpdater.addEncoder("Enc-Elev", elevatorEnc);
		
		SmartDashboardUpdater.addJagaur("FR", frontRight);
		SmartDashboardUpdater.addJagaur("FL", frontLeft);
		SmartDashboardUpdater.addJagaur("RR", rearRight);
		SmartDashboardUpdater.addJagaur("RL", rearLeft);
		
		SmartDashboardUpdater.addDigitalInput("LS-Top", topElevLS);
		SmartDashboardUpdater.addDigitalInput("LS-Bottom", botElevLS);
		SmartDashboardUpdater.addDigitalInput("Photo-R", rightPhotoSensor);
		SmartDashboardUpdater.addDigitalInput("Photo-L", leftPhotoSensor);
		
		SmartDashboardUpdater.addGyro("G-Pivot", pivotGyro);
		SmartDashboardUpdater.addGyro("G-Pitch", pitchGyro);
		
		SmartDashboardUpdater.setUltrasonic(ultra);
		
		SmartDashboardUpdater.startUpdating(multiLog);
		
		try {
			ElevatorConfigurator.configure(
				talons,
				elevatorStick,
				elevatorEnc,
				limitSwitches,
				new SetpointMapWrapper(setpoints), 
				multiLog
			);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the elevator controls", ex);
		}
		
		try {
			DriveConfigurator.configure(jaguars, driveStick, pivotGyro, multiLog);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the drive train", ex);
		}
		
		try {
			autos = new AutoRoutines(
				DriveConfigurator.getMecanumDrive(), 
				ElevatorConfigurator.getElevatorController(), 
				ultra, 
				leftPhotoSensor, 
				rightPhotoSensor, 
				pivotGyro, 
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
			LoggingMonitor.startMonitoring();
		} catch(Exception ex) {
			multiLog.warning("Failed to initialize Logging Monitor");
		}
    }
    
	/**
	 * Initialization code for autonomous
	 */
	@Override
    public void autonomousInit() {
		try {
			pivotGyro.reset();
			pitchGyro.reset();
			
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
			CANJaguarLoader.init(jaguars, false);
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
		
    }
	
	/**
	 * Initialization code for disabled
	 */
	@Override
	public void disabledInit() {
		try {
			CANJaguarLoader.setCoast(jaguars);
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

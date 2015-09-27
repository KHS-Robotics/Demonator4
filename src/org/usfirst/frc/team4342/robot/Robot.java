package org.usfirst.frc.team4342.robot;

import org.usfirst.frc.team4342.robot.autonomous.AutoRoutine;
import org.usfirst.frc.team4342.robot.autonomous.AutoRoutineLoader;
import org.usfirst.frc.team4342.robot.autonomous.AutoRoutines;
import org.usfirst.frc.team4342.robot.drive.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.drive.DriveHealthMonitor;
import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.elevator.ElevatorHealthMonitor;
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
	
	private boolean enableFod;
	private boolean logged;
	private static long numLoops;
	
	private Joystick driveStick, elevatorStick;
	
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private CANJaguar[] jaguars = { frontRight, frontLeft, rearRight, rearLeft };
	
	private Talon rightElev, leftElev;
	
	private Encoder elevatorEnc;
	
	private DigitalInput topElevLS, botElevLS, rightPhotoSensor, leftPhotoSensor;
	
	private Ultrasonic ultra;
	
	private Gyro pivotGyro, pitchGyro;
	
	private CameraServer camera;
	
	private MecanumDrive mecDrive;
	private ElevatorController elevController;
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
			driveStick = new Joystick(0);
			elevatorStick = new Joystick(1);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the joysticks", ex);
		}
		
		try {
			rightElev = new Talon(0);
			leftElev = new Talon(1);
			
			topElevLS = new DigitalInput(7);
			botElevLS = new DigitalInput(4);
			
			elevatorEnc = new Encoder(8, 9, false, EncodingType.k1X);
			
			elevController = new ElevatorController(
				rightElev, 
				leftElev,
				elevatorStick,
				elevatorEnc, 
				topElevLS, 
				botElevLS, 
				new SetpointMapWrapper(setpoints)
			);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the elevator controls", ex);
		}
			
		try {
			pivotGyro = new Gyro(0);
			pitchGyro = new Gyro(1);
			pivotGyro.setSensitivity(0.007);
			pitchGyro.setSensitivity(0.007);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the gyros", ex);
		}
		
		try {
			frontRight = new CANJaguar(22);
			frontLeft = new CANJaguar(21);
			rearRight = new CANJaguar(23);
			rearLeft = new CANJaguar(20);
			
			CANJaguarLoader.init(jaguars, false);
			
			mecDrive = new MecanumDrive(
				frontLeft,
				frontRight,
				rearLeft, 
				rearRight, 
				driveStick,
				pivotGyro
			);
		} catch(Exception ex) {
			multiLog.error("Unexpected error while initializing the drive train", ex);
		}
		
		try {
			ultra = new Ultrasonic(2, 3, Ultrasonic.Unit.kInches);
			ultra.setAutomaticMode(true);
			
			rightPhotoSensor = new DigitalInput(0);
			leftPhotoSensor = new DigitalInput(1);
			
			autos = new AutoRoutines(
				mecDrive, 
				elevController, 
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
			SmartDashboardUpdater.addJoystick("Joy-Drive", driveStick);
			SmartDashboardUpdater.addJoystick("Joy-Elev", elevatorStick);
		} catch(Exception ex) {
			multiLog.warning("Failed to add Joysticks on SDB");
		}
		
		try {
			SmartDashboardUpdater.addEncoder("Enc-Elev", elevatorEnc);
		} catch(Exception ex) {
			multiLog.warning("Failed to add Encoders on SDB");
		}
		
		try {
			SmartDashboardUpdater.addJagaur("FR", frontRight);
			SmartDashboardUpdater.addJagaur("FL", frontLeft);
			SmartDashboardUpdater.addJagaur("RR", rearRight);
			SmartDashboardUpdater.addJagaur("RL", rearLeft);
		} catch(Exception ex) {
			multiLog.warning("Failed to add CANJaguars on SDB");
		}
		
		try {
			SmartDashboardUpdater.addDigitalInput("LS-Top", topElevLS);
			SmartDashboardUpdater.addDigitalInput("LS-Bottom", botElevLS);
			SmartDashboardUpdater.addDigitalInput("Photo-R", rightPhotoSensor);
			SmartDashboardUpdater.addDigitalInput("Photo-L", leftPhotoSensor);
		} catch(Exception ex) {
			multiLog.warning("Failed to add DigitalInputs on SDB");
		}
		
		try {
			SmartDashboardUpdater.addGyro("G-Pivot", pivotGyro);
			SmartDashboardUpdater.addGyro("G-Pitch", pitchGyro);
		} catch(Exception ex) {
			multiLog.warning("Failed to add Gyros on SDB");
		}
		
		try {
			SmartDashboardUpdater.setUltrasonic(ultra);
		} catch(Exception ex) {
			multiLog.warning("Failed to add Ultrasonic on SDB");
		}
		
		try {
			SmartDashboardUpdater.startUpdating(multiLog);
		} catch(Exception ex) {
			multiLog.warning("Failed to start updating SmartDashboard");
		}
		
		try {
			DriveHealthMonitor dhm = new DriveHealthMonitor(
				driveStick, 
				frontRight, 
				frontLeft, 
				rearRight, 
				rearLeft,
				multiLog
			);
			
			dhm.startMonitoring();
			
		} catch(Exception ex) {
			multiLog.warning("Failed to start the DHM");
		}
		
		try {
			ElevatorHealthMonitor ehm = new ElevatorHealthMonitor(
				elevatorStick, 
				elevatorEnc, 
				topElevLS, 
				botElevLS, 
				multiLog
			);
			
			ehm.startMonitoring();
			
		} catch(Exception ex) {
			multiLog.warning("Failed to start the EHM");
		}
		
		try {
			camera = CameraServer.getInstance();
			camera.setQuality(50);
			camera.startAutomaticCapture("cam0");
		} catch(Exception ex) {
			multiLog.warning("Failed to initialize the camera");
		}
    }
    
	/**
	 * Initialization code for autonomous
	 */
	@Override
    public void autonomousInit() {
		try {
			logged = false;
			
			numLoops = 0;
			
			CANJaguarLoader.init(jaguars, true);
			
			pivotGyro.reset();
			pitchGyro.reset();
			
			autoRoutine = AutoRoutineLoader.getAutoRoutine();
			
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in autonomousInit()", ex);
			logged = false;
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
			logged = false;
			numLoops = 0;
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in teleopInit()", ex);
			logged = false;
		}
    }
    
	/**
	 * Code to run operator control. This method runs 50 times per second
	 * while the robot is in auto
	 */
	@Override
    public void teleopPeriodic() {
		try {
			mecDrive.drive();
			checkForDriveTypeChange(driveStick, 7);
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in teleopPeriodic()", ex);
		}
    }
	
	/**
	 * Initialization code for disabled
	 */
	@Override
	public void disabledInit() {
		try {
			logged = false;
			numLoops = 0;
			CANJaguarLoader.setCoast(jaguars);
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in disabledInit()", ex);
			logged = false;
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
	 * Gets the current number of invocations of the periodic method
	 * the robot is executing
	 * @return the number of invocations of the periodic method the robot is executing
	 */
	public static long getNumLoops() {
		return numLoops;
	}
	
	/**
	 * Checks if the user wants field oriented or robot oriented
	 */
	private void checkForDriveTypeChange(Joystick joystick, int button) {
		if(joystick.getRawButton(button)) {
			enableFod = enableFod ? false : true;
			if(enableFod) {
				mecDrive.enableFod();
			} else {
				mecDrive.disableFod();
			}
		}
	}
	
	/**
	 * Tries to log to the specified text file and Driver Station.
	 * @param message the message to write and display
	 * @param ex the exception associated with the error
	 */
	private void tryLogError(String message, Exception ex) {
		if(!logged) {
			multiLog.error(message, ex);
		}
		
		logged = true;
	}
}

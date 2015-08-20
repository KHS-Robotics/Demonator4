package org.usfirst.frc.team4342.robot;

import org.usfirst.frc.team4342.robot.autonomous.AutoRoutineLoader;
import org.usfirst.frc.team4342.robot.autonomous.AutoRoutines;
import org.usfirst.frc.team4342.robot.drive.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.elevator.Setpoint;
import org.usfirst.frc.team4342.robot.elevator.SetpointMapWrapper;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.PDPLogger;
import org.usfirst.frc.team4342.robot.logging.RobotLogFactory;

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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A letter from the editor: these are the only methods
 * that should exist within this class. Please do not
 * add any more.
 * 
 * Main class for the entire robot!
 * 
 * Code goes in here: http://www.andymark.com/product-p/am-3000.htm
 * 
 * Pictures of Robot: 
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336234
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336362
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336356
 * 		http://www.demonrobotics4342.org/apps/photos/photo?photoid=198336354
 * 
 * @author khsrobotics
 */
public class Robot extends IterativeRobot {
	
	private boolean enableFod;
	private boolean logged;
	private int autoRoutine;
	private static long numLoops;
	
	private Joystick driveStick, elevatorStick;
	
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	
	private Talon rightElev, leftElev;
	
	private Encoder elevatorEnc;
	
	private DigitalInput topElevLS, botElevLS, rightPhotoSensor, leftPhotoSensor;
	
	private Ultrasonic ultra;
	
	private Gyro pivotGyro, pitchGyro;
	
	private CameraServer camera;
	
	private MecanumDrive mecDrive;
	private ElevatorController elevController;
	private AutoRoutines autos;
	
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
	private static PDPLogger pdpLogger;
	
	/**
	 * Initialization code for when the robot is first powered on
	 */
	@Override
    public void robotInit() {
		
		ExceptionInfo exInfo = null;
		
		String path = "/home/lvuser/ActiveLog.txt";
		ActiveLog.setLogFile(path);
		ActiveLog.info("Demonator4", "Robot turned on");
		
		consoleLog = RobotLogFactory.createRobotConsoleLog();
		
		try {
			log = RobotLogFactory.createAsyncLog();
		} catch(Exception ex) {
			// hmmm... where to log when the log fails...
			exInfo = new ExceptionInfo(ex);
		}
		
		if(log == null) {
			consoleLog.warning("Robot log failed to initalize :: " + exInfo.getType());
		}
		
		try {
			pdpLogger = new PDPLogger(new PowerDistributionPanel(), log, consoleLog);
			pdpLogger.start();
		} catch(Exception ex) {
			tryLogError("Failed to start PDPMonitor :: " + ex.getClass().getSimpleName(), ex);
		}
		
		try {
			
			driveStick = new Joystick(0);
			elevatorStick = new Joystick(1);
			
			frontRight = new CANJaguar(22);
			frontLeft = new CANJaguar(21);
			rearRight = new CANJaguar(23);
			rearLeft = new CANJaguar(20);
			
			CANJaguarLoader.init(
				new CANJaguar[] {
					frontRight,
					frontLeft,
					rearRight,
					rearLeft
				},
				false
			);
			
			rightElev = new Talon(0);
			leftElev = new Talon(1);
			
			elevatorEnc = new Encoder(8, 9, false, EncodingType.k1X);
			
			topElevLS = new DigitalInput(7);
			botElevLS = new DigitalInput(4);
			
			ultra = new Ultrasonic(2, 3, Ultrasonic.Unit.kInches);
			ultra.setAutomaticMode(true);
			
			rightPhotoSensor = new DigitalInput(0);
			leftPhotoSensor = new DigitalInput(1);
			
			pivotGyro = new Gyro(0);
			pitchGyro = new Gyro(1);
			pivotGyro.setSensitivity(0.007);
			pitchGyro.setSensitivity(0.007);
			
			camera = CameraServer.getInstance();
			camera.setQuality(50);
			camera.startAutomaticCapture("cam0");
			
			mecDrive = new MecanumDrive(
				frontLeft,
				frontRight,
				rearLeft, 
				rearRight, 
				driveStick,
				pivotGyro
			);
			
			elevController = new ElevatorController(
				rightElev, 
				leftElev,
				elevatorStick,
				elevatorEnc, 
				topElevLS, 
				botElevLS, 
				new SetpointMapWrapper(setpoints)
			);
			
			autos = new AutoRoutines(
				mecDrive, 
				elevController, 
				ultra, 
				leftPhotoSensor, 
				rightPhotoSensor, 
				pivotGyro, 
				log, 
				consoleLog
			);
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in robotInit()", ex);
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
			
			CANJaguarLoader.init(
				new CANJaguar[] {
					frontRight,
					frontLeft,
					rearRight,
					rearLeft
				},
				true
			);
			
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
			
			putDataToSmartDb();
			
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
			
			putDataToSmartDb();
			
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
		try {
			putDataToSmartDb();
		} catch(Exception ex) {
			tryLogError(ExceptionInfo.getType(ex) + " in autonomousPeriodic()", ex);
		}
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
		if(!logged && log != null) {
			log.error(message, ex);
		}
		
		if(!logged) {
			consoleLog.error(message, ex);
		}
		
		logged = true;
	}
	
	/**
	 * Prints sensor data to the driver stations smart dash board,
	 * mostly used for debugging
	 */
	private void putDataToSmartDb() {
		if(numLoops % 10 == 0) {
			SmartDashboard.putBoolean("Fod", enableFod);
			SmartDashboard.putBoolean("MecD-Fod", mecDrive.isFodEnabled());
			
			SmartDashboard.putNumber("Joy-D-X", driveStick.getX());
			SmartDashboard.putNumber("Joy-D-Y", driveStick.getY());
			SmartDashboard.putNumber("Joy-D-Z", driveStick.getZ());
			
			SmartDashboard.putNumber("Joy-E-Y", elevatorStick.getY());
			
			SmartDashboard.putNumber("Enc-FR", frontRight.getPosition());
			SmartDashboard.putNumber("Enc-FL", frontLeft.getPosition());
			SmartDashboard.putNumber("Enc-RL", rearLeft.getPosition());
			SmartDashboard.putNumber("Enc-RR", rearRight.getPosition());
			
			SmartDashboard.putNumber("Enc-Elev", elevatorEnc.get());
			
			SmartDashboard.putBoolean("LS-Top", topElevLS.get());
			SmartDashboard.putBoolean("LS-Bot", botElevLS.get());
			
			SmartDashboard.putNumber("Gyro-Piv", pivotGyro.getAngle());
			SmartDashboard.putNumber("Gyro-Pit", pitchGyro.getAngle());
			
			SmartDashboard.putNumber("U-Dist", ultra.getRangeInches());
		}
	}
}

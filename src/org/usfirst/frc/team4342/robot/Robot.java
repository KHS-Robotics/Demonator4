package org.usfirst.frc.team4342.robot;

import java.io.IOException;

import Logging.*;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
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
 * @author khsrobotics
 */
public class Robot extends IterativeRobot {
	
	private boolean enableFod;
	private boolean logged;
	private long numLoops;
	
	private static DriverStation ds;
	
	private Joystick driveStick, elevatorStick;
	
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	
	private Talon rightElev, leftElev;
	
	private Encoder elevatorEnc;
	
	private DigitalInput topElevLS, botElevLS, rightPhotoSensor, leftPhotoSensor;
	
	private Gyro pivotGyro, pitchGyro;
	
	private CameraServer camera;
	
	
	private MecanumDrive mecDrive;
	private ElevatorController elevController;
	
	private int[] setpointButtons = {
			2, 3, 4
	};
	
	private int[] setpointEncValues = {
			0, 400, 1700
	};
	
	private static ILog log;
	private static PDPMonitor pdpMonitor;
	
	/**
	 * Initialization code for when the robot is first powered on
	 */
	@Override
    public void robotInit() {
		
		ds = DriverStation.getInstance();
		
		try {
			log = RobotLogFactory.createLocalLog();
		} catch(IOException ex) {
			// hmmm... where to log when the
			// log fails...
			printWarningToDS("Failed to initalize logger, "
				+ "please contact Ernie or Magnus when you can");
		}
		
		try {
			pdpMonitor = new PDPMonitor(new PowerDistributionPanel());
		} catch(Exception ex) {
			printWarningToDS("Failed to initalize and start PDP monitor, "
					+ "please contact Ernie or Magnus when you can");
		}
		
		try
		{
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
			
			rightPhotoSensor = new DigitalInput(0);
			leftPhotoSensor = new DigitalInput(1);
			
			pivotGyro = new Gyro(0);
			pitchGyro = new Gyro(1);
			pivotGyro.setSensitivity(0.007);
			pitchGyro.setSensitivity(0.007);
			
			camera = CameraServer.getInstance();
			camera.setQuality(50);
			camera.startAutomaticCapture("cam0");
			
			
			mecDrive = new MecanumDrive(frontLeft, frontRight, rearLeft, 
									rearRight, driveStick, pivotGyro, false);
			
			elevController = new ElevatorController(rightElev, leftElev, elevatorStick,
													elevatorEnc, topElevLS, botElevLS,
											new SetpointMapWrapper(setpointButtons, setpointEncValues));
		}
		catch(Exception ex)
		{
			if(log != null) {
				log.error("Error in robotInit()", ex);
			}
			
			printErrorToDS("Exception in robotInit(), please alert Ernie or Magnus");
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
		} catch(Exception ex) {
			if(log != null) {
				log.error("Error in autonomousInit()", ex);
			}
			
			printErrorToDS("Exception in autonomousInit(), please alert Ernie or Magnus");
		}
    }
	
	/**
	 * Code to run autonomous. This method runs 50 times per second
	 * while the robot is in auto
	 */
	@Override
    public void autonomousPeriodic() {
		try {
			
			
			
			if(numLoops % 10 == 0) {
				putDataToSmartDb();
			}
		} catch(Exception ex) {
			if(!logged && log != null) {
				log.error("Error in autonomousPeriodic()", ex);
				printErrorToDS("Exception in autonomousPeriodic(), please alert Ernie or Magnus");
				logged = true;
			}
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
			if(log != null) {
				log.error("Error in teleopInit()", ex);
			}
			
			printErrorToDS("Exception in teleopInit(), please alert Ernie or Magnus");
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
			
			if(driveStick.getRawButton(7)) {
				enableFod = enableFod ? false : true;
				if(enableFod) {
					mecDrive.enableFod();
				} else {
					mecDrive.disableFod();
				}
			}
			
			if(numLoops % 10 == 0) {
				putDataToSmartDb();
			}
		} catch(Exception ex) {
			if(!logged && log != null) {
				log.error("Error in teleopPeriodic()", ex);
				printErrorToDS("Exception in teleopPeriodic(), please alert Ernie or Magnus");
				logged = true;
			}
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
			if(log != null) {
				log.error("Error in disabledInit()", ex);
			}
			
			printErrorToDS("Exception in disabledInit(), please alert Ernie or Magnus");
		}
	}
	
	/**
	 * Code to run disabled. This method runs 50 times per second
	 * while in disabled
	 */
	@Override
	public void disabledPeriodic() {
		try {
			if(numLoops % 10 == 0) {
				putDataToSmartDb();
			}
		} catch(Exception ex) {
			if(!logged && log != null) {
				log.error("Error in autonomousPeriodic()", ex);
				printErrorToDS("Exception in disabledPeriodic(), please alert Ernie or Magnus");
				logged = true;
			}
		}
	}
	
	/**
	 * Gets the logger for the robot so other classes can use it
	 * @return the logger for the robot
	 */
	public static ILog getRobotLog() {
		if(log == null) {
			throw new NullPointerException("Robot log is equal to null");
		}
		
		return log;
	}
	
	/**
	 * Prints an error message to the driver station
	 * @param message the error message to print
	 */
	public static void printErrorToDS(String message) {
		DriverStation.reportError("ERROR: " + message, false);
	}
	
	/**
	 * Prints a warning message to the driver station
	 * @param message the warning message to print
	 */
	public static void printWarningToDS(String message) {
		DriverStation.reportError("WARNING: " + message, false);
	}
	
	/**
	 * Gets the robot's driver station
	 * @return the robot's driver station
	 */
	public static DriverStation getDriverStation() {
		return ds;
	}
	
	/**
	 * Prints sensor data to the driver stations smart dash board,
	 * mostly used for debugging
	 */
	private void putDataToSmartDb() {
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
	}
}

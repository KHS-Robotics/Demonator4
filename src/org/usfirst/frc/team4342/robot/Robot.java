package org.usfirst.frc.team4342.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Gyro;
import Logging.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 * 
 * WO SO THIS IS HOW GITHUB WORKS
 */
public class Robot extends IterativeRobot 
{
	private Joystick driveStick, elevatorStick;
	
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	
	private Talon rightElevatorMotor, leftElevatorMotor, containerGrabber;
	
	private Encoder elevatorEncoder;
	
	private DigitalInput topElevLS, botElevLS;
	
	private CameraServer camera;
	
	private ILog log;
	private LogRunnable logRunnable;
	private Thread logThread;
	private Thread t;
	
	private static long numLoops;
	private boolean loggedError, fieldOriented = true;
	private Ultrasonic ultra;
	
	private Gyro gyro;
	private DigitalInput rightPhotoSensor, leftPhotoSensor;
	private ArrayList<Setpoint> points;
	private ElevatorControl elevC;
	
	private RobotDrive robotDrive;
	private Drive fod;
	
	private PIDTuner pidTuner;
	
	private AutoRoutines autoRoutines;
	
	private double initAngle;
	
	private double kRLPD,kRLID,kRLDD;
	private double kRLP, kRLI, kRLD,kRLSetpoint; //rear left pid
	
	private int autoStep;
	
	private double offset;

	
	private double distanceError = 0;
	private double distanceDer = 0;
	
	private int autoRoutine; // 0 = pickup 3 totes, 1 = pickup 1 totes, 2 = pickup 1 container
	
	private PowerDistributionPanel pdp;
	
	static class PID {
		static class FrontRight {
			static final double kP = 1.0;
			static final double kI = 0.005;
			static final double kD = 0.001;
		}
		
		static class FrontLeft {
			static final double kP = 0.6;
			static final double kI = 0.003;
			static final double kD = 0.002;
		}
		
		static class RearRight {
			static final double kP = 1.0;
			static final double kI = 0.009;
			static final double kD = 0.001;
		}
		
		static class RearLeft {
			static final double kP = 1.0;
			static final double kI = 0.009;
			static final double kD = 0.001;
		}
		
		static final int kCodesPerRev = 512;
	}
	
    /**
     * This method is run when the robot is first started up and should be
     * used for any initialization code
     */
	@Override
    public void robotInit()
	{
//		try
//		{
			loggedError = false;
//			log = new LocalLog("Demonator IV", RobotConstants.LOG_TEXT_FILE, true);
//			logRunnable = new LogRunnable(log);
//			logThread = new Thread(logRunnable);
			
//			camera = CameraServer.getInstance();
//			camera.setQuality(50);
//			camera.startAutomaticCapture("cam0");
			
			driveStick = new Joystick(0);
			elevatorStick = new Joystick(1);
			
			elevatorEncoder = new Encoder(8, 9,false,EncodingType.k1X);
			
			frontRight = new CANJaguar(22);
			frontLeft = new CANJaguar(21);
			rearRight = new CANJaguar(23);
			rearLeft = new CANJaguar(20);
			
			frontRight.configNeutralMode(CANJaguar.NeutralMode.Coast);
			frontLeft.configNeutralMode(CANJaguar.NeutralMode.Coast);
			rearRight.configNeutralMode(CANJaguar.NeutralMode.Coast);
			rearLeft.configNeutralMode(CANJaguar.NeutralMode.Coast);
			
			frontRight.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.FrontRight.kP, PID.FrontRight.kI, PID.FrontRight.kD);
			frontLeft.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.FrontLeft.kP, PID.FrontLeft.kI, PID.FrontLeft.kD);
			rearLeft.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.RearRight.kP, PID.RearRight.kI, PID.RearRight.kD);
			rearRight.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.RearLeft.kP, PID.RearLeft.kI, PID.RearLeft.kD);
			
			frontRight.enableControl();
			frontLeft.enableControl();
			rearRight.enableControl();
			rearLeft.enableControl();
			
			fod = new Drive(frontLeft, frontRight, rearLeft, rearRight, driveStick);
			//robotDrive = new RobotDrive(frontLeft, rearLeft, frontRight, rearRight);
			
			SmartDashboard.putNumber("Elev-P Up", kRLP);
			SmartDashboard.putNumber("Elev-I Up", kRLI);
			SmartDashboard.putNumber("Elev-D Up", kRLD);
			SmartDashboard.putNumber("Elev-P Down",kRLPD );
			SmartDashboard.putNumber("Elev-I Down",kRLID );
			SmartDashboard.putNumber("Elev-D Down",kRLDD );
			
			ultra = new Ultrasonic(2, 3, Ultrasonic.Unit.kInches);
			ultra.setAutomaticMode(true);
			
			rightElevatorMotor = new Talon(0);
			leftElevatorMotor = new Talon(1);
			containerGrabber = new Talon(2);
			
			pidTuner = new PIDTuner(frontRight, frontLeft, rearRight, rearLeft, elevC,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
			
			points = new ArrayList<Setpoint>();
			points.add(new Setpoint(2, new int[]{-50}));
			points.add(new Setpoint(3, new int[]{500}));
			points.add(new Setpoint(4, new int[]{1000}));
			points.add(new Setpoint(5, new int[]{2000}));
			points.add(new Setpoint(6,new int[]{3000}));
			points.add(new Setpoint(10,new int[]{2000,1000,2000,-10}));
			
			
			botElevLS = new DigitalInput(4);
			topElevLS = new DigitalInput(7);
			
			elevC = new ElevatorControl(elevatorStick, rightElevatorMotor, leftElevatorMotor, 
										elevatorEncoder, points,botElevLS , topElevLS);
			
			gyro = new Gyro(0);
			gyro.setSensitivity(0.007);
			
			rightPhotoSensor = new DigitalInput(0);
			leftPhotoSensor = new DigitalInput(1);
			
			SmartDashboard.putNumber("Setpoint", 0.0);
			SmartDashboard.putNumber("angleP", 0.01);
			SmartDashboard.putNumber("U-Close", 12.0);
			SmartDashboard.putNumber("U-Far", 15.0);
			SmartDashboard.putNumber("howCloseP", 0.01);
			SmartDashboard.putNumber("Auto-Routine", (int) autoRoutine);
			
			pdp = new PowerDistributionPanel();
		}
//		catch(Exception ex)
//		{
////			logRunnable.setLogInfo(Severity.ERROR, "Unexpected error while attempting to start robot", ex);
////			logThread.start();
//			ex.printStackTrace();
//			DriverStation.reportError("ERROR in robotInit()... Go get Ernie or Magnus!\n", false);
//		}
    //}
	
	/**
	 * This method is run when the robot enters operator control and should 
	 * be used for any initialization code
	 */
	@Override
	public void teleopInit() 
	{
		try
		{
			//elevC.isEnabled = true;
			loggedError = false;
			numLoops = 0;
		}
		catch(Exception ex)
		{
//			logRunnable.setLogInfo(Severity.ERROR, "Unexpected error while initializing operator control", ex);
//			logThread.start();
			DriverStation.getInstance();
			DriverStation.reportError("ERROR in teleopInit()... Go get Ernie or Magnus!", false);
		}
	}
	
	/**
	 * This method is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic()
	{
//		try
//		{

			
			if(driveStick.getRawButton(3)) {
				gyro.reset();
			}
			
			if(driveStick.getRawButton(12)) {
				fieldOriented = fieldOriented ? false : true;
			}
			
			fod.fieldOriented(-sensitivityControl(driveStick.getX()),sensitivityControl(driveStick.getY()), 
							-sensitivityControl(driveStick.getZ()), fieldOriented ? gyro.getAngle() : 0.0);
			
			if(elevatorStick.getRawButton(7)) {
				elevatorEncoder.reset();
			}
			
			if(numLoops % 10 == 0) {
				putDataToSmartDashboard();
			}
			
			numLoops++;
		//}
//		catch(Exception ex)
//		{
//			if(!loggedError)
//			{
////				logRunnable.setLogInfo(Severity.ERROR, "Unexpected error while in operator control", ex);
////				logThread.start();
//				DriverStation.getInstance();
//				DriverStation.reportError("ERROR in teleopPeriodic()... Go get Ernie or Magnus!", true);
//			}
//			loggedError = true;
//		}
	}
	/**
	 * This method is run when the robot enters autonomous and should 
	 * be used for any initialization code
	 */
	@Override
	public void autonomousInit()
	{
		try
		{
			loggedError = false;
			
			frontRight.setPositionMode(CANJaguar.kQuadEncoder, 512, 0.0, 0.0, 0.0);
			frontLeft.setPositionMode(CANJaguar.kQuadEncoder, 512, 0.0, 0.0, 0.0);
			rearRight.setPositionMode(CANJaguar.kQuadEncoder, 512, 0.0, 0.0, 0.0);
			rearLeft.setPositionMode(CANJaguar.kQuadEncoder, 512, 0.0, 0.0, 0.0);
			
			frontRight.enableControl(0);
			frontLeft.enableControl(0);
			rearRight.enableControl(0);
			rearLeft.enableControl(0);
			
			frontRight.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.FrontRight.kP, PID.FrontRight.kI, PID.FrontRight.kD);
			frontLeft.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.FrontLeft.kP, PID.FrontLeft.kI, PID.FrontLeft.kD);
			rearLeft.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.RearRight.kP, PID.RearRight.kI, PID.RearRight.kD);
			rearRight.setSpeedMode(CANJaguar.kQuadEncoder, PID.kCodesPerRev, PID.RearLeft.kP, PID.RearLeft.kI, PID.RearLeft.kD);
			
			frontRight.enableControl();
			frontLeft.enableControl();
			rearRight.enableControl();
			rearLeft.enableControl();
			
			autoRoutine = (int) SmartDashboard.getNumber("Auto-Routine");
			
			autoStep = 0;
			numLoops = 0;
			gyro.reset();
			initAngle = gyro.getAngle();
			autoRoutines = new AutoRoutines(fod, gyro, initAngle, rightPhotoSensor, leftPhotoSensor,
											elevC, ultra);
		}
		catch(Exception ex)
		{
//			logRunnable.setLogInfo(Severity.ERROR, "Unexpected error while initializing autonomous", ex);
//			logThread.start();
			DriverStation.getInstance();
			DriverStation.reportError("ERROR in autonomousInit()... Go get Ernie or Magnus!", false);
		}
	}

    /**
     * This method is called periodically during autonomous
     */
	@Override
    public void autonomousPeriodic()
	{
		try
		{
			switch(autoRoutine)
			{
				case 1:
					autoRoutines.autoRoutinePickUpThreeTotes(numLoops);
					
				break;
				
				case 2:
					autoRoutines.autoRoutinePickUpOneTote(numLoops);
				break;
				
				case 3:
					autoRoutines.autoRoutinePickupOneContainer(numLoops);
				break;
				
				default:
					// Move backward
				break;
			}
			
			if(numLoops % 5 == 0)
				putDataToSmartDashboard();
			
			numLoops++;
		}
		catch(Exception ex)
		{
			if(!loggedError)
			{
//				logRunnable.setLogInfo(Severity.ERROR, "Unexpected error while in autonomous", ex);
//				logThread.start();
				DriverStation.getInstance();
				DriverStation.reportError("ERROR in autonomousPeriodic()... Go get Ernie or Magnus!", false);
			}
			loggedError = true;
		}
    }
	
	/**
	 * This method is run when the robot enters disabled and should 
	 * be used for any initialization code
	 */
	@Override
	public void disabledInit()
	{
		//elevC.isEnabled = false;
//		try
//		{
			loggedError = false;
			numLoops = 0;
//		}
//		catch(Exception ex)
//		{
////			logRunnable.setLogInfo(Severity.ERROR, "Unexpected error whie inializing disabled", ex);
////			logThread.start();
//			DriverStation.getInstance();
//			DriverStation.reportError("ERROR in disabledInit()... Go get Ernie or Magnus!", false);
//		}
	}
	
	/**
	 * This method is called periodically during disabled
	 */
	@Override
	public void disabledPeriodic()
	{
//		try
//		{
			if(numLoops % 10 == 0)
				putDataToSmartDashboard();
			
			gyro.reset();
				
//		}
//		catch(Exception ex)
//		{
//			if(!loggedError)
//			{
////				logRunnable.setLogInfo(Severity.ERROR, "Unexpected error while disabled", ex);
////				logThread.start();
//				DriverStation.getInstance();
//				DriverStation.reportError("ERROR in disabledPeriodic()... Go get Ernie or Magnus!", false);
//			}
//			loggedError = true;
//		}
	}
	
	/**
	 * This method is for putting data to the SmartDashboard
	 */
	public void putDataToSmartDashboard()
	{
		SmartDashboard.putNumber("Elev-Enc", elevatorEncoder.getRaw());
		
		SmartDashboard.putBoolean("Photo-Right", rightPhotoSensor.get());
		SmartDashboard.putBoolean("Photo-Left", leftPhotoSensor.get());
		
		SmartDashboard.putBoolean("LS-Top", topElevLS.get());
		SmartDashboard.putBoolean("LS-Bot", botElevLS.get());
		
		SmartDashboard.putNumber("U-Dist", ultra.getRangeInches());
		
		SmartDashboard.putNumber("RR-Speed:", rearRight.getSpeed());
		SmartDashboard.putNumber("FR-Speed:", frontRight.getSpeed());
		SmartDashboard.putNumber("FL-Speed:", frontLeft.getSpeed());
		SmartDashboard.putNumber("RL-Speed:", rearLeft.getSpeed());
		
		SmartDashboard.putNumber("FR-Posi", frontRight.getPosition());
		SmartDashboard.putNumber("FL-Posi", frontLeft.getPosition());
		SmartDashboard.putNumber("RR-Posi", rearRight.getPosition());
		SmartDashboard.putNumber("RL-Posi", rearLeft.getPosition());
		
		SmartDashboard.putBoolean("FOD-Enabled", fieldOriented);
		
		SmartDashboard.putNumber("AutoStep", autoStep);
		SmartDashboard.putNumber("Offset", offset);
		
		SmartDashboard.putNumber("G-Ang", wrapGyroAngle(gyro.getAngle()));
		
		SmartDashboard.putNumber("NumLoops", (double) numLoops);
	}
	
	/**
	 * Graph this, it should work fine.. We think...
	 * @param output a bad output
	 * @return a good output
	 */
	public static double sensitivityControl(double output)
	{
		return -(1.75*Math.pow(output, 3));
	}
	public double wrapGyroAngle(double gyroAngle)
	{
		gyroAngle %= 360.0;
		if (gyroAngle < 0) {
			gyroAngle += 360;
		}
		return gyroAngle;
	}
	
	public static int getNumLoops() {
		return (int) numLoops;
	}
}

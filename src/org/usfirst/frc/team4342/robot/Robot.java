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
 */
public class Robot extends IterativeRobot 
{
	private Joystick driveStick, elevatorStick;
	
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	
	private Talon rightElevatorMotor, leftElevatorMotor, testMotor;
	
	private Encoder elevatorEncoder;
	
	private DigitalInput topElevLS, botElevLS;
	
	private CameraServer camera;
	
	private ILog log;
	private LogRunnable logRunnable;
	private Thread logThread;
	private Thread t;
	
	private long numLoops;
	private boolean loggedError, fieldOriented;
	private Ultrasonic ultra;
	
	private Gyro gyro;
	private DigitalInput rightPhotoSensor, leftPhotoSensor;
	private ArrayList<Setpoint> points;
	private ElevatorControl elevC;
	
	private RobotDrive robotDrive;
	private Drive fod;
	
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
			
			fod = new Drive(frontLeft, frontRight, rearLeft, rearRight, driveStick, gyro, true);
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
			testMotor = new Talon(2);
			
			points = new ArrayList<Setpoint>();
			points.add(new Setpoint(2, new int[]{-50}));
			points.add(new Setpoint(3, new int[]{500}));
			points.add(new Setpoint(4, new int[]{1000}));
			points.add(new Setpoint(5, new int[]{2000}));
			points.add(new Setpoint(6,new int[]{3000}));
			points.add(new Setpoint(10,new int[]{2000,1000,2000,-10}));
			
			
			botElevLS = new DigitalInput(4);
			topElevLS = new DigitalInput(7);
			
			elevC = new ElevatorControl(elevatorStick, rightElevatorMotor, leftElevatorMotor, elevatorEncoder, points,botElevLS , topElevLS);
			
			gyro = new Gyro(0);
			gyro.setSensitivity(0.007);
			
			rightPhotoSensor = new DigitalInput(0);
			leftPhotoSensor = new DigitalInput(1);
			
//			topElevLS = new DigitalInput(4);

			
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
			//robotDrive.mecanumDrive_Cartesian(driveStick.getX(), driveStick.getY(), driveStick.getZ(), gyro.getAngle());
			
			if(driveStick.getRawButton(3))
				gyro.reset();
			
			if(driveStick.getRawButton(12))
			{
				fieldOriented = fieldOriented ? false : true;
			}
			
			fod.fieldOriented(driveStick.getX(), driveStick.getY(), driveStick.getZ(), fieldOriented ? gyro.getAngle() : 0.0);
			
			if(elevatorStick.getRawButton(7))
				elevatorEncoder.reset();
			//elevC.numLoops=numLoops;
			//testElevator();
			//speedTest();
			elevatorSpeedTest();
			motorTest();
			
			if(numLoops % 10 == 0)
				putDataToSmartDashboard();
			
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
	
	private void testElevator() 
	{
		rightElevatorMotor.set(elevatorStick.getY());
		leftElevatorMotor.set(elevatorStick.getY());
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
					autoRoutinePickUpOneTote();
				break;
				
				case 2:
					autoRoutinePickupOneContainer();
				break;
				
				default:
					autoRoutinePickUpThreeTotes();
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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// END OF OVERRIDED METHODS FROM ROBOTBASE /////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// BELOW ARE CUSTOM METHODS FOR THE ROBOT //////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This method is for putting data to the SmartDashboard
	 */
	public void putDataToSmartDashboard()
	{
		SmartDashboard.putNumber("Elev-Enc", elevatorEncoder.getRaw());
		
		SmartDashboard.putBoolean("Photo-Right", rightPhotoSensor.get());
		SmartDashboard.putBoolean("Photo-Left", leftPhotoSensor.get());
		
		SmartDashboard.putBoolean("LS-Bot", botElevLS.get());
		
		SmartDashboard.putNumber("U-Dist", ultra.getRangeInches());
		
		SmartDashboard.putBoolean("top ls", topElevLS.get());
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
		
		for(int i = 0; i < 16; i++)
			SmartDashboard.putNumber("PDP-A" + i, pdp.getCurrent(i));
		
		SmartDashboard.putNumber("PDP-V", pdp.getVoltage());
		SmartDashboard.putNumber("PDP-Temp", pdp.getTemperature());
		SmartDashboard.putNumber("PDP-E", pdp.getTotalEnergy());
		SmartDashboard.putNumber("PDP-A", pdp.getTotalCurrent());
		SmartDashboard.putNumber("PDP-P", pdp.getTotalPower());
	}
	
	public static double sensitivityControl(double output)
	{
		if(output < 0)
		{
			return -Math.sqrt(Math.abs(output));
		}
		else
		{
			return Math.sqrt(Math.abs(output));
		}
	}
	
	public double wrapGyroAngle(double gyroAngle)
	{
		gyroAngle %= 360.0;
		if (gyroAngle < 0) {
			gyroAngle += 360;
		}
		return gyroAngle;
	}
	
	public void motorTest()
	{
		if(driveStick.getRawButton(9))
		{
			testMotor.set(1);
		}
		else if (driveStick.getRawButton(10))
		{
			testMotor.set(-1);
		}
		else
		{
			testMotor.set(0);
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////// END OF CUSTOM METHODS FOR THE ROBOT /////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  This nested class is used for logging info about the PDP
	 */
	public void speedTest()
	{
		if(numLoops % 10 == 0)
		{
			double tempP = SmartDashboard.getNumber("P");
			double tempI = SmartDashboard.getNumber("I");
			double tempD = SmartDashboard.getNumber("D");
			
			kRLSetpoint = SmartDashboard.getNumber("Setpoint");
			
			if(tempP != kRLP || tempI != kRLI || tempD != kRLD)
			{
				kRLP = tempP;
				kRLI = tempI;
				kRLD = tempD;
				
				rearRight.setSpeedMode(CANJaguar.kQuadEncoder, 512, kRLP ,kRLI,kRLD);
				rearRight.enableControl();
				rearLeft.setSpeedMode(CANJaguar.kQuadEncoder, 512, kRLP ,kRLI,kRLD);
				rearLeft.enableControl();
				frontRight.setSpeedMode(CANJaguar.kQuadEncoder, 512, kRLP ,kRLI,kRLD);
				frontRight.enableControl();
				frontLeft.setSpeedMode(CANJaguar.kQuadEncoder, 512, kRLP ,kRLI,kRLD);
				frontLeft.enableControl();
			}
		}
		
		frontLeft.set(-kRLSetpoint);
		frontRight.set(kRLSetpoint);
		rearRight.set(-kRLSetpoint);
		rearLeft.set(kRLSetpoint);
	}
	
	public void elevatorSpeedTest()
	{
		if(numLoops % 10 == 0)
		{
			double tempPup = SmartDashboard.getNumber("Elev-P Up") / 100.0;
			double tempIup = SmartDashboard.getNumber("Elev-I Up") / 1000.0;
			double tempDup = SmartDashboard.getNumber("Elev-D Up") / 100.0;
			double tempPdown = SmartDashboard.getNumber("Elev-P Down") / 100.0;
			double tempIdown = SmartDashboard.getNumber("Elev-I Down") / 1000.0;
			double tempDdown = SmartDashboard.getNumber("Elev-D Down") / 100.0;
			
			if(tempPup != kRLP || tempIup != kRLI || tempDup != kRLD)
			{
				kRLP = tempPup;
				kRLI = tempIup;
				kRLD = tempDup;			
				elevC.setP(kRLP);
				elevC.setI(kRLI);
				elevC.setD(kRLD);
			}
			if(tempPup != kRLPD || tempIup != kRLID || tempDup != kRLDD)
			{
				kRLPD = tempPdown;
				kRLID = tempIdown;
				kRLDD = tempDdown;
				elevC.setPd(kRLPD);
				elevC.setId(kRLID);
				elevC.setDd(kRLDD);
			}
		}
	}
	
	public void autoRoutinePickUpThreeTotes()
	{
		if(autoStep == 0)
		{
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(moveDist(2.5, 0.11))
			{
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 2)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(wait(savingPvtBrian+10))
			{
				autoStep++;
			}
		}
		else if(autoStep == 3)
		{
			if(autoMcgriddle(100))
			{
				
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep==5)
		{
			autoMcgriddle(2000);
			if(moveBackDist(1, 0.2))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
		else if(autoStep == 6)
		{
			autoMcgriddle(2000);
			if(autoStrafe(25))
			{
				autoStep++;
			}
		}
		else if(autoStep == 7)
		{
			autoMcgriddle(2000);
			if(autoForward())
			{
				autoStep++;
			}
		}
		else if(autoStep == 8)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(0))
			{
				autoStep++;
			}
		}
		else if(autoStep == 9)
		{
			if(autoMcgriddle(100))
			{
				autoStep++;
			}
		}
		else if(autoStep == 10)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 11)
		{
			autoMcgriddle(2000);
			if(moveBackDist(1, 0.11))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
		else if(autoStep == 12)
		{
			autoMcgriddle(2000);
			if(autoStrafe(25))
			{
				autoStep++;
			}
		}
		else if(autoStep == 13)
		{
			autoMcgriddle(2000);
			if(autoForward())
			{
				autoStep++;
			}
		}
		else if(autoStep == 14)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(0))
			{
				autoStep++;
			}
		}
		else if(autoStep == 15)
		{
			if(autoMcgriddle(100))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 16)
		{
			autoMcgriddle(2000);
			if(moveBackDist(3, 0.07))
			{
				autoStep++;
			}
		}
		else if(autoStep == 17)
		{
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 18)
		{
			autoMcgriddle(2000);
			if(moveBackDist(4, 0.11))
			{
				autoStep++;
			}
		}
		else if(autoStep ==19)
		{
			if(autoMcgriddle(-50))
			{
				autoStep++;
			}
		}
		else if(autoStep==20){
			moveBackDist(4,0.5);
		}
		
	}
	
	public void autoRoutinePickUpOneTote()
	{
		if(autoStep == 0)
		{
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(moveDist(2.5, 0.11))
			{
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 2)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(wait(savingPvtBrian+10))
			{
				autoStep++;
			}
		}
		else if(autoStep == 3)
		{
			if(autoMcgriddle(100))
			{
				
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep==5)
		{
			autoMcgriddle(2000);
			if(moveBackDist(3, 0.2))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
	}
	
	public void autoRoutinePickupOneContainer()
	{
		if(autoStep == 0)
		{
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(autoMcgriddle(697))
			{
				autoStep++;
			}
		}
		else if(autoStep == 2)
		{
			autoMcgriddle(697);
			if(autoForward())
			{
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 3)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(wait(savingPvtBrian+10))
			{
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			if(autoMcgriddle(2250))
			{
				offset = 0;
				autoStep++;
			}
		}
		else if(autoStep == 5)
		{
			autoMcgriddle(2250);
			if(moveBackDist(17, 0.1))
			{
				initAngle+=90;
				autoStep++;
			}
		}
		else if(autoStep == 6)
		{
			autoMcgriddle(2250);
			fod.autoMove(0.01, 0.0, gyro.getAngle(), initAngle);
		}
	}
	
	public void snapshotEncoderValues()
	{
		offset = Math.abs(frontRight.getPosition()) + Math.abs(frontLeft.getPosition())
					+ Math.abs(rearRight.getPosition()) + Math.abs(rearRight.getPosition());
	}
	
	public double getEncoderValues()
	{
		return Math.abs(frontRight.getPosition()) + Math.abs(frontLeft.getPosition()) 
				+ Math.abs(rearRight.getPosition()) + Math.abs(rearRight.getPosition());
	}
	public double totals()
	{
		return (frontRight.getPosition()) + (frontLeft.getPosition()) 
				+ (rearRight.getPosition()) + (rearRight.getPosition());
	}
	
	private boolean madeContact;
	public boolean autoStrafe(double encDistance)
	{
		//TODO: add strafing capabilities for both ways
		if(rightPhotoSensor.get() && leftPhotoSensor.get())
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			return true;
		}
		else if(!leftPhotoSensor.get() && rightPhotoSensor.get()||madeContact)
		{
			//autoStep = 2;
			madeContact = true;
			fod.autoMove(0.04, 0.0, gyro.getAngle(), initAngle);
		}
		else if((getEncoderValues() - offset) > (encDistance - 4) && !madeContact)
		{
			fod.autoMove(0.05, 0, gyro.getAngle(), initAngle);
		}
		else if((getEncoderValues() - offset) > (encDistance - 6) && !madeContact)
		{
			fod.autoMove(0.12, 0, gyro.getAngle(), initAngle);
		}
		else
		{
			fod.autoMove(0.25, 0.0, gyro.getAngle(), initAngle);
		}
		
		return false;
	}
	public boolean autoForward()
	{
		fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
		double P = SmartDashboard.getNumber("howCloseP");
		double close = SmartDashboard.getNumber("U-Close");
		double far = SmartDashboard.getNumber("U-Far");
		double dist = ultra.getRangeInches();
		distanceDer = dist - distanceError;
		distanceError = dist - ((close + far) / 2);
		
		if((dist > close && dist < far))
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			//elevC.setAutoSetpoint(0);
			return true;
			
		}
		else if(dist < close)
		{
			fod.autoMove(0.0, (distanceError*P), gyro.getAngle(), initAngle);
		}
		else if(dist > far)
		{
			fod.autoMove(0.0,(distanceError*P), gyro.getAngle(), initAngle);
		}
		SmartDashboard.putNumber("distP", distanceError*P);
		SmartDashboard.putNumber("distError", distanceError);
		SmartDashboard.putNumber("distDerr", distanceDer);
		return false;
	}
	public boolean moveDist(double revolutions, double speed)
	{
		
		if(totals()-offset>=revolutions)
		{
			fod.autoMove(0.0,0.0,gyro.getAngle(),initAngle);
			return true;
		}
		else
		{
			fod.autoMove(0.0,speed,gyro.getAngle(),initAngle);
		}
		return false;
	}
	public boolean moveBackDist(double revolutions, double speed)
	{
		
		if(totals() - offset <= -revolutions)
		{
			fod.autoMove(0.0,0.0,gyro.getAngle(),initAngle);
			return true;
		}
		else
		{
			fod.autoMove(0.0,-speed,gyro.getAngle(),initAngle);
		}
		return false;
	}
	
	private int autoBand = 10, error;
	private double p = 0.25, i = 0.0, d = 0.02, pd = 0.1, id = 0.0, dd = 0.01;
	
	public boolean autoMcgriddle(int setpoint) {
		error = setpoint - elevatorEncoder.get();
		double out=0;
		if (Math.abs(error) <= autoBand) {
			rightElevatorMotor.set(out);
			leftElevatorMotor.set(out);
			return true;
		}
		if (error > 0) {
			out = 0.7;
			//out = pid(p, i, d, error);
		} else {
			out =-0.1;
			//out = pid(pd, id, dd, error);
		}
		if (botElevLS.get() && out < 0) {
			out = 0;
		}
		if(topElevLS.get() && out > 0)
		{
			out = 0;
		}
		rightElevatorMotor.set(out);
		leftElevatorMotor.set(out);
		return false;
//		if(elevatorEncoder.get()<)
		
	}

	private int accumulated = 0, perr;
	long howLong = System.currentTimeMillis();
	private double pPID;

	private double pid(double p, double i, double d, int err) {
		double out = 0;
		if (Math.abs(err) <= 5) {
			accumulated = 0;
			return 0;
		} else if (Math.abs(perr - err) > Math.abs(err + perr)) {
			accumulated = 0;
		}
		accumulated += err;

		double P = p * err;
		double I = i * accumulated;
		double D = (err - perr) * d;
		perr = err;
		out = P + I + D;
		if (out > 1) {
			out = 1;
		} else if (out < -.5) {
			out = -.5;
		}
		if (out - pPID > .1) {
			out = pPID + .1;
		} else if (out - pPID < -.1) {
			out = pPID - .1;
		}
		pPID = out;
		return out;

	}
	
	private int savingPvtBrian;
	public boolean wait(int waitLoops)
	{
		if(numLoops >= waitLoops)
		{
			return true;
		}
		return false;
	}
}

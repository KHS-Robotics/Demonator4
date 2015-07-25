package org.usfirst.frc.team4342.robot;

import RobotExceptions.NonFatalRobotException;
import RobotExceptions.RobotException;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * 
 * @author khsrobotics
 * 
 * This class contains all of the autonomous routines for the robot.
 * Autonomous is the 15 second period at the beginning, when
 * it runs by itself, of the match.
 */
public class AutoRoutines {
	
	private static final double minUltrasonicValue = 9.0;
	private static final double maxUltrasonicValue = 12.0;
	
	private int autoStep;
	private boolean currentlyExecuting;
	
	private long waitLoops;
	
	private MecanumDrive drive;
	private ElevatorController ec;
	
	private Ultrasonic ultra;
	private DigitalInput leftPhotoSensor, rightPhotoSensor;
	private Gyro gyro;
	
	/**
	 * 
	 * @param drive the drive train for Demonator IV
	 * @param ec the elevator controller to stack totes
	 * @param ultra the ultrasonic sensor to detect distance
	 * @param leftPhotoSensor used to align the tote to the fork lift
	 * @param rightPhotoSensor used to align the tote to the fork lift
	 * @param gyro the gyro to ensure the robot is moving straight
	 */
	public AutoRoutines(MecanumDrive drive, ElevatorController ec,
						Ultrasonic ultra, DigitalInput leftPhotoSensor,
						DigitalInput rightPhotoSensor, Gyro gyro) {
		this.drive = drive;
		this.ec = ec;
		this.ultra = ultra;
		this.leftPhotoSensor = leftPhotoSensor;
		this.rightPhotoSensor = rightPhotoSensor;
		this.gyro = gyro;
	}
	
	/**
	 * Executes the auto routine 
	 * @param autoRoutine the auto routine to run
	 */
	public void executeAutonomous(int autoRoutine) throws RobotException {
		if(DriverStation.getInstance().isAutonomous()) {
			switch(autoRoutine) {
				case 1:
					pickupOneTote();
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				default:
					 break;
			}
		}
	}
	
	/**
	 * Auto routine will execute in the following steps:
	 * 0) Move elevator to 250 encoder counts
	 * 1) Move forward until it's between 8" and 12" of the tote
	 * 2) Move the elevator to 0 encoder counts
	 * 3) Wait half a second for the elevator to move
	 * 4) Move the elevator to 100 encoder counts
	 * 5) Wait half a second for the elevator to move
	 * 6) Move backwards for 16 total encoder counts of the drive train
	 * 7) Move the elevator to 0 encoder counts
	 * 8) Wait half a second for the elevator to move
	 * 9) Move backwards for a total of 4 encoder counts
	 */
	private void pickupOneTote() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(250);
			autoStep++;
		}
		else if(autoStep == 1) {
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			double ultrasonicValue = ultra.getRangeInches();
			
			if(ultrasonicValue < maxUltrasonicValue && ultrasonicValue > minUltrasonicValue) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			ec.setAutoSetpoint(0);
			waitLoops = Robot.getNumLoops()+25;
			autoStep++;
		}
		else if(autoStep == 3) {
			if(waitForNumLoops(Robot.getNumLoops())) {
				autoStep++;
			}
		}
		else if(autoStep == 4) {
			ec.setAutoSetpoint(100);
			waitLoops = Robot.getNumLoops()+10;
			autoStep++;
		}
		else if(autoStep == 5) {
			if(waitForNumLoops(Robot.getNumLoops())) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 6) {
			
			drive.autoDrive(0.0, -0.5, gyro.getAngle());
			
			if(currentDriveEncoderValues() >= 16) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 7) {
			ec.setAutoSetpoint(0);
			waitLoops = Robot.getNumLoops();
			autoStep++;
		}
		else if(autoStep == 8) {
			if(waitForNumLoops(Robot.getNumLoops())) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 9) {
			
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			if(currentDriveEncoderValues() >= 4) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
	}
	
	/**
	 * Auto routine will execute the following steps:
	 * 0) Move elevator to 250 encoder counts
	 * 1) Move forward until it's between 8" and 12" of the tote
	 * 2) Move the elevator to 0 encoder counts
	 * 3) Wait half a second for the elevator to move
	 * 4) Move elevator to 350 encoder counts
	 * 5) Wait half a second for the elevator to move
	 * 6) Move backwards for a total of 6 encoder counts
	 * 7) Move left until both photosensors read true
	 * 8) Move forward until it's between 8" and 12" of the tote
	 * 9) Move the elevator to 0 encoder counts
	 * 10) Wait half a second for the elevator to move
	 * 11) Move elevator to 100 encoder counts
	 * 12) Wait half a second for elevator to move
	 * 13) Move backwards for a total of 16 encoder counts
	 * 14) Move elevator to 0 encoder counts
	 * 15) Wait half a second for elevator to move
	 * 16) Move backwards for a total of 4 encoder counts
	 */
	private void pickupTwoTotes() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(250);
			autoStep++;
		}
		else if(autoStep == 1) {
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			double ultrasonicValue = ultra.getRangeInches();
			
			if(ultrasonicValue < maxUltrasonicValue && ultrasonicValue > minUltrasonicValue) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			ec.setAutoSetpoint(0);
			waitLoops = Robot.getNumLoops()+25;
			autoStep++;
		}
		else if(autoStep == 3) {
			
		}
	}
	
	private void pickupThreeTotes() {
		
	}
	
	private void pickupOneContainer() {
		
	}
	
	private int currentDriveEncoderValues() {
		int frontRight = Math.abs((int) drive.getFrontRight().getPosition());
		int frontLeft = Math.abs((int) drive.getFrontLeft().getPosition());
		int rearRight = Math.abs((int) drive.getRearRight().getPosition());
		int rearLeft = Math.abs((int) drive.getRearLeft().getPosition());
		
		return (frontRight + frontLeft + rearRight + rearLeft);
	}
	
	private void resetDriveEncoders() {
		CANJaguarLoader.init(new CANJaguar[] {
				drive.getFrontLeft(),
				drive.getFrontRight(),
				drive.getRearLeft(),
				drive.getRearRight()
			},
			true
		);
	}
	
	private boolean waitForNumLoops(long numLoops) {
		if(numLoops >= waitLoops) {
			return true;
		}
		
		return false;
	}
}

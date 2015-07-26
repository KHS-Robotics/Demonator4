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
	
	private static final double MinToteDist = 10.0;
	private static final double MaxToteDist = 12.0;
	
	private int autoStep;
	private boolean currentlyExecuting;
	
	private boolean leftPhotoSensorMadeContact;
	
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
					pickupTwoTotes();
					break;
				case 3:
					pickupThreeTotes();
					break;
				case 4:
					pickupOneContainer();
					break;
				default:
					Robot.printWarningToDS(
						"No valid autonomous value selected, please alert Ernie or Magnus"
					);
					break;
			}
		}
	}
	
	/**
	 * Auto routine will execute in the following steps:
	 * 0) Move elevator to 250 encoder counts
	 * 1) Move forward until it's between 10" and 12" of the tote
	 * 2) Move the elevator to 0 encoder counts
	 * 3) Wait for the elevator to move
	 * 4) Move the elevator to 100 encoder counts
	 * 5) Wait for the elevator to move
	 * 6) Move backwards for 16 total encoder counts of the drive train
	 * 7) Move the elevator to 0 encoder counts
	 * 8) Wait for the elevator to move
	 * 9) Move backwards for a total of 4 encoder counts
	 */
	private void pickupOneTote() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(250);
			autoStep++;
		}
		else if(autoStep == 1) {
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			double dist = ultra.getRangeInches();
			
			if(dist < MaxToteDist && dist > MinToteDist) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 3) {
			if(ec.isAtAutoSetpoint()) {
				autoStep++;
			}
		}
		else if(autoStep == 4) {
			ec.setAutoSetpoint(100);
			autoStep++;
		}
		else if(autoStep == 5) {
			if(ec.isAtAutoSetpoint()) {
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
			autoStep++;
		}
		else if(autoStep == 8) {
			if(ec.isAtAutoSetpoint()) {
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
	 * 1) Move forward until it's between 10" and 12" of the tote
	 * 2) Move the elevator to 0 encoder counts
	 * 3) Wait for the elevator to move, then move elevator to 250 encoder counts
	 * 4) Wait for the elevator to move
	 * 5) Move backwards for a total of 6 encoder counts
	 * 6) Move left until both photosensors read true
	 * 7) Move forward until it's between 10" and 12" of the tote
	 * 8) Move the elevator to 0 encoder counts
	 * 9) Wait for the elevator to move, move the elevator to 100 encoder counts
	 * 10) Wait for the elevator move
	 * 11) Move backwards for a total of 16 encoder counts
	 * 12) Move elevator to 0 encoder counts
	 * 13) Wait for the elevator to move
	 * 14) Move backwards for a total of 4 encoder counts
	 */
	private void pickupTwoTotes() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(250);
			autoStep++;
		}
		else if(autoStep == 1) {
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			double ultrasonicValue = ultra.getRangeInches();
			
			if(ultrasonicValue < MaxToteDist && ultrasonicValue > MinToteDist) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 3) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(250);
				autoStep++;
			}
		}
		else if(autoStep == 4) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 5) {
			drive.autoDrive(0.0, -0.25, gyro.getAngle());
			
			if(currentDriveEncoderValues() >= 6) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 6) {
			
			checkLeftPhotoSensor();
			
			if(!leftPhotoSensorMadeContact) {
				drive.autoDrive(0.25, 0.0, gyro.getAngle());
			}
			else if(leftPhotoSensorMadeContact && !rightPhotoSensor.get()) {
				drive.autoDrive(-0.10, 0.0, gyro.getAngle());
			}
			else if(leftPhotoSensor.get() && leftPhotoSensor.get()) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				leftPhotoSensorMadeContact = false;
				autoStep++;
			}
		}
		else if(autoStep == 7) {
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			double ultrasonicValue = ultra.getRangeInches();
			
			if(ultrasonicValue < MaxToteDist && ultrasonicValue > MinToteDist) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 8) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 9) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(100);
			}
		}
		else if(autoStep == 10) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 11) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderValues() >= 16) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 12) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 13) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 14) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderValues() >= 4) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
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
	
	private void checkLeftPhotoSensor() {
		if(leftPhotoSensor.get()) {
			leftPhotoSensorMadeContact = true;
		}
	}
	
	private boolean waitForNumLoops() {
		if(Robot.getNumLoops() >= waitLoops) {
			return true;
		}
		
		return false;
	}
}

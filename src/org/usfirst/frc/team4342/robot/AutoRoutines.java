package org.usfirst.frc.team4342.robot;

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
	
	//TODO: test'n'tune these values
	private static final double MinToteDist = 10.0;
	private static final double MaxToteDist = 12.0;
	
	private int autoStep;
	
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
	public void executeAutonomous(int autoRoutine) {
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
	
	//TODO: test'n'tune the drive train encoder counts
	
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
			
			if(currentDriveEncoderCounts() >= 16) {
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
			
			if(currentDriveEncoderCounts() >= 4) {
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
			
			if(currentDriveEncoderCounts() >= 6) {
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
			
			if(currentDriveEncoderCounts() >= 16) {
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
			
			if(currentDriveEncoderCounts() >= 4) {
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
	 * 9) Wait for the elevator to move, move the elevator to 250 encoder counts
	 * 10) Wait for the elevator move
	 * 11) Move backwards for a total of 6 encoder counts
	 * 12) Move left until both photosensors read true
	 * 13) Move forward until it's between 10" and 12" of the tote
	 * 14) Move the elevator to 0 encoder counts
	 * 15) Wait for the elevator to move, move the elevator to 100 encoder counts
	 * 16) Wait for the elevator to move
	 * 17) Move backwards for a total of 16 encoder counts
	 * 18) Move the elevator to 0 encoder counts
	 * 19) Wait for the elevator to move
	 * 20) Move backwards for a total of 4 encoder counts
	 */
	private void pickupThreeTotes() {
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
			
			if(currentDriveEncoderCounts() >= 6) {
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
				ec.setAutoSetpoint(250);
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
			
			if(currentDriveEncoderCounts() >= 6) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 12) {
			
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
		else if(autoStep == 13) {
			drive.autoDrive(0.0, 0.25, gyro.getAngle());
			
			double ultrasonicValue = ultra.getRangeInches();
			
			if(ultrasonicValue < MaxToteDist && ultrasonicValue > MinToteDist) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 14) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 15) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(100);
				autoStep++;
			}
		}
		else if(autoStep == 16) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 17) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 16) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 18) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 19) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 20) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 4) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
	}
	
	/**
	 * Auto routine will execute the following steps:
	 * 0) Move elevator to 350 encoder counts
	 * 1) Move forward until it's between 10" and 12"
	 * 2) Move elevator to 800 encoder counts
	 * 3) Wait for elevator to move
	 * 4) Move backwards for 16 encoder counts
	 */
	private void pickupOneContainer() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(350);
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
			ec.setAutoSetpoint(800);
			autoStep++;
		}
		else if(autoStep == 3) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 4) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 16) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
 	}
	
	/**
	 * Adds up all encoder counts of the drive train
	 * @return the total encoder counts of the drive train
	 */
	private int currentDriveEncoderCounts() {
		int frontRight = Math.abs((int) drive.getFrontRight().getPosition());
		int frontLeft = Math.abs((int) drive.getFrontLeft().getPosition());
		int rearRight = Math.abs((int) drive.getRearRight().getPosition());
		int rearLeft = Math.abs((int) drive.getRearLeft().getPosition());
		
		return (frontRight + frontLeft + rearRight + rearLeft);
	}
	
	/**
	 * Resets the encoders on the drive train
	 */
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
	
	/**
	 * Checks to see if the left photosensor has made contact with a tote
	 * when strafing. This method is used for any multi-tote autonomous.
	 * This comes in handy because we want the robot to slow down in order to
	 * properly align with the tote it will pickup.
	 */
	private void checkLeftPhotoSensor() {
		if(leftPhotoSensor.get()) {
			leftPhotoSensorMadeContact = true;
		}
	}
	
	/**
	 * A simple wait method in case anything the robot does will need extra
	 * time to complete. In order to use this method, one needs to set
	 * waitLoops to Robot.getNumLoops() + the amount iterations they want to wait
	 * (50 is 1 second).
	 * @return true if done waiting, false otherwise
	 */
	private boolean waitForNumLoops() {
		if(Robot.getNumLoops() >= waitLoops) {
			return true;
		}
		
		return false;
	}
}

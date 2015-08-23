package org.usfirst.frc.team4342.robot.autonomous;

import org.usfirst.frc.team4342.robot.Robot;
import org.usfirst.frc.team4342.robot.drive.CANJaguarLoader;
import org.usfirst.frc.team4342.robot.drive.DrivePID;
import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;

import Logging.ActiveLog;
import Logging.LoggerAsync;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;
import org.usfirst.frc.team4342.robot.drive.DrivePID;
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
	
	// TODO: test'n'tune these two values
	private static final double MinToteDist = 11.0;
	private static final double MaxToteDist = 13.0;
	
	private double distanceError;
	
	private boolean logged;
	
	private int autoStep;
	
	private boolean leftPhotoSensorMadeContact;
	
	private long waitLoops;
	
	private MecanumDrive drive;
	private ElevatorController ec;
	
	private Ultrasonic ultra;
	private DigitalInput leftPhotoSensor, rightPhotoSensor;
	private Gyro gyro;
	
	private LoggerAsync log;
	private RobotConsoleLog consoleLog;
	
	/**
	 * Constructs an object to run auto routines
	 * @param drive the drive train for Demonator IV
	 * @param ec the elevator controller to stack totes
	 * @param ultra the ultrasonic sensor to detect distance
	 * @param leftPhotoSensor used to align the tote to the fork lift
	 * @param rightPhotoSensor used to align the tote to the fork lift
	 * @param gyro the gyro to ensure the robot is moving straight
	 */
	public AutoRoutines(MecanumDrive drive, ElevatorController ec,
						Ultrasonic ultra, DigitalInput leftPhotoSensor,
						DigitalInput rightPhotoSensor, Gyro gyro,
						LoggerAsync log, RobotConsoleLog consoleLog) {
		this.drive = drive;
		this.ec = ec;
		this.ultra = ultra;
		this.leftPhotoSensor = leftPhotoSensor;
		this.rightPhotoSensor = rightPhotoSensor;
		this.gyro = gyro;
		
		this.log = log;
		this.consoleLog = consoleLog;
		
		new AutoChecker().start();
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
					if(!logged) {
						log.warning("No valid autonomous value selected, please alert Ernie or Magnus");
						consoleLog.warning("No valid autonomous value selected, please alert Ernie or Magnus");
						
						logged = true;
					}
					break;
			}
		}
	}
	
	//TODO: test'n'tune the drive train encoder counts
	
	/**
	 * Auto routine will execute in the following steps:
	 * 0) Move the elevator to 200 encoder counts
	 * 1) Wait for the elevator to move
	 * 2) Move backwards for 141 total encoder counts of the drive train
	 * 3) Move the elevator to 0 encoder counts
	 * 4) Wait for the elevator to move
	 * 5) Move backwards 1 encoder count
	 */
	private void pickupOneTote() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(350);
			autoStep++;
		}
		else if(autoStep == 1) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			
			drive.autoDrive(0.0, -0.5, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 14) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 3) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 4) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 5) {
			
			drive.autoDrive(0.0, -0.25, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 1) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
	}
	
	/**
	 * Auto routine will execute the following steps:
	 * 0) Move elevator to 250 encoder counts
	 * 1) Wait for the elevator to move
	 * 2) Move backwards for a total of 6 encoder counts
	 * 3) Move left until both photo sensors read true
	 * 4) Move forward until it's between 10" and 12" of the tote
	 * 5) Move the elevator to 0 encoder counts
	 * 6) Wait for the elevator to move, move the elevator to 100 encoder counts
	 * 7) Wait for the elevator move
	 * 8) Move backwards for a total of 14 encoder counts
	 * 9) Move elevator to 0 encoder counts
	 * 10) Wait for the elevator to move
	 * 11) Move backwards 1 encoder count
	 */
	private void pickupTwoTotes() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(250);
		}
		else if(autoStep == 1) {
			if(ec.isAtAutoSetpoint()) {
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			drive.autoDrive(0.0, -0.25, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 6) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 3) {
			
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
		else if(autoStep == 4) {
			if(autoMoveDist()) {
				autoStep++;
			}
		}
		else if(autoStep == 5) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 6) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(100);
			}
		}
		else if(autoStep == 7) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 8) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 14) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 9) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 10) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 11) {
			drive.autoDrive(0.0, -0.25, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 1) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
	}
	
	/**
	 * Auto routine will execute the following steps:
	 * 0) Move elevator to 250 encoder counts
	 * 1) Wait for the elevator to move
	 * 2) Move backwards for a total of 6 encoder counts
	 * 3) Move left until both photo sensors read true
	 * 4) Move forward until it's between 10" and 12" of the tote
	 * 5) Move the elevator to 0 encoder counts
	 * 6) Wait for the elevator to move, move the elevator to 250 encoder counts
	 * 7) Wait for the elevator move
	 * 8) Move backwards for a total of 6 encoder counts
	 * 9) Move left until both photo sensors read true
	 * 10) Move forward until it's between 10" and 12" of the tote
	 * 11) Move the elevator to 0 encoder counts
	 * 12) Wait for the elevator to move, move the elevator to 100 encoder counts
	 * 13) Wait for the elevator to move
	 * 14) Move backwards for a total of 14 encoder counts
	 * 15) Move the elevator to 0 encoder counts
	 * 16) Wait for the elevator to move
	 * 17) Move backwards 1 encoder count
	 */
	private void pickupThreeTotes() {
		if(autoStep == 0) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(250);
				autoStep++;
			}
		}
		else if(autoStep == 1) {
			if(ec.isAtAutoSetpoint()) {
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			drive.autoDrive(0.0, -0.25, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 6) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 3) {
			
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
		else if(autoStep == 4) {
			if(autoMoveDist()) {
				autoStep++;
			}
		}
		else if(autoStep == 5) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 6) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(250);
			}
		}
		else if(autoStep == 7) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 8) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 6) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 9) {
			
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
		else if(autoStep == 10) {
			if(autoMoveDist()) {
				autoStep++;
			}
		}
		else if(autoStep == 11) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 12) {
			if(ec.isAtAutoSetpoint()) {
				ec.setAutoSetpoint(100);
				autoStep++;
			}
		}
		else if(autoStep == 13) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 14) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 14) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
		else if(autoStep == 15) {
			ec.setAutoSetpoint(0);
			autoStep++;
		}
		else if(autoStep == 16) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 17) {
			drive.autoDrive(0.0, -0.25, gyro.getAngle());
			
			if(currentDriveEncoderCounts() >= 1) {
				drive.autoDrive(0.0, 0.0, gyro.getAngle());
				autoStep++;
			}
		}
	}
	
	/**
	 * Auto routine will execute the following steps:
	 * 0) Move elevator to 800 encoder counts
	 * 1) Wait for elevator to move
	 * 2) Move backwards for 14 encoder counts
	 */
	private void pickupOneContainer() {
		if(autoStep == 0) {
			ec.setAutoSetpoint(800);
			autoStep++;
		}
		else if(autoStep == 1) {
			if(ec.isAtAutoSetpoint()) {
				resetDriveEncoders();
				autoStep++;
			}
		}
		else if(autoStep == 2) {
			drive.autoDrive(0.0, -0.50, gyro.getAngle());
			
		if(currentDriveEncoderCounts() >= 14) {
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
		
		int total = (frontRight + frontLeft + rearRight + rearLeft);
		
		return total;
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
	 * Automatically moves the robot a perfect distance to the tote.
	 * @return true if finished, false otherwise
	 */
	public boolean autoMoveDist() {
		double dist = ultra.getRangeInches();
		
		distanceError = dist - ((MinToteDist + MaxToteDist) / 2);
		
		if(dist > MinToteDist && dist < MaxToteDist) {
			drive.autoDrive(0.0, 0.0, gyro.getAngle());
			return true;
		} else {
			drive.autoDrive(0.0, (distanceError*DrivePID.Autonomous.kP), gyro.getAngle());
			return false;
		}
	}
	
	/**
	 * Checks to see if the left photo sensor has made contact with a tote
	 * when strafing. This method is used for any multiple tote autonomous's.
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
	
	/**
	 * Used to reset the logged variable
	 */
	private class AutoChecker extends Thread implements Runnable {
		@Override
		public void run() {
			while(true) {
				if(!DriverStation.getInstance().isAutonomous() || !DriverStation.getInstance().isEnabled()) {
					autoStep = 0;
					logged = false;
				}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
					ActiveLog.warning(Robot.ACTIVE_LOG_PATH, "Demonator4", ExceptionInfo.getType(ex) + " in AutoRoutines.AutoChecker");
				}
			}
		}
	}
}

package org.usfirst.frc.team4342.robot;

import RobotExceptions.NonFatalRobotException;
import RobotExceptions.RobotException;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Ultrasonic;

//TODO: Re-write and test the auto routines
public class AutoRoutines {
	
	private int autoRoutine;
	
	private MecanumDrive drive;
	private ElevatorController ec;
	
	private Ultrasonic ultra;
	private DigitalInput leftPhotoSensor, rightPhotoSensor;
	private Gyro gyro;
	
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
	 * @param autoRoutine
	 */
	public void executeAutonomous(int autoRoutine) throws RobotException {
		if(!Robot.getDriverStation().isAutonomous()) {
			Robot.printWarningToDS("Robot cannot execute auto routine unless"
									+ " in autonomous");
			throw new NonFatalRobotException("Robot is not in autonomous!");
		}
		
		switch(autoRoutine) {
			case 1:
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

package org.usfirst.frc.team4342.robot.autonomous;

import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

import edu.wpi.first.wpilibj.CANJaguar;
import Logging.ILog;

/**
 * This class is for running tests on the robot to make sure
 * all sensors are working properly
 * 
 * @author khsrobotics
 */
public class Diagnostic {
	
	private Diagnostic() {
		
	}
	
	/**
	 * Runs tests to take sure all sensors are working properly
	 * @param drive the drive train controls
	 * @param ec the elevator controls
	 * @param log the logger to log messages to the RIO
	 * @param consoleLog the logger to log messages to the Driver Station
	 * @return true if no warnings; false otherwise
	 */
	public static boolean runSelfTest(MecanumDrive drive, ElevatorController ec, ILog log, RobotConsoleLog consoleLog) {
		int warnings = 0;
		
		try {
			log.debug("Starting self diagnostic test...");
			
			log.debug("Testing front right drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getFrontRight())) {
				log.warning("Possible problem with front right encoder");
				warnings++;
			}
			
			log.debug("Testing front left drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getFrontLeft())) {
				log.warning("Possible problem with front left encoder");
				warnings++;
			}
			
			log.debug("Testing rear right drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getRearRight())) {
				log.warning("Possible problem with rear right encoder");
				warnings++;
			}
			
			log.debug("Testing rear left drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getRearLeft())) {
				log.warning("Possible problem with rear left encoder");
				warnings++;
			}
			
			log.debug("Testing elevator encoder...");
			
			if(!elevatorEncoderWorks(ec)) {
				log.warning("Possible problem with elevator encoder");
				warnings++;
			}
			
			log.debug("Testing bottom limit switch...");
			
			if(!elevatorBottomLSWorks(ec)) {
				log.warning("Possible problem with bottom limit switch");
				warnings++;
			}
			
			log.debug("Testing top limit switch...");
			
			if(!elevatorTopLSWorks(ec)) {
				log.warning("Possible problem with the top limit switch");
				warnings++;
			}
			
			if(warnings > 0) {
				log.warning("Robot finished self diagnostic test with " + warnings + " warning(s)");
				consoleLog.warning("Robot finished self diagnostic test with " + warnings + " warning(s), check the log for more information");
				return false;
			} else {
				log.info("Robot finished self diagnostic test without any warnings");
				consoleLog.info("Robot finished self diagnostic test without any warnings");
				return true;
			}
		} catch(Exception ex) {
			log.error(ExceptionInfo.getType(ex) + " in Diagnostic.java", ex);
			consoleLog.error(ExceptionInfo.getType(ex) + " in Diagnostic.java", ex);
			
			return false;
		}
	}
	
	/**
	 * Tests an encoder on one of the CANJaguars on the drive train 
	 * @param jag the jaguar to test
	 * @return true if the encoder is working; false otherwise
	 * @throws InterruptedException ignore
	 */
	private static boolean CANJaguarEncoderWorks(CANJaguar jag) throws InterruptedException {
		jag.set(1.0);
		Thread.sleep(1000);
		jag.set(0.0);
		
		return Math.abs((int) jag.getPosition()) > 1;
	}
	
	/**
	 * Tests the bottom limit switch for the elevator
	 * @param ec the elevator controls
	 * @return true if the bottom limit switch works; false otherwise
	 */
	private static boolean elevatorBottomLSWorks(ElevatorController ec) {
		ec.setAutoSetpoint(0);
		while(!ec.isAtAutoSetpoint()) {
			// Wait until it's at the bottom...
		}
		
		return ec.getBottomLS().get();
	}
	
	/**
	 * Tests the top limit switch for the elevator
	 * @param ec the elevator controls
	 * @return true if the top limit switch works; false otherwise
	 */
	private static boolean elevatorTopLSWorks(ElevatorController ec) {
		ec.setAutoSetpoint(3750);
		while(!ec.isAtAutoSetpoint()) {
			// Wait until it's at the top...
		}
		
		return ec.getTopLS().get();
	}
	
	/**
	 * Tests the encoder for the elevator
	 * @param ec the elevator controls
	 * @return true if the encoder works; false otherwise
	 */
	private static boolean elevatorEncoderWorks(ElevatorController ec) {
		ec.setAutoSetpoint(1000);
		while(!ec.isAtAutoSetpoint()) {
			// Wait until it's at the setpoint
		}
		
		boolean isWorking = ec.getEncoder().get() > 0;
		
		ec.setAutoSetpoint(0);
		while(!ec.isAtAutoSetpoint()) {
			// Wait until it's at the bottom...
		}
		
		return isWorking;
	}
}

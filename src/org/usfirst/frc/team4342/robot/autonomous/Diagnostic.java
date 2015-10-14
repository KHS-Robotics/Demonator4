package org.usfirst.frc.team4342.robot.autonomous;

import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;
import org.usfirst.frc.team4342.robot.shared.TimeoutTimer;

import edu.wpi.first.wpilibj.CANJaguar;
import ernie.logging.loggers.ILogger;

/**
 * This class is for running tests on the robot to make sure
 * all sensors are working properly
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
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
	public static boolean runSelfTest(MecanumDrive drive, ElevatorController ec, ILogger log, RobotConsoleLogger consoleLog) {
		int warnings = 0;
		
		try {
			
			if(!CANJaguarEncoderWorks(drive.getFrontRight())) {
				log.warning("Possible problem with front right encoder");
				warnings++;
			}
			
			if(!CANJaguarEncoderWorks(drive.getFrontLeft())) {
				log.warning("Possible problem with front left encoder");
				warnings++;
			}
			
			if(!CANJaguarEncoderWorks(drive.getRearRight())) {
				log.warning("Possible problem with rear right encoder");
				warnings++;
			}
			
			if(!CANJaguarEncoderWorks(drive.getRearLeft())) {
				log.warning("Possible problem with rear left encoder");
				warnings++;
			}
			
			log.debug("Testing elevator encoder...");
			
			if(!elevatorEncoderWorks(ec)) {
				log.warning("Possible problem with elevator encoder");
				warnings++;
			}
			
			if(!elevatorBottomLSWorks(ec)) {
				log.warning("Possible problem with bottom limit switch");
				warnings++;
			}
			
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
	 */
	private static boolean CANJaguarEncoderWorks(CANJaguar jag) {
		TimeoutTimer t = new TimeoutTimer(2);
		t.start();
		
		jag.set(1.0);
		
		while(!t.isTimedOut()) {
			
		}
		
		t.kill();
		
		jag.set(0.0);
		
		return Math.abs((int) jag.getPosition()) > 1;
	}
	
	/**
	 * Tests the bottom limit switch for the elevator
	 * @param ec the elevator controls
	 * @return true if the bottom limit switch works; false otherwise
	 */
	private static boolean elevatorBottomLSWorks(ElevatorController ec) {
		TimeoutTimer t = new TimeoutTimer(5);
		t.start();
		
		ec.setAutoSetpoint(0);
		
		while(!ec.isAtAutoSetpoint() || !t.isTimedOut()) {
			// Wait until it's at the bottom...
		}
		
		t.kill();
		
		return ec.getBottomLS().get();
	}
	
	/**
	 * Tests the top limit switch for the elevator
	 * @param ec the elevator controls
	 * @return true if the top limit switch works; false otherwise
	 */
	private static boolean elevatorTopLSWorks(ElevatorController ec) {
		TimeoutTimer t = new TimeoutTimer(10);
		t.start();
		
		ec.setAutoSetpoint(3750);
		
		while(!ec.isAtAutoSetpoint() || !t.isTimedOut()) {
			// Wait until it's at the top...
		}
		
		t.start();
		
		return ec.getTopLS().get();
	}
	
	/**
	 * Tests the encoder for the elevator
	 * @param ec the elevator controls
	 * @return true if the encoder works; false otherwise
	 */
	private static boolean elevatorEncoderWorks(ElevatorController ec) {
		TimeoutTimer t = new TimeoutTimer(5);
		t.start();
		
		ec.setAutoSetpoint(1000);
		
		while(!ec.isAtAutoSetpoint() || !t.isTimedOut()) {
			// Wait until it's at the setpoint
		}
		
		boolean isWorking = ec.getEncoder().get() > 0;
		
		if(isWorking) {
			t.reset();
			t.startTimer();
			
			ec.setAutoSetpoint(0);
			while(!ec.isAtAutoSetpoint() || !t.isTimedOut()) {
				// Wait until it's at the bottom...
			}
		}
		
		t.kill();
		
		return isWorking;
	}
}

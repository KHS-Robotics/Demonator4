package org.usfirst.frc.team4342.robot.autonomous;

import org.usfirst.frc.team4342.robot.drive.MecanumDrive;
import org.usfirst.frc.team4342.robot.elevator.ElevatorController;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

import edu.wpi.first.wpilibj.CANJaguar;
import Logging.ILog;

public class Diagnostic {
	
	private Diagnostic() {
		
	}
	
	public static boolean runSelfTest(MecanumDrive drive, ElevatorController ec, ILog log, RobotConsoleLog consoleLog) {
		int warnings = 0;
		
		try {
			log.debug("Starting self diagnostic test...");
			
			log.debug("Testing front right drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getFrontRight())) {
				warnings++;
			}
			
			log.debug("Testing front left drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getFrontLeft())) {
				warnings++;
			}
			
			log.debug("Testing rear right drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getRearRight())) {
				warnings++;
			}
			
			log.debug("Testing rear left drive encoder...");
			
			if(!CANJaguarEncoderWorks(drive.getRearLeft())) {
				warnings++;
			}
			
			log.debug("Testing elevator encoder...");
			
			if(!elevatorEncoderWorks(ec)) {
				warnings++;
			}
			
			log.debug("Testing bottom limit switch...");
			
			if(!elevatorBottomLSWorks(ec)) {
				warnings++;
			}
			
			log.debug("Testing top limit switch...");
			
			if(!elevatorTopLSWorks(ec)) {
				warnings++;
			}
			
			if(warnings > 0) {
				log.warning("Robot finished self diagnostic test with " + warnings + " warning(s)");
				consoleLog.warning("Robot finished self diagnostic test with " + warnings + " warning(s)");
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
	
	private static boolean CANJaguarEncoderWorks(CANJaguar jag) throws InterruptedException {
		jag.set(1.0);
		Thread.sleep(1000);
		jag.set(0.0);
		
		return Math.abs((int) jag.getPosition()) > 1;
	}
	
	private static boolean elevatorBottomLSWorks(ElevatorController ec) {
		ec.setAutoSetpoint(0);
		while(!ec.isAtAutoSetpoint()) {
			// Wait until it's at the bottom...
		}
		
		return ec.getBottomLS().get();
	}
	
	private static boolean elevatorTopLSWorks(ElevatorController ec) {
		ec.setAutoSetpoint(3750);
		while(!ec.isAtAutoSetpoint()) {
			// Wait until it's at the top...
		}
		
		return ec.getTopLS().get();
	}
	
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

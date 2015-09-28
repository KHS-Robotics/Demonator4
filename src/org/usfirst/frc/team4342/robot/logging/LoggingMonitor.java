package org.usfirst.frc.team4342.robot.logging;

import org.usfirst.frc.team4342.robot.Robot;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;

import edu.wpi.first.wpilibj.DriverStation;
import ernie.logging.loggers.ActiveLog;

/**
 * This class is used to tell whether or not the robot should log
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class LoggingMonitor {
	private LoggingMonitor() {
		
	}
	
	private static boolean started;
	private static boolean logged;
	
	/**
	 * Starts checking if the robot should log
	 */
	public static void startMonitoring() {
		if(!started) {
			new LoggingMonitorThread().start();
			started = true;
		}
	}
	
	/**
	 * Used to tell if the logger has already logged
	 * @return true if logged; false otherwise
	 */
	public static boolean hasLogged() {
		return logged;
	}
	
	public static void logged() {
		logged = true;
	}
	
	/**
	 * The magic behind this class...
	 */
	private static class LoggingMonitorThread extends Thread implements Runnable {
		/**
		 * Checks if the robot is in disabled to reset the logged variable
		 */
		@Override
		public void run() {
			while(true) {
				if(DriverStation.getInstance().isDisabled()) {
					logged = false;
				}
				
				try {
					Thread.sleep(5000);
				} catch(Exception ex) {
					ActiveLog.error(Robot.ACTIVE_LOG_PATH, "D4-LM", ExceptionInfo.getType(ex) + " in LoggingMonitor", ex);
				}
			}
		}
	}
}

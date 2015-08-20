package org.usfirst.frc.team4342.robot.logging;

import java.io.File;
import java.io.IOException;

import Logging.ActiveLog;
import Logging.LocalLog;
import Logging.LoggerAsync;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

/**
 * 
 * @author khsrobotics
 * 
 * Factory to create loggers for the robot
 */
public class RobotLogFactory {
	
	public static final String ROOT = "/home/lvuser/";
	
	/**
	 * Creates a simple logger that logs to a specified text file.
	 * WARNING: LocalLog is not thread safe, consider using
	 * LoggingThread for a thread safe logger
	 * @param clearLogs true to clear the log file, false to append
	 * @return a logger that logs to a text file
	 * @throws IOException
	 */
	public static LocalLog createLocalLog() throws IOException {
		return new LocalLog("Demonator4", getValidLogFile(), true);
	}
	
	/**
	 * Creates a thread safe logger that logs in the background of
	 * the program to a specified text file
	 * @param clearLogs true to clear the log file, false to append
	 * @return a logger that logs to a text file
	 * @throws IOException
	 */
	public static LoggerAsync createAsyncLog() throws IOException {
		return new LoggerAsync(createLocalLog());
	}
	
	/**
	 * Creates a new logger for the console on the Driver Station
	 * @return a logger to log to the console on the DS
	 */
	public static RobotConsoleLog createRobotConsoleLog() {
		return new RobotConsoleLog();
	}
	
	/**
	 * Gets a valid log location and file
	 * @return a valid log file location
	 */
	private static File getValidLogFile() {
		for(int i = 1; i <= 5; i++) {
			File f = new File(ROOT + "Log[" + i + "].txt");
			
			if(!f.exists()) {
				return f;
			}
		}
		
		shiftLogFiles();
		
		return new File(ROOT + "Log[1].txt");
	}
	
	/**
	 * We can save up to 5 log files! Each time we make a new
	 * LocalLog, we want to check if we have to shift
	 * the log file index values up one and delete
	 * the oldest file and make way for the latest
	 * log file, [1].
	 */
	private static void shiftLogFiles() {
		if(!new File(ROOT + "Log[5].txt").exists()) {
			return;
		}
		
		new File(ROOT + "Log[5].txt").delete();
		
		for(int i = 4; i >= 1; i--) {
			File f = new File(ROOT + "Log[" + i + "].txt");
			
			boolean renamed = f.renameTo(new File(ROOT + "Log[" + (i+1) + "].txt"));
			
			if(!renamed) {
				ActiveLog.warning("Demonator4", "The file at path \"" + f.getPath() + "\" was not successfully renamed");
				
				System.err.println("The file at path \"" + f.getPath() + "\" was not successfully renamed");
			}
		}
	}
}

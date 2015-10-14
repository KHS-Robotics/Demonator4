package org.usfirst.frc.team4342.robot.logging.factories;

import java.io.IOException;

import ernie.logging.loggers.LocalLogger;
import ernie.logging.loggers.LoggerAsync;

import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;
import org.usfirst.frc.team4342.robot.logging.shared.FileHelper;

/**
 * Factory to create loggers for the robot
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
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
	 * @throws LoggingException 
	 */
	public static LocalLogger createLocalLog() throws IOException {
		return new LocalLogger("Demonator4", FileHelper.getValidLogFile(), true);
	}
	
	/**
	 * Creates a thread safe logger that logs in the background of
	 * the program to a specified text file
	 * @param clearLogs true to clear the log file, false to append
	 * @return a logger that logs to a text file
	 * @throws IOException
	 * @throws LoggingException 
	 */
	public static LoggerAsync createAsyncLogger() throws IOException {
		return new LoggerAsync(createLocalLog());
	}
	
	/**
	 * Creates a new logger for the console on the Driver Station
	 * @return a logger to log to the console on the DS
	 */
	public static RobotConsoleLogger createRobotConsoleLogger() {
		return new RobotConsoleLogger();
	}
}

package org.usfirst.frc.team4342.robot;

import java.io.File;
import java.io.IOException;

import Logging.LocalLog;
import Logging.LoggerAsync;

/**
 * 
 * @author khsrobotics
 * 
 * Factory to create loggers for the robot
 */
public class RobotLogFactory {
	
	public static final String PATH = "/home/lvuser/Log.txt";
	
	/**
	 * Clears the log file
	 * 
	 * @throws IOException
	 */
	public static void clearLogs() throws IOException {
		File f = new File(PATH);
		
		if(f.exists()) {
			f.delete();
		}
		
		f.createNewFile();
	}
	
	/**
	 * Creates a simple logger that logs to a specified text file.
	 * WARNING: LocalLog is not thread safe, consider using
	 * LoggingThread for a thread safe logger
	 * @param clearLogs true to clear the log file, false to append
	 * @return a logger that logs to a text file
	 * @throws IOException
	 */
	public static LocalLog createLocalLog(boolean clearLogs) throws IOException {
		if(clearLogs) {
			clearLogs();
		}
		
		return new LocalLog("Demonator4", PATH, true);
	}
	
	/**
	 * Creates a thread safe logger that logs in the background of
	 * the program to a specified text file
	 * @param clearLogs true to clear the log file, false to append
	 * @return a logger that logs to a text file
	 * @throws IOException
	 */
	public static LoggerAsync createAsyncLog(boolean clearLogs) throws IOException {
		return new LoggerAsync(createLocalLog(clearLogs));
	}

	//TODO: Get these two methods working
	private static File getValidLogFile() throws IOException {
		//deleteAndRenameLogFiles();
		
		File validFile = new File("/home/lvuser/Log[0].txt");
		
		for(int i = 0; i < 10; i++) {
			validFile = new File("/home/lvuser/Log[" + i + "].txt");
			if(!validFile.exists()) {
				break;
			}
		}
		
		return validFile;
	}
	
	/**
	 * We can save up to 10 log files! Each time we make a new
	 * LocalLog, we want to check if we have to shift
	 * the log file index values up one and delete
	 * the oldest file and make way for the latest
	 * log file, [1].
	 */
	private static void deleteAndRenameLogFiles() {
		if(!new File("/home/lvuser/Log[10].txt").exists()) {
			return;
		}
		
		for(int i = 9; i >= 0; i--) {
			String fileName = "/home/lvuser/Log[" + i + "].txt";
			
			if(i == 10) {
				new File(fileName).delete();
			}
			else if(i <= 9 && i > 1) {
				new File(fileName).renameTo(new File("/home/lvuser/Log[" + (i+1) + "].txt"));
			}
		}
	}
}
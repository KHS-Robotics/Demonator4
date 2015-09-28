package org.usfirst.frc.team4342.robot.logging.shared;

import java.io.File;

import org.usfirst.frc.team4342.robot.Robot;

import ernie.logging.loggers.ActiveLog;
import ernie.logging.loggers.MultiLog;

/**
 * This class is used to shift and get valid log files
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class FileHelper {
	
	private static final String ROOT = "/home/lvuser/";
	
	private FileHelper() {
		
	}
	
	/**
	 * Gets a valid log location and file
	 * @return a valid log file location
	 */
	public static File getValidLogFile() {
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
	 * Gets a valid log location and file
	 * 
	 * @param log used to log warnings about the files
	 * @param consoleLog used to log warnings about the files
	 * @return a valid log file location
	 */
	public static File getValidPdpLogFile(MultiLog multiLog) {
		for(int i = 1; i <= 5; i++) {
			File f = new File(ROOT + "PdpLog[" + i + "].csv");
			
			if(!f.exists()) {
				return f;
			}
		}
		
		shiftPdpLogFiles(multiLog);
		
		return new File(ROOT + "PdpLog[1].csv");
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
				ActiveLog.warning(Robot.ACTIVE_LOG_PATH, "Demonator4", "The file at path \"" + f.getPath() + "\" was not successfully renamed");
				
				System.err.println("The file at path \"" + f.getPath() + "\" was not successfully renamed");
			}
		}
	}
	
	/**
	 * We can save up to 5 log files! Each time we make a new
	 * LocalLog, we want to check if we have to shift
	 * the log file index values up one and delete
	 * the oldest file and make way for the latest
	 * log file, [1].
	 * 
	 * @param log used to log warnings about the files
	 * @param consoleLog used to log warnings about the files
	 */
	private static void shiftPdpLogFiles(MultiLog multiLog) {
		
		File lastFile = new File(ROOT + "PdpLog[5].csv");
		
		if(!lastFile.exists()) {
			return;
		}
		
		lastFile.delete();
		
		for(int i = 4; i >= 1; i--) {
			File f = new File(ROOT + "PdpLog[" + i + "].csv");
			
			boolean renamed = f.renameTo(new File(ROOT + "PdpLog[" + (i+1) + "].csv"));
			
			if(!renamed) {
				multiLog.warning("The file at path \"" + f.getPath() + "\" was not successfully renamed");
				
				System.err.println("The file at path \"" + f.getPath() + "\" was not successfully renamed");
			}
		}
	}
}
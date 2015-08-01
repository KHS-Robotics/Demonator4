package Logging;

import java.io.File;
import java.io.IOException;

public final class ActiveLog {
	
	private ActiveLog() {
		
	}
	
	private static final Object lock = new Object();
	
	private static String path;
	
	public static void info(String facility, String message) {
		log(Severity.INFO, facility, message, null);
	}
	
	public static void debug(String facility, String message) {
		log(Severity.DEBUG, facility, message, null);
	}
	
	public static void warning(String facility, String message) {
		log(Severity.WARNING, facility, message, null);
	}
	
	public static void error(String facility, String message, Exception ex) {
		log(Severity.ERROR, facility, message, ex);
	}
	
	private static void log(Severity severity, String facility, String message, Exception ex) {
		synchronized(lock) {
			try {
				
				LocalLog localLog = new LocalLog(facility, path, true);
				LoggerAsync log = new LoggerAsync(localLog);
				
				if(severity == Severity.ERROR) {
					log.error(message, ex);
				}
				else if(severity == Severity.INFO) {
					log.info(message);
				} 
				else if(severity == Severity.DEBUG) {
					log.debug(message);
				}
				else if(severity == Severity.WARNING) {
					log.warning(message);
				} else {
					System.err.println("Invalid parameter for Severity in ActiveLog");
				}
				
			} catch (IOException e) {
				System.err.println(e.getClass().getSimpleName() + " in ActiveLog");
			}
		}
	}
	
	public static void setLogFile(String p) {
		path = p;
		
		File f = new File(path);
		
		try {
			f.createNewFile();
		} catch (IOException ex) {
			System.err.println(ex.getClass().getSimpleName() + " in ActiveLog");
		}
	}
}

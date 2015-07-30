package Logging;

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
	
	public static void log(Severity severity, String facility, String message, Exception ex) {
		synchronized(lock) {
			try {
				
				LocalLog log = new LocalLog(facility, path, true);
				
				if(ex != null) {
					log.error(message, ex);
				} else {
					log.log(severity, BaseLog.FormatMessageLogData(severity, facility, message));
				}
				
			} catch (IOException e) {
				System.err.println(e.getClass().getSimpleName() + " in ActiveLog");
			}
		}
	}
	
	public static void setLogFile(String p) {
		path = p;
	}
}

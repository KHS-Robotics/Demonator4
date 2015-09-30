package org.usfirst.frc.team4342.robot.logging.loggers;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.shared.FileHelper;

import ernie.logging.loggers.MultiLog;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Logs the Power Distribution Panel's voltage and amperage to a CSV file
 * 
 * About the PDP: http://www.vexrobotics.com/217-4244.html
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class PDPLogger {
	private boolean started;
	
	private static final int LOG_SECONDS = 5;
	
	private PDPLoggingThread logger;
	
	/**
	 * Constructs a PDP Logger to log PDP data
	 * @param pdp the PDP to get data from
	 * @param log the log to log to
	 * @param consoleLog the console log to log to
	 */
	public PDPLogger(PowerDistributionPanel pdp, MultiLog multiLog)  {
		logger = new PDPLoggingThread(pdp, multiLog);
	}
	
	/**
	 * Starts logging for 10 minutes
	 */
	public void start() {
		if(!started) {
			logger.start();
			started = true;
		}
	}
	
	/**
	 * The magic behind this class...
	 */
	private class PDPLoggingThread extends Thread implements Runnable {
		private int numLogs = 0;
		
		File csvLogFile;
		
		private static final int MAX_LOGS = 100;
		
		private PowerDistributionPanel pdp;
		private MultiLog multiLog;
		
		/**
		 * Constructs a PDP Logger to log PDP data
		 * @param pdp the PDP to get data from
		 * @param multiLog the loggers to log to
		 */
		public PDPLoggingThread(PowerDistributionPanel pdp, MultiLog multiLog) {
			this.pdp = pdp;
			this.multiLog = multiLog;
			
			csvLogFile = FileHelper.getValidPdpLogFile();
		}
		
		/**
		 * Logs to the RoboRIO for 10 minutes
		 */
		@Override
		public void run() {
			FileWriter writer = null;
			
			try {
				csvLogFile.createNewFile();
				
				writer = new FileWriter(csvLogFile);
				
				for(int channel = 0; channel < 16; channel++) {
		        	writer.write("PDP-A" + channel);
		        	writer.write(',');
		        }
				
				writer.write("PDP-V");
				writer.write(',');
				
				writer.write("Timestamp");
				
				writer.write('\r');
	
				while(numLogs < MAX_LOGS) {
			        for(int channel = 0; channel < 16; channel++) {
			        	writer.write("" + pdp.getCurrent(channel));
			        	writer.write(',');
			        }
			        
			        writer.write("" + pdp.getVoltage());
			        writer.write(',');
			        
			        writer.write(new Date(System.currentTimeMillis()).toString());
			        
			        writer.write('\r');
			        writer.flush();
			        
			        numLogs++;
			        
			        Thread.sleep(LOG_SECONDS*1000);
				}
				
				writer.close();
			} catch(Exception ex) {
				multiLog.warning("Failed to write to CSV for PDP logger :: " + ExceptionInfo.getType(ex));
			} finally {
				try {
					if(writer != null) {
						writer.close();
					}
				} catch (Exception ex) {
					multiLog.warning("Failed to close writer to CSV for PDP logger");
				}
			}
		}
	}
}
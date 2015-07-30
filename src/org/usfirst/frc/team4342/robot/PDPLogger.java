package org.usfirst.frc.team4342.robot;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import Logging.LoggerAsync;
import Logging.RobotConsoleLog;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * 
 * @author khsrobotics
 * 
 * Logs the Power Distribution Panel's voltage and amperage to a CSV file
 * 
 * About the PDP: http://www.vexrobotics.com/217-4244.html
 */
public class PDPLogger
{
	private boolean started;
	
	private static final String ROOT = "/home/lvuser/";
	private static final int LOG_SECONDS = 5;
	
	private PDPLoggingThread logger;
	
	public PDPLogger(PowerDistributionPanel pdp, LoggerAsync log, RobotConsoleLog consoleLog) 
	{
		logger = new PDPLoggingThread(pdp, log, consoleLog);
	}
	
	public void start() {
		if(!started) {
			logger.start();
		}
		
		started = true;
	}
	
	
	private class PDPLoggingThread extends Thread implements Runnable
	{
		private int numLogs = 0;
		
		File csvLogFile;
		
		private static final int MAX_LOGS = 100;
		
		private PowerDistributionPanel pdp;
		private LoggerAsync log;
		private RobotConsoleLog consoleLog;
		
		public PDPLoggingThread(PowerDistributionPanel pdp, LoggerAsync log, RobotConsoleLog consoleLog)
		{
			this.pdp = pdp;
			this.log = log;
			this.consoleLog = consoleLog;
			
			csvLogFile = getValidLogFile(log ,consoleLog);
		}
		
		/**
		 * Logs to the RoboRIO for 10 minutes
		 */
		@Override
		public void run()
		{
			FileWriter writer = null;
			
			try
			{
				csvLogFile.createNewFile();
				
				writer = new FileWriter(csvLogFile);
				
				for(int channel = 0; channel < 16; channel++)
		        {
		        	writer.write("PDP-A" + channel);
		        	writer.write(',');
		        }
				
				writer.write("PDP-V");
				writer.write(',');
				
				writer.write("Timestamp");
				
				writer.write('\r');
	
				while(numLogs < MAX_LOGS)
				{
			        for(int channel = 0; channel < 16; channel++)
			        {
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
			}
			catch(Exception ex)
			{
				consoleLog.warning(ExceptionInfo.getType(ex) + ": Failed to write to CSV for PDP logger,"
						+ " please alert Ernie or Magnus when you can");
				log.warning("Failed to write to CSV for PDP logger: " + ex.getMessage());
			}
			finally
			{
				try 
				{
					if(writer != null)
						writer.close();
				} 
				catch (Exception ex) 
				{
					consoleLog.warning(ExceptionInfo.getType(ex) + ": Failed to close writer to CSV for PDP logger,"
							+ " please alert Ernie or Magnus when you can");
					log.warning("Failed to close writer to CSV for PDP logger");
				}
			}
		}
	}
	
	/**
	 * Gets a valid log location and file
	 * 
	 * @param log used to log warnings about the files
	 * @param consoleLog used to log warnings about the files
	 * @return a valid log file location
	 */
	private static File getValidLogFile(LoggerAsync log, RobotConsoleLog consoleLog) {
		for(int i = 1; i <= 5; i++) {
			File f = new File(ROOT + "PdpLog[" + i + "].txt");
			
			if(!f.exists()) {
				return f;
			}
		}
		
		shiftLogFiles(log, consoleLog);
		
		return new File(ROOT + "PdpLog[1].txt");
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
	private static void shiftLogFiles(LoggerAsync log, RobotConsoleLog consoleLog) {
		if(!new File(ROOT + "PdpLog[5].txt").exists()) {
			return;
		}
		
		new File(ROOT + "PdpLog[5].txt").delete();
		
		for(int i = 4; i >= 1; i--) {
			File f = new File(ROOT + "PdpLog[" + i + "].txt");
			
			boolean renamed = f.renameTo(new File(ROOT + "PdpLog[" + (i+1) + "].txt"));
			
			if(!renamed) {
				log.warning("The file at path \"" + f.getPath() + "\" was not successfully renamed");
				consoleLog.warning("The file at path \"" + f.getPath() + "\" was not successfully renamed");
				
				System.err.println("The file at path \"" + f.getPath() + "\" was not successfully renamed");
			}
		}
	}
}
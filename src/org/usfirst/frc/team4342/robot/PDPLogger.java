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
	
	private static final int LOG_SECONDS = 5;
	
	private PDPLoggingThread logger;
	
	public PDPLogger(PowerDistributionPanel pdp, LoggerAsync log, RobotConsoleLog consoleLog) 
	{
		//deleteAndRenameLogFiles();
		
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
			csvLogFile = new File("/home/lvuser/PdpLog.csv");
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
				boolean createLogFile = csvLogFile.exists() ? 
						csvLogFile.delete() : csvLogFile.createNewFile();
						
				if(createLogFile)
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
	
	//TODO: Get these two methods working...
	private void initalizeLogFile()
	{
		for(int i = 0; i < 10; i++)
		{
			logger.csvLogFile  = new File("/home/lvuser/PdpLog[" + i + "].csv");
			if(!logger.csvLogFile.exists())
				break;
		}
	}
	
	/**
	 * We can save up to 10 log files! Each time we make a new
	 * LocalLog, we want to check if we have to shift
	 * the log file index values up one and delete
	 * the oldest file and make way for the latest
	 * log file, [1].
	 */
	private void deleteAndRenameLogFiles() {
		if(!new File("/home/lvuser/PdpLog[10].csv").exists()) {
			return;
		}
		
		for(int i = 10; i >= 1; i--) {
			String fileName = "/home/lvuser/PdpLog[" + i + "].csv";
			
			if(i == 10) {
				new File(fileName).delete();
			}
			else if(i <= 9 && i >= 1) {
				new File(fileName).renameTo(new File("/home/lvuser/PdpLog[" + (i+1) + "].csv"));
			}
		}
	}
}
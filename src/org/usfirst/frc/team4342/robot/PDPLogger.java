package org.usfirst.frc.team4342.robot;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

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
	
	private PDPLoggingThread logger;
	
	public PDPLogger(PowerDistributionPanel pdp) 
	{
		//deleteAndRenameLogFiles();
		
		logger = new PDPLoggingThread(pdp);
	}
	
	public void startLogging() {
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
		
		public PDPLoggingThread(PowerDistributionPanel pdp)
		{
			this.pdp = pdp;
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
			        Thread.sleep(5000);
				}
			}
			catch(Exception ex)
			{
				try
				{
					Robot.printWarningToDS("Failed to write to CSV for PDP logger,"
							+ " please alert Ernie or Magnus when you can");
					Robot.getRobotLog().warning("Failed to write to CSV for PDP logger: " + ex.getMessage());
				}
				catch(Exception ex2)
				{
					Robot.printWarningToDS("Failed to write to CSV for PDP logger,"
							+ " please alert Ernie or Magnus when you can");
				}
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
					try
					{
						Robot.printWarningToDS("Failed to close writer to CSV for PDP logger,"
								+ " please alert Ernie or Magnus when you can");
						Robot.getRobotLog().warning("Failed to close writer to CSV for PDP logger: " + ex.getMessage());
					}
					catch(Exception ex2)
					{
						Robot.printWarningToDS("Failed to close writer to CSV for PDP logger,"
								+ " please alert Ernie or Magnus when you can");
					}
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
		
		for(int i = 9; i >= 0; i--) {
			String fileName = "/home/lvuser/PdpLog[" + i + "].csv";
			
			if(i == 10) {
				new File(fileName).delete();
			}
			else if(i <= 9 && i > 1) {
				new File(fileName).renameTo(new File("/home/lvuser/PdpLog[" + (i+1) + "].csv"));
			}
		}
	}
}
package org.usfirst.frc.team4342.robot;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class PDPMonitor implements Runnable
{
	private int numLogs = 0;
	private static final int MAX_LOGS = 100;
	private static File csvLogFile;
	
	private Thread logger;
	private PowerDistributionPanel pdp;
	
	public PDPMonitor(PowerDistributionPanel pdp) 
	{
		this.pdp = pdp;
		
		//deleteAndRenameLogFiles();
		
		csvLogFile = new File("/home/lvuser/PdpLog.csv");
		
		logger = new Thread(this);
		logger.start();
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
				// oh well...
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
					// oh well...
				}
			}
		}
	}
	
	private static void initalizeLogFile()
	{
		for(int i = 0; i < 10; i++)
		{
			csvLogFile  = new File("/home/lvuser/PdpLog[" + i + "].csv");
			if(!csvLogFile.exists())
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
	private static void deleteAndRenameLogFiles() {
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
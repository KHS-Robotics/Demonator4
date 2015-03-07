package org.usfirst.frc.team4342.robot;

import java.io.FileWriter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;


public class PDPMonitor extends Thread implements Runnable
{
	private int numLogs = 0;
	private static final int MAX_LOGS = 100;
	
	private PowerDistributionPanel pdp;
	
	private Thread logThread;
	
	public PDPMonitor(PowerDistributionPanel pdp)
	{
		this.pdp = pdp;
		logThread = new Thread(this);
		logThread.start();
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
			boolean createLogFile = RobotConstants.LOG_CSV_FILE.exists() ? 
					RobotConstants.LOG_CSV_FILE.delete() : RobotConstants.LOG_CSV_FILE.createNewFile();
					
			if(createLogFile)
				RobotConstants.LOG_CSV_FILE.createNewFile();
			
			writer = new FileWriter(RobotConstants.LOG_CSV_FILE);
			
			for(int channel = 0; channel < 16; channel++)
	        {
	        	writer.write("PDP-A" + channel);
	        	writer.write(',');
	        }
			writer.write("PDP-V");
			writer.write('\r');

			while(numLogs < MAX_LOGS)
			{
		        for(int channel = 0; channel < 16; channel++)
		        {
		        	writer.write("" + pdp.getCurrent(channel));
		        	writer.write(',');
		        }
		        writer.write("" + pdp.getVoltage());
		        
		        writer.write('\r');
		        writer.flush();
		        
		        numLogs++;
		        Thread.sleep(5000);
			}
		}
		catch(Exception ex)
		{
			//log.error("Unexpected error while running log thread", ex);
			ex.printStackTrace();
			DriverStation.reportError("ERROR! Go get Ernie or Magnus\n", false);
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
				//log.error("Unexpected error while attempting to close log writer", ex);
				ex.printStackTrace();
				DriverStation.reportError("ERROR! Go get Ernie or Magnus\n", false);
			}
		}
	}
}
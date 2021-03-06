package org.usfirst.frc.team4342.robot.elevator;

import org.usfirst.frc.team4342.robot.components.repository.RobotRepository;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;

import ernie.logging.loggers.ILogger;
import ernie.logging.loggers.MultiLogger;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is for monitoring the health of the encoder and limit switches
 * for the elevator
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class ElevatorHealthMonitor 
{
	private static Joystick elevStick;
	private static Encoder enc;
	private static DigitalInput top, bottom;
	private static MultiLogger multiLog;
	
	// We really only want one instance of this class,
	// so if someone creates multiple instances, there
	// isn't any harm done
	private static boolean constructed;
	private static boolean started;
	
	private static boolean loggedEnc;
	private static boolean loggedBotLS;
	private static boolean loggedTopLS;
	
	/**
	 * Constructs a health monitor for the elevator. This includes making sure:
	 * the top limit, bottom limit switch an the encoder are functioning properly.
	 * @param log the log to log to
	 * @param consoleLog the log to log to
	 */
	public ElevatorHealthMonitor(ILogger log, RobotConsoleLogger consoleLog) {
		if(!constructed) 
		{
			this.elevStick = RobotRepository.ElevatorStick;
			this.enc = RobotRepository.ElevatorEncoder;
			this.top = RobotRepository.TopLimitSwitch;
			this.bottom = RobotRepository.BottomLimitSwitch;
			this.multiLog = new MultiLogger(new ILogger[] { log, consoleLog });
		}
	}
	
	/**
	 * Starts monitoring the elevator
	 */
	public void startMonitoring() 
	{
		if(!started) 
		{
			new MonitoringThread().start();
			started = true;
		}
	}
	
	/**
	 * Magic of the class... this works by checking if the user is giving an input but
	 * the encoder is not changing, and if the limit switches are not returning true
	 * when the elevator is all the way down or up
	 */
	private class MonitoringThread extends Thread implements Runnable 
	{
		@Override
		public void run() 
		{
			while(true) 
			{
				try 
				{
					if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isOperatorControl()) 
					{
						boolean y = Math.abs(elevStick.getY()) > 0.10;
						
						if(Math.abs(enc.get()) == 0 && y && !loggedEnc) 
						{
							multiLog.warning("Elevator encoder may not be operating correctly");
							loggedEnc = true;
						}
						
						if(enc.get() <= 0 && !bottom.get() && !loggedBotLS) 
						{
							multiLog.warning("Bottom limit may not be operating correctly");
							loggedBotLS = true;
						}
						
						if(enc.get() >= 3700 && !top.get() && !loggedTopLS) 
						{
							multiLog.warning("Top limit may not be operating correctly");
							loggedTopLS = true;
						}
					}
					
					if(loggedEnc && loggedBotLS && loggedTopLS) 
					{
						// Nice! Everything in here has indicated a
						// warning! Hopefully you're not in a match!
						multiLog.warning("Nice! Everything in DHM has indicated an error. Hopefully you weren't in a match!");
						constructed = started = false;
						break;
					}
					
					Thread.sleep(250);
						
				} 
				catch (Exception ex) 
				{
					multiLog.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
				}
			}
		}
	}
}

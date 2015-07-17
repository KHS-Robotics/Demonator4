package Logging;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Do not use this class, still under testing
 * @author Ernie
 *
 */
public class LoggingThread extends Thread implements Runnable
{
	private volatile ILog log;
	private Thread logThread;
	
	private volatile Severity severity;
	private volatile String message;
	private volatile Exception ex;
	
	public LoggingThread(LocalLog log)
	{
		throw new UnsupportedOperationException("Do not use this class, still under testing");
		//this.log = log;
		//logThread = new Thread(this);
	}
	
	@Override
	public synchronized void run()
	{
		switch(severity)
		{
			case INFO:
				log.info(message);
			break;
			
			case DEBUG:
				log.debug(message);
			break;
			
			case WARNING:
				log.warning(message);
			break;
			
			case ERROR:
				log.error(message, ex);
			break;
			
			default:
				DriverStation.reportError(
					"WARNING: Logging thread's severity could not be determiend, "
					+ "please contact Ernie or Magnus when you can", 
					false
				);
		}
	}
	
	public synchronized void info(String message)
	{
		waitForLog(2000);
		setSeverity(Severity.INFO);
		setMessage(message);
		new Thread(this).start();
	}
	
	public synchronized void debug(String message)
	{
		waitForLog(2000);
		setSeverity(Severity.DEBUG);
		setMessage(message);
		new Thread(this).start();
	}
	
	public synchronized void warning(String message)
	{
		waitForLog(2000);
		setSeverity(Severity.WARNING);
		setMessage(message);
		new Thread(this).start();
	}
	
	public synchronized void error(String message, Exception ex)
	{
		waitForLog(2000);
		setSeverity(Severity.ERROR);
		setErrorInfo(message, ex);
		new Thread(this).start();
	}
	
	private void waitForLog(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(InterruptedException ex)
		{
			DriverStation.reportError(
				"WARNING: Exception thrown while waiting for "
				+ "logger to log, please contact Ernie or Magnus "
				+ "when you can",
				false
			);
		}
	}
	
	private synchronized void setMessage(String message)
	{
		this.message = message;
	}
	
	private synchronized void setException(Exception ex)
	{
		this.ex = ex;
	}
	
	private synchronized void setErrorInfo(String message, Exception ex)
	{
		setMessage(message);
		setException(ex);
	}
	
	private synchronized void setSeverity(Severity severity)
	{
		this.severity = severity;
	}
}

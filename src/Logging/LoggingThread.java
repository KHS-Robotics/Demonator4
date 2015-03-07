package Logging;

public class LoggingThread extends Thread implements Runnable
{
	private final ILog log;
	private Thread logThread;
	
	private Severity severity;
	private String message;
	private Exception ex;
	
	public LoggingThread(LocalLog log)
	{
		this.log = log;
		logThread = new Thread(this);
	}
	
	@Override
	public void run()
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
		}
	}
	
	public synchronized void info(String message)
	{
		if(verifySeverity(Severity.INFO))
			setSeverity(Severity.INFO);
		
		setMessage(message);
		logThread.start();
	}
	
	public synchronized void debug(String message)
	{
		if(verifySeverity(Severity.DEBUG))
			setSeverity(Severity.DEBUG);
		
		setMessage(message);
		logThread.start();
	}
	
	public synchronized void warning(String message)
	{
		if(verifySeverity(Severity.WARNING))
			setSeverity(Severity.WARNING);
		
		setMessage(message);
		logThread.start();
	}
	
	public synchronized void error(String message, Exception ex)
	{
		if(verifySeverity(Severity.ERROR))
			setSeverity(Severity.ERROR);
		
		setErrorInfo(message, ex);
		logThread.start();
	}
	
	private void setMessage(String message)
	{
		this.message = message;
	}
	
	private void setException(Exception ex)
	{
		this.ex = ex;
	}
	
	private void setErrorInfo(String message, Exception ex)
	{
		setMessage(message);
		setException(ex);
	}
	
	private void setSeverity(Severity severity)
	{
		this.severity = severity;
	}
	
	private boolean verifySeverity(Severity sev)
	{
		return sev == severity;
	}
}

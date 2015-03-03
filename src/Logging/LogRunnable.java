package Logging;

public class LogRunnable implements Runnable
{
	private final ILog log;
	
	private Severity severity;
	private String message;
	private Exception ex;
	
	public LogRunnable(ILog log)
	{
		this.log = log;
	}
	
	@Override
	public void run()
	{
		if(severity == Severity.INFO)
		{
			log.info(message);
		}
		else if(severity == Severity.DEBUG)
		{
			log.debug(message);
		}
		else if(severity == Severity.WARNING)
		{
			log.warning(message);
		}
		else if(severity == Severity.ERROR)
		{
			log.error(message, ex);
		}
	}
	
	public void setSeverity(Severity severity)
	{
		this.severity = severity;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public void setException(Exception ex)
	{
		this.ex = ex;
	}
	
	public void setLogInfo(Severity severity, String message, Exception ex)
	{
		setSeverity(severity);
		setMessage(message);
		setException(ex);
	}
}

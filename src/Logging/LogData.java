package Logging;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Ernie Wilson
 */
public class LogData 
{
    private final Exception exception;
    private final String message;
    private final ArrayList<String> stacktrace = new ArrayList<String>();
    private final Date timestamp;
    
    /**
     * Constructs a standard LogData object that stores the exception and a
     * message describing the error.
     * @param ex the exception being logged
     * @param message the message being logged
     */
    protected LogData(String message, Exception ex)
    {
        if (ex == null)
            throw new IllegalArgumentException("ex cannot be null");
        if (message == null)
            throw new IllegalArgumentException("message cannot be null");
        
        exception = ex;
        this.message = message;
        
        for(StackTraceElement ste : exception.getStackTrace())
        {
            stacktrace.add(ste.toString() + "\n");
        }
        
        timestamp = new Date(System.currentTimeMillis());
    }
    
    /**
     * Gets the message describing the error.
     * @return the message describing the error
     */
    protected String getMessage()
    {
        return message;
    }
    
    /**
     * Gets the stack trace of the exception.
     * @return the stack trace of the exception
     */
    protected String[] getStacktrace()
    {
        return stacktrace.toArray(new String[stacktrace.size()]);
    }
    
    /**
     * Gets the time of the error's creation.
     * @return the time of the error's creation
     */
    protected String getTimestamp()
    {
        return timestamp.toString();
    }
}
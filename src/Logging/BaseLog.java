package Logging;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Ernie Wilson
 * 
 * This class contains the basics for what the log requires
 */
public abstract class BaseLog implements ILog
{
    /** The name of the facility logging the info. */
    protected String facilityName;
    
    @Override
    public void info(String message)
    {
        Log(Severity.INFO, FormatMessageLogData(Severity.INFO, message));
    } 
    
    @Override
    public void debug(String message)
    {
        Log(Severity.DEBUG, FormatMessageLogData(Severity.DEBUG, message));
    }
    
    @Override
    public void warning(String message)
    {
        Log(Severity.WARNING, FormatMessageLogData(Severity.WARNING, message));
    }
    
    @Override
    public void error(String message, Exception ex)
    {
        Log(Severity.ERROR, FormatErrorLogData(Severity.ERROR, new LogData(message, ex)));
    }
    
    abstract void Log(Severity severity, Object... message);
    
    private Object[] FormatMessageLogData(Severity severity, String message)
    {
        return new String[] {
            "Facility: " + facilityName,
            "Severity: " + severity.toString(),
            "Message: " + message,
            "Date: " + new Date(System.currentTimeMillis()).toString()
        };
    }
    
    private Object[] FormatErrorLogData(Severity severity, LogData message)
    {
        ArrayList<String> temp = new ArrayList<String>();
        
        temp.add("Facility: " + this.facilityName + "-");
        temp.add("Severity: " + severity.toString() + "-");
        temp.add("Message: " + message.getMessage() + "-");
        
        String[] stacktrace = message.getStacktrace();
        
        for (int i = 0; i < stacktrace.length; i++)
        {
            if (i == 0)
                temp.add("Stacktrace: " + stacktrace[i]);
            else if (i == stacktrace.length)
                temp.add(stacktrace[i] + "-");
            else
                temp.add("            " + stacktrace[i]);
        }
        
        temp.add("Timestamp: " + message.getTimestamp() + "-");
        
        String[] data = new String[temp.size()];
        data = temp.toArray(data);
        
        String formated = "";
        for (String s : data)
        {
            formated += s;
        }
        
        return formated.split("-");
    }
}
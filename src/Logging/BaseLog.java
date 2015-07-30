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
        log(Severity.INFO, FormatMessageLogData(Severity.INFO, facilityName, message));
    } 
    
    @Override
    public void debug(String message)
    {
        log(Severity.DEBUG, FormatMessageLogData(Severity.DEBUG, facilityName, message));
    }
    
    @Override
    public void warning(String message)
    {
        log(Severity.WARNING, FormatMessageLogData(Severity.WARNING, facilityName, message));
    }
    
    @Override
    public void error(String message, Exception ex)
    {
        log(Severity.ERROR, FormatErrorLogData(Severity.ERROR, facilityName, new LogData(message, ex)));
    }
    
    abstract void log(Severity severity, Object... message);
    
    public static Object[] FormatMessageLogData(Severity severity, String facility, String message)
    {
        return new String[] {
            "Facility: " + facility,
            "Severity: " + severity.toString(),
            "Message: " + message,
            "Date: " + new Date(System.currentTimeMillis()).toString()
        };
    }
    
    public static Object[] FormatErrorLogData(Severity severity, String facility, LogData message)
    {
        ArrayList<String> temp = new ArrayList<String>();
        
        temp.add("Facility: " + facility + "-");
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
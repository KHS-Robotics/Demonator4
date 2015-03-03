package Logging;

/**
 * @author Ernie Wilson
 * 
 * This interface contains the basic methods of the logs
 */
public interface ILog 
{
    void info(String message);
    void debug(String message);
    void warning(String message);
    void error(String message, Exception ex);
}
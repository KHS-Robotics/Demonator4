package Logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Ernie Wilson
 */
public class LocalLog extends BaseLog
{
    private File logFile;
    private boolean append;
    
    /**
     * Constructs a new LocalLog and writes to a a specified txt file
     * @param facilityName
     * @param logFile
     * @param append if true, the log will append the new logging info to the already existing
     * test file; if false, the log will delete the current log file and start in a fresh txt file
     */
    public LocalLog(String facilityName, File logFile, boolean append) throws IOException
    {
        if(logFile == null)
            throw new IllegalArgumentException("Log file cannot be null");
        if (facilityName == null)
            throw new IllegalArgumentException("Facility name cannot be null");
        if (!isTxtFile(logFile))
            throw new IllegalArgumentException("File for LocalLog isn't a txt file!");
        
        if(!logFile.exists())
        {
        	logFile.createNewFile();
        }
        else
        {
        	if(!append)
        	{
        		logFile.delete();
        		logFile.createNewFile();
        	}
        }
        
        this.facilityName = facilityName;
        this.logFile = logFile;
        this.append = append;
    }
    
    /**
     * Constructs a new LocalLog and writes to a a specified txt file
     * @param facilityName the name of the facility logging the info
     * @param logFile the specified file to log the info
     */
    public LocalLog(String facilityName, File logFile) throws IOException
    {
        this(facilityName, logFile, false);
    }
    
    /**
     * Constructs a new LocalLog and writes to a a specified txt file
     * @param facilityName the name of the facility logging the info
     * @param logFile the specified file to log the info
     */
    public LocalLog(String facilityName, String logFile) throws IOException
    {
    	this(facilityName, new File(logFile), false);
    }
    
    public LocalLog(String facilityName, String logFile, boolean append) throws IOException
    {
    	this(facilityName, new File(logFile), append);
    }
    
    @Override
    protected void Log(Severity severity, Object... message)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, append));
            
            for (Object info : message)
            {
                bw.write(info.toString());
                bw.newLine();
            }
            
            bw.write("-------------------------------------------------------------------------------------------------------------------");
            bw.newLine();
            bw.close();
        }
        catch(Exception ex)
        {
            //How do to log when the log fails?!?!?
            System.err.println("Error while attempting to write logging information");
            ex.printStackTrace();
        }
    }
    
    /**
     * Determines if a specified file is a txt file.
     * @param file the file to check
     * @return true if the file is a txt file, false otherwise
     */
    private static boolean isTxtFile(File file) 
    {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');
        
        if (i > 0 && i < s.length()-1)
            ext = s.substring(i+1).toLowerCase();
        
        return ext != null ? ext.equals("txt") : false;
    }
}
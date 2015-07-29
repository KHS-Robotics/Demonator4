package org.usfirst.frc.team4342.robot;

/**
 * @author khsrobotics
 * 
 * This class is used to quickly get info out of an Exception. This is useful
 * for logging exceptions without having to decode the exception within your
 * catch block.
 */
public class ExceptionInfo {
	
	private Exception ex;
	private Exception[] exs;
	
	/**
	 * Used for only one type of Exception
	 * @param ex the exception
	 */
	public ExceptionInfo(Exception ex) {
		this.ex = ex;
	}
	
	/**
	 * Used for multiple exceptions
	 * @param exs the exceptions
	 */
	public ExceptionInfo(Exception... exs) {
		this.exs = exs;
	}
	
	/**
	 * Gets the type of the exception, or the type of first index of the exceptions
	 * @return the type of exception (the class name)
	 */
	public String getType() {
		if(exs == null)
			return ex.getClass().getSimpleName();
		
		return exs[0].getClass().getSimpleName();
	}
	
	/**
	 * Returns the exception, or the first index of the exceptions
	 * @return the exception
	 */
	public Exception getException() {
		if(exs == null)
			return ex;
		
		return exs[0];
	}
	
	/**
	 * Returns the exceptions, or the only given exception
	 * @return the exception(s)
	 */
	public Exception[] getExceptions() {
		if(exs != null)
			return exs;
		
		return new Exception[] { ex };
	}
	
	/**
	 * Gets the type of exception, without having to instantiate this class
	 * @param ex the exception
	 * @return the type of exception (the class name)
	 */
	public static String getType(Exception ex) {
		return ex.getClass().getSimpleName();
	}
}

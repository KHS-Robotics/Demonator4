package org.usfirst.frc.team4342.robot.logging.shared;

/**
 * This class is used to quickly get info out of an Exception. This is useful
 * for logging exceptions without having to decode the exception within your
 * catch block.
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class ExceptionInfo {
	
	/**
	 * Gets the type of exception, without having to instantiate this class
	 * @param ex the exception
	 * @return the type of exception (the class name)
	 */
	public static String getType(Exception ex) {
		return ex.getClass().getSimpleName();
	}
}

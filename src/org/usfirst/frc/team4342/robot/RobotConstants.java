package org.usfirst.frc.team4342.robot;

import java.io.File;

public final class RobotConstants 
{
	private RobotConstants() {}
	
	/** Dead band for the <code>Joystick</code> to determine if the user wants to move */
	public static final double JOYSTICK_DEADBAND = 0.02;
	
	/** Dead band to determine if the user wants to override the arm moving itself */
	public static final double ELEVATOR_DEADBAND = 0.1;
	
	/** File for logging info to the roboRIO for further review */
	public static final File LOG_CSV_FILE = new File("/home/lvuser/Log.csv");
	
	/** Text file used for logging errors */
	public static final File LOG_TEXT_FILE = new File("/home/lvuser/Log.txt");
}
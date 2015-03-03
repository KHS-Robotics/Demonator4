package org.usfirst.frc.team4342.robot;

import java.io.File;

public final class RobotConstants 
{
	private RobotConstants() {}
	
	/** Dead band for the <code>Joystick</code> to determine if the user wants to move */
	public static final double JOYSTICK_DEADBAND = 0.05;
	
	/** Dead band to determine if the user wants to override the arm moving itself */
	public static final double ELEVATOR_DEADBAND = 0.1;
	
	/** Tolerance value for the elevator set point */
	public static final int ELEVATOR_SETPOINT_DEADBAND = 5;
	
	/** P value for elevator mechanism */
	public static final double ELEV_KP = 0.001;
	
	/** Elevator preset for picking up one tote */
	public static final int ELEVATOR_PRESET1 = 50;
	
	/** Elevator preset for picking up two totes */
	public static final int ELEVATOR_PRESET2 = 100;
	
	/** Elevator preset for picking up three totes */
	public static final int ELEVATOR_PRESET3 = 150;
	
	/** Elevator preset for picking up four totes */
	public static final int ELEVATOR_PRESET4 = 200;
	
	/** Elevator preset for picking up five totes */
	public static final int ELEVATOR_PRESET5 = 250;
	
	/** Elevator preset for picking up six totes */
	public static final int ELEVATOR_PRESET6 = 300;
	
	/** File for logging info to the roboRIO for further review */
	public static final File LOG_CSV_FILE = new File("/home/lvuser/Log.csv");
	
	/** Text file used for logging errors */
	public static final File LOG_TEXT_FILE = new File("/home/lvuser/Log.txt");
}
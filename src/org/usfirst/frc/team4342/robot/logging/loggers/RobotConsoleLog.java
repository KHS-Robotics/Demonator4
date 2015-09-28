package org.usfirst.frc.team4342.robot.logging.loggers;

import ernie.logging.loggers.BaseLog;
import ernie.logging.Severity;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Logger that logs to the console on the driver station
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class RobotConsoleLog extends BaseLog {

	/**
	 * Logs to the console on the driver station. This method only
	 * logs the severity level and the message.
	 */
	@Override
	public void log(Severity severity, Object[] message) {
		String mssg = severity.toString().toUpperCase() + ": ";

		String hackMessage = message[2].toString();
		hackMessage = hackMessage.substring(hackMessage.indexOf(":")+1);
		
		mssg += hackMessage + "\n";
		
		DriverStation.reportError(mssg, false);
	}

}
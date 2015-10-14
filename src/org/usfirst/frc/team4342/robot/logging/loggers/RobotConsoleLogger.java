package org.usfirst.frc.team4342.robot.logging.loggers;

import ernie.logging.data.InfoLogData;
import ernie.logging.loggers.BaseLogger;
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
public class RobotConsoleLogger extends BaseLogger {

	/**
	 * Logs to the console on the driver station. This method only
	 * logs the severity level and the message.
	 */
	@Override
	public void log(Severity severity, InfoLogData data) {
		String mssg = severity.toString().toUpperCase() + ": ";
		
		mssg += data.getMessage();
		
		DriverStation.reportError(mssg, false);
	}

}

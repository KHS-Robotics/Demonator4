package Logging;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author Ernie
 * 
 * Logger that logs to the console on the driver station
 */
public class RobotConsoleLog extends BaseLog {

	/**
	 * Logs to the console on the driver station. This method only
	 * logs the severity level and the message.
	 */
	@Override
	public void log(Severity severity, Object... message) {
		String mssg = severity.toString().toUpperCase() + ": ";

		String hackMessage = message[2].toString();
		hackMessage = hackMessage.substring(hackMessage.indexOf(":")+1);
		
		mssg += hackMessage;
		
		DriverStation.reportError(mssg, false);
	}

}

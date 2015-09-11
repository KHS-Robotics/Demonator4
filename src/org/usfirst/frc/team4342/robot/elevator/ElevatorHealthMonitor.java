package org.usfirst.frc.team4342.robot.elevator;

import org.usfirst.frc.team4342.robot.Robot;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

import Logging.ActiveLog;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is for monitoring the health of the encoder and limit switches
 * for the elevator
 * 
 * @author khsrobotics
 */
public class ElevatorHealthMonitor {
	
	private Joystick elevStick;
	private Encoder enc;
	private DigitalInput top, bottom;
	private RobotConsoleLog consoleLog;
	
	private static boolean started;
	private static boolean loggedEnc;
	private static boolean loggedBotLS;
	private static boolean loggedTopLS;
	
	public ElevatorHealthMonitor(Joystick elevStick, Encoder enc, 
								 DigitalInput top, DigitalInput bottom,
								 RobotConsoleLog consoleLog) {
		this.elevStick = elevStick;
		this.enc = enc;
		this.top = top;
		this.bottom = bottom;
		this.consoleLog = consoleLog;
	}
	
	/**
	 * Starts monitoring the elevator
	 */
	public void startMonitoring() {
		if(!started) {
			new MonitoringThread().start();
			started = true;
		}
	}
	
	/**
	 * Magic of the class... this works by checking if the user is giving an input but
	 * the encoder is not changing, and if the limit switches are not returning true
	 * when the elevator is all the way down or up
	 */
	private class MonitoringThread extends Thread implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isOperatorControl()) {
						boolean y = Math.abs(elevStick.getY()) > 0.10;
						
						if(Math.abs(enc.get()) <= 0 && y && !loggedEnc) {
							ActiveLog.warning(Robot.ACTIVE_LOG_PATH, "D4-ehm", "Elevator encoder may not be operating correctly");
							consoleLog.warning("Elevator encoder may not be operating correctly");
							loggedEnc = true;
						}
						
						if(enc.get() <= 0 && !bottom.get() && !loggedBotLS) {
							ActiveLog.warning(Robot.ACTIVE_LOG_PATH, "D4-ehm", "Bottom limit switch may not be operating correctly");
							consoleLog.warning("Bottom limit may not be operating correctly");
							loggedBotLS = true;
						}
						
						if(enc.get() >= 3700 && !top.get() && !loggedTopLS) {
							ActiveLog.warning(Robot.ACTIVE_LOG_PATH, "D4-ehm", "Top limit switch may not be operating correctly");
							consoleLog.warning("Top limit may not be operating correctly");
							loggedTopLS = true;
						}
					}
					
					if(loggedEnc && loggedBotLS && loggedTopLS) {
						// Nice! Everything in here has indicated a
						// warning! Hopefully you're not in a match!
						ActiveLog.warning(Robot.ACTIVE_LOG_PATH, "D4-ehm", "Nice! Everything in DHM has indicated an error. Hopefully you weren't in a match!");
						break;
					}
					
					Thread.sleep(100);
						
				} catch (Exception ex) {
					ActiveLog.error(Robot.ACTIVE_LOG_PATH, "D4-ehm", ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
					consoleLog.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
				}
			}
		}
	}
}

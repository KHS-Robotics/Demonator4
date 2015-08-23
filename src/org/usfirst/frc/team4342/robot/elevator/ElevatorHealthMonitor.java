package org.usfirst.frc.team4342.robot.elevator;

import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

import Logging.ILog;
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
	private ILog log;
	private RobotConsoleLog consoleLog;
	
	private static boolean started;
	
	public ElevatorHealthMonitor(Joystick elevStick, Encoder enc, 
								 DigitalInput top, DigitalInput bottom, 
								 ILog log, RobotConsoleLog consoleLog) {
		this.elevStick = elevStick;
		this.enc = enc;
		this.top = top;
		this.bottom = bottom;
		this.log = log;
		this.consoleLog = consoleLog;
	}
	
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
			while(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isOperatorControl()) {
				try {
					
					boolean y = elevStick.getY() > 0.10;
					
					if(enc.get() <= 10 && y) {
						log.warning("Elevator encoder may not be operating correctly");
						consoleLog.warning("Elevator encoder may not be operating correctly");
					}
					
					if(enc.get() <= 0 && !bottom.get()) {
						log.warning("Bottom limit switch may not be operating correctly");
						consoleLog.warning("Bottom limit may not be operating correctly");
					}
					
					if(enc.get() >= 3700 && !top.get()) {
						log.warning("Top limit switch may not be operating correctly");
						consoleLog.warning("Top limit may not be operating correctly");
					}
					
					Thread.sleep(100);
				} catch (Exception ex) {
					log.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
					consoleLog.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
				}
			}
		}
	}
}

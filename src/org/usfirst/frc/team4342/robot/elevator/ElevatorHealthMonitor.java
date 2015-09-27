package org.usfirst.frc.team4342.robot.elevator;

import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;

import Logging.MultiLog;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is for monitoring the health of the encoder and limit switches
 * for the elevator
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class ElevatorHealthMonitor {
	
	private static Joystick elevStick;
	private static Encoder enc;
	private static DigitalInput top, bottom;
	private static MultiLog multiLog;
	
	// We really only want one instance of this class,
	// so if someone creates multiple instances, there
	// isn't any harm done
	private static boolean constructed;
	private static boolean started;
	
	private static boolean loggedEnc;
	private static boolean loggedBotLS;
	private static boolean loggedTopLS;
	
	/**
	 * Constructs a health monitor for the elevator. This includes making sure:
	 * the top limit, bottom limit switch an the encoder are functioning properly.
	 * @param elevStick the joystick to monitor
	 * @param enc the encoder to monitor
	 * @param top the top limit switch to monitor
	 * @param bottom the bottom limit switch to monitor
	 * @param consoleLog the log to log to
	 */
	public ElevatorHealthMonitor(Joystick elevStick, Encoder enc, 
								 DigitalInput top, DigitalInput bottom,
								 MultiLog consoleLog) {
		if(!constructed) {
			this.elevStick = elevStick;
			this.enc = enc;
			this.top = top;
			this.bottom = bottom;
			this.multiLog = multiLog;
		}
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
							multiLog.warning("Elevator encoder may not be operating correctly");
							loggedEnc = true;
						}
						
						if(enc.get() <= 0 && !bottom.get() && !loggedBotLS) {
							multiLog.warning("Bottom limit may not be operating correctly");
							loggedBotLS = true;
						}
						
						if(enc.get() >= 3700 && !top.get() && !loggedTopLS) {
							multiLog.warning("Top limit may not be operating correctly");
							loggedTopLS = true;
						}
					}
					
					if(loggedEnc && loggedBotLS && loggedTopLS) {
						// Nice! Everything in here has indicated a
						// warning! Hopefully you're not in a match!
						multiLog.warning("Nice! Everything in DHM has indicated an error. Hopefully you weren't in a match!");
						constructed = started = false;
						break;
					}
					
					Thread.sleep(100);
						
				} catch (Exception ex) {
					multiLog.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
				}
			}
		}
	}
}

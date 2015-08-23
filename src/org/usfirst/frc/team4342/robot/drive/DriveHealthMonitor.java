package org.usfirst.frc.team4342.robot.drive;

import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.RobotConsoleLog;

import Logging.ILog;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is used to monitor the health of the drive train encoders
 * 
 * @author khsrobotics
 */
public class DriveHealthMonitor {
	
	private Joystick driveStick;
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private ILog log;
	private RobotConsoleLog consoleLog;
	
	private static boolean started;
	
	private static boolean loggedFR;
	private static boolean loggedFL;
	private static boolean loggedRR;
	private static boolean loggedRL;
	
	public DriveHealthMonitor(Joystick driveStick, CANJaguar frontRight,
								CANJaguar frontLeft, CANJaguar rearRight,
								CANJaguar rearLeft, ILog log, RobotConsoleLog consoleLog) {
		this.driveStick = driveStick;
		this.frontRight = frontRight;
		this.frontLeft = frontLeft;
		this.rearRight = rearRight;
		this.rearLeft = rearLeft;
		this.log = log;
		this.consoleLog = consoleLog;
	}
	
	public void startMonitoring() {
		if(!started) {
			new MonitorThread().start();
			started = true;
		}
	}
	
	private double getFrontRightEncCount() {
		return frontRight.getPosition();
	}
	
	private double getFrontLeftEncCount() {
		return frontLeft.getPosition();
	}
	
	private double getRearRightEncCount() {
		return rearRight.getPosition();
	}
	
	private double getRearLeftEncCount() {
		return rearLeft.getPosition();
	}
	
	/**
	 * Magic of the class... this works by checking to see if the user is giving the robot
	 * an input and the encoder counts are changing
	 */
	private class MonitorThread extends Thread implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					
					if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isOperatorControl()) {
						
						boolean x = Math.abs(driveStick.getX()) > 0.10;
						boolean y = Math.abs(driveStick.getY()) > 0.10;
						boolean z = Math.abs(driveStick.getZ()) > 0.10;
						
						// We use 0.05 revolutions here because the encoder counts should change
						// more dramatically than 1/20 of a spin of the wheel
						
						if(getFrontRightEncCount() <= 0.05 && (x || y || z) && !loggedFR) {
							log.warning("Front right drive encoder may not be operating correctly");
							consoleLog.warning("Front right drive encoder may not be operating correctly");
							loggedFR = true;
						}
						
						if(getFrontLeftEncCount() <= 0.05 && (x || y || z) && !loggedFL) {
							log.warning("Front left drive encoder may not be operating correctly");
							consoleLog.warning("Front left drive encoder may not be operating correctly");
							loggedFL = true;
						}
						
						if(getRearRightEncCount() <= 0.05 && (x || y || z) && !loggedRR) {
							log.warning("Rear right drive encoder may not be operating correctly");
							consoleLog.warning("Rear right drive encoder may not be operating correctly");
							loggedRR = true;
						}
						
						if(getRearLeftEncCount() <= 0.05 && (x || y || z) && !loggedRL) {
							log.warning("Rear left drive encoder may not be operating correctly");
							consoleLog.warning("Rear left drive encoder may not be operating correctly");
							loggedRL = true;
						}
						
						if(loggedFR && loggedFL && loggedRR && loggedRL) {
							// Nice! Everything in here has indicated a
							// warning! Hopefully you're not in a match!
							return;
						}
					}
					
					Thread.sleep(100);
				} catch(Exception ex) {
					log.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
					consoleLog.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
				}
			}
		}
	}
}
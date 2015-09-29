package org.usfirst.frc.team4342.robot.drive;

import org.usfirst.frc.team4342.robot.components.DriveTrain;
import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLog;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;

import ernie.logging.loggers.ILog;
import ernie.logging.loggers.MultiLog;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is used to monitor the health of the drive train encoders
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class DriveHealthMonitor {
	
	private static Joystick driveStick;
	private static CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private static MultiLog multiLog;
	
	// We really only want one instance of this class,
	// so if someone creates multiple instances, there
	// isn't any harm done
	private static boolean constructed;
	private static boolean started;
	
	private static boolean loggedFR;
	private static boolean loggedFL;
	private static boolean loggedRR;
	private static boolean loggedRL;
	
	/**
	 * Constructs a health monitor for the drive train. This includes making sure:
	 * the front right encoder, front left encoder, rear right encoder and the rear 
	 * left encoder are functioning properly.
	 * @param driveStick the joystick to monitor
	 * @param frontRight the front right motor controller to monitor
	 * @param frontLeft the front left motor controller to monitor
	 * @param rearRight the rear right motor controller to monitor
	 * @param rearLeft the rear left motor controller to monitor
	 * @param consoleLog the log to log to
	 */
	public DriveHealthMonitor(ILog log, RobotConsoleLog consoleLog) {
		if(!constructed) {
			this.driveStick = DriveTrain.Stick.getInstance();
			this.frontRight = DriveTrain.FrontRight.getInstance();
			this.frontLeft = DriveTrain.FrontLeft.getInstance();
			this.rearRight = DriveTrain.RearRight.getInstance();
			this.rearLeft = DriveTrain.RearLeft.getInstance();
			this.multiLog = new MultiLog(new ILog[] { log, consoleLog });
		}
	}
	
	/**
	 * Starts monitoring the drive train
	 */
	public void startMonitoring() {
		if(!started) {
			new MonitorThread().start();
			started = true;
		}
	}
	
	private double getFrontRightEncCount() {
		return Math.abs(frontRight.getPosition());
	}
	
	private double getFrontLeftEncCount() {
		return Math.abs(frontLeft.getPosition());
	}
	
	private double getRearRightEncCount() {
		return Math.abs(rearRight.getPosition());
	}
	
	private double getRearLeftEncCount() {
		return Math.abs(rearLeft.getPosition());
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
							frontRight.setPercentMode(CANJaguar.kQuadEncoder, DrivePID.kCodesPerRev);
							frontRight.enableControl();
							
							multiLog.warning("Front right drive encoder may not be operating correctly");
							loggedFR = true;
						}
						
						if(getFrontLeftEncCount() <= 0.05 && (x || y || z) && !loggedFL) {
							frontLeft.setPercentMode(CANJaguar.kQuadEncoder, DrivePID.kCodesPerRev);
							frontLeft.enableControl();
							
							multiLog.warning("Front left drive encoder may not be operating correctly");
							loggedFL = true;
						}
						
						if(getRearRightEncCount() <= 0.05 && (x || y || z) && !loggedRR) {
							rearRight.setPercentMode(CANJaguar.kQuadEncoder, DrivePID.kCodesPerRev);
							rearRight.enableControl();
							
							multiLog.warning("Rear right drive encoder may not be operating correctly");
							loggedRR = true;
						}
						
						if(getRearLeftEncCount() <= 0.05 && (x || y || z) && !loggedRL) {
							rearLeft.setPercentMode(CANJaguar.kQuadEncoder, DrivePID.kCodesPerRev);
							rearLeft.enableControl();
							
							multiLog.warning("Rear left drive encoder may not be operating correctly");
							loggedRL = true;
						}
						
						if(loggedFR && loggedFL && loggedRR && loggedRL) {
							// Everything has indicated a warning, so no need
							// to use unnecessary resources
							multiLog.warning("Nice! Everything in DHM has indicated an error. Hopefully you weren't in a match!");
							constructed = started = false;
							return;
						}
					}
					
					Thread.sleep(100);
				} catch(Exception ex) {
					multiLog.error(ExceptionInfo.getType(ex) + " in HealthMonitor.MonitorThread.java", ex);
				}
			}
		}
	}
}


package org.usfirst.frc.team4342.robot.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ernie.logging.loggers.MultiLog;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Asynchronously puts data to the Smart Dashboard
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class SmartDashboardUpdater {
	
	private static boolean started;
	
	private static ArrayList<String> joystickKeys = new ArrayList<String>();
	private static ArrayList<String> encoderKeys = new ArrayList<String>();
	private static ArrayList<String> jagaurKeys = new ArrayList<String>();
	private static ArrayList<String> limitSwitchKeys = new ArrayList<String>();
	private static ArrayList<String> gyroKeys = new ArrayList<String>();
	
	private static Map<String, Joystick> joysticks = new HashMap<String, Joystick>();
	private static Map<String, Encoder> encoders = new HashMap<String, Encoder>();
	private static Map<String, CANJaguar> jaguars = new HashMap<String, CANJaguar>();
	private static Map<String, DigitalInput> limitSwitches = new HashMap<String, DigitalInput>();
	private static Map<String, Gyro> gyros = new HashMap<String, Gyro>();
	
	private static Ultrasonic ultra;
	
	/**
	 * Adds a joystick to put on the Smart Dashboard
	 * @param key the key to use when putting to the Smart Dashboard
	 * @param joystick the joystick to get data from
	 */
	public static void addJoystick(String key, Joystick joystick) {
		joystickKeys.add(key);
		joysticks.put(key, joystick);
	}
	
	/**
	 * Adds an Encoder to put on the Smart Dashboard
	 * @param key the key to use when putting to the Smart Dashboard
	 * @param encoder the encoder to get data from
	 */
	public static void addEncoder(String key, Encoder encoder) {
		encoderKeys.add(key);
		encoders.put(key, encoder);
	}
	
	/**
	 * Adds a Jagaur to put on the Smart Dashboard
	 * @param key the key to use when putting to the Smart Dashboard
	 * @param jaguar the jagaur to get data from
	 */
	public static void addJagaur(String key, CANJaguar jaguar) {
		jagaurKeys.add(key);
		jaguars.put(key, jaguar);
	}
	
	/**
	 * Adds a limit switch to put on the Smart Dashboard
	 * @param key the key to use when putting to the Smart Dashboard
	 * @param limitSwitch the limit switch to get data from
	 */
	public static void addDigitalInput(String key, DigitalInput limitSwitch) {
		limitSwitchKeys.add(key);
		limitSwitches.put(key, limitSwitch);
	}
	
	/**
	 * Adds a gyro to put on the Smart Dashboard
	 * @param key the key to use when putting to the Smart Dashboard
	 * @param gyro the gyro to get data from
	 */
	public static void addGyro(String key, Gyro gyro) {
		gyroKeys.add(key);
		gyros.put(key, gyro);
	}
	
	/**
	 * Adds an Ultrasonic sensor to put on the Smart Dashboard
	 * @param ultra the ulrasonic sensor to get data from
	 */
	public static void setUltrasonic(Ultrasonic ultra) {
		if(ultra == null) {
			throw new NullPointerException("Ultrasonic cannot be null");
		}
		
		SmartDashboardUpdater.ultra = ultra;
	}
	
	/**
	 * Starts updating the Smart Dashboard
	 */
	public static void startUpdating(MultiLog multiLog) {
		if(!started) {
			new SmartDashboardUpdaterThread(multiLog).start();
			started = true;
		}
	}
	
	/**
	 * The magic behind this class...
	 */
	private static class SmartDashboardUpdaterThread extends Thread implements Runnable {
		
		private boolean loggedJoysticks;
		private boolean loggedEncoders;
		private boolean loggedJaguars;
		private boolean loggedDigitalInput;
		private boolean loggedGyros;
		private boolean loggedUltra;
		
		private MultiLog multiLog;
		
		public SmartDashboardUpdaterThread(MultiLog multiLog) {
			this.multiLog = multiLog;
		}
		
		/**
		 * Puts data to the Smart Dashboard
		 */
		@Override
		public void run() {
			while(true) {
				putJoystickData();
				putEncoderData();
				putJagaurData();
				putLimitSwitchData();
				putGyroData();
				putUltrasonicData();
				
				try {
					Thread.sleep(100);
				} catch(Exception ex) {
					multiLog.error(ExceptionInfo.getType(ex) + " in SDU", ex);
				}
			}
		}
		
		/**
		 * Puts Joystick data to the Smart Dashboard
		 */
		private void putJoystickData() {
			try {
				for(String key : joystickKeys) {
					Joystick joystick = joysticks.get(key);
					SmartDashboard.putNumber(key + "-X", joystick.getX());
					SmartDashboard.putNumber(key + "-Y", joystick.getY());
					SmartDashboard.putNumber(key + "-Z", joystick.getZ());
				}
			} catch(Exception ex) {
				if(!loggedJoysticks) {
					multiLog.error("Error while putting Joystick data", ex);
					loggedJoysticks = true;
				}
			}
		}
		
		/**
		 * Puts Encoder data to the Smart Dashboard
		 */
		private void putEncoderData() {
			try {
				for(String key : encoderKeys) {
					Encoder encoder = encoders.get(key);
					SmartDashboard.putNumber(key + "-Count", encoder.get());
				}
			} catch(Exception ex) {
				if(!loggedEncoders) {
					multiLog.error("Error while putting Encoder data", ex);
					loggedEncoders = true;
				}
			}
		}
		
		/**
		 * Puts Jagaur data to the Smart Dashboard
		 */
		private void putJagaurData() {
			try {
				for(String key : jagaurKeys) {
					CANJaguar jaguar = jaguars.get(key);
					SmartDashboard.putNumber(key + "-Enc", jaguar.getPosition());
				}
			} catch(Exception ex) {
				if(!loggedJaguars) {
					multiLog.error("Error while putting CANJaguar data", ex);
					loggedJaguars = true;
				}
			}
		}
		
		/**
		 * Puts Limit Switch data to the Smart Dashboard
		 */
		private void putLimitSwitchData() {
			try {
				for(String key : limitSwitchKeys) {
					DigitalInput limitSwitch = limitSwitches.get(key);
					SmartDashboard.putBoolean(key, limitSwitch.get());
				}
			} catch(Exception ex) {
				if(!loggedDigitalInput) {
					multiLog.error("Error while putting DigitalInput data", ex);
					loggedDigitalInput = true;
				}
			}
		}
		
		/**
		 * Puts Gyro data to the Smart Dashboard
		 */
		private void putGyroData() {
			try {
				for(String key : gyroKeys) {
					Gyro gyro = gyros.get(key);
					SmartDashboard.putNumber(key + "-Ang", gyro.getAngle());
				}
			} catch(Exception ex) {
				if(!loggedGyros) {
					multiLog.error("Error while putting Gyro data", ex);
					loggedGyros = true;
				}
			}
		}
		
		/**
		 * Puts Ultrasonic data to the Smart Dashboard
		 */
		private void putUltrasonicData() {
			try {
				SmartDashboard.putNumber("U-Dist", ultra.getRangeInches());
			} catch(Exception ex) {
				if(!loggedUltra) {
					multiLog.error("Error while putting Ultrasonic data", ex);
					loggedUltra = true;
				}
			}
		}
	}
}

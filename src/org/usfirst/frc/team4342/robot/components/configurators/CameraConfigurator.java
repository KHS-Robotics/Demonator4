package org.usfirst.frc.team4342.robot.components.configurators;

import org.usfirst.frc.team4342.robot.logging.loggers.RobotConsoleLogger;

import edu.wpi.first.wpilibj.CameraServer;
import ernie.logging.loggers.ILogger;

/**
 * This class is used for initializing the camera
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class CameraConfigurator {
	private CameraConfigurator() {
		
	}
	
	/**
	 * Initializes the camera
	 * @param camera the camera to initialize
	 */
	public static void configure(ILogger log, RobotConsoleLogger consoleLog) {
		try {
			CameraServer camera = CameraServer.getInstance();
			camera.setQuality(50);
			camera.startAutomaticCapture("cam0");
		} catch(Exception ex) {
			consoleLog.warning("Failed to start camera");
			log.error("Failed to start camera", ex);
		}
	}
}

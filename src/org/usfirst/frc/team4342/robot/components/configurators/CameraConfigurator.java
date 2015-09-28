package org.usfirst.frc.team4342.robot.components.configurators;

import edu.wpi.first.wpilibj.CameraServer;

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
	
	private static CameraServer camera;
	
	/**
	 * Gets the camera
	 * @return the camera
	 */
	public static CameraServer getCamera() {
		return camera;
	}
	
	/**
	 * Initializes the camera
	 * @param camera the camera to initialize
	 */
	public static void configure(CameraServer camera, ILog log, RobotConsoleLog consoleLog) {
		try {
			camera = CameraServer.getInstance();
			camera.setQuality(50);
			camera.startAutomaticCapture("cam0");
			
			CameraConfigurator.camera = camera;
		} catch(Exception ex) {
			consoleLog.warning("Failed to start camera");
			log.error("Failed to start camera", ex);
		}
	}
}

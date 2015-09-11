package org.usfirst.frc.team4342.robot.shared;

import org.usfirst.frc.team4342.robot.Robot;
import org.usfirst.frc.team4342.robot.logging.ExceptionInfo;

import Logging.ActiveLog;

/**
 * 
 * This class is for creating timeouts for automated robot tasks in case
 * a sensor breaks and causes an infinite loop in the automated task.
 * 
 * @author khsrobotics
 *
 */
public class TimeoutTimer {
	
	private volatile boolean timedOut;
	private int timeOut;
	
	private TimeoutTimerThread t;
	
	/**
	 * Initialize a timer to create a timeout on a separate thread
	 * @param timeOut
	 */
	public TimeoutTimer(int timeOut) {
		this.timeOut = timeOut;
	}
	
	/**
	 * Starts the timer on another thread
	 */
	public void start() {
		t = new TimeoutTimerThread(timeOut);
		t.start();
	}
	
	/**
	 * Used to tell whether or not the timer has reached its timeout
	 * @return true if timed out; false if not or if the timer has not been started
	 */
	public boolean isTimedOut() {
		return timedOut;
	}
	
	/**
	 * Instantly times out the timer
	 */
	public void kill() {
		t.kill();
	}
	
	/**
	 * Instantly times out the timer and frees the resources used by this class
	 */
	public void dispose() {
		t.dispose();
	}
	
	/**
	 * The magic behind this class...
	 */
	private class TimeoutTimerThread extends Thread implements Runnable {
		private int timeout;
		
		/**
		 * Creates a timer that's on a separate thread
		 * @param timeout
		 */
		public TimeoutTimerThread(int timeout) {
			this.timeout = timeout;
		}
		
		/**
		 * Manually times out the timer
		 */
		public void kill() {
			timedOut = true;
		}
		
		/**
		 * Manually times out the time and frees the resources used by this class
		 */
		public void dispose() {
			kill();
			t = null;
		}
		
		/**
		 * Continually iterates until the timeout is reached.
		 */
		@Override
		public void run() {
			int currentTime = 0;
			
			while(!timedOut) {
				try {
					if(currentTime < timeout) {
						currentTime++;
						Thread.sleep(1000);
					} else {
						timedOut = true;
					}
				} catch(Exception ex) {
					ActiveLog.error(Robot.ACTIVE_LOG_PATH, "D4-timer", ExceptionInfo.getType(ex) + " in TimedTimeout", ex);
					timedOut = true;
				}
			}
		}
	}
}

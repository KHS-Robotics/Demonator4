package org.usfirst.frc.team4342.robot.shared;

import org.usfirst.frc.team4342.robot.Robot;
import org.usfirst.frc.team4342.robot.logging.shared.ExceptionInfo;
import org.usfirst.frc.team4342.robot.logging.shared.FileHelper;

import ernie.logging.loggers.ActiveLog;

/**
 * This class is for creating timeouts for automated robot tasks in case
 * a sensor breaks and causes an infinite loop in the automated task.
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class TimeoutTimer {
	
	private volatile boolean timedOut;
	private volatile boolean killed;
	private volatile boolean stopped;
	
	private TimeoutTimerThread t;
	
	/**
	 * Initialize a timer to create a timeout on a separate thread
	 * @param timeOut
	 */
	public TimeoutTimer(int timeOut) {
		this.timedOut = true;
		t = new TimeoutTimerThread(timeOut);
	}
	
	/**
	 * Starts the timer on another thread
	 */
	public void start() {
		t.start();
		t.startTimer();
	}
	
	/**
	 * Starts the timer. Only call this if you plan on using the timer more than once.
	 * Otherwise, use start() instead.
	 */
	public void startTimer() {
		t.startTimer();
	}
	
	/**
	 * Stops the timer
	 */
	public void stop() {
		stopped = true;
	}
	
	/**
	 * Sets the timeout for the timer
	 * @param timeout the timeout
	 */
	public void setTimeout(int timeout) {
		t.setTimeout(timeout);
	}
	
	/**
	 * Used to tell whether or not the timer has reached its timeout
	 * @return true if timed out; false if not or if the timer has not been started
	 */
	public boolean isTimedOut() {
		return timedOut;
	}
	
	public boolean isKilled() {
		return killed;
	}
	
	/**
	 * Gets the current ticks of the timer
	 * @return the current ticks of the timer
	 */
	public int currentTicks() {
		return t.currentTicks;
	}
	
	/**
	 * Resets the timer
	 */
	public void reset() {
		t.reset();
	}
	
	/**
	 * Instantly times out the timer and frees the resources used by the thread
	 */
	public void kill() {
		t.kill();
	}
	
	/**
	 * The magic behind this class...
	 */
	private class TimeoutTimerThread extends Thread implements Runnable {
		private int timeout;
		private int currentTicks;
		
		/**
		 * Creates a timer that's on a separate thread
		 * @param timeout
		 */
		public TimeoutTimerThread(int timeout) {
			this.timeout = timeout;
		}
		
		/**
		 * Starts the timer
		 */
		public void startTimer() {
			timedOut = false;
		}
		
		/**
		 * Sets the timeout of the timer
		 * @param timeout the timeout
		 */
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
		
		/**
		 * Resets the timer
		 */
		public void reset() {
			currentTicks = 0;
			timedOut = true;
		}
		
		/**
		 * Kills the timer and frees the resources used by this object
		 */
		public void kill() {
			timedOut = true;
			killed = true;
		}
		
		/**
		 * Continually iterates until the timeout is reached.
		 */
		@Override
		public void run() {
			while(!killed) {
				while(!timedOut && !stopped) {
					try {
						if(currentTicks < timeout) {
							currentTicks++;
							Thread.sleep(1000);
						} else {
							timedOut = true;
						}
					} catch(Exception ex) {
						ActiveLog.error(FileHelper.ACTIVE_LOG_PATH, "D4-timer", ExceptionInfo.getType(ex) + " in TimedTimeout", ex);
						timedOut = true;
					}
				}
			}
		}
	}
}

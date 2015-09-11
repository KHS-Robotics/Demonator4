package org.usfirst.frc.team4342.robot.elevator;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * 
 * @author khsrobotics
 * 
 * This class is for controller the elevator of the robot.
 * The elevator is for picking up totes and containers in order
 * to stack them to earn points.
 */
public class ElevatorController {
	
	/**
	 * The elevator operates on a separate thread, since it contains
	 * complex math and things that should be done separate from the
	 * main thread
	 */
	private class ElevatorThread extends Thread implements Runnable {
		
		private ElevatorController ec;
		
		public ElevatorThread(ElevatorController ec) {
			this.ec = ec;
		}
		
		/**
		 * Starts listening for both teleop and auto inputs
		 */
		@Override
		public void run() {
			while(true) {
				ec.move();
			}
		}
	}
	
	private static final double JOYSTICK_DEADBAND = 0.05;
	
	private static final int TOP_WINDOW_SIZE = 1200;
	private static final int BOTTOM_WINDOW_SIZE = 2350;
	private static final int START_TOP_WINDOW = 2500;
	private static final int START_BOTTOM_WINDOW = 2350;
	
	private int accumulatedError, prevError, error;
	private double prevPidOut;
	
	private int setpoint;
	private boolean isAtAutoSetpoint;
	
	private boolean buttonPressed;
	private int buttonSelected;
	
	private ElevatorThread elevThread;
	
	private SetpointMapWrapper setpoints;
	
	private DriverStation ds;
	
	private Talon rightMotor, leftMotor;
	private Joystick elevStick;
	private Encoder enc;
	private DigitalInput topLS, botLS;
	
	/**
	 * Crates the elevator thread and starts it
	 * @param rightMotor one of the motors to move the elevator
	 * @param leftMotor one of the motors to move the elevator
	 * @param elevStick the joystick to control the elevator
	 * @param enc the encoder to keep track of the location for elevator
	 * @param topLS the top limit switch to stop the elevator from moving off the track
	 * @param botLS the bottom limit switch to stop the elevator from moving off the track
	 * @param setpoints the encoder setpoints for buttons on the joystick for automatic arm movement
	 */
	public ElevatorController(Talon rightMotor, Talon leftMotor, 
							Joystick elevStick, Encoder enc,
							DigitalInput topLS, DigitalInput botLS,
							SetpointMapWrapper setpoints) {
		
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.elevStick = elevStick;
		this.enc = enc;
		this.topLS = topLS;
		this.botLS = botLS;
		this.setpoints = setpoints;
		
		ds = DriverStation.getInstance();
		enc.setDistancePerPulse(1);
		
		elevThread = new ElevatorThread(this);
		elevThread.start();
	}
	
	/**
	 * Automatically moves the arm to a specific encoder setpoint
	 * @param setpoint the number of encoder revolutions to move
	 */
	public void autoMove(int setpoint) {
		error = setpoint - enc.get();
		
		if (Math.abs(error) <= 50) {
			isAtAutoSetpoint = true;
			return;
		}
		
		isAtAutoSetpoint = false;
		
		double out;
		
		if (error > 0) {
			out = pid(ElevatorPID.kP, ElevatorPID.kI, ElevatorPID.kD, error);
		} else {
			out = pid(ElevatorPID.kPd, ElevatorPID.kId, ElevatorPID.kDd, error);
		}
		
		if (botLS.get() && out < 0) {
			out = 0;
		}
		if (topLS.get() && out > 0) {
			out = 0;
		}
		
		setMotors(controlSpeed(out, enc.get()));
	}
	
	/**
	 * Sets the setpoint for automatic elevator movement
	 * @param setpoint
	 */
	public void setAutoSetpoint(int setpoint) {
		this.setpoint = setpoint;
	}
	
	/**
	 * Returns true if the elevator is at the auto setpoint, false otherwise
	 * @return true if the elevator is at the auto setpoint, false otherwise
	 */
	public boolean isAtAutoSetpoint() {
		return isAtAutoSetpoint;
	}
	
	/**
	 * Primary method to move the elevator for operator control
	 * and autonomous
	 */
	private void move() {
		if(botLS.get()) {
			enc.reset();
		}
		
		if(ds.isEnabled() && ds.isOperatorControl()) {
			
			// Check to see if the user
			// pressed a preset
			checkButtonStatus();
			
			if(Math.abs(elevStick.getY()) < JOYSTICK_DEADBAND) {
				if(buttonPressed) {
					autoMove(setpoints.getSetpoint(buttonSelected));
				} else {
					stopMotors();
				}
			} else {
				stopOperatorAutoMove();
				
				double input = elevStick.getY();
				setMotors(controlSpeed(input, enc.get()));
			}
		}
		else if(ds.isEnabled() && ds.isAutonomous()) {
			autoMove(setpoint);
		}
		else if(ds.isDisabled()) {
			stopOperatorAutoMove();
		}
	}
	
	/**
	 * Loop thru the buttons on the joystick to see
	 * if one is pressed, if one is, then save the button
	 * value and let the program know that the elevator
	 * will move automatically
	 */
	private synchronized void checkButtonStatus() {
		for(int i = 1; i < elevStick.getButtonCount(); i++) {
			if(elevStick.getRawButton(i) && setpoints.containsButton(i)) {
				buttonPressed = true;
				buttonSelected = i;
				
			}
		}
	}
	
	/**
	 * Calculates the PID for automatic elevator movement
	 * 
	 * @see ElevatorPID.java
	 * 
	 * @param p proportional
	 * @param i integral
	 * @param d derivative
	 * @param err the current elevator position vs the setpoint
	 * @return the new calculation for the elevator output
	 */
	private synchronized double pid(double p, double i, double d, int err) {
		double out = 0;
		if (Math.abs(err) <= 5) {
			accumulatedError = 0;
			return 0;
		} else if (Math.abs(prevError - err) > Math.abs(err + prevError)) {
			accumulatedError = 0;
		}

		accumulatedError += err;

		double P = p * err;
		double I = i * accumulatedError;
		double D = (err - prevError) * d;
		
		prevError = err;
		out = P + I + D;
		
		if (out > 1) {
			out = 1;
		} else if (out < -.5) {
			out = -.5;
		}
		
		if (out - prevPidOut > .1) {
			out = prevPidOut + .1;
		} else if (out - prevPidOut < -.1) {
			out = prevPidOut - .1;
		}
		
		prevPidOut = out;
		
		if(out < 0.1 && out > 0.0) {
			out = 0.1;
		}
		else if(out > -0.1 && out < 0.0) {
			out = -0.1;
		}
		
		return out;
	}
	
	/**
	 * Gives the two elevator motors outputs, up being between 0 and 1.0
	 * and down being between 0 and -1.0
	 * @param output the output value to the motors
	 */
	private void setMotors(double output) {
		if(topLS.get() && output > 0) {
			output = 0;
		}
		
		if(botLS.get() && output < 0) {
			output = 0;
		}
		
		rightMotor.set(output);
		leftMotor.set(output);
	}
	
	private void stopMotors() {
		rightMotor.set(0);
		leftMotor.set(0);
	}
	
	/**
	 * Decelerates the elevator speed as it approaches the top or
	 * bottom to prevent it from slamming harshly
	 * @param input the input from the joysticks or autoMove
	 * @param encCounts the current position of the elevator
	 * @return the new output for the motors
	 */
	private double controlSpeed(double input, int encCounts) {
		double output = input;
		
		if(input > 0 && isInTopWindow(encCounts)) {
			double penetration = (encCounts - START_TOP_WINDOW);
			output = input - (penetration*(input/(TOP_WINDOW_SIZE)));
			
			output = output < .35 ? .35 : output;
		}
		else if(input < 0 && isInBottomWindow(encCounts)) {
			double penetration = (BOTTOM_WINDOW_SIZE - encCounts);
			output = input - (penetration*(input / (BOTTOM_WINDOW_SIZE)));
			
			output = output > -.15 ? -.15 : output;
		}

		return output;
	}
	
	/**
	 * Used to determine if the elevator is getting close to the bottom
	 * @param encCounts the current elevator position
	 * @return true if close, false otherwise
	 */
	private boolean isInBottomWindow(int encCounts) {
		return encCounts <= START_BOTTOM_WINDOW;
	}
	
	/**
	 * Used to determine if the elevator is getting close to the top
	 * @param encCounts the current elevator postion
	 * @return true if close, false otherwise
	 */
	private boolean isInTopWindow(int encCounts) {
		return encCounts >= START_TOP_WINDOW;
	}
	
	/**
	 * Stops the elevator from automatically moving to the user
	 * selected setpoint
	 */
	private void stopOperatorAutoMove() {
		buttonPressed = false;
		buttonSelected = -1;
	}
	
	/**
	 * Gets the status of the bottom elevator limit switch
	 * @return true if being pressed, false if not
	 */
	public DigitalInput getBottomLS() {
		return botLS;
	}
	/**
	 * Gets the status of the top elevator limit switch
	 * @return true if being pressed, false if not
	 */
	public DigitalInput getTopLS() {
		return topLS;
	}
	
	/**
	 * Gets the encoder for the elevator
	 * @return the encoder for the elevator
	 */
	public Encoder getEncoder() {
		return enc;
	}
}

package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

public class ElevatorController {
	
	private class ElevatorThread extends Thread implements Runnable {
		
		private volatile ElevatorController ec;
		
		public ElevatorThread(ElevatorController ec) {
			this.ec = ec;
		}
		
		@Override
		public void run() {
			while(true) {
				ec.move();
			}
		}
	}
	
	private static final double JOYSTICK_DEADBAND = 0.05;
	
	private static final int TOP_WINDOW_SIZE = 1000;
	private static final int BOTTOM_WINDOW_SIZE = 2700;
	private static final int START_TOP_WINDOW = 2700;
	private static final int START_BOTTOM_WINDOW = 2700;
	
	private int accumulatedError, prevError, error;
	private double prevPidOut;
	
	private int setpoint;
	
	private boolean buttonPressed;
	private int buttonSelected;
	
	private ElevatorThread elevThread;
	
	private SetpointMapWrapper setpoints;
	
	private DriverStation ds;
	
	private Talon rightMotor, leftMotor;
	private Joystick elevStick;
	private Encoder enc;
	private DigitalInput topLS, botLS;
	
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
		
		elevThread = new ElevatorThread(this);
		elevThread.start();
	}
	
	public void autoMove(int setpoint) {
		error = setpoint - enc.get();
		
		if (Math.abs(error) <= 80) {
			return;
		}
		
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
	
	public void setAutoSetpoint(int setpoint) {
		this.setpoint = setpoint;
	}
	
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
					autoMove(setpoints.get(buttonSelected));
				} else {
					stopMotors();
				}
			} else {
				buttonPressed = false;
				buttonSelected = -1;
				
				double input = elevStick.getY();
				setMotors(controlSpeed(input, enc.get()));
			}
		}
		else if(ds.isEnabled() && ds.isAutonomous()) {
			autoMove(setpoint);
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
	
	private double controlSpeed(double input, int encCounts) {
		double output = input;
		
		if(input > 0 && isInTopWindow(encCounts)) {
			double penetration = (encCounts - START_TOP_WINDOW);
			output = input - (penetration*(input/TOP_WINDOW_SIZE));
			
			output = output < .30 ? .30 : output;
		}
		else if(input < 0 && isInBottomWindow(encCounts)) {
			double penetration = (BOTTOM_WINDOW_SIZE - encCounts);
			output = input - (penetration*(input / BOTTOM_WINDOW_SIZE));
			
			output = output > -.15 ? -.15 : output;
		}

		return output;
	}
	
	private boolean isInBottomWindow(int encCounts) {
		return encCounts <= START_BOTTOM_WINDOW;
	}
	
	private boolean isInTopWindow(int encCounts) {
		return encCounts >= START_TOP_WINDOW;
	}
}

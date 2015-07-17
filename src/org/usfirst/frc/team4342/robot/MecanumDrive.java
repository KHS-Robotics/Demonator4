package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;

public class MecanumDrive {
	
	private static final double JOYSTICK_DEADBAND = 0.1;
	private static final double kP = 0.02;
	
	private double forward;
	private double right;
	private double twist;
	private double temp;

	private double f_l;
	private double f_r;
	private double r_l;
	private double r_r;

	private double gyroAngle;
	private boolean enableGyro;
	private double initialAngle;

	private CANJaguar fl;
	private CANJaguar fr;
	private CANJaguar rl;
	private CANJaguar rr;

	private Joystick joystick;

	private Gyro gyro;

	public MecanumDrive(CANJaguar frontLeft, CANJaguar frontRight, CANJaguar rearLeft,
			CANJaguar rearRight, Joystick joystick, Gyro gyro, boolean enableGyro) {
		fl = frontLeft;
		fr = frontRight;
		rl = rearLeft;
		rr = rearRight;
		this.joystick = joystick;
		this.gyro = gyro;
		this.enableGyro = enableGyro;
	}

	/*
	 * field oriented control with tuning params for joystick inputs
	 * 
	 * use normalization to regulate all speeds of wheels to only be as fast as
	 * the fastest wheel SHOULD be going- input should match output otherwise
	 * slow input down until it does
	 * 
	 * kX, kY, kZ are for tuning inputs leave them as 1 for now
	 */
	public void drive() {
		double kX = Math.abs(joystick.getX()) < JOYSTICK_DEADBAND ? 0 : joystick.getX();
		double kY = Math.abs(joystick.getY()) < JOYSTICK_DEADBAND ? 0 : joystick.getY();
		double kZ = Math.abs(joystick.getZ()) < JOYSTICK_DEADBAND ? 0 : joystick.getZ();

		forward = -kY;
		right = kX;
		twist = kZ;
		
		if(enableGyro) {
			gyroAngle = gyro.getAngle();
			
			//gyroAngle += 180;
			gyroAngle %= 360.0;
			
			if (gyroAngle < 0) {
				gyroAngle += 360;
			}
		} else {
			gyroAngle = 0.0;
		}

		temp = forward * Math.cos(gyroAngle * (Math.PI / 180)) + right
				* Math.sin(gyroAngle * (Math.PI / 180));
		right = -forward * Math.sin(gyroAngle * (Math.PI / 180)) + right
				* Math.cos(gyroAngle * (Math.PI / 180));
		forward = temp;

		f_l = forward + twist + right;
		f_r = forward - twist - right;
		r_l = forward + twist - right;
		r_r = forward - twist + right;

		double max = Math.abs(f_l);
		if (Math.abs(f_r) > max) {
			max = Math.abs(f_r);
		}
		if (Math.abs(r_l) > max) {
			max = Math.abs(r_l);
		}
		if (Math.abs(r_r) > max) {
			max = Math.abs(r_r);
		}
		if (max > 1) {
			f_l /= max;
			f_r /= max;
			r_l /= max;
			r_r /= max;
		}

		fl.set(f_l * 470);
		fr.set(f_r * 470);
		rl.set(r_l * 470);
		rr.set(r_r * 470);

	}

	public void autoDrive(double x, double y, double gyroAngle) {
		forward = y;
		right = x;

		if (x == 0.0 && y == 0.0) {
			fl.set(0);
			fr.set(0);
			rl.set(0);
			rr.set(0);
			return;
		}
		
		// gyroAngle += 180;
		gyroAngle %= 360;
		
		double error = initialAngle - gyroAngle;
		
		if (error >= 180) {
			error -= 360;
		} else if (error <= -180) {
			error += 360;
		}
		
		if (error >= -0.5 && error <= 0.5) {
			twist = 0.0;
		} else {
			twist = error * kP;
		}
		
		gyroAngle %= 360.0;
		if (gyroAngle < 0) {
			gyroAngle += 360;
		}
		
		temp = forward * Math.cos(gyroAngle * (Math.PI / 180)) + right
				* Math.sin(gyroAngle * (Math.PI / 180));
		right = -forward * Math.sin(gyroAngle * (Math.PI / 180)) + right
				* Math.cos(gyroAngle * (Math.PI / 180));
		forward = temp;

		f_l = forward + twist + right;
		f_r = forward - twist - right;
		r_l = forward + twist - right;
		r_r = forward - twist + right;

		double max = Math.abs(f_l);
		
		if (Math.abs(f_r) > max) {
			max = Math.abs(f_r);
		}
		
		if (Math.abs(r_l) > max) {
			max = Math.abs(r_l);
		}
		
		if (Math.abs(r_r) > max) {
			max = Math.abs(r_r);
		}
		
		if (max > 1) {
			f_l /= max;
			f_r /= max;
			r_l /= max;
			r_r /= max;
		}

		fl.set(f_l * 470);
		fr.set(f_r * 470);
		rl.set(r_l * 470);
		rr.set(r_r * 470);
	}
	
	/**
	 * Enabled field oriented drive
	 */
	public void enableFod() {
		enableGyro = true;
	}
	
	
	// TODO: change to percent v bus mode if the encoders fail to give a value
	/**
	 * Disables field oriented drive and goes to robot oriented
	 */
	public void disableFod() {
		enableGyro = false;
	}
	
	public boolean isFodEnabled() {
		return enableGyro;
	}
	
	/**
	 * Sets the initial angle for the auto drive function
	 * so the robot will go straight properly
	 * 
	 * @param angle the initial angle to set
	 */
	public void setInitalAngle(double angle) {
		initialAngle = angle;
	}
}

package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive {
	private double forward;
	private double right;
	private double twist;
	private double temp;

	private double f_l;
	private double f_r;
	private double r_l;
	private double r_r;
	
	private CANJaguar fl;
	private CANJaguar fr;
	private CANJaguar rl;
	private CANJaguar rr;

	private Joystick j;

	private Gyro gyro;

	public Drive(CANJaguar frontLeft, CANJaguar frontRight, CANJaguar rearLeft,
			CANJaguar rearRight, Joystick j, Gyro gyro) {
		fl = frontLeft;
		fr = frontRight;
		rl = rearLeft;
		rr = rearRight;
		this.j = j;
		this.gyro = gyro;
	}

	public void fieldOriented() {
		//this.fieldOriented(j.getX(), j.getY(), j.getZ());
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
	public void fieldOriented(double kX, double kY, double kZ, double gyroAngle) {

		kX = Math.abs(kX) < RobotConstants.JOYSTICK_DEADBAND ? 0 : kX;
		kY = Math.abs(kY) < RobotConstants.JOYSTICK_DEADBAND ? 0 : kY;
		kZ = Math.abs(kZ) < RobotConstants.JOYSTICK_DEADBAND ? 0 : kZ;

		forward = kY;
		right = kX;
		twist = kZ;
		
		gyroAngle+=180;
		
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
	public void autoMove(double x, double y, double gyroAngle,
			double initialAngle) {
		forward = y;
		right = x;

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
			twist = error * SmartDashboard.getNumber("angleP");
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

		if (x == 0.0 && y == 0.0) {
			stopMotors();
			return;
		}
		fl.set(f_l * 470);
		fr.set(f_r * 470);
		rl.set(r_l * 470);
		rr.set(r_r * 470);
	}
	public void turnInPlace(double speed, double degrees) {
		if(gyro.getAngle()>=degrees) {
			stopMotors();
			return;
		}
		fl.set(-speed*470);
		fr.set(speed*470);
		rl.set(-speed*470);
		rr.set(speed*470);
	}
	
	public void stopMotors()
	{
		fl.set(0);
		fr.set(0);
		rl.set(0);
		rr.set(0);
	}
	
	public CANJaguar getFrontRight() {
		return fr;
	}
	
	public CANJaguar getFrontLeft() {
		return fl;
	}
	
	public CANJaguar getRearRight() {
		return rr;
	}
	
	public CANJaguar getRearLeft() {
		return rl;
	}
}

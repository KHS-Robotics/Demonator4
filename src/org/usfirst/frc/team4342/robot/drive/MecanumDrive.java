package org.usfirst.frc.team4342.robot.drive;

import org.usfirst.frc.team4342.robot.components.repository.RobotRepository;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is for the drive train of the robot. The drive train is composed
 * of four motors, using mecanum wheels.
 * 
 * About mecanum wheels: https://en.wikipedia.org/wiki/Mecanum_wheel
 * How mecanum wheels work in 30 seconds: https://www.youtube.com/watch?v=o-j9TReI1aQ
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class MecanumDrive 
{	
	private static final double JOYSTICK_DEADBAND = 0.1;
	
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

	/**
	 * Main constructor for the class
	 * @param frontLeft the front left motor
	 * @param frontRight the front right motor
	 * @param rearLeft the rear left motor
	 * @param rearRight the rear right motor
	 * @param joystick the joystick to get inputs from
	 * @param gyro the gyro to ensure the robot is driving straight
	 * @param enableGyro true for field oriented, false for robot oriented
	 */
	public MecanumDrive() 
	{
		fl = RobotRepository.FrontLeft;
		fr = RobotRepository.FrontRight;
		rl = RobotRepository.RearLeft;
		rr = RobotRepository.RearRight;
		this.joystick = RobotRepository.DriveStick;
		this.gyro = RobotRepository.PivotGyro;
	}
	
	/**
	 * Moved the robot based on the joystick inputs
	 */
	public void drive() 
	{
		double kX = Math.abs(joystick.getX()) > JOYSTICK_DEADBAND ? joystick.getX() : 0.0;
		double kY = Math.abs(joystick.getY()) > JOYSTICK_DEADBAND ? joystick.getY() : 0.0;
		double kZ = Math.abs(joystick.getZ()) > JOYSTICK_DEADBAND ? joystick.getZ() : 0.0;

		forward = -kY;
		right = Math.pow(kX,1.4);
		twist = Math.pow(kZ,2.6);
		
		if(enableGyro) 
		{
			gyroAngle = gyro.getAngle();
			
			gyroAngle %= 360.0;
			
			if (gyroAngle < 0)
				gyroAngle += 360;
		} 
		else 
		{
			gyroAngle = 0.0;
		}

		temp = forward * Math.cos(gyroAngle * (Math.PI / 180)) + right
				* Math.sin(gyroAngle * (Math.PI / 180));
		
		right = -forward * Math.sin(gyroAngle * (Math.PI / 180)) + right
				* Math.cos(gyroAngle * (Math.PI / 180));
		
		forward = temp;
		
		// Used for sensitivity, throttles speed from
		// x to range 0.0x to 2.0x
		double divider = -joystick.getThrottle();
		divider += 1.0;

		f_l = (forward + twist + right) * divider;
		f_r = (forward - twist - right) * divider;
		r_l = (forward + twist - right) * divider;
		r_r = (forward - twist + right) * divider;

		double max = Math.abs(f_l);
		
		if (Math.abs(f_r) > max)
			max = Math.abs(f_r);
		
		if (Math.abs(r_l) > max)
			max = Math.abs(r_l);
		
		if (Math.abs(r_r) > max)
			max = Math.abs(r_r);
		
		if (max > 1) 
		{
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
	 * Automatically moves the robot for autonomous
	 * @param x the X value to move
	 * @param y the Y value to move
	 * @param gyroAngle the current angle of the robot
	 */
	public void autoDrive(double x, double y, double gyroAngle) 
	{
		if(DriverStation.getInstance().isEnabled() && DriverStation.getInstance().isAutonomous()) 
		{
			forward = y;
			right = x;
	
			if (x == 0.0 && y == 0.0) 
			{
				stopMotors();
				
				return;
			}
			
			// gyroAngle += 180;
			gyroAngle %= 360;
			
			double error = initialAngle - gyroAngle;
			
			if (error >= 180) 
			{
				error -= 360;
			} else if (error <= -180) 
			{
				error += 360;
			}
			
			if (error >= -0.5 && error <= 0.5) 
			{
				twist = 0.0;
			} 
			else 
			{
				twist = error * DrivePID.Autonomous.kP;
			}
			
			gyroAngle %= 360.0;
			if (gyroAngle < 0)
				gyroAngle += 360;
			
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
			
			if (Math.abs(f_r) > max)
				max = Math.abs(f_r);
			
			if (Math.abs(r_l) > max)
				max = Math.abs(r_l);
			
			if (Math.abs(r_r) > max)
				max = Math.abs(r_r);
			
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
	}
	
	/*
	* Converts the joystick x and y values to a single magnitude
	*/
	public double resolveVectorMagnitude(double x, double y) 
	{
		return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
	}
	
	/*
	* Resolves the angle of the vector given the x and y components.
	*/
	public double resolveVectorAngle(double x, double y) 
	{
		return (Math.atan(y/x)*(180/Math.PI));
	}
	
	/*
	* Give it a vector, get a driving robot
	* direction is in degrees
	* magnitude is from -1 to 1, is a velocity in encoder counts/second/470
	*/
	public void vectorDriveTeleop(double magnitude, double direction, double twist) 
	{	
		//GOES IN METHOD CALL (inside resolveVectorMagnitude())
		//double z=Math.pow(joystick.getZ(),2.6); for twist
		//double y=joystick.getY(); //no modification whatsoever
		//double x=Math.pow(joystick.getX(),1.4);
		
		direction*=Math.PI/180;
		direction+=Math.PI/4;
		
		//field oriented angle, measured from clockwise from +y
		if(enableGyro) 
		{
			gyroAngle = gyro.getAngle();
			
			gyroAngle %= 360.0;
			
			if (gyroAngle < 0) {
				gyroAngle += 360;
			}
		} 
		else 
		{
			gyroAngle = 0.0;
		}
		gyroAngle*=Math.PI/180;
		
		// Used for sensitivity, throttles speed from
		// x to range 0.0x to 2.0x
		double divider = -joystick.getThrottle();
		divider += 1.0;

		f_l = ((Math.sin(direction)*magnitude)-twist); //* divider;
		f_r = ((Math.cos(direction)*magnitude)+twist); //* divider;
		r_l = ((Math.cos(direction)*magnitude)-twist); //* divider;
		r_r = ((Math.sin(direction)*magnitude)+twist); //* divider;

		double max = Math.abs(f_l);
		
		if (Math.abs(f_r) > max)
			max = Math.abs(f_r);
		
		if (Math.abs(r_l) > max)
			max = Math.abs(r_l);
		
		if (Math.abs(r_r) > max)
			max = Math.abs(r_r);
		
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
	public void enableFod()
	{
		enableGyro = true;
	}
	
	/**
	 * Disables field oriented drive and goes to robot oriented
	 */
	public void disableFod() 
	{
		enableGyro = false;
	}
	
	public boolean isFodEnabled() 
	{
		return enableGyro;
	}
	
	/**
	 * Sets the initial angle for the auto drive function
	 * so the robot will go straight properly
	 * 
	 * @param angle the initial angle to set
	 */
	public void setInitalAngle(double angle) 
	{
		initialAngle = angle;
	}
	
	/**
	 * Stops all drive motors
	 */
	public void stopMotors() 
	{
		fr.set(0.0);
		fl.set(0.0);
		rr.set(0.0);
		rl.set(0.0);
	}
	
	/**
	 * Gets the front right CANJaguar for the drive train
	 * @return the front right CANJaguar of the drive train
	 */
	public CANJaguar getFrontRight() 
	{
		return fr;
	}
	
	/**
	 * Gets the front left CANJaguar for the drive train
	 * @return the front left CANJaguar of the drive train
	 */
	public CANJaguar getFrontLeft() 
	{
		return fl;
	}
	
	/**
	 * Gets the rear right CANJaguar for the drive train
	 * @return the rear right CANJaguar of the drive train
	 */
	public CANJaguar getRearRight() 
	{
		return rr;
	}
	
	/**
	 * Gets the rear left CANJaguar for the drive train
	 * @return the rear left CANJaguar of the drive train
	 */
	public CANJaguar getRearLeft() 
	{
		return rl;
	}
}

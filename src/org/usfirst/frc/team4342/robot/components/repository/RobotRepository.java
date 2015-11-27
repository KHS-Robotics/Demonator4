package org.usfirst.frc.team4342.robot.components.repository;

import org.usfirst.frc.team4342.robot.elevator.setpoints.Setpoint;
import org.usfirst.frc.team4342.robot.elevator.setpoints.SetpointMapWrapper;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * This is a central repository for all components of the robot.
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public class RobotRepository 
{
	public static final Joystick DriveStick = new Joystick(0);
	public static final Joystick ElevatorStick = new Joystick(1);
	
	public static final CANJaguar FrontRight = new CANJaguar(22);
	public static final CANJaguar FrontLeft = new CANJaguar(21);
	public static final CANJaguar RearRight = new CANJaguar(23);
	public static final CANJaguar RearLeft = new CANJaguar(20);
	
	public static final Talon RightMotor = new Talon(0);
	public static final Talon LeftMotor = new Talon(1);
	
	public static final Encoder ElevatorEncoder = new Encoder(8, 9, false, EncodingType.k1X);
	
	public static final DigitalInput TopLimitSwitch = new DigitalInput(7);
	public static final DigitalInput BottomLimitSwitch = new DigitalInput(4);
	public static final DigitalInput RightPhotoSensor = new DigitalInput(0);
	public static final DigitalInput LeftPhotoSensor = new DigitalInput(1);
	
	public static final SetpointMapWrapper ElevatorSetpoints = new SetpointMapWrapper(
			new Setpoint[] {
				new Setpoint(2, 0),
				new Setpoint(4, 325),
				new Setpoint(3, 750),
				new Setpoint(5, 1475),
				new Setpoint(8, 1200),
				new Setpoint(9, 2800)	
			}
	);
	
	public static final Ultrasonic Ultra = new Ultrasonic(3, 4);
	
	public static final Gyro PivotGyro = new Gyro(0);
	public static final Gyro PitchGyro = new Gyro(1);
	
	static 
	{
		Ultra.setAutomaticMode(true);
		
		PivotGyro.setSensitivity(0.007);
		PitchGyro.setSensitivity(0.007);
	}
}

package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoRoutines 
{
	private Drive fod;
	private ElevatorControl elevC;
	private Gyro gyro;
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private DigitalInput rightPhotoSensor, leftPhotoSensor;
	private Ultrasonic ultra;
	
	private int autoStep, savingPvtBrian;
	private double offset, initAngle, distanceDer, distanceError;
	
	public AutoRoutines(Drive fod, Gyro gyro, double initAngle, DigitalInput rightPhotoSensor, 
						DigitalInput leftPhotoSensor, ElevatorControl elevC, Ultrasonic ultra)
	{
		this.fod = fod;
		this.elevC = elevC;
		this.frontRight = fod.getFrontRight();
		this.frontLeft = fod.getFrontLeft();
		this.rearRight = fod.getRearRight();
		this.rearLeft = fod.getRearLeft();
		
		this.rightPhotoSensor = rightPhotoSensor;
		this.leftPhotoSensor = leftPhotoSensor;
		
		this.ultra = ultra;
		
		this.gyro = gyro;
		this.initAngle = initAngle;
	}
	
	public void autoRoutinePickUpThreeTotes(long numLoops)
	{
		if(autoStep == 0)
		{
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(moveDist(2.5, 0.11))
			{
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 2)
		{
			
			if(wait(savingPvtBrian+10))
			{
				elevC.setAutoSetpoint((150));
				autoStep++;
			}
		}
		else if(autoStep == 3)
		{
			
			if(elevC.isDone())
			{
				elevC.setAutoSetpoint(2000); //TODO: find min height to clear totes- use in place of 2000 every time it appears
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep==4)
		{
			
			if(moveBackDist(1, 0.2))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
		else if(autoStep == 5)
		{
			if(autoStrafe(25))
			{
				autoStep++;
			}
		}
		else if(autoStep == 6)
		{
			if(autoForward())
			{
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				elevC.setAutoSetpoint(0);
				autoStep++;
			}
		}
		else if(autoStep == 7)
		{
			if(elevC.isDone())
			{
				elevC.setAutoSetpoint(100);
				autoStep++;
			}
		}
		else if(autoStep == 8)
		{
			if(elevC.isDone())
			{	
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				elevC.setAutoSetpoint(2000);
				autoStep++;
			}
		}
		else if(autoStep == 9)
		{
			if(elevC.isDone())
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 10)
		{
			if(moveBackDist(1, 0.11))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
		else if(autoStep == 11)
		{
			if(autoStrafe(25))
			{
				autoStep++;
			}
		}
		else if(autoStep == 12)
		{
			if(autoForward())
			{
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				elevC.setAutoSetpoint(0);
				autoStep++;
			}
		}
		else if(autoStep == 13)
		{
			if(elevC.isDone())
			{
				elevC.setAutoSetpoint(100);
				autoStep++;
			}
		}
		else if(autoStep == 14)
		{	
			if(elevC.isDone())
			{
				elevC.setAutoSetpoint(2000);
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 15)
		{
			if(moveBackDist(3, 0.07))
			{
				autoStep++;
			}
		}
		else if(autoStep == 16)
		{
			if(elevC.isDone())
			{
				elevC.setAutoSetpoint(2000);
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 17)
		{
			if(moveBackDist(4, 0.11))
			{
				elevC.setAutoSetpoint(-50);
				autoStep++;
			}
		}
		else if(autoStep == 18)
		{
			
			if(elevC.isDone())
			{
				autoStep++;
			}
		}
		else if(autoStep == 19)
		{
			moveBackDist(4, 0.5);
		}
		
	}
	
	public void autoRoutinePickUpOneTote(long numLoops)
	{
		if(autoStep == 0)
		{
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(moveDist(2.5, 0.11))
			{
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 2)
		{	
			if(wait(savingPvtBrian+10))
			{
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				elevC.setAutoSetpoint(2000);
				autoStep++;
			}
		}
		else if(autoStep == 3)
		{
			if(elevC.isDone())
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep==4)
		{
			if(moveBackDist(3, 0.2))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
	}
	
	public void autoRoutinePickupOneContainer(long numLoops)
	{
		if(autoStep == 0)
		{
			elevC.setAutoSetpoint(697);
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(elevC.isDone())
			{
				autoStep++;
			}
		}
		else if(autoStep == 2)
		{
			if(autoForward())
			{
				fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 3)
		{
			if(wait(savingPvtBrian+10))
			{
				elevC.setAutoSetpoint(2250);
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			if(elevC.isDone())
			{
				elevC.setAutoSetpoint(2250);
				offset = 0;
				autoStep++;
			}
		}
		else if(autoStep == 5)
		{
			if(moveBackDist(17, 0.1))
			{
				elevC.setAutoSetpoint(2250);
				initAngle+=90;
				autoStep++;
			}
		}
		else if(autoStep == 6)
		{
			fod.autoMove(0.01, 0.0, gyro.getAngle(), initAngle);
		}
	}
	
	private void snapshotEncoderValues()
	{
		offset = Math.abs(frontRight.getPosition()) + Math.abs(frontLeft.getPosition())
					+ Math.abs(rearRight.getPosition()) + Math.abs(rearLeft.getPosition());
	}
	
	private double getEncoderValues()
	{
		return Math.abs(frontRight.getPosition()) + Math.abs(frontLeft.getPosition()) 
				+ Math.abs(rearRight.getPosition()) + Math.abs(rearLeft.getPosition());
	}
	private double totals()
	{
		return (frontRight.getPosition()) + (frontLeft.getPosition()) 
				+ (rearRight.getPosition()) + (rearRight.getPosition());
	}
	
	private boolean madeContact;
	private boolean autoStrafe(double encDistance)
	{
		//TODO: add strafing capabilities for both ways
		if(rightPhotoSensor.get() && leftPhotoSensor.get())
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			return true;
		}
		else if(!leftPhotoSensor.get() && rightPhotoSensor.get()||madeContact)
		{
			//autoStep = 2;
			madeContact = true;
			fod.autoMove(0.04, 0.0, gyro.getAngle(), initAngle);
		}
		else if((getEncoderValues() - offset) > (encDistance - 4) && !madeContact)
		{
			fod.autoMove(0.05, 0, gyro.getAngle(), initAngle);
		}
		else if((getEncoderValues() - offset) > (encDistance - 6) && !madeContact)
		{
			fod.autoMove(0.12, 0, gyro.getAngle(), initAngle);
		}
		else
		{
			fod.autoMove(0.25, 0.0, gyro.getAngle(), initAngle);
		}
		
		return false;
	}
	
	private boolean autoForward()
	{
		fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
		double P = SmartDashboard.getNumber("howCloseP");
		double close = SmartDashboard.getNumber("U-Close");
		double far = SmartDashboard.getNumber("U-Far");
		double dist = ultra.getRangeInches();
		distanceDer = dist - distanceError;
		distanceError = dist - ((close + far) / 2);
		
		if((dist > close && dist < far))
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			//elevC.setAutoSetpoint(0);
			return true;
		}
		else if(dist < close)
		{
			fod.autoMove(0.0, (distanceError*P), gyro.getAngle(), initAngle);
		}
		else if(dist > far)
		{
			fod.autoMove(0.0,(distanceError*P), gyro.getAngle(), initAngle);
		}
		SmartDashboard.putNumber("distP", distanceError*P);
		SmartDashboard.putNumber("distError", distanceError);
		SmartDashboard.putNumber("distDerr", distanceDer);
		return false;
	}
	private boolean moveDist(double revolutions, double speed)
	{
		
		if(totals()-offset>=revolutions)
		{
			fod.autoMove(0.0,0.0,gyro.getAngle(),initAngle);
			return true;
		}
		else
		{
			fod.autoMove(0.0,speed,gyro.getAngle(),initAngle);
		}
		return false;
	}
	private boolean moveBackDist(double revolutions, double speed)
	{
		
		if(totals() - offset <= -revolutions)
		{
			fod.autoMove(0.0,0.0,gyro.getAngle(),initAngle);
			return true;
		}
		else
		{
			fod.autoMove(0.0,-speed,gyro.getAngle(),initAngle);
		}
		return false;
	}
	private double p = 0.25, i = 0.0, d = 0.02, pd = 0.1, id = 0.0, dd = 0.01;
	

	
	private boolean wait(int waitLoops)
	{
		if(Robot.getNumLoops() >= waitLoops)
		{
			return true;
		}
		return false;
	}
}

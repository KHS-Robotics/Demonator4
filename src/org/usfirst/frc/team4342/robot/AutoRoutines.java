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
	private Gyro gyro;
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private DigitalInput rightPhotoSensor, leftPhotoSensor, botElevLS, topElevLS;
	private Talon rightElevatorMotor, leftElevatorMotor;
	private Encoder elevatorEncoder;
	private Ultrasonic ultra;
	
	private int autoStep, savingPvtBrian;
	private double offset, initAngle, distanceDer, distanceError;
	
	public AutoRoutines(Drive fod, Gyro gyro, double initAngle, DigitalInput rightPhotoSensor, DigitalInput leftPhotoSensor,
						DigitalInput botElevLS, DigitalInput topElevLS, Talon rightElevatorMotor, Talon leftElevatorMotor,
						Encoder elevatorEncoder, Ultrasonic ultra)
	{
		this.fod = fod;
		
		this.frontRight = fod.getFrontRight();
		this.frontLeft = fod.getFrontLeft();
		this.rearRight = fod.getRearRight();
		this.rearLeft = fod.getRearLeft();
		
		this.rightPhotoSensor = rightPhotoSensor;
		this.leftPhotoSensor = leftPhotoSensor;
		
		this.botElevLS = botElevLS;
		this.topElevLS = topElevLS;
		
		this.rightElevatorMotor = rightElevatorMotor;
		this.leftElevatorMotor = leftElevatorMotor;
		
		this.elevatorEncoder = elevatorEncoder;
		
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
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 2)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(wait(savingPvtBrian+10))
			{
				autoStep++;
			}
		}
		else if(autoStep == 3)
		{
			if(autoMcgriddle(100))
			{
				
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep==5)
		{
			autoMcgriddle(2000);
			if(moveBackDist(1, 0.2))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
		else if(autoStep == 6)
		{
			autoMcgriddle(2000);
			if(autoStrafe(25))
			{
				autoStep++;
			}
		}
		else if(autoStep == 7)
		{
			autoMcgriddle(2000);
			if(autoForward())
			{
				autoStep++;
			}
		}
		else if(autoStep == 8)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(0))
			{
				autoStep++;
			}
		}
		else if(autoStep == 9)
		{
			if(autoMcgriddle(100))
			{
				autoStep++;
			}
		}
		else if(autoStep == 10)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 11)
		{
			autoMcgriddle(2000);
			if(moveBackDist(1, 0.11))
			{
				snapshotEncoderValues();
				madeContact=false;
				autoStep++;
			}
		}
		else if(autoStep == 12)
		{
			autoMcgriddle(2000);
			if(autoStrafe(25))
			{
				autoStep++;
			}
		}
		else if(autoStep == 13)
		{
			autoMcgriddle(2000);
			if(autoForward())
			{
				autoStep++;
			}
		}
		else if(autoStep == 14)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(0))
			{
				autoStep++;
			}
		}
		else if(autoStep == 15)
		{
			if(autoMcgriddle(100))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 16)
		{
			autoMcgriddle(2000);
			if(moveBackDist(3, 0.07))
			{
				autoStep++;
			}
		}
		else if(autoStep == 17)
		{
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep == 18)
		{
			autoMcgriddle(2000);
			if(moveBackDist(4, 0.11))
			{
				autoStep++;
			}
		}
		else if(autoStep ==19)
		{
			if(autoMcgriddle(-50))
			{
				autoStep++;
			}
		}
		else if(autoStep==20){
			moveBackDist(4,0.5);
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
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 2)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(wait(savingPvtBrian+10))
			{
				autoStep++;
			}
		}
		else if(autoStep == 3)
		{
			if(autoMcgriddle(100))
			{
				
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(autoMcgriddle(2000))
			{
				offset = totals();
				autoStep++;
			}
		}
		else if(autoStep==5)
		{
			autoMcgriddle(2000);
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
			snapshotEncoderValues();
			autoStep++;
		}
		else if(autoStep == 1)
		{
			if(autoMcgriddle(697))
			{
				autoStep++;
			}
		}
		else if(autoStep == 2)
		{
			autoMcgriddle(697);
			if(autoForward())
			{
				autoStep++;
				savingPvtBrian = (int)numLoops;
			}
		}
		else if(autoStep == 3)
		{
			fod.autoMove(0.0, 0.0, gyro.getAngle(), initAngle);
			if(wait(savingPvtBrian+10))
			{
				autoStep++;
			}
		}
		else if(autoStep == 4)
		{
			if(autoMcgriddle(2250))
			{
				offset = 0;
				autoStep++;
			}
		}
		else if(autoStep == 5)
		{
			autoMcgriddle(2250);
			if(moveBackDist(17, 0.1))
			{
				initAngle+=90;
				autoStep++;
			}
		}
		else if(autoStep == 6)
		{
			autoMcgriddle(2250);
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
	
	private int autoBand = 10, error;
	private double p = 0.25, i = 0.0, d = 0.02, pd = 0.1, id = 0.0, dd = 0.01;
	
	private boolean autoMcgriddle(int setpoint) {
		error = setpoint - elevatorEncoder.get();
		double out=0;
		if (Math.abs(error) <= autoBand) {
			rightElevatorMotor.set(out);
			leftElevatorMotor.set(out);
			return true;
		}
		if (error > 0) {
			out = 0.7;
			//out = pid(p, i, d, error);
		} else {
			out =-0.1;
			//out = pid(pd, id, dd, error);
		}
		if (botElevLS.get() && out < 0) {
			out = 0;
		}
		if(topElevLS.get() && out > 0)
		{
			out = 0;
		}
		rightElevatorMotor.set(out);
		leftElevatorMotor.set(out);
		return false;
//		if(elevatorEncoder.get()<)
		
	}

	private int accumulated = 0, perr;
	long howLong = System.currentTimeMillis();
	private double pPID;

	private double pid(double p, double i, double d, int err) {
		double out = 0;
		if (Math.abs(err) <= 5) {
			accumulated = 0;
			return 0;
		} else if (Math.abs(perr - err) > Math.abs(err + perr)) {
			accumulated = 0;
		}
		accumulated += err;

		double P = p * err;
		double I = i * accumulated;
		double D = (err - perr) * d;
		perr = err;
		out = P + I + D;
		if (out > 1) {
			out = 1;
		} else if (out < -.5) {
			out = -.5;
		}
		if (out - pPID > .1) {
			out = pPID + .1;
		} else if (out - pPID < -.1) {
			out = pPID - .1;
		}
		pPID = out;
		return out;

	}
	
	private boolean wait(int waitLoops)
	{
		if(Robot.getNumLoops() >= waitLoops)
		{
			return true;
		}
		return false;
	}
}

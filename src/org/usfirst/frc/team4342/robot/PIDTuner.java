package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDTuner 
{
	private CANJaguar frontRight, frontLeft, rearRight, rearLeft;
	private ElevatorControl elevC;
	
	private static final String jagP = "Jag-P",
								jagI = "Jag-I",
								jagD = "Jag-D",
								elevPUp = "Elev-P Up",
								elevIUp = "Elev-I Up",
								elevDUp = "Elev-D Up",
								elevPDown = "Elev-P Down",
								elevIDown = "Elev-I Down",
								elevDDown = "Elev-D Down";
	
	public PIDTuner(CANJaguar frontRight, CANJaguar frontLeft, CANJaguar rearRight, CANJaguar rearLeft, ElevatorControl elevC,
					double p, double i, double d, double pUp, double iUp, double dUp, double pDown, double iDown, double dDown) 
	{
		this.frontRight = frontRight;
		this.frontLeft = frontLeft;
		this.rearRight = rearRight;
		this.rearLeft = rearLeft;
		
		this.elevC = elevC;
		
		SmartDashboard.putNumber(jagP, p);
		SmartDashboard.putNumber(jagI, i);
		SmartDashboard.putNumber(jagD, d);
		
		SmartDashboard.putNumber(elevPUp, pUp);
		SmartDashboard.putNumber(elevIUp, iUp);
		SmartDashboard.putNumber(elevDUp, dUp);
		
		SmartDashboard.putNumber(elevPDown, pDown);
		SmartDashboard.putNumber(elevIDown, iDown);
		SmartDashboard.putNumber(elevDDown, dDown);
		
		rearRight.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
		rearRight.enableControl();
		rearLeft.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
		rearLeft.enableControl();
		frontRight.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
		frontRight.enableControl();
		frontLeft.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
		frontLeft.enableControl();
	}
	
	public void speedTest(int numLoops, double setpoint, double p, double i, double d)
	{
		if(numLoops % 10 == 0)
		{
			double tempP = SmartDashboard.getNumber(jagP);
			double tempI = SmartDashboard.getNumber(jagI);
			double tempD = SmartDashboard.getNumber(jagD);
			
			if(tempP != p || tempI != i || tempD != d)
			{
				p = tempP;
				i = tempI;
				d = tempD;
				
				rearRight.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
				rearRight.enableControl();
				rearLeft.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
				rearLeft.enableControl();
				frontRight.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
				frontRight.enableControl();
				frontLeft.setSpeedMode(CANJaguar.kQuadEncoder, 512, p ,i,d);
				frontLeft.enableControl();
			}
		}
		
		frontLeft.set(-setpoint);
		frontRight.set(setpoint);
		rearRight.set(-setpoint);
		rearLeft.set(setpoint);
	}
	
	public void elevatorSpeedTest(int numLoops, double setpoint, double pUp, double iUp, double dUp, double pDown, double iDown, double dDown)
	{
		if(numLoops % 10 == 0)
		{
			double tempPup = SmartDashboard.getNumber(elevPUp) / 100.0;
			double tempIup = SmartDashboard.getNumber(elevIUp) / 1000.0;
			double tempDup = SmartDashboard.getNumber(elevDUp) / 100.0;
			double tempPdown = SmartDashboard.getNumber(elevPDown) / 100.0;
			double tempIdown = SmartDashboard.getNumber(elevIDown) / 1000.0;
			double tempDdown = SmartDashboard.getNumber(elevDDown) / 100.0;
			
			if(tempPup != pUp || tempIup != iUp || tempDup != dUp)
			{
				pUp = tempPup;
				iUp = tempIup;
				dUp = tempDup;
				
				elevC.setP(pUp);
				elevC.setI(iUp);
				elevC.setD(dUp);
			}
			if(tempPup != pDown || tempIup != iDown || tempDup != dDown)
			{
				pDown = tempPdown;
				iDown = tempIdown;
				dDown = tempDdown;
				elevC.setPd(pDown);
				elevC.setId(iDown);
				elevC.setDd(dDown);
			}
		}
	}
}

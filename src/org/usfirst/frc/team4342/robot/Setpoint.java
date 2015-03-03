package org.usfirst.frc.team4342.robot;

public class Setpoint 
{
	private int Button;
	private int[] setpoint;
	
	public Setpoint(int numb,int[] setpoint)
	{
		Button=numb;
		this.setpoint=setpoint;
	}
	public int getButton() {
		return Button;
	}
	public int[] getSetpoint()
	{
		return setpoint;
	}
	
}

package org.usfirst.frc.team4342.robot;

//
//import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Joystick.ButtonType;
//import edu.wpi.first.wpilibj.SpeedController;
//
//public class ElevatorControl
//{
//	private double p;
//	private double i;
//	private double d;
//	
//	private double encR;
//	private double error;
//	private double deadband;
//	
//	private int picking1=0;
//	private int picking2=100;
//	private int picking3=200;
//	private int picking4=300;
//	private int picking5=400;
//	private int container=500;
//	private int top=1000;
//	
//	
//	private boolean autostop;
//	
//	private Encoder e;
//	
//	private SpeedController ma;
//	private SpeedController mb;
//	
//	private Joystick j;
//	Thread t;
//	
//	public ElevatorControl(Joystick joy, SpeedController a, SpeedController b, Encoder enc, double P, double I, double D)
//	{
//		t=new Thread(new StopWatch());
//
//		e=enc;
//		j=joy;
//		ma=a;
//		mb=b;
//		
//		p=P;
//		i=I;
//		d=D;
//		
//		t.start();
//	}
//	/*
//	 * elevator controller that automatically holds elevator in position if operator leaves controller at 0
//	 * 
//	 */
//	public void control()
//	{
//		//if(!autostop)
//	}
//	
//	public double getP() 
//	{
//		return p;
//	}
//	public void setP(double p) 
//	{
//		this.p = p;
//	}
//
//	/**
//	 * @return the i
//	 */
//	public double getI() 
//	{
//		return i;
//	}
//	/**
//	 * @param i the i to set
//	 */
//	public void setI(double i) {
//		this.i = i;
//	}
//
//	/**
//	 * @return the d
//	 */
//	public double getD() {
//		return d;
//	}
//	/**
//	 * @param d the d to set
//	 */
//	public void setD(double d) {
//		this.d = d;
//	}
//
//	/**
//	 * @return the deadband
//	 */
//	public double getDeadband() {
//		return deadband;
//	}
//	/**
//	 * @param deadband the deadband to set
//	 */
//	public void setDeadband(double deadband) {
//		this.deadband = deadband;
//	}
//
//	private class StopWatch implements Runnable
//	{
//		private double jsM=0;
//		private boolean autoStop=true;
//		private double jstickwaittime=50;
//		private long encwaittime=50;
//		private boolean pressed=false;
//		@Override
//		public void run()
//		{
//			while (true) 
//			{
//				try 
//				{
//					for(int i=j.getButtonCount();i<11;i++)
//					{
//						pressed|=j.getRawButton(i);
//					}
//					if(j.getY()<getDeadband()||!pressed)
//					{
//						// decides if joystick has moved over a period of more or
//						// less jstickwaittime ms - adjust how operator likes
//						if(pressed)
//						{
//							//encR=
//						}
//						for (int i = 0; i < jstickwaittime; i++) 
//						{
//							jsM += j.getY() > getDeadband() ? j.getY() : 0;
//							Thread.sleep(1);
//						}
//						if (jsM == 0&&autoStop) 
//						{
//							encR = e.getRaw();
//							autoStop = true;
//							Thread.sleep(encwaittime);
//							error=encR-e.getRaw();
//						}
//						else
//						{
//							autoStop=false;
//						}
//					}
//					else
//					{
//						autoStop=false;
//					}
//					autostop=this.autoStop;
//				} 
//				catch (Exception e) 
//				{
//					break; // stopwatch is broken- print to logger most likely
//				}
//			}
//			
//		}
//		
//	}
//}
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ElevatorControl {

	private class Control extends TimerTask {
		private ElevatorControl e;

		public Control(ElevatorControl e) {
			this.e = e;
		}

		@Override
		public void run() {
			e.mcgriddle();
		}
	}

	private double pPID;
	private double p;
	private double i;
	private double d;
	private double pd;
	private double id;
	private double dd;

	private boolean done = false;
	private int autoBand = 10;
	private int autoSetpoint;

	private int[] encR;
	private int error;
	private double deadband = .1;
	private int index = 0;

	private Encoder e;

	private SpeedController ma;
	private SpeedController mb;

	public boolean isEnabled = false;
	private long numLoops = 0;
	private double jsM = 0;
	private boolean autoStop = false;
	private double jstickwaittime = 5000;
	private long encwaittime = 50;
	private double irate = 10; // period for i and d in ms- based on system time
	private double drate = 50;
	long howLong = System.currentTimeMillis();
	private int perr;
	private int accumulated = 0;
	private boolean jcheck = false;
	private int button = 10000;
	private boolean buttonChanged = false;
	private DigitalInput bls;
	private DigitalInput tls;
	DriverStation ds;

	private Joystick j;

	private ArrayList<Setpoint> setpoints;

	private ArrayList<Setpoint> autoSetpoints;

	public ElevatorControl(Joystick joy, SpeedController a, SpeedController b,
			Encoder enc, ArrayList<Setpoint> setpoints, DigitalInput bls,
			DigitalInput tls) {
		Timer t = new Timer();
		Control c = new Control(this);
		t.schedule(c, (long) 0, 20);
		this.bls = bls;
		this.tls = tls;
		this.autoSetpoints = autoSetpoints;
		this.setpoints = setpoints;
		e = enc;
		j = joy;
		ma = a;
		mb = b;
		button = j.getButtonCount();
		ds = DriverStation.getInstance();
		e.reset();

	}

	/*
	 * elevator controller that automatically holds elevator in position if
	 * operator leaves controller at 0 kY- tuning constant for joystick
	 */
	private double pid(double p, double i, double d, int err) {
		double out = 0;
		// if((System.currentTimeMillis()-howLong)%irate<10)
		if (Math.abs(err) <= 5) {
			accumulated = 0;
			return 0;
		} else if (Math.abs(perr - err) > Math.abs(err + perr)) {
			accumulated = 0;
		}

		accumulated += err;
		// if((System.currentTimeMillis()-howLong)%drate<10)
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

	public boolean autoMcgriddle(int setpoint) {
		error = setpoint - e.get();
		if (Math.abs(error) <= autoBand) {
			return true;
		}
		double out;
		if (error > 0) {
			out = pid(p, i, d, error);
		} else {
			out = pid(pd, id, dd, error);
		}
		if (bls.get() && out < 0) {
			out = 0;
		}
		ma.set(out);
		mb.set(out);
		return false;
	}

	int perror;

	public void mcgriddle() {
		if (bls.get()) {
			e.reset();
		}
		if (ds.isOperatorControl()) {
			for (int i = 1; i < j.getButtonCount(); i++) {
				if (j.getRawButton(i)) {
					if (button != i) {
						button = i;
						for (Setpoint a : setpoints) {
							if (a.getButton() == button && button != 10000) {
								index = 0;
								encR = a.getSetpoint();
								break;
							}
						}
					}
					jcheck = true;

					break;
				}
			}
			if ((Math.abs(j.getY()) < deadband)) {

				if (jcheck) {
					error = encR[index] - e.get();
					if (Math.abs(error) <= 5) {
						if (index < encR.length - 1) {
							index++;
							error = encR[index] - e.get();

						}
					}
					double out;
					if (error > 0) {
						out = pid(p, i, d, error);
					} else {
						out = pid(pd, id, dd, error);
					}
					if (bls.get() && out < 0) {
						out = 0;
					}
					if (tls.get() && out > 0) {
						out = 0;
					}
					ma.set(out);
					mb.set(out);
				} else {
					ma.set(0);
					mb.set(0);
					jcheck = false;
				}
			} else {

				double y = j.getY();
				if (bls.get() && y < 0) {
					y = 0;
				}
				if (tls.get() && y > 0) {
					y = 0;
				}
				ma.set(y);
				mb.set(y);
				jcheck = false;
				autoStop = false;
			}
		}
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public double getI() {
		return i;
	}

	public void setI(double i) {
		this.i = i;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getPd() {
		return pd;
	}

	public void setPd(double pd) {
		this.pd = pd;
	}

	public double getId() {
		return id;
	}

	public void setId(double id) {
		this.id = id;
	}

	public double getDd() {
		return dd;
	}

	public void setDd(double dd) {
		this.dd = dd;
	}

	public int getAutoSetpoint() {
		return autoSetpoint;
	}

	public void setAutoSetpoint(int autoSetpoint) {
		this.autoSetpoint = autoSetpoint;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}

// public class StopWatch implements Runnable
// {
// private double jsM=0;
// private boolean autoStop=true;
// private double jstickwaittime=5000;
// private long encwaittime=50;
// private double irate=10; //period for i and d in ms- based on system time
// private double drate=50;
// long howLong=System.currentTimeMillis();
// private int perr;
// private int accumulated=0;
// private boolean jcheck=false;
// private int button=-1;
// private double pid(double p, double i, double d, int err)
// {
// if (Math.abs(err) > (-3721) / 2)
// {
// if (err > 0)
// {
// err = err - 3721;
// }
// else
// {
// err = err + 3721;
// }
// }
//
// if((System.currentTimeMillis()-howLong)%irate<10)
// accumulated+=err;
// if((System.currentTimeMillis()-howLong)%drate<10)
// perr=error-perr;
// double P=err*p;
// double I=accumulated*i;
// double D=perr*d;
// perr=err;
// return P+I+D;
// }
//
// @Override
// public void run()
// {
// while (true)
// {
// try
// {
// for(int i=1;i<numB;i++)
// {
// if(j.getRawButton(i))
// {
// button=i;
// jcheck=true;
// break;
// }
// }
// if(j.getY()<deadband||jcheck)
// {
// if(jcheck)
// {
// error=e.get()-encR;
// double out=pid(p,i,d,error);
// ma.set(out);
// mb.set(out);
// }
// else
// {
// jcheck=false;
//
// // decides if joystick has moved over a period of more or
// // less jstickwaittime ms - adjust how operator likes
// if(jstickwaittime==-1)
// {
// for (int i = 0; i < jstickwaittime; i++)
// {
// jsM += j.getY() > deadband ? j.getY() : 0;
// Thread.sleep(1);
// }
// }
// if (jsM == 0&&autoStop)
// {
// encR = e.getRaw();
// double out=pid(p,i,d,error);
// ma.set(out);
// mb.set(out);
// autoStop = true;
// Thread.sleep(encwaittime);
// }
// else
// {
// autoStop=false;
// }
// }
// }
// else
// {
// double setpoint=j.getY();
// ma.set(setpoint);
// mb.set(setpoint);
// jcheck=false;
// autoStop=false;
// }
// }
// catch (Exception e)
// {
// break; // stopwatch is broken- print to logger most likely
// }
// }
//
// }
//
// public int getButton() {
// return button;
// }
//
// public void setButton(int button) {
// this.button = button;
// }
//
// }
// }
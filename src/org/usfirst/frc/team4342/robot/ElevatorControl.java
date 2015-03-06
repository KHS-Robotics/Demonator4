package org.usfirst.frc.team4342.robot;

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
	
	private volatile int currentSetpoint;

	private Encoder e;

	private SpeedController ma;
	private SpeedController mb;

	public boolean isEnabled = false;
	long howLong = System.currentTimeMillis();
	private int perr;
	private int accumulated = 0;
	private boolean jcheck = false;
	private int button = -1;
	private DigitalInput bls;
	private DigitalInput tls;
	DriverStation ds;

	private Joystick j;

	private ArrayList<Setpoint> setpoints;

	public ElevatorControl(Joystick joy, SpeedController a, SpeedController b,
			Encoder enc, ArrayList<Setpoint> setpoints, DigitalInput bls,
			DigitalInput tls) {
		Timer t = new Timer();
		Control c = new Control(this);
		t.schedule(c, (long) 0, 20);
		this.bls = bls;
		this.tls = tls;
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
	
	public void setAutoSetpoint(int setpoint) {
		this.currentSetpoint = setpoint;
	}
	
	private void moveElevator(int setpoint) {
		error = setpoint - e.get();
		if (Math.abs(error) <= 5) {
			if (index < encR.length - 1) {
				index++;
				error = encR[index] - e.get();
			}
			done = true;
			return;
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
		done = false;
	}
	
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
							if (a.getButton() == button && button != -1) {
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
					moveElevator(encR[index]);
				} else {
					ma.set(0);
					mb.set(0);
					jcheck = false;
				}
			} else {
				jcheck = false;
				double y = j.getY();
				if (bls.get() && y < 0) {
					y = 0;
				}
				if (tls.get() && y > 0) {
					y = 0;
				}
				ma.set(y);
				mb.set(y);
			}
		}
		else if (ds.isAutonomous()) {
			moveElevator(currentSetpoint);
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

	public boolean isDone() {
		return done;
	}
}

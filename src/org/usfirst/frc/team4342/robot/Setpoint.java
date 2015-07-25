package org.usfirst.frc.team4342.robot;

public class Setpoint {
	
	private final int button;
	private final int encoderCounts;
	
	public Setpoint(int button, int encoderCounts) {
		this.button = button;
		this.encoderCounts = encoderCounts;
	}
	
	public int getButton() {
		return button;
	}
	
	public int getEncoderCounts() {
		return encoderCounts;
	}
}

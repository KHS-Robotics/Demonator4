package org.usfirst.frc.team4342.robot;

public class DrivePID {
	static class FrontRight {
		static final double kP = 0.45;
		static final double kI = 0.005;
		static final double kD = 0.001;
	}
	
	static class FrontLeft {
		static final double kP = 0.45;
		static final double kI = 0.003;
		static final double kD = 0.002;
	}
	
	static class RearRight {
		static final double kP = 0.45;
		static final double kI = 0.009;
		static final double kD = 0.001;
	}
	
	static class RearLeft {
		static final double kP = 0.45;
		static final double kI = 0.009;
		static final double kD = 0.001;
	}
	
	static final int kCodesPerRev = 512;
}
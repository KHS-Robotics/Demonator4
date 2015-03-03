package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * This class is a revised version of <code>RobotDrive</code> by FRC designed
 * specifically for mecanum. This version includes encoders for PID and various
 * logical edits.
 */
public class MecanumDrive implements MotorSafety {
	
	protected MotorSafetyHelper _motorSafetyHelper;
	
	public static final double kMaxMotorOutput = 1.0;
	
	public static final int kMaxMotors = 4;
	
	protected static final int kFrontRight_val = 0, kFrontLeft_val = 1,
							   kRearRight_val = 2, kRearLeft_val = 3;
	
	protected static final double kP = 0.3, kI = 0.0, kD = 0.0;
	
	private static final int _invertedMotors[] = { 1, 1, 1, 1 };
	
	private byte _syncGroup = 0;
	private boolean freed = false;
	
	private SpeedController _frontRightMotor, _frontLeftMotor, _rearRightMotor, _rearLeftMotor;
	private Encoder _frontRightEncoder, _frontLeftEncoder, _rearRightEncoder, _rearLeftEncoder;
	private PIDController _frontRightPID, _frontLeftPID, _rearRightPID, _rearLeftPID;
	
	/**
	 * A wrapper around a SpeedController that is used as a PIDOutput for the
	 * purpose of controlling the rate at which something spins.
	 */
	public final class RateControlledMotor implements PIDOutput {
		
		private final SpeedController motor;
		
		/**
		 * Constructs a new instance and associate a speed controller
		 * with the object
		 * @param motor
		 */
		public RateControlledMotor(SpeedController motor) {
			this.motor = motor;
		}
		
		/**
	     * Apply power value computed by PID to the motor.
	     *
	     * <p>
	     * The standard PID system basis the power output on the amount of "error".
	     * This results in the power going to 0 as the error goes to 0. While this
	     * works well for a distance based PID (where you want to stop once you get
	     * to where you are going). It does not work well for a rate system (where
	     * you want to keep spinning at the same rate).</p>
	     *
	     * <p>
	     * Instead of treating the value passed as a new power level, we treat it as
	     * an adjustment to the current power level when we apply it.</p>
	     *
	     * @param output Power value to apply (computed by PID loop). Goes to zero
	     * as we reach the desired spin rate.
	     */
		@Override
		public void pidWrite(double output) {
			// Treat new PID computed power output as an "adjustment"
	        double rateOutput = motor.get() + output;
	        rateOutput = Math.min(1.0, rateOutput);
	        rateOutput = Math.max(-1.0, rateOutput);
	        motor.set(rateOutput);
		}
	}
	
	/**
	 * Constructs a drive train configured for mecanum.
	 * 
	 * @param frontRightMotor
	 *            the front right <code>CANJaguar</code> on the drive train
	 * @param frontLeftMotor
	 *            the front left <code>CANJaguar</code> on the drive train
	 * @param rearRightMotor
	 *            the rear right <code>CANJaguar</code> on the drive train
	 * @param rearLeftMotor
	 *            the rear left <code>CANJaguar</code> on the drive train
	 * @param frontRightEnc
	 *            the front right <code>Encoder</code> on the drive train
	 * @param frontLeftEnc
	 *            the front left <code>Encoder</code> on the drive train
	 * @param rearRightEnc
	 *            the rear right <code>Encoder</code> on the drive train
	 * @param rearLeftEnc
	 *            the rear left <code>Encoder</code> on the drive train
	 */
	public MecanumDrive(SpeedController frontRightMotor, SpeedController frontLeftMotor,
			SpeedController rearRightMotor, SpeedController rearLeftMotor, Encoder frontRightEnc,
			Encoder frontLeftEnc, Encoder rearRightEnc, Encoder rearLeftEnc) {
		if(frontRightMotor == null || frontLeftMotor == null || rearRightMotor == null || rearLeftMotor == null ||
				frontRightEnc == null || frontLeftEnc == null || rearRightEnc == null | rearLeftEnc == null) {
			throw new NullPointerException("Null value provided in MecanumDrive constructor");
		}
		
		_frontRightMotor = frontRightMotor;
		_frontLeftMotor = frontLeftMotor;
		_rearRightMotor = rearRightMotor;
		_rearLeftMotor = rearLeftMotor;
		
		_frontRightEncoder = frontRightEnc;
		_frontLeftEncoder = frontLeftEnc;
		_rearRightEncoder = rearRightEnc;
		_rearLeftEncoder = rearLeftEnc;
		
		_frontRightPID = new PIDController(kP, kI, kD, _frontRightEncoder, new RateControlledMotor(_frontRightMotor));
		_frontLeftPID = new PIDController(kP, kI, kD, _frontLeftEncoder, new RateControlledMotor(_frontLeftMotor));
		_rearRightPID = new PIDController(kP, kI, kD, _rearRightEncoder, new RateControlledMotor(_rearRightMotor));
		_rearLeftPID = new PIDController(kP, kI, kD, _rearLeftEncoder, new RateControlledMotor(_rearLeftMotor));
		
		_frontRightPID.enable();
		_frontLeftPID.enable();
		_rearRightPID.enable();
		_rearLeftPID.enable();
		
		setupMotorSafety();
	}
	
	/**
     * Drive method for Mecanum wheeled robots.
     *
     * A method for driving with Mecanum wheeled robots. There are 4 wheels
     * on the robot, arranged so that the front and back wheels are toed in 45 degrees.
     * When looking at the wheels from the top, the roller axles should form an X across the robot.
     *
     * This is designed to be directly driven by joystick axes.
     *
     * @param x The speed that the robot should drive in the X direction. [-1.0..1.0]
     * @param y The speed that the robot should drive in the Y direction.
     * This input is inverted to match the forward == -1.0 that joysticks produce. [-1.0..1.0]
     * @param rotation The rate of rotation for the robot that is completely independent of
     * the translation. [-1.0..1.0]
     * @param gyroAngle The current angle reading from the gyro.  Use this to implement field-oriented controls.
     */
	public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {
        double xIn = x;
        double yIn = -y; //negate Y for the Joystick
        
        double[] rotated = rotateVector(xIn, yIn, gyroAngle);
        xIn = rotated[0];
        yIn = rotated[1];
        
        double wheelSpeeds[] = new double[kMaxMotors];
        wheelSpeeds[kFrontRight_val] = -xIn + yIn - rotation;
        wheelSpeeds[kFrontLeft_val] = xIn + yIn + rotation;
        wheelSpeeds[kRearRight_val] = xIn + yIn - rotation;
        wheelSpeeds[kRearLeft_val] = -xIn + yIn + rotation;
        
        normalize(wheelSpeeds);
        
        _frontRightMotor.set(wheelSpeeds[kFrontRight_val] * _invertedMotors[kFrontRight_val] * kMaxMotorOutput, _syncGroup);
        _frontLeftMotor.set(wheelSpeeds[kFrontLeft_val] * _invertedMotors[kFrontLeft_val] * kMaxMotorOutput, _syncGroup);
        _rearRightMotor.set(wheelSpeeds[kRearRight_val] * _invertedMotors[kRearRight_val] * kMaxMotorOutput, _syncGroup);
        _rearLeftMotor.set(wheelSpeeds[kRearLeft_val] * _invertedMotors[kRearLeft_val] * kMaxMotorOutput, _syncGroup);
        
        if(_syncGroup != 0) {
        	CANJaguar.updateSyncGroup(_syncGroup);
        }
        
        if(_motorSafetyHelper != null) {
        	_motorSafetyHelper.feed();
        }
    }
	
	/**
     * Set the number of the sync group for the motor controllers.  If the motor controllers are {@link CANJaguar}s,
     * then they will all be added to this sync group, causing them to update their values at the same time.
     *
     * @param syncGroup the update group to add the motor controllers to
     */
    public void setCANJaguarSyncGroup(byte syncGroup) {
        _syncGroup = syncGroup;
    }
	
	/**
     * Rotate a vector in Cartesian space.
     */
    protected static double[] rotateVector(double x, double y, double angle) {
        double cosA = Math.cos(angle * (3.14159 / 180.0));
        double sinA = Math.sin(angle * (3.14159 / 180.0));
        double out[] = new double[2];
        out[0] = x * cosA - y * sinA;
        out[1] = x * sinA + y * cosA;
        return out;
    }
	
    /**
     * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
     * 
     * @param wheelSpeeds the wheel speeds to normalize
     */
	private static void normalize(double wheelSpeeds[]) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);
        int i;
        
        for (i = 0; i < kMaxMotors; i++) {
            double temp = Math.abs(wheelSpeeds[i]);
            if (maxMagnitude < temp) 
            	maxMagnitude = temp;
        }
        if (maxMagnitude > 1.0) {
            for (i = 0; i < kMaxMotors; i++) {
                wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
            }
        }
    }
	
	/**
	 * Free the resources used by <code>MecanumDrive</code>
	 */
	public void free() {
		if(!freed) {
			_frontRightPID.free();
			_frontLeftPID.free();
			_rearRightPID.free();
			_rearLeftPID.free();
			
			_frontRightMotor = null;
			_frontLeftMotor = null;
			_rearRightMotor = null;
			_rearLeftMotor = null;
			
			_frontRightEncoder.free();
			_frontLeftEncoder.free();
			_rearRightEncoder.free();
			_rearLeftEncoder.free();
		}
		freed = true;
	}
	
	/**
	 * Configure the motor safety for the drive train
	 */
	private void setupMotorSafety() {
		_motorSafetyHelper = new MotorSafetyHelper(this);
		_motorSafetyHelper.setExpiration(0.1);
		_motorSafetyHelper.setSafetyEnabled(true);
	}
	
	/**
     * Set the expiration time for the corresponding motor safety object.
     * @param expirationTime The timeout value in seconds.
     */
	@Override
	public void setExpiration(double expirationTime) {
		_motorSafetyHelper.setExpiration(expirationTime);
	}
	
	/**
     * Retrieve the timeout value for the corresponding motor safety object.
     * @return the timeout value in seconds.
     */
	@Override
	public double getExpiration() {
		return _motorSafetyHelper.getExpiration();
	}
	
	/**
     * Determine of the motor is still operating or has timed out.
     * @return a true value if the motor is still operating normally and hasn't timed out.
     */
	@Override
	public boolean isAlive() {
		return _motorSafetyHelper.isAlive();
	}
	
	/**
	 * Sets all motors to zero output
	 */
	@Override
	public void stopMotor() {
		if(_frontRightMotor != null) {
			_frontRightMotor.set(0.0);
		}
		if(_frontLeftMotor != null) {
			_frontLeftMotor.set(0.0);
		}
		if(_rearRightMotor != null) {
			_rearRightMotor.set(0.0);
		}
		if(_rearLeftMotor != null) {
			_rearLeftMotor.set(0.0);
		}
		if(_motorSafetyHelper != null) {
			_motorSafetyHelper.feed();
		}
	}
	
	/**
     * Enable/disable motor safety for this device
     * Turn on and off the motor safety option for this PWM object.
     * @param enabled True if motor safety is enforced for this object
     */
	@Override
	public void setSafetyEnabled(boolean enabled) {
		_motorSafetyHelper.setSafetyEnabled(enabled);
	}
	
	/**
     * Return the state of the motor safety enabled flag
     * Return if the motor safety is currently enabled for this devicce.
     * @return True if motor safety is enforced for this device
     */
	@Override
	public boolean isSafetyEnabled() {
		return _motorSafetyHelper.isSafetyEnabled();
	}

	@Override
	public String getDescription() {
		return "Mecanum Drive";
	}
}
package org.usfirst.frc.team4342.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;

public class Gyro_I2C implements Runnable {
	
	public class AllAxes {
		public int kX;
		public int kY;
		public int kZ;
	}

	private long xAccum, yAccum, zAccum;

	private AllAxes allAxes = new AllAxes();

	private I2C i2c;
	private long time;
	private double xangle = 0;
	private double yangle = 0;
	private double zangle = 0;
	
	private DigitalInput di;

	public Gyro_I2C(I2C.Port port) {
		i2c = new I2C(port, 0x68);
		i2c.write(0x16, 0x19); // Set internal clock to 1kHz with 42Hz LPF and
								// Full Scale to 3 for proper operation
		i2c.write(0x15, 9); // Set sample rate divider for 100 Hz operation
		i2c.write(0x17, 5); // Setup the interrupt to trigger when new data is
							// ready.
		i2c.write(0x3E, 1); // Select X gyro PLL for clock source
		
		//di=new DigitalInput(1);
		

	}

	public void update() {
		byte[] buffer = new byte[6]; // 0-1 = X, 2-3 = Y, 4-5 = Z
		i2c.read(0x1D, buffer.length, buffer);

		allAxes.kX = (buffer[0] << 8) | buffer[1];
		allAxes.kY = (buffer[2] << 8) | buffer[3];
		allAxes.kZ = (buffer[4] << 8) | buffer[5];
	}

	public double getX() {
		return convert(xAccum);
	}

	public double getY() {
		return convert(yAccum);
	}

	public double getZ() {
		return convert(zAccum)+90;
	}
	public double getXrate()
	{
		return allAxes.kX / 14.375;
	}
	public double getYrate()
	{
		return allAxes.kX / 14.375;
	}
	public double getZrate()
	{
		return allAxes.kX / 14.375;
	}

	private double convert(long d)
	{
		double degree=(((double)d / 14.375) / 100) % 360.0;
		if(degree < 0)
		{
			degree += 360;
		}
		
		return degree;
	}

	public I2C getI2C() {
		return i2c;
	}

	public void run() {
		while (true) {
			update();
			xAccum += allAxes.kX;
			yAccum += allAxes.kY;
			zAccum += allAxes.kZ;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
//	public void run()
//	{
//		while(true)
//		{
//			//di.waitForInterrupt(1000);
//			update();
//		}
//	}
}

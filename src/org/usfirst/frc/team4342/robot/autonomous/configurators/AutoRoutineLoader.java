package org.usfirst.frc.team4342.robot.autonomous.configurators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is for loading the specified auto routine from a text file
 * on the robot. This allows us to quickly switch out auto routines without
 * recompling and building code to the robot
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public final class AutoRoutineLoader 
{
	
	/** The path to the text file that holds the auto routine */
	public static final String PATH = "/home/lvuser/AutoRoutine.txt";
	
	private AutoRoutineLoader() {}
	
	/**
	 * Gets the current auto routine from a text file
	 * @return the specified auto routine
	 * @throws IOException if an error occurs while attempting to read the auto routine
	 */
	public static AutoRoutine getAutoRoutine() throws IOException 
	{
		int id = loadFromTextFile();
		
		switch(id) 
		{
			case 1:
				return AutoRoutine.PickUpOneTote;
			case 2:
				return AutoRoutine.PickUpTwoTotes;
			case 3:
				return AutoRoutine.PickUpThreeTotes;
			case 4:
				return AutoRoutine.PickUpOneContainer;
			case 5:
				return AutoRoutine.DiagnosticCheck;
		}
		
		return null;
	}
	
	private static int loadFromTextFile() throws IOException 
	{
		try 
		{
			FileReader fr = new FileReader(PATH);
			BufferedReader br = new BufferedReader(fr);
			
			String number = br.readLine();
			br.close();
			
			return new Integer(number);
		} 
		catch(IOException ex) 
		{
			throw ex;
		} 
		catch(NumberFormatException ex) 
		{
			return new Integer(9001);
		}
	}
}

package org.usfirst.frc.team4342.robot.autonomous.configurators;


/**
 * This enum classifies each auto routine
 * 
 * @see AutoRoutineLoader.java
 * 
 * @author Magnus Murray
 * @author Ernest Wilson
 * @author Katie Schuetz
 * @author Brian Lucas
 * @author Steve Chapman
 */
public enum AutoRoutine 
{
	PickUpOneTote(1), 
	PickUpTwoTotes(2), 
	PickUpThreeTotes(3), 
	PickUpOneContainer(4),
	DiagnosticCheck(5);
	
	private final int id;
	
	/**
	 * An Id is needed in order to match the text file's Id to the proper enum constant
	 * @param id the auto routine
	 */
	private AutoRoutine(int id) 
	{
		this.id = id;
	}
	
	/**
	 * Get's the Id of the enum
	 * @return the id of the enum
	 */
	public int getId() 
	{
		return id;
	}
}

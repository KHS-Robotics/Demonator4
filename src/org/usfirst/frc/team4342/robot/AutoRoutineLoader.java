package org.usfirst.frc.team4342.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class AutoRoutineLoader {
	
	private AutoRoutineLoader() {
		
	}
	
	public static int getAutoRoutine() throws IOException {
		FileReader fr = new FileReader("/home/lvuser/AutoRoutine.txt");
		BufferedReader br = new BufferedReader(fr);
		
		String number = br.readLine();
		br.close();
		
		return new Integer(number);
	}
}

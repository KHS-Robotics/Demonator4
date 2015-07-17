package org.usfirst.frc.team4342.robot;

import java.util.Hashtable;
import java.util.Map;



public class SetpointMapWrapper
{
	private static final long serialVersionUID = -5792610831329435703L;
	
	private Map<Integer, Integer> map;
	
	public SetpointMapWrapper(int[] buttonValues, int[] encoderValues) {
		if(buttonValues == null) {
			throw new IllegalArgumentException("Button values cannot be null");
		}
		if(encoderValues == null) {
			throw new IllegalArgumentException("Encoder values cannot be null");
		}
		
		if(buttonValues.length != encoderValues.length) {
			throw new IllegalArgumentException("Button and encoder values must be the same length");
		}
		
		map = new Hashtable<Integer, Integer>();
		
		init(buttonValues, encoderValues);
	}
	
	public void put(int button, int encValue) {
		map.put(button, encValue);
	}
	
	public int get(int button) {
		return map.get(button);
	}
	
	public void remove(int button) {
		map.remove(button);
	}
	
	public boolean containsButton(int button) {
		return map.containsKey(button);
	}
	
	private void init(int[] buttonValues, int[] encoderValues) {
		for(int i = 0; i < buttonValues.length; i++) {
			map.put(buttonValues[i], encoderValues[i]);
		}
	}
	
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
}
package cern.ch.mathexplorer.utils;

public class Console {
	
	static boolean enabled = true;
	
	public static void print(Object o) {
		if (enabled) {
			System.out.println(o);
		}
	}
}

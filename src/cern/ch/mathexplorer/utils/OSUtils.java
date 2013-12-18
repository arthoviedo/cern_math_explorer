package cern.ch.mathexplorer.utils;


public class OSUtils {
	public enum OS{
		WINDOWS, LINUX, MAC, OTHER;
	}
	
	public static OS getOS() {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			return OS.WINDOWS;
		}
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			return OS.LINUX;
		}
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			return OS.MAC;
		}
		return OS.OTHER;
	}
	
	public static void main(String[] args) {
		System.out.println(getOS());
	}
}

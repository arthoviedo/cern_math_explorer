package cern.ch.mathexplorer.mathematica;

public class StructuralFeature {
	/**
	 * The name of this feature
	 */
	private String name;
	
	/**
	 * The String representing the pattern in Mathematica Notation
	 */
	private String pattern;
	
	
	
	public StructuralFeature(String name, String pattern) {
		super();
		this.name = name;
		this.pattern = pattern;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
}

package cern.ch.mathexplorer.mathematica;

/**
 * Represents one patterns that a mathematical expression can contain.
 * A simple exam can be defined as "The sum of 2 elements"
 * In Mathematica notation, this pattern would be represented as
 * x_ + y_ 
 * For better documentation on how to use patterns in Mathematica:
 * http://reference.wolfram.com/mathematica/guide/Patterns.html
 * @author cern
 *
 */
public class StructuralPattern {
	/**
	 * The name of this feature
	 */
	private String name;
	
	/**
	 * The String representing the pattern in Mathematica Notation
	 */
	private String pattern;
	
	public StructuralPattern(String name, String pattern) {
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

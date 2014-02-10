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
	 * The String representing the pattern in Mathematica Notation
	 */
	private String pattern;
	
	public StructuralPattern(String pattern) {
		super();
		this.pattern = pattern;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String toString(){
		return pattern;
	}
}

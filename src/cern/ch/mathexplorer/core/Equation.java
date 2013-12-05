package cern.ch.mathexplorer.core;

public class Equation {
	
	String filename;
	int lineNumber;
	String mathmlExpression;
	
	
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int linenumber) {
		this.lineNumber = linenumber;
	}

	public String getMathmlExpression() {
		return mathmlExpression;
	}

	public void setMathmlExpression(String mathmlExpression) {
		this.mathmlExpression = mathmlExpression;
	}

	private Equation(){}
	
	public String toString() {
		return mathmlExpression;
	}
	
	static class EquationBuilder {
		private Equation equation;
		public EquationBuilder(){
			equation = new Equation();
		}
		
		public EquationBuilder setLineNumber(int line) {
			equation.lineNumber = line;
			return this;
		}
		
		public EquationBuilder setMathmlExpression(String mathml) {
			equation.mathmlExpression = mathml;
			return this;
		}
		
		public EquationBuilder setFilename (String filename) {
			equation.filename = filename;
			return this;
		}
		
		public Equation build() {
			return equation;
		}
	}
}

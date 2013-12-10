package cern.ch.mathexplorer.core;

import java.io.Serializable;

public class EquationResult implements Serializable {
	
	
	String filename;
	int lineNumber;
	String mathmlExpression;
	String downloadLink;
	
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
	
	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	private EquationResult(){}
	
	public String toString() {
		return mathmlExpression;
	}
	
	static class EquationBuilder {
		private EquationResult equation;
		public EquationBuilder(){
			equation = new EquationResult();
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
			equation.downloadLink = Constants.URL_PUBLIC_FOLDER + 
					filename.replace(Constants.INDEX_FILE_EXTENSION, Constants.XML_EXTENSION); 
			return this;
		}
		
		public EquationResult build() {
			return equation;
		}
	}
}

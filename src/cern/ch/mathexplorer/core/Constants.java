package cern.ch.mathexplorer.core;

import java.io.Serializable;

import cern.ch.mathexplorer.utils.OSUtils;

public final class Constants implements Serializable {

	public final static String SAMPLE_EQUATION_1 = "<math><mrow><mrow><mrow><mi>d</mi><mo>⁢</mo><msup><mi>s</mi><mn>2</mn></msup></mrow>\n"
			+ "<mo>=</mo><mrow><mrow><msup><mi>e</mi><mrow><mn>2</mn><mo>⁢</mo><mover accent=\"true\"><mi>A</mi><mo>~</mo>\n"
			+ "</mover></mrow></msup><mo>⁢</mo><msub><mi>η</mi><mrow><mi>μ</mi><mo>⁢</mo><mi>ν</mi>\n" +
					"</mrow></msub><mo>⁢</mo><mi>d</mi><mo>⁢</mo><msup><mi>x</mi><mi>μ</mi></msup><mo>⁢</mo><mi>d</mi><mo>⁢</mo>"
					+ "<msup><mi>x</mi><mi>ν</mi></msup>\n" +
					"</mrow><mo>+</mo><mrow><msup><mi>e</mi><mrow><mn>2</mn><mo>⁢</mo><mover accent=\"true\"><mi>B</mi><mo>~</mo>\n"
					+ "</mover></mrow></msup><mo>⁢</mo>" +
							"<msub><mi>δ</mi><mrow><mi>m</mi><mo>⁢</mo><mi>n</mi></mrow></msub><mo>⁢</mo><mi>d</mi><mo>\n"
							+ "⁢</mo><msup><mi>y</mi><mi>m</mi></msup><mo>" +
							"⁢</mo><mi>d</mi><mo>⁢</mo><msup><mi>y</mi><mi>n</mi></msup></mrow></mrow></mrow><mo>,</mo></mrow></math>";
	public final static String SAMPLE_EQUATION_2 = "<math><mrow><msub><mi>ε</mi><mrow><mi>μ</mi><mo>⁢</mo><mi>ν</mi><mo>⁢\n"
			+ "</mo><mi>ρ</mi></mrow></msub><mo>⁢</mo><mrow><msub>"
			+ "<mo>∂</mo><mi>ν</mi></msub><mo>⁡</mo><msub><mi>A</mi><mi>ρ</mi></msub></mrow></mrow></math>";
	public final static String SAMPLE_EQUATION_3 = "<math><mrow><mi>S</mi><mo>=</mo><mrow><msub><mi>S</mi><mi>B</mi></msub>\n"
			+ "<mo>+</mo><msub><mi>S</mi><mi>F</mi></msub></mrow></mrow></math>";
	public final static String SAMPLE_EQUATION_4 = "<math><mrow><mrow><msub><mi>ω</mi><mi>n</mi></msub><mo>/</mo>\n"
			+ "<msup><mi>α</mi><mo>′</mo></msup></mrow><mo>⁢</mo><msup><mi>p</mi><mo>+</mo></msup></mrow></math>";
	public final static String SAMPLE_EQUATION_5 = "<math><mrow><mrow><mrow><mi>t</mi><mo>→</mo><mrow><mi>t</mi><mo>+</mo><mi>β</mi>\n"
			+ "</mrow></mrow><mo>,</mo><mrow><mrow><mi>β</mi><mo>=</mo><mrow><mn>8</mn><mo>⁢</mo><mi>π</mi><mo>⁢</mo><mi>M</mi>\n"
			+ "</mrow></mrow><mo>,</mo><mrow><mi>r</mi><mo>≥</mo><mrow><mn>2</mn><mo>⁢</mo><mi>M</mi></mrow></mrow></mrow></mrow>\n"
			+ "<mo>.</mo></mrow></math>";
	public final static String SAMPLE_EQUATION_6 = "<math><mi>¼</mi></math>";
	
	public final static String SAMPLE_EQUATION_7 = "";
	public final static String SAMPLE_EQUATION_8 = "<math><mi>Å</mi><mi>Ĳ</mi></math>";
			
	
	public final static String [] SAMPLE_EQUATIONS = {SAMPLE_EQUATION_1, 
		SAMPLE_EQUATION_2, SAMPLE_EQUATION_3, SAMPLE_EQUATION_4, SAMPLE_EQUATION_5};
	
	public final static String MATHML = "MathML";
	public final static String LATEX = "LaTeX";
	
	public static final String XML_EXTENSION = ".xhtml";
	public static final String INDEX_FILE_EXTENSION = ".eq";
	public static final String URL_PUBLIC_FOLDER = "https://googledrive.com/host/0B3PGNL_gfdnqWWZ6eUpBTWRqc1k/";

	public static String getDataSetLocation() {
		if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
			return "C:/Users/Artie/Desktop/Estudio/master_thesis_epfl/math_6262";
		}
		if (OSUtils.getOS().equals(OSUtils.OS.LINUX)) {
			return "/share/math/ok_6226";
		}
		return "";
	}
	
	public static String getMathematicaLocation() {
		if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
			return "C:/Program Files/Wolfram Research/Mathematica/9.0/SystemFiles/Links/JLink";
		}
		if (OSUtils.getOS().equals(OSUtils.OS.LINUX)) {
			return "/usr/local/Wolfram/Mathematica/9.0/SystemFiles/Links/JLink";
		}
		return "";
	}
}

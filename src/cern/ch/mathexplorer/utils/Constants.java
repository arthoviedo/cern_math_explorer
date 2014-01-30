package cern.ch.mathexplorer.utils;

import java.io.Serializable;

public final class Constants implements Serializable {
	
	public static final String MATH_NOTATIONAL_FIELD = "math_notational_field";
	public static final String MATH_STRUCTURAL_FIELD = "math_structural_field";
	
	public static final String EQUATIONS_TEXT = "EQUATIONS_TEXT";

	//Schrodinger Equation
	public final static String SAMPLE_EQ_1 = "<math><mi>i</mi><mi>ℏ</mi><mfrac><mo>∂</mo><mrow><mo>∂</mo><mi>t</mi></mrow></mfrac>"
			+ "<mi>Ψ</mi><mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced><mo>=</mo>"
			+ "<mfenced close=\"]\" open=\"[\"><mrow><mfrac><mrow><mo>-</mo><msup><mi>ℏ</mi><mn>2</mn></msup></mrow><mrow><mn>2</mn>"
			+ "<mi>m</mi></mrow></mfrac><msup><mo>∇</mo><mn>2</mn></msup><mo>+</mo><mi>V</mi><mfenced close=\")\" open=\"(\">"
			+ "<mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced></mrow></mfenced><mi>Ψ</mi>"
			+ "<mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced></math>";
	//Strong force
	public final static String SAMPLE_EQ_2 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><msub><mi mathvariant=\"script\">L</mi>"
			+ "<mstyle mathvariant=\"normal\"><mi>Q</mi><mi>C</mi><mi>D</mi></mstyle></msub><mo>=</mo><msub><mover accent=\"true\">"
			+ "<mi>ψ</mi><mo>̄</mo></mover><mi>i</mi></msub><mfenced close=\")\" open=\"(\"><mrow><mi>i</mi><msup><mi>γ</mi><mi>μ</mi>"
			+ "</msup><msub><mfenced close=\")\" open=\"(\"><msub><mi>D</mi><mi>μ</mi></msub></mfenced><mrow><mi>i</mi><mi>j</mi></mrow>"
			+ "</msub><mo>-</mo><mi>m</mi><mspace width=\"0.167em\"/><msub><mi>δ</mi><mrow><mi>i</mi><mi>j</mi></mrow></msub></mrow>"
			+ "</mfenced><msub><mi>ψ</mi><mi>j</mi></msub><mo>-</mo><mfrac><mn>1</mn><mn>4</mn></mfrac><msubsup><mi>G</mi><mrow>"
			+ "<mi>μ</mi><mi>ν</mi></mrow><mi>a</mi></msubsup><msubsup><mi>G</mi><mi>a</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msubsup>"
			+ "</math>";
	//Quantum electrodynamics
	public final static String SAMPLE_EQ_3 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi mathvariant=\"script\">L</mi>"
			+ "<mo>=</mo><mover accent=\"true\"><mi>ψ</mi><mo>̄</mo></mover><mfenced close=\")\" open=\"(\"><mrow><mi>i</mi><msup>"
			+ "<mi>γ</mi><mi>μ</mi></msup><msub><mi>D</mi><mi>μ</mi></msub><mo>-</mo><mi>m</mi></mrow></mfenced><mi>ψ</mi><mo>-</mo>"
			+ "<mfrac><mn>1</mn><mn>4</mn></mfrac><msub><mi>F</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msub><msup><mi>F</mi><mrow>"
			+ "<mi>μ</mi><mi>ν</mi></mrow></msup></math>";
	//Mass number
	public final static String SAMPLE_EQ_4 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi>A</mi><mo>=</mo><mi>Z</mi><mo>+</mo>"
			+ "<mi>N</mi></math>";
	//Radiation flux
	public final static String SAMPLE_EQ_5 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi>I</mi><mo>=</mo><msub><mi>I</mi>"
			+ "<mn>0</mn></msub><msup><mi>e</mi><mrow><mo>-</mo><mi>μ</mi><mi>x</mi></mrow></msup></math>";
	//Ampere circuital law
	public final static String SAMPLE_EQ_6 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><msub><mo>∮</mo><mi>S</mi></msub>"
			+ "<mstyle mathvariant=\"bold\"><mi>B</mi></mstyle><mo>⋅</mo><mstyle mathvariant=\"normal\"><mi>d</mi></mstyle>"
			+ "<mstyle mathvariant=\"bold\"><mi>l</mi></mstyle><mo>=</mo><msub><mi>μ</mi><mn>0</mn></msub><msub><mo>∮</mo><mi>S</mi>"
			+ "</msub><mfenced close=\")\" open=\"(\"><mrow><mstyle mathvariant=\"bold\"><mi>J</mi></mstyle><mo>+</mo><msub><mi>ϵ</mi>"
			+ "<mn>0</mn></msub><mfrac><mrow><mo>∂</mo><mstyle mathvariant=\"bold\"><mi>E</mi></mstyle></mrow><mrow><mo>∂</mo><mi>t</mi>"
			+ "</mrow></mfrac></mrow></mfenced><mo>⋅</mo><mstyle mathvariant=\"normal\"><mi>d</mi></mstyle><mstyle mathvariant=\"bold\">"
			+ "<mi>A</mi></mstyle></math>";
	//Einstein field equation
	public final static String SAMPLE_EQ_7 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><msub><mi>G</mi><mrow><mi>μ</mi>"
			+ "<mi>ν</mi></mrow></msub><mo>≡</mo><msub><mi>R</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msub><mo>-</mo><mfrac><mn>1</mn>"
			+ "<mn>2</mn></mfrac><mi>R</mi><msub><mi>g</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msub>"
			+ "<mo>=</mo><mfrac><mrow><mn>8</mn><mi>π</mi><mi>G</mi></mrow><msup><mi>c</mi><mn>4</mn></msup></mfrac><msub><mi>T</mi>"
			+ "<mrow><mi>μ</mi><mi>ν</mi></mrow></msub><mo>.</mo><mspace width=\"0.167em\"/></math>";
	public final static String SAMPLE_EQ_1_N = "<math><mi>i</mi><mi>ℏ</mi><mfrac><mn>1.0264</mn><mrow><mn>4.6</mn><mn>9</mn></mrow></mfrac>"
			+ "<mn>9...15</mn><mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced><mo>=</mo>"
			+ "<mfenced close=\"]\" open=\"[\"><mrow><mfrac><mrow><mn>2300002500</mn><msup><mn>90000</mn><mn>2.0135</mn></msup></mrow><mrow><mn>2</mn>"
			+ "<mi>m</mi></mrow></mfrac><msup><mo>∇</mo><mn>2</mn></msup><mo>+</mo><mi>V</mi><mfenced close=\")\" open=\"(\">"
			+ "<mstyle mathvariant=\"bold\"><mn>1.38197</mn></mstyle><mi>t</mi></mfenced></mrow></mfenced><mi>Ψ</mi>"
			+ "<mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced></math>";
	public final static String [] SAMPLE_EQUATIONS = {SAMPLE_EQ_1, SAMPLE_EQ_2, SAMPLE_EQ_3, SAMPLE_EQ_4, SAMPLE_EQ_5,
		SAMPLE_EQ_6, SAMPLE_EQ_7};
	
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
	
	public final static boolean USE_MATHEMATICA = true;
	
	public static String getMathematicaCommand() {
		if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
			return "C:/Program Files/Wolfram Research/Mathematica/9.0/MathKernel.exe";
		}
		if (OSUtils.getOS().equals(OSUtils.OS.LINUX)) {
			return "math";
		}
		return "";
	}
	
	public static String getMathematicaLinkLocation() {
		if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
			return "C:/Program Files/Wolfram Research/Mathematica/9.0/SystemFiles/Links/JLink";
		}
		if (OSUtils.getOS().equals(OSUtils.OS.LINUX)) {
			return "/usr/local/Wolfram/Mathematica/9.0/SystemFiles/Links/JLink";
		}
		return "";
	}
}

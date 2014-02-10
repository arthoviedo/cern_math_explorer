package cern.ch.mathexplorer.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Constants implements Serializable {

	public static enum MATH_FIELD {
		MATH_NOTATIONAL_FIELD("math_notational_field"),
		MATH_NORMALIZED_NOTATIONAL_FIELD("math_normalized_notational_field"),
		MATH_STRUCTURAL_FIELD("math_structural_field");
		
		String name;
		MATH_FIELD(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}

	public static final String EQUATIONS_TEXT = "EQUATIONS_TEXT";
	// Schrodinger Equation
	public final static String SAMPLE_EQ_1 = "<math><mi>i</mi><mi>ℏ</mi><mfrac><mo>∂</mo><mrow><mo>∂</mo><mi>t</mi></mrow></mfrac>"
			+ "<mi>Ψ</mi><mfenced close=\")\" open=\"(\"><mi>r</mi><mi>t</mi></mfenced><mo>=</mo>"
			+ "<mfenced close=\"]\" open=\"[\"><mrow><mfrac><mrow><mo>-</mo><msup><mi>ℏ</mi><mn>2</mn></msup></mrow><mrow><mn>2</mn>"
			+ "<mi>m</mi></mrow></mfrac><msup><mo>∇</mo><mn>2</mn></msup><mo>+</mo><mi>V</mi><mfenced close=\")\" open=\"(\">"
			+ "<mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced></mrow></mfenced><mi>Ψ</mi>"
			+ "<mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced></math>";
	// Strong force
	public final static String SAMPLE_EQ_2 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><msub><mi mathvariant=\"script\">L</mi>"
			+ "<mstyle mathvariant=\"normal\"><mi>Q</mi><mi>C</mi><mi>D</mi></mstyle></msub><mo>=</mo><msub><mover accent=\"true\">"
			+ "<mi>ψ</mi><mo>̄</mo></mover><mi>i</mi></msub><mfenced close=\")\" open=\"(\"><mrow><mi>i</mi><msup><mi>γ</mi><mi>μ</mi>"
			+ "</msup><msub><mfenced close=\")\" open=\"(\"><msub><mi>D</mi><mi>μ</mi></msub></mfenced><mrow><mi>i</mi><mi>j</mi></mrow>"
			+ "</msub><mo>-</mo><mi>m</mi><mspace width=\"0.167em\"/><msub><mi>δ</mi><mrow><mi>i</mi><mi>j</mi></mrow></msub></mrow>"
			+ "</mfenced><msub><mi>ψ</mi><mi>j</mi></msub><mo>-</mo><mfrac><mn>1</mn><mn>4</mn></mfrac><msubsup><mi>G</mi><mrow>"
			+ "<mi>μ</mi><mi>ν</mi></mrow><mi>a</mi></msubsup><msubsup><mi>G</mi><mi>a</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msubsup>"
			+ "</math>";
	// Quantum electrodynamics
	public final static String SAMPLE_EQ_3 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi mathvariant=\"script\">L</mi>"
			+ "<mo>=</mo><mover accent=\"true\"><mi>ψ</mi><mo>̄</mo></mover><mfenced close=\")\" open=\"(\"><mrow><mi>i</mi><msup>"
			+ "<mi>γ</mi><mi>μ</mi></msup><msub><mi>D</mi><mi>μ</mi></msub><mo>-</mo><mi>m</mi></mrow></mfenced><mi>ψ</mi><mo>-</mo>"
			+ "<mfrac><mn>1</mn><mn>4</mn></mfrac><msub><mi>F</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msub><msup><mi>F</mi><mrow>"
			+ "<mi>μ</mi><mi>ν</mi></mrow></msup></math>";
	// Mass number
	public final static String SAMPLE_EQ_4 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi>A</mi><mo>=</mo><mi>Z</mi><mo>+</mo>"
			+ "<mi>N</mi></math>";
	// Radiation flux
	public final static String SAMPLE_EQ_5 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi>I</mi><mo>=</mo><msub><mi>I</mi>"
			+ "<mn>0</mn></msub><msup><mi>e</mi><mrow><mo>-</mo><mi>μ</mi><mi>x</mi></mrow></msup></math>";
	// Ampere circuital law
	public final static String SAMPLE_EQ_6 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><msub><mo>∮</mo><mi>S</mi></msub>"
			+ "<mstyle mathvariant=\"bold\"><mi>B</mi></mstyle><mo>⋅</mo><mstyle mathvariant=\"normal\"><mi>d</mi></mstyle>"
			+ "<mstyle mathvariant=\"bold\"><mi>l</mi></mstyle><mo>=</mo><msub><mi>μ</mi><mn>0</mn></msub><msub><mo>∮</mo><mi>S</mi>"
			+ "</msub><mfenced close=\")\" open=\"(\"><mrow><mstyle mathvariant=\"bold\"><mi>J</mi></mstyle><mo>+</mo><msub><mi>ϵ</mi>"
			+ "<mn>0</mn></msub><mfrac><mrow><mo>∂</mo><mstyle mathvariant=\"bold\"><mi>E</mi></mstyle></mrow><mrow><mo>∂</mo><mi>t</mi>"
			+ "</mrow></mfrac></mrow></mfenced><mo>⋅</mo><mstyle mathvariant=\"normal\"><mi>d</mi></mstyle><mstyle mathvariant=\"bold\">"
			+ "<mi>A</mi></mstyle></math>";
	// Einstein field equation
	public final static String SAMPLE_EQ_7 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><msub><mi>G</mi><mrow><mi>μ</mi>"
			+ "<mi>ν</mi></mrow></msub><mo>≡</mo><msub><mi>R</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msub><mo>-</mo><mfrac><mn>1</mn>"
			+ "<mn>2</mn></mfrac><mi>R</mi><msub><mi>g</mi><mrow><mi>μ</mi><mi>ν</mi></mrow></msub>"
			+ "<mo>=</mo><mfrac><mrow><mn>8</mn><mi>π</mi><mi>G</mi></mrow><msup><mi>c</mi><mn>4</mn></msup></mfrac><msub><mi>T</mi>"
			+ "<mrow><mi>μ</mi><mi>ν</mi></mrow></msub><mo>.</mo><mspace width=\"0.167em\"/></math>";
	public final static String SAMPLE_EQ_8 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi>A</mi><mo>=</mo><mi>d</mi><mi>N</mi>"
			+ "<mo>/</mo><mi>d</mi><mi>t</mi></math>";
	public final static String SAMPLE_EQ_9 = "<math><mn>1E+2</mn></math>";
	
	 public final static String SAMPLE_EQ_10 = "<math><mi>x</mi><mo>*</mo><mi>y</mi></math>"; 
	public final static String SAMPLE_EQ_1_N = "<math><mi>i</mi><mi>ℏ</mi><mfrac><mn>1.0264</mn><mrow><mn>4.6</mn><mn>9</mn></mrow></mfrac>"
			+ "<mn>9...15</mn><mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced><mo>=</mo>"
			+ "<mfenced close=\"]\" open=\"[\"><mrow><mfrac><mrow><mn>2300002500</mn><msup><mn>90000</mn><mn>2.0135</mn></msup></mrow><mrow><mn>2</mn>"
			+ "<mi>m</mi></mrow></mfrac><msup><mm>4000.0000</mm><mn>2</mn></msup><mo>+</mo><mn>V</mn><mfenced close=\")\" open=\"(\">"
			+ "<mstyle mathvariant=\"bold\"><mn>1.38197</mn></mstyle><mi>t</mi></mfenced></mrow></mfenced><mn>0.0000</mn>"
			+ "<mfenced close=\")\" open=\"(\"><mstyle mathvariant=\"bold\"><mi>r</mi></mstyle><mi>t</mi></mfenced></math>";
	public final static String SAMPLE_EQ_TEXT = "<math alttext=\"\\mbox{\\bf\\{}(\\mathcal{H}|\\textrm{d}J\\,)\\mbox{\\bf,}\\,"
			+ "(\\mathcal{H}|\textrm{d}K\\,%)\\mbox{\\bf\\}}=({\\mathcal{M}}_{i}|{\\textrm{d}J}\\,\\overleftrightarrow{\\partial}"
			+ "^%{i}{\\textrm{d}K})\\,,\"><mrow><mtext>{</mtext><mrow><mo>(</mo><mi>ℋ</mi><mo>|</mo><mtext>d</mtext><mi>J</mi>"
			+ "<mo>)</mo></mrow><mtext>,</mtext><mrow><mo>(</mo><mi>ℋ</mi><mo>|</mo><mtext>d</mtext><mi>K</mi><mo>)</mo></mrow>"
			+ "<mtext>}</mtext><mo>=</mo><mrow><mo>(</mo><msub><mi>ℳ</mi><mi>i</mi></msub><mo>|</mo><mtext>d</mtext><mi>J</mi><msup>"
			+ "<mover accent=\"true\"><mo>∂</mo><mo>↔</mo></mover><mi>i</mi></msup><mtext>d</mtext><mi>K</mi><mo>)</mo></mrow>"
			+ "<mo>,</mo></mrow></math>";
	public final static String SAMPLE_TRIG_EX = "<math xmlns=\" http://www.w3.org/1998/Math/MathML\"><msup><mfenced close=\")\" open=\"(\"><mrow>"
			+ "<mi>cos</mi><mfenced close=\")\" open=\"(\"><mi>x</mi></mfenced></mrow></mfenced><mn>2</mn></msup><mo>+</mo><msup>"
			+ "<mfenced close=\")\" open=\"(\"><mrow><mi>sin</mi><mfenced close=\")\" open=\"(\"><mi>x</mi></mfenced></mrow></mfenced>"
			+ "<mn>2</mn></msup></math>";
	public final static String SAMPLE_HEP_PROCESS = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mi>H</mi><mo>→</mo><msup><mi>W</mi><mo>+</mo>"
			+ "</msup><msup><mi>W</mi><mo>-</mo></msup></math>";
	public final static String SAMPLE_EQ_TIMEOUT = "<math alttext=\"{}^{(b)}High\\ Energy\\ Accelerator\\ Research\\ Organization\\ (KEK),\\ Tsukuba,\\ %Ibaraki,"
			+ "\\ Japan\"><mrow><mrow><mmultiscripts><mi>H</mi><mprescripts/><none/><mrow><mo>(</mo><mi>b</mi><mo>)</mo></mrow></mmultiscripts><mo>⁢</mo><mi>i</mi>"
			+ "<mo>⁢</mo><mi>g</mi><mo>⁢</mo><mi>h</mi></mrow><mo></mo><mrow><mi>E</mi><mo>⁢</mo><mi>n</mi><mo>⁢</mo><mi>e</mi><mo>⁢</mo><mi>r</mi><mo>⁢</mo><mi>g</mi><mo>"
			+ "⁢</mo><mi>y</mi></mrow><mo></mo><mrow><mi>A</mi><mo>⁢</mo><mi>c</mi><mo>⁢</mo><mi>c</mi><mo>⁢</mo><mi>e</mi><mo>⁢</mo><mi>l</mi><mo>⁢</mo><mi>e</mi><mo>"
			+ "⁢</mo><mi>r</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>t</mi><mo>⁢</mo><mi>o</mi><mo>⁢</mo><mi>r</mi></mrow><mo></mo><mrow><mi>R</mi><mo>⁢</mo><mi>e</mi><mo>"
			+ "⁢</mo><mi>s</mi><mo>⁢</mo><mi>e</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>r</mi><mo>⁢</mo><mi>c</mi><mo>⁢</mo><mi>h</mi></mrow><mo></mo><mrow><mi>O</mi><mo>⁢"
			+ "</mo><mi>r</mi><mo>⁢</mo><mi>g</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>n</mi><mo>⁢</mo><mi>i</mi><mo>⁢</mo><mi>z</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>t</mi>"
			+ "<mo>⁢</mo><mi>i</mi><mo>⁢</mo><mi>o</mi><mo>⁢</mo><mi>n</mi></mrow><mo> </mo><mrow><mo>(</mo><mrow><mi>K</mi><mo>⁢</mo><mi>E</mi><mo>⁢</mo><mi>K</mi>"
			+ "</mrow><mo>)</mo></mrow><mo>, </mo><mrow><mi>T</mi><mo>⁢</mo><mi>s</mi><mo>⁢</mo><mi>u</mi><mo>⁢</mo><mi>k</mi><mo>⁢</mo><mi>u</mi><mo>⁢</mo><mi>b</mi>"
			+ "<mo>⁢</mo><mi>a</mi></mrow><mo>, </mo><mrow><mi>I</mi><mo>⁢</mo><mi>b</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>r</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>k</mi>"
			+ "<mo>⁢</mo><mi>i</mi></mrow><mo>, </mo><mrow><mi>J</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>p</mi><mo>⁢</mo><mi>a</mi><mo>⁢</mo><mi>n</mi></mrow></mrow></math>";
	
	public final static String[] SAMPLE_MATHML_EQUATIONS = { SAMPLE_EQ_1, SAMPLE_EQ_2,
			SAMPLE_EQ_3, SAMPLE_EQ_4, SAMPLE_EQ_5, SAMPLE_EQ_6, SAMPLE_EQ_7, SAMPLE_EQ_8, SAMPLE_EQ_9, SAMPLE_EQ_1_N, SAMPLE_EQ_TEXT, SAMPLE_HEP_PROCESS };
	//LHC instantaneous luminosity depending only on the beam parameters
	public final static String SAMPLE_LATEX_EQ_1 = "$\\mathcal{L} = \\frac{N_b^2 n_b f_{rev} \\gamma_r}{4 \\pi \\epsilon_n \\beta^*} \\mathnormal{F}$";
	
	//Electron effective mass
	public final static String SAMPLE_LATEX_EQ_2 = "${m^*}^{-1} = \\frac{1}{\\hbar^{2}} \\bigtriangledown_k \\bigtriangledown_k E$";
	
	//Fermi energy
	public final static String SAMPLE_LATEX_EQ_3 = "$E_F = \\frac{E_C + E_V}{2} + \\frac{\\kappa_{B}T}{2} \\ln \\left(\\frac{N_V}{N_C}\\right)$";
	
	public final static String SAMPLE_LATEX_EQ_4 = "$ \\bigtriangledown\\cdot\\overrightarrow{J_n} = q~R_{net} + q~\\frac{\\partial n}{\\partial t}$";
	
	public final static String SAMPLE_LATEX_EQ_5 = "$ X\\tau_{n,p} = \\tau_{min} + \\frac{\\tau_{max} - \\tau_{min}}{1 + (\\frac{N_A + N_D}{N_{ref}})^{\\gamma}} $";
	
	public static void main(String[] args) {
		for (String s: SAMPLE_MATHML_EQUATIONS) {
			System.out.println(Regex.cleanQuery(s));
		}
	}
	
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

	public enum CHARACTER_CATEGORIES {
		INTEGRALS(new HashSet<>(Arrays.asList("\u222B" // Integral
				, "\u222C" // Double integral
				, "\u222D" // Triple integral
				, "\u222E" // Contour integral
				, "\u222F" // Surface integral
				, "\u2230" // Volume integral
				, "\u2231" // Clockwise integral
				, "\u2232" // Clockwise contour integral
				, "\u2233" // Anticlockwise contour integral
				, "\u2A0B" // Summation with integral
				, "\u2A0C" // Quadruple integral
				, "\u2A0D" // Finite part integral
				, "\u2A0E" // Integral with double stroke
				, "\u2A0F" // Integral Average with slash
				, "\u2A10" // Circulation function
				, "\u2A11" // Anticlockwise integral
				, "\u2A12" // Line integration with rectangular path around pole
				, "\u2A13" // Line integration with semicircular path around
							// pole
				, "\u2A14" // Line integration not including pole
				, "\u2A15" // Integral aroud a point operator
				, "\u2A16" // Quaternion integral
				, "\u2A17" // Integral with leftwards arrow with hook
				, "\u2A18" // Integral with times sign
				, "\u2A19" // Integral with intersection
				, "\u2A1A" // Integral with union
				, "\u2A1B" // Integral with overbar
				, "\u2A14" // Line integration not including pole
				, "\u2A1C" // Integral with underbar
		))), 
		MULTIPLICATION(new HashSet<>(Arrays.asList(
				"\u2062",	//Invisible times
				"*",
				"×",
				"·")))
		, ARROWS(new HashSet<>(Arrays.asList("←" //
				, "→" //
				, "↑" //
				, "↓" //
				, "↔" //
				, "↕" //
				, "↖" //
				, "↗" //
				, "↘" //
				, "↙" //
				, "↚" //
				, "↛" //
				, "↮" //
				, "⟵" //
				, "⟶" //
				, "⟷" //
				, "⇐" //
				, "⇒" //
				, "⇑" //
				, "⇓" //
				, "⇔" //
				, "⇕" //
				, "⇖" //
				, "⇗" //
				, "⇘" //
				, "⇙" //
				, "⇍" //
				, "⇏" //
				, "⇎" //
				, "⟸" //
				, "⟹" //
				, "⟺" //
				, "⇦" //
				, "⇨" //
				, "⇧" //
				, "⇩" //
				, "⬄" //
				, "⇳" //
				, "⬀" //
				, "⬁" //
				, "⬂" //
				, "⬃" //
				, "⬅" //
				, "➡" //
				, "⬆" //
				, "⬇" //
				, "⬈" //
				, "⬉" //
				, "⬊" //
				, "⬋" //
				, "⬌" //
				, "⬍" //
				, "⇆" //
				, "⇄" //
				, "⇅" //
				, "⇵" //
				, "⇈" //
				, "⇊" //
				, "⇇" //
				, "⇉" //
				, "⬱" //
				, "⇶" //
				, "⇠" //
				, "⇢" //
				, "⇡" //
				, "⇣" //
				, "⇚" //
				, "⇛" //
				, "⤊" //
				, "⤋" //
				, "⭅" //
				, "⭆" //
				, "⟰" //
				, "⟱" //
				, "↢" //
				, "↣" //
				, "↼" //
				, "⇀" //
				, "↽" //
				, "⇁" //
				, "↿" //
				, "↾" //
				, "⇃" //
				, "⇂" //
				, "⇋" //
				, "⇌" //
				, "⟻" //
				, "⟼" //
				, "⇽" //
				, "⇾" //
				, "⇿" //
				, "⇜" //
				, "⇝" //
				, "⬳" //
				, "⟿" //
				, "⥊" //
				, "⥋" //
				, "⥌" //
				, "⥍" //
				, "⥎" //
				, "⥏" //
				, "⥐" //
				, "⥑" //
				, "⥒" //
				, "⥓" //
				, "⥔" //
				, "⥕" //
				, "⥖" //
				, "⥗" //
				, "⥘" //
				, "⥙" //
				, "⥚" //
				, "⥛" //
				, "⥜" //
				, "⥝" //
				, "⥞" //
				, "⥟" //
				, "⥠" //
				, "⥡" //
				, "⥢" //
				, "⥤" //
				, "⥣" //
				, "⥥" //
				, "⥦" //
				, "⥨" //
				, "⥧" //
				, "⥩" //
				, "⥮" //
				, "⥯" //
				, "⥪" //
				, "⥬" //
				, "⥫" //
				, "⥭" //
				, "↤" //
				, "↦" //
				, "↥" //
				, "↧" //
				, "⇤" //
				, "⇥" //
				, "⤒" //
				, "⤓" //
				, "↨" //
				, "↞" //
				, "↠" //
				, "↟" //
				, "↡" //
				, "⇷" //
				, "⇸" //
				, "⤉" //
				, "⤈" //
				, "⇹" //
				, "⇺" //
				, "⇻" //
				, "⇞" //
				, "⇟" //
				, "⇼" //
				, "⬴" //
				, "⤀" //
				, "⬵" //
				, "⤁" //
				, "⬹" //
				, "⤔" //
				, "⬺" //
				, "⤕" //
				, "⤂" //
				, "⤃" //
				, "⤄" //
				, "⬶" //
				, "⤅" //
				, "⬻" //
				, "⤖" //
				, "⬷" //
				, "⤐" //
				, "⬼" //
				, "⤗" //
				, "⬽" //
				, "⤘" //
				, "⤆" //
				, "⤇" //
				, "⤌" //
				, "⤍" //
				, "⤎" //
				, "⤏" //
				, "⬸" //
				, "⤑" //
				, "⤝" //
				, "⤞" //
				, "⤟" //
				, "⤠" //
				, "⤙" //
				, "⤚" //
				, "⤛" //
				, "⤜" //
				, "⤡" //
				, "⤢" //
				, "⤣" //
				, "⤤" //
				, "⤥" //
				, "⤦" //
				, "⤪" //
				, "⤨" //
				, "⤧" //
				, "⤩" //
				, "⤭" //
				, "⤮" //
				, "⤯" //
				, "⤰" //
				, "⤱" //
				, "⤲" //
				, "⤫" //
				, "⤬" //
				, "↰" //
				, "↱" //
				, "↲" //
				, "↳" //
				, "⬐" //
				, "⬎" //
				, "⬑" //
				, "⬏" //
				, "↴" //
				, "↵" //
				, "⤶" //
				, "⤷" //
				, "⤴" //
				, "⤵" //
				, "↩" //
				, "↪" //
				, "↫" //
				, "↬" //
				, "⥼" //
				, "⥽" //
				, "⥾" //
				, "⥿" //
				, "⥂" //
				, "⥃" //
				, "⥄" //
				, "⭀" //
				, "⥱" //
				, "⥶" //
				, "⥸" //
				, "⭂" //
				, "⭈" //
				, "⭊" //
				, "⥵" //
				, "⭁" //
				, "⭇" //
				, "⭉" //
				, "⥲" //
				, "⭋" //
				, "⭌" //
				, "⥳" //
				, "⥴" //
				, "⥆" //
				, "⥅" //
				, "⥹" //
				, "⥻" //
				, "⬰" //
				, "⇴" //
				, "⥈" //
				, "⬾" //
				, "⥇" //
				, "⬲" //
				, "⟴" //
				, "⥷" //
				, "⭃" //
				, "⥺" //
				, "⭄" //
				, "⇱" //
				, "⇲" //
				, "↸" //
				, "↹" //
				, "↯" //
				, "↭" //
				, "⥉" //
				, "⥰" //
				, "⬿" //
				, "⤳" //
				, "↜" //
				, "↝" //
				, "⤼" //
				, "⤽" //
				, "↶" //
				, "↷" //
				, "⤾" //
				, "⤿" //
				, "⤸" //
				, "⤹" //
				, "⤺" //
				, "⤻" //
				, "↺" //
				, "↻" //
				, "⥀" //
				, "⥁" //
				, "⟲" //
				, "⟳" //
				, "➳" //
				, "➴" //
				, "➵" //
				, "➶" //
				, "➷" //
				, "➸" //
				, "➹" //
				, "➔" //
				, "➘" //
				, "➙" //
				, "➚" //
				, "⇪" //
				, "⇫" //
				, "⇬" //
				, "⇭" //
				, "⇮" //
				, "⇯" //
				, "➩" //
				, "➪" //
				, "➫" //
				, "➬" //
				, "➭" //
				, "➮" //
				, "➯" //
				, "➱" //
				, "⇰" //
				, "➛" //
				, "➜" //
				, "➝" //
				, "➞" //
				, "➟" //
				, "➠" //
				, "➢" //
				, "➣" //
				, "➤" //
				, "➥" //
				, "➦" //
				, "➧" //
				, "➨" //
				, "➲" //
				, "➺" //
				, "➻" //
				, "➼" //
				, "➽" //
				, "➾" //
				, "◄" //
				, "►" //
				, "◅" //
				, "▻" //
				, "☚" //
				, "☛" //
				, "☜" //
				, "☝" //
				, "☞" //
				, "☟" //
		)));
		private Set<String> characters;

		private CHARACTER_CATEGORIES(Set<String> characters) {
			this.characters = characters;
		}

		public Set<String> getCharacters() {
			return characters;
		}
	}

	public static final Map<String, CHARACTER_CATEGORIES> characterToCategoryMap = initCharactersMap();

	private static Map<String, CHARACTER_CATEGORIES> initCharactersMap() {
		Map<String, CHARACTER_CATEGORIES> map = new HashMap<>();
		for (CHARACTER_CATEGORIES category : CHARACTER_CATEGORIES.values()) {
			for (String character : category.characters) {
				map.put(character, category);
			}
		}
		return map;
	}
}

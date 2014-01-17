package cern.ch.mathexplorer.mathematica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.OSUtils;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class MathematicaIntegrator {
	
	private static MathematicaIntegrator instance;
	private KernelLink ml = null;
	private LinkedList<StructuralFeature> features = new LinkedList<>();
	
	private final static String EMPTY_RESULT = "{}";
	
	public static MathematicaIntegrator getInstance() {
		if (instance == null) {
			instance = new MathematicaIntegrator();
		}
		return instance;
	}
	
	private MathematicaIntegrator() {
		String command = "";
		if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
			command = "C:/Program Files/Wolfram Research/Mathematica/9.0/MathKernel.exe";
		}
		if (OSUtils.getOS().equals(OSUtils.OS.LINUX)) {
			command = "math";
		}
		String [] extraArgs = {"-linkmode", "launch", "-linkname", command + " -mathlink"};
		String jLinkDir = Constants.getMathematicaLocation();
		System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
		try {
			ml = MathLinkFactory.createKernelLink(extraArgs);
			loadFeatures();
		} catch (MathLinkException e) {
			System.out.println("Fatal error opening link: " + e.getMessage());
			return;
		}
	}
	
	private void loadFeatures() {
		features.add(new StructuralFeature("simple_sum", "x_+y_"));
		features.add(new StructuralFeature("simple_substraction", "x_-y_"));
		features.add(new StructuralFeature("simple_product", "x_*y_"));
		features.add(new StructuralFeature("simple_division", "x_/y_"));
		features.add(new StructuralFeature("sine", "Sin[x_]"));
		features.add(new StructuralFeature("cosine", "Cos[x_]"));
		features.add(new StructuralFeature("tangent", "Tan[x_]"));
		
	}

	/**
	 * This method should be called after an invokation of the tryInterpretRoot
	 * sincew it relies on the Mathematica variable interpretedResults holding 
	 * a list of all the interpreted subExpressions
	 * @throws MathLinkException 
	 */
	public Set<StructuralFeature> getPatterns() throws MathLinkException {
		
		int subExpressionsNumber = Integer.parseInt(ml.evaluateToOutputForm(("Length[interpretedResults]"), 0));
		Set<StructuralFeature> result = new HashSet<>();
		for (int i = 1; i <= subExpressionsNumber; i++) {
			System.out.println(ml.evaluateToOutputForm("currentExpression = interpretedResults[["+i+"]]",0));
			for (StructuralFeature currentFeature : features ) {
				
			}
		}
		return result;
	}
	
	static String testExpression1 = "<math><mrow><mi>U</mi><mo>==</mo><mrow><mo>(</mo><mi>y</mi><mo>-</mo><msub><mi>y</mi><mn>0</mn></msub><mo>)</mo></mrow><msub><mi>W</mi><mn>0</mn></msub><mo></mo><mo>,</mo><mi>δ</mi><msup><mi>ϕ</mi><mi>A</mi></msup><mo>==</mo><msup><mi>e</mi><mrow><mo>-</mo><mrow><mfrac><mn>1</mn><mn>3</mn></mfrac><mo>⁢</mo><msub><mi>Δ</mi><mrow><mo>(</mo><mi>A</mi><mo>)</mo></mrow></msub><mo>⁢</mo><msub><mi>W</mi><mn>0</mn></msub><mo>⁢</mo><mrow><mo>(</mo><mrow><mi>y</mi><mo>-</mo><msub><mi>y</mi><mn>0</mn></msub></mrow><mo>)</mo></mrow></mrow></mrow></msup><mo>==</mo><msup><mi>e</mi><mrow><mo>-</mo><mrow><mfrac><mn>1</mn><mn>3</mn></mfrac><mo>⁢</mo><msub><mi>Δ</mi><mrow><mo>(</mo><mi>A</mi><mo>)</mo></mrow></msub><mo>⁢</mo><mi>U</mi></mrow></mrow></msup></mrow></math>";	
	static String testExpression2 = "<math><mrow><mrow><msub><mi>𝐀</mi><mi>μ</mi></msub><mo>=</mo><mrow><mrow><mi>𝐧</mi><mo>⁢</mo><msub><mi>C</mi><mi>μ</mi></msub></mrow><mo>+</mo><mrow><mrow><mo>(</mo><mrow><msub><mo>∂</mo><mi>μ</mi></msub><mo>⁡</mo><mi>𝐧</mi></mrow><mo>)</mo></mrow><mo>×</mo><mi>𝐧</mi></mrow><mo>+</mo><msub><mi>𝐖</mi><mi>μ</mi></msub></mrow></mrow><mo>,</mo></mrow></math>";
	static String testExpression3 = "<math><msub><mi>S</mi><mrow><mi>B</mi><mo>⁢</mo><mover><mi>A</mi><mo>¯</mo></mover></mrow></msub></math>";
	
	public String importString(String mathMlExpression) throws MathLinkException {
		ml.discardAnswer();
		String result = ml.evaluateToOutputForm("importedString = ImportString[\"" + mathMlExpression + "\", \"MathML\"]",0);
		System.out.println(result);
		return result;
	}
	
	/**
	 * Tries to interpret the given mathml equation
	 * The subexpressions that could be interpreted correctly are kept in the result
	 * by aplying the filtering pattern HoldComplete[x_] 
	 */
	public String tryInterpretByRowBox() throws MathLinkException {
		ml.evaluateToInputForm("boxForm = importedString", 0);
		ml.evaluateToOutputForm("boxesPositions = Position[boxForm, RowBox]", 0);
		String firstElement = ml.evaluateToOutputForm("firstElement = boxesPositions[[1]]", 0);
		String firstElementFixed = firstElement.replace("0}", "1}");
		String result = ml.evaluateToOutputForm("interpretedResults = Cases[MakeExpression[Extract[boxForm, "+firstElementFixed+"]], HoldComplete[x_]]", 0);
		System.out.println(result);
		return result;
	}
	
	/**
	 * 
	 */
	public String tryInterpretOriginalString() throws MathLinkException {
		String result = ml.evaluateToOutputForm("MakeExpression[importedString]", 0); 
		System.out.println(result);
		return result;
	}
	
	public static void main(String[] args) throws MathLinkException {
		MathematicaIntegrator mi = getInstance();
		String expression = mi.prepareMathMLString(testExpression2);
		mi.importString(expression);
		boolean ok = false;
		
		if (!mi.tryInterpretOriginalString().equals(EMPTY_RESULT)) {
			ok = true;
		}
		if (!mi.tryInterpretByRowBox().equals(EMPTY_RESULT)) {
			ok = true;
		}
		if (ok) {
			mi.getPatterns();
		}
	}
	
	public String prepareMathMLString(String equation) {
		return equation.replace("<mo>=</mo>", "<mo>==</mo>");
	}
}


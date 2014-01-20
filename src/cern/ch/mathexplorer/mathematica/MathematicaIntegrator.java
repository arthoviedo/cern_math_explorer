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

	private final static String HOLD_COMPLETE = "HoldComplete";

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
		String[] extraArgs = { "-linkmode", "launch", "-linkname",
				command + " -mathlink" };
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
		features.add(new StructuralFeature("numeric_fraction",
				"x_Integer/y_Integer"));
		features.add(new StructuralFeature("numeric_fraction_1_numerator",
				"1/x_Integer"));
		features.add(new StructuralFeature("sine", "Sin[x_]"));
		features.add(new StructuralFeature("cosine", "Cos[x_]"));
		features.add(new StructuralFeature("tangent", "Tan[x_]"));
		features.add(new StructuralFeature("exponential", "e^x_"));
		features.add(new StructuralFeature("quadratic", "a_.+b_.x_+c_.x_^2"));
		features.add(new StructuralFeature(
				"sum_same_symbol_different_subscript",
				"a_*Subscript[x_, y_] + b_*Subscript[x_, z_]"));
	}

	/**
	 * This method should be called after an invokation of the tryInterpretRoot
	 * sincew it relies on the Mathematica variable interpretedResults holding a
	 * list of all the interpreted subExpressions
	 * 
	 * @throws MathLinkException
	 */
	public List<StructuralFeature> getPatterns() throws MathLinkException {

		boolean couldInterpret = false;
		if (tryInterpretOriginalString()) {
			couldInterpret = true;
		} else if (tryInterpretByRowBox()) {
			couldInterpret = true;
		}
		if (!couldInterpret) {
			return new ArrayList<>();
		}
		Set<StructuralFeature> result = new HashSet<>();
		int subExpressionsNumber = Integer.parseInt(ml.evaluateToOutputForm(
				("Length[interpretedResults]"), 0));
		for (int i = 1; i <= subExpressionsNumber; i++) {
			System.out.println(ml.evaluateToOutputForm(
					"currentExpression = interpretedResults[[" + i + "]]", 0));
			for (StructuralFeature currentFeature : features) {
				String resultFeature = ml.evaluateToOutputForm(
						"Position[currentExpression, "
								+ currentFeature.getPattern() + "]", 0);
				if (!resultFeature.equals(EMPTY_RESULT)) {
					result.add(currentFeature);
				}
			}
		}
		return new ArrayList(result);
	}

	public String importString(String mathMlExpression)
			throws MathLinkException {
		mathMlExpression = prepareMathMLString(mathMlExpression);
		ml.discardAnswer();
		String result = ml.evaluateToOutputForm(
				"importedString = ImportString[\"" + mathMlExpression
						+ "\", \"MathML\"]", 0);
		System.out.println(result);
		return result;
	}

	/**
	 * Tries to interpret the given mathml equation The subexpressions that
	 * could be interpreted correctly are kept in the result by aplying the
	 * filtering pattern HoldComplete[x_]
	 */
	public boolean tryInterpretByRowBox() throws MathLinkException {
		System.out.println("Trying interpret by row boxes");
		ml.evaluateToInputForm("boxForm = importedString", 0);
		ml.evaluateToOutputForm("boxesPositions = Position[boxForm, RowBox]", 0);
		String firstElement = ml.evaluateToOutputForm(
				"firstElement = boxesPositions[[1]]", 0);
		String firstElementFixed = firstElement.replace("0}", "1}");
		String result = ml.evaluateToOutputForm(
				"interpretedResults = Cases[MakeExpression[Extract[boxForm, "
						+ firstElementFixed + "]], HoldComplete[x_]]", 0);
		System.out.println(result);
		return !result.equals(EMPTY_RESULT);
	}

	/**
	 * 
	 */
	public boolean tryInterpretOriginalString() throws MathLinkException {
		System.out.println("Trying interpret original String");
		String result = ml.evaluateToOutputForm(
				"interpretedResults = MakeExpression[importedString]", 0);
		System.out.println(result);
		return result.contains(HOLD_COMPLETE);
	}

	public String prepareMathMLString(String equation) {
		equation = equation.replace("<mo>=</mo>", "<mo>==</mo>");
		equation = equation.replace("\"", "\\\"");
		return equation;
	}

	public static void main(String[] args) throws MathLinkException {
		MathematicaIntegrator mi = getInstance();
		String expression = Constants.SAMPLE_EQUATION_1;
		System.out.println(expression);
		mi.importString(expression);
		List<StructuralFeature> features = mi.getPatterns();
		for (StructuralFeature f : features) {
			System.out.println(f.getName());
		}
	}

}

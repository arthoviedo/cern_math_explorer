package cern.ch.mathexplorer.mathematica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.OSUtils;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class MathematicaEngine {

	private final static String EMPTY_RESULT = "{}";
	private final static String HOLD_COMPLETE = "HoldComplete";

	/**
	 * Singleton instance
	 */
	private static MathematicaEngine instance;
	private KernelLink ml = null;
	private ArrayList<StructuralFeature> features = new ArrayList<>();

	public static MathematicaEngine getInstance() {
		if (instance == null) {
			instance = new MathematicaEngine();
		}
		return instance;
	}

	private MathematicaEngine() {
		String command = "";
		command = Constants.getMathematicaCommand();
		String[] extraArgs = { "-linkmode", "launch", "-linkname",
				command + " -mathlink" };
		String jLinkDir = Constants.getMathematicaLinkLocation();
		System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
		try {
			ml = MathLinkFactory.createKernelLink(extraArgs);
			loadFeatures();
			ml.discardAnswer();
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
	public List<StructuralFeature> getPatterns(String mathMLExpression) throws MathLinkException {
		
		try {
			importString(mathMLExpression);
			
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
			String sizeString = ml.evaluateToOutputForm(("Length[interpretedResults]"), 0);
			int subExpressionsNumber = Integer.parseInt(sizeString);
			for (int i = 1; i <= subExpressionsNumber; i++) {
				String currentExpression = ml.evaluateToOutputForm("currentExpression = interpretedResults[[" + i + "]]", 0);
				for (StructuralFeature currentFeature : features) {
					String resultFeature = ml.evaluateToOutputForm(
							"Position[currentExpression, "
									+ currentFeature.getPattern() + "]", 0);
					if (!resultFeature.equals(EMPTY_RESULT)) {
						result.add(currentFeature);
					}
				}
			}
			clearVariables();
			return new ArrayList<StructuralFeature>(result);
		} catch ( Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
		
	}
	
	/**
	 * Removes all symbols definitions.
	 * This helps to prevent blocks when importing an equation because
	 * of previous relationships between the symbols of the imported equations
	 */
	private void clearVariables () {
		ml.evaluateToOutputForm("Remove[\"Global`*\"];", 0);
	}

	/**
	 * Tries to import the given MathML expression.
	 * The result is stored in the variable importedString
	 * This variable would be used afterwards when tryin to be interpreted
	 * as a math expression.
	 * For more information about the command visit:
	 * http://reference.wolfram.com/mathematica/ref/ImportString.html
	 * @param mathMlExpression
	 * @return
	 * @throws MathLinkException
	 */
	private String importString(String mathMlExpression)
			throws MathLinkException {
		mathMlExpression = prepareMathMLString(mathMlExpression);
		String result = ml.evaluateToOutputForm(
				"importedString = ImportString[\"" + mathMlExpression
						+ "\", \"MathML\"]", 0);
		Console.print(result);
		return result;
	}

	/**
	 * Tries to interpret the expression stored in the "importedString" variable.
	 * If the original MathML string could not be interpreted, this function
	 * tries to decompose the string by finding the RowBoxes that make up the expression
	 * and tries to interpret the root element. 
	 * For more information:
	 * http://reference.wolfram.com/mathematica/ref/MakeExpression.html
	 * The subexpressions that could be interpreted correctly are kept in the 
	 * result by applying the filtering pattern HoldComplete[x_]
	 */
	private boolean tryInterpretByRowBox() throws MathLinkException {
		ml.evaluateToInputForm("boxForm = importedString", 0);
		ml.evaluateToOutputForm("boxesPositions = Position[boxForm, RowBox]", 0);
		String firstElement = ml.evaluateToOutputForm(
				"firstElement = boxesPositions[[1]]", 0);
		String firstElementFixed = firstElement.replace("0}", "1}");
		String result = ml.evaluateToOutputForm(
				"interpretedResults = Cases[MakeExpression[Extract[boxForm, "
						+ firstElementFixed + "]], HoldComplete[x_]]", 0);
		return !result.equals(EMPTY_RESULT);
	}

	/**
	 * Tries to interpret the expression 
	 */
	private boolean tryInterpretOriginalString() throws MathLinkException {
		String result = ml.evaluateToOutputForm(
				"interpretedResults = MakeExpression[importedString]", 0);
		return result.contains(HOLD_COMPLETE);
	}

	private String prepareMathMLString(String equation) {
		equation = equation.replace("<mo>=</mo>", "<mo>==</mo>");
		equation = equation.replace("\"", "\\\"");
		return equation;
	}

	public static void main(String[] args) throws MathLinkException {
		MathematicaEngine mi = getInstance();
		String expression = Constants.SAMPLE_EQUATION_17;
		Console.print(expression);
		List<StructuralFeature> features = mi.getPatterns(expression);
		for (StructuralFeature f : features) {
			Console.print(f.getName());
		}
	}

}

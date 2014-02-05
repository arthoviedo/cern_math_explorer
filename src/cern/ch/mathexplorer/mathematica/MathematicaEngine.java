package cern.ch.mathexplorer.mathematica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Constants;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class MathematicaEngine {

	private final static String EMPTY_RESULT = "{}";
	private final static String HOLD_COMPLETE = "HoldComplete";

	/**
	 * Singleton instance
	 */
	// private static MathematicaEngine instance;
	private KernelLink ml = null;

	private static final Map<String, MathematicaEngine> instances = new HashMap<String, MathematicaEngine>();

	public static MathematicaEngine getInstance(String key) {
		MathematicaEngine instance = instances.get(key);
		if (instance == null) {
			instance = new MathematicaEngine();

			instances.put(key, instance);
		}
		return instance;
	}

	private MathematicaEngine() {
		try {
			initLink();
		} catch (MathLinkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initLink() throws MathLinkException {
		String command = "";
		command = Constants.getMathematicaCommand();
		String[] extraArgs = { "-linkmode", "launch", "-linkname",
				command + " -mathlink" };

		String jLinkDir = Constants.getMathematicaLinkLocation();
		System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
		
		try {
			KernelLink newLink = MathLinkFactory.createKernelLink(extraArgs);
			// ml.clearError();
			
			newLink.discardAnswer();
			ml = newLink;
		} catch (MathLinkException e) {
			e.printStackTrace();
			Console.print("Fatal error opening link: " + e.getMessage());
			throw e;
		}

	}

	private void killMathKernel() {
		try {
			String command1 = "killall -s 9 Mathematica";
			String command2 = "killall -s 9 MathKernel";

			Runtime.getRuntime().exec(command1);
			Runtime.getRuntime().exec(command2);
		} catch (Exception e) {

		}
	}



	/**
	 * This method should be called after an invokation of the tryInterpretRoot
	 * sincew it relies on the Mathematica variable interpretedResults holding a
	 * list of all the interpreted subExpressions
	 * 
	 * @throws MathLinkException
	 */
	public List<StructuralPattern> getPatterns(String mathMLExpression)
			throws MathLinkException {
		if (ml == null) {
			try {
				initLink();
			} catch (Exception e) {
				return new ArrayList<>();
			}
		}
		try {
			Console.print(mathMLExpression);
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
			Set<StructuralPattern> result = new HashSet<>();
			String sizeString = ml.evaluateToOutputForm(
					("Length[interpretedResults]"), 0);
			int subExpressionsNumber = Integer.parseInt(sizeString);
			HashSet<String> analyzedExpressions = new HashSet<>();
			for (int i = 1; i <= subExpressionsNumber; i++) {
				String currentExpression = ml.evaluateToOutputForm(
						"currentExpression = interpretedResults[[" + i + "]]",
						0);
				Console.print(currentExpression);
				if (!analyzedExpressions.contains(currentExpression)) {
					for (StructuralPattern currentFeature : Patterns.getPatterns()) {
						String resultFeature = ml.evaluateToOutputForm(
								"Position[currentExpression, "
										+ currentFeature.getPattern() + "]", 0);
						if (!resultFeature.equals(EMPTY_RESULT)) {
							result.add(currentFeature);
						}
					}
					analyzedExpressions.add(currentExpression);
				}
			}
			clearVariables();
			return new ArrayList<StructuralPattern>(result);
		} catch (Exception e) {
			e.printStackTrace();
			Console.print("Error while importing String");
			// TODO: Solve better
			// Just in case, we restart our link
			// Cleanup operation can still fail
			try {
				killMathKernel();
				initLink();
			} catch (Exception e1) {
				Console.print("Error while cleaning up");
				e1.printStackTrace();
			}
			return new ArrayList<>();
		}

	}

	/**
	 * Removes all symbols definitions. This helps to prevent blocks when
	 * importing an equation because of previous relationships between the
	 * symbols of the imported equations
	 */
	private void clearVariables() {
		ml.evaluateToOutputForm("Remove[\"Global`*\"];", 0);
	}

	/**
	 * Tries to import the given MathML expression. The result is stored in the
	 * variable importedString This variable would be used afterwards when tryin
	 * to be interpreted as a math expression. For more information about the
	 * command visit:
	 * http://reference.wolfram.com/mathematica/ref/ImportString.html
	 * 
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
		if (result == null) {
			Console.print(ml.getLastError());
			Console.print("Error while importing string (" + ml.error() + "):"
					+ ml.errorMessage());
			throw new MathLinkException(ml.error());
		}
		Console.print(result);
		return result;
	}

	/**
	 * Tries to interpret the expression stored in the "importedString"
	 * variable. If the original MathML string could not be interpreted, this
	 * function tries to decompose the string by finding the RowBoxes that make
	 * up the expression and tries to interpret the root element. For more
	 * information:
	 * http://reference.wolfram.com/mathematica/ref/MakeExpression.html The
	 * subexpressions that could be interpreted correctly are kept in the result
	 * by applying the filtering pattern HoldComplete[x_]
	 */
	private boolean tryInterpretByRowBox() throws MathLinkException {
		ml.evaluateToInputForm("boxForm = importedString", 0);
		ml.evaluateToInputForm("interpretedResults = List[]", 0);
		String positions = ml.evaluateToOutputForm(
				"boxesPositions = Position[boxForm, RowBox]", 0);
		positions = positions.substring(1, positions.length() - 1);
		Console.print(positions);

		String sizePositions = ml.evaluateToOutputForm(
				("Length[boxesPositions]"), 0);
		int numberOccurences = Integer.parseInt(sizePositions);

		String result = "";
		for (int i = 1; i <= numberOccurences; i++) {
			String subExpressionPos = ml.evaluateToOutputForm(
					"subexpressionPos=boxesPositions[[" + i + "]]", 0);
			String subExpressionPosFixed = subExpressionPos.replace("0}", "1}");
			result = ml.evaluateToOutputForm(
					"AppendTo[interpretedResults, Cases[MakeExpression[Extract[boxForm, "
							+ subExpressionPosFixed + "]], HoldComplete[x_]]]",
					0);
		}
		if (result == null) {
			Console.print("Error while interpreting original string ("
					+ ml.error() + "):" + ml.errorMessage());
			throw new MathLinkException(ml.error());
		}
		return !result.equals(EMPTY_RESULT);
	}

	/**
	 * Tries to interpret the expression
	 */
	private boolean tryInterpretOriginalString() throws MathLinkException {
		String result = ml.evaluateToOutputForm(
				"interpretedResults = MakeExpression[importedString]", 0);
		if (result == null) {
			Console.print("Error while interpreting original string ("
					+ ml.error() + "):" + ml.errorMessage());
			throw new MathLinkException(ml.error());
		}
		return result.contains(HOLD_COMPLETE);
	}

	private String prepareMathMLString(String equation) {
		equation = equation.replace("<mo>=</mo>", "<mo>==</mo>");
		equation = equation.replace("<mo>≡</mo>", "<mo>==</mo>");
		equation = equation.replace("\"", "\\\"");
		return equation;
	}

	public static void main(String[] args) throws Exception {
		test();
		MathematicaEngine mi = getInstance("TESTING");
		String expression = "<math alttext=\"\\frac{dP(0,0,...;t)}{dt}=Q+u\\sum_{nn}{P(0,0,...;t)}-(2du+k)P(0,0,...;t)\"><mrow><mfrac><mrow><mi>d</mi><mo>⁢</mo><mi>P</mi><mo>⁢</mo><mrow><mo>(</mo><mrow><mn>0</mn><mo>,</mo><mn>0</mn><mo>,</mo><mi>…</mi><mo>;</mo><mi>t</mi></mrow><mo>)</mo></mrow></mrow><mrow><mi>d</mi><mo>⁢</mo><mi>t</mi></mrow></mfrac><mo>=</mo><mrow><mi>Q</mi><mo>+</mo><mrow><mi>u</mi><mo>⁢</mo><mrow><munder><mo>∑</mo><mrow><mi>n</mi><mo>⁢</mo><mi>n</mi></mrow></munder><mrow><mi>P</mi><mo>⁢</mo><mrow><mo>(</mo><mrow><mn>0</mn><mo>,</mo><mn>0</mn><mo>,</mo><mi>…</mi><mo>;</mo><mi>t</mi></mrow><mo>)</mo></mrow></mrow></mrow></mrow><mo>-</mo><mrow><mrow><mo>(</mo><mrow><mrow><mn>2</mn><mo>⁢</mo><mi>d</mi><mo>⁢</mo><mi>u</mi></mrow><mo>+</mo><mi>k</mi></mrow><mo>)</mo></mrow><mo>⁢</mo><mi>P</mi><mo>⁢</mo><mrow><mo>(</mo><mrow><mn>0</mn><mo>,</mo><mn>0</mn><mo>,</mo><mi>…</mi><mo>;</mo><mi>t</mi></mrow><mo>)</mo></mrow></mrow></mrow></mrow></math>";
		Console.print(expression);
		List<StructuralPattern> features = mi.getPatterns(expression);
		for (StructuralPattern f : features) {
			Console.print(f.getName());
		}
	}
	
	public static void test() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File("/share/math/arxiv_cds/arx1312.6708.eq")));
		String line = "";
		MathematicaEngine instance = getInstance("TESTING");
		int count = 0;
		while ( (line = br.readLine())!=null ) {
			instance.getPatterns(line);
			Console.print(count++);
		}
		br.close();
	}

}

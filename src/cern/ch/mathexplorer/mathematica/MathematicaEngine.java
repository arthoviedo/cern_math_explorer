package cern.ch.mathexplorer.mathematica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;

import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Constants;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;

public class MathematicaEngine {

	private final static String EMPTY_RESULT = "{}";
	
	// The imported string contained text instead of an actual equation
	private final static String TEXT_IN_EQUATION = "XML`MathML`Symbols`Multiscripts";
	private final static String HOLD_COMPLETE = "HoldComplete";

	private KernelLink ml = null;

	/**
	 * Multiton pattern, one instance per type of element
	 */
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
			ml = MathematicaUtils.getNewLink();
		} catch (MathLinkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * This method should be called after an invokation of the tryInterpretRoot
	 * sincew it relies on the Mathematica variable interpretedResults holding a
	 * list of all the interpreted subExpressions
	 * Returns a list of the structural patterns that were identified in the given expression
	 * If the process takes more than the defined timeout, an empty result would be returned.
	 */
	public List<StructuralPattern> getPatternsWithTimeout(String mathMLExpression){
		List<StructuralPattern> result = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(new MathematicaTask(mathMLExpression, TASK.GET_PATTERNS));
        try {
        	result = (List<StructuralPattern>) future.get(MathematicaConfig.TIMEOUT_TIME, MathematicaConfig.TIMEOUT_TIMEUNIT);
        } catch (TimeoutException te) {
        	ml.abortEvaluation();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return result;
	}
	
	/**
	 * This method should be called after an invokation of the tryInterpretRoot
	 * sincew it relies on the Mathematica variable interpretedResults holding a
	 * list of all the interpreted subExpressions
	 * 
	 * @throws MathLinkException
	 */
	private List<StructuralPattern> getPatterns(String mathMLExpression)
			throws MathLinkException {
		if (ml == null) {
			try {
				ml = MathematicaUtils.getNewLink();
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
					if(currentExpression.contains(TEXT_IN_EQUATION)){
						continue;
					}
					for (StructuralPattern currentFeature : Patterns
							.getPatterns()) {
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
			cleanLink();
			return new ArrayList<>();
		}
	
	}

	public String simplyExpressionWithTimeout(String mathMLExpression){
		if (MathematicaConfig.NORMALIZATION_MODE.equals(MathematicaConfig.NORMALIZATION_MODES.FULL_SIMPLIFICATION)) {
			return simplyExpressionWithTimeout(mathMLExpression, true);
		} else {
			return simplyExpressionWithTimeout(mathMLExpression, false);
		}
	}

	
	/**
	 * Calls the method for simplifyin a given string.
	 * If the method takes more than the defined timeout, the original string is returned 
	 */
	public String simplyExpressionWithTimeout(String mathMLExpression, boolean full){
		ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(new MathematicaTask(mathMLExpression, TASK.SIMPLIFY_EXPRESSION, full));
        try {
        	return (String) future.get(MathematicaConfig.TIMEOUT_TIME, MathematicaConfig.TIMEOUT_TIMEUNIT);
        } catch (TimeoutException te) {
        	ml.abortEvaluation();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return mathMLExpression;
	}
	
	private void cleanLink(){
		try {
			MathematicaUtils.killMathKernel(ml);
			ml = MathematicaUtils.getNewLink();
		} catch (Exception e1) {
			Console.print("Error while cleaning up");
			e1.printStackTrace();
		}
	}
	
	/**
	 * Tries to apply the SIMPLIFY/FULL_SIMPLIFY procedures to the given
	 * expression If the process cannot be done, it return the original given
	 * string
	 */
	private String simplifyExpression(String mathMLExpression, boolean full) {
		try {
	
			importString(mathMLExpression);
			String mode = full? "FullSimplify":"Simplify";
			String result = ml.evaluateToOutputForm("ExportString["+mode+"[ReleaseHold[MakeExpression[importedString]]],\"MathML\"]", 0);
			clearVariables();
			if (result.contains("<merror>") || result.contains("$Failed")) { // There was some error in the process, so we return the original string
				return mathMLExpression;
			}
			result = result.replaceAll("\\r|\\n|  +", "");
			result = result.replaceAll("<annotation.*?</annotation>", "");
			
			
			return StringEscapeUtils.unescapeHtml4(result);
		} catch (Exception e) {
			e.printStackTrace();
			Console.print("Error while importing String");
			// TODO: Solve better
			// Just in case, we restart our link
			// Cleanup operation can still fail
			cleanLink();
			return mathMLExpression;
		}
	}

	enum TASK {
		GET_PATTERNS,
		SIMPLIFY_EXPRESSION;
	}

	private class MathematicaTask implements Callable {

		String mathMLExpression;
		TASK taskType;
		Object [] args;
		
		public MathematicaTask(String mathMLExpression, TASK taskType, Object ... extraArgs) {
			this.mathMLExpression = mathMLExpression;
			this.taskType = taskType;
			this.args = extraArgs;
		}
		
		@Override
		public Object call() throws Exception {
			if(taskType.equals(TASK.GET_PATTERNS)) {
				return getPatterns(mathMLExpression);
			}
			else if(taskType.equals(TASK.SIMPLIFY_EXPRESSION)) {
				boolean full = (Boolean) args[0];
				return simplifyExpression(mathMLExpression, full);
			}
			 
			throw new UnsupportedOperationException("Task type not recognized");
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
		return !result.equals(EMPTY_RESULT) && !result.contains(TEXT_IN_EQUATION);
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
		if (result.contains(TEXT_IN_EQUATION)) 
			return false;
		return result.contains(HOLD_COMPLETE);
	}

	private String prepareMathMLString(String equation) {
		equation = equation.replace("<mo>=</mo>", "<mo>==</mo>");
		equation = equation.replace("<mo>≡</mo>", "<mo>==</mo>");
		equation = equation.replace("\"", "\\\"");
		return equation;
	}

	private enum SimplifyMode {
		SIMPLIFY, FULL_SIMPLIFY;
	}

	public static void main(String[] args) throws Exception {
		test();
		
		MathematicaEngine mi = getInstance("TESTING");
		String expression = Constants.SAMPLE_EQ_5;
		Console.print(expression);
		List<StructuralPattern> features = mi.getPatterns(expression);
		for (StructuralPattern f : features) {
			Console.print(f.getPattern());
		}
		Console.print(mi.simplifyExpression(expression, false));
		Console.print(mi.simplifyExpression(expression, true));

	}
	
	public static void test() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"./data/arxiv_cds/arx1312.6708.eq")));
		String line = "";
		MathematicaEngine instance = getInstance("TESTING");
		int count = 0;
		while ((line = br.readLine()) != null) {
			instance.simplyExpressionWithTimeout(line);
			instance.getPatternsWithTimeout(line);
			//instance.getPatterns(line);
			//instance.simplifyExpression(line, true);
			
			Console.print(count++);
		}
		br.close();
	}

}

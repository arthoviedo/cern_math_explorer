package cern.ch.mathexplorer.mathprocessing;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cern.ch.mathexplorer.utils.Constants;

public class ExpressionGeneralizator {
	private static Pattern mathIdPattern = Pattern.compile("<mi>.*?</mi>");
	private static String mathIdTagOpening = "<mi>";
	private static String mathIdTagEnding = "</mi>";
	
	
	public static String generalizateExpression(String mathMLExpression) {
		String result = mathMLExpression;
		Hashtable<String, String> replacements = new Hashtable<>();
		int fromIndex = 0;
		int foundIndex = -1;
		int idCounter = 1;
		while ( (foundIndex = mathMLExpression.indexOf(mathIdTagOpening, fromIndex)) != -1) {
			int endIndex = mathMLExpression.indexOf(mathIdTagEnding, foundIndex);
			String id = mathMLExpression.substring(foundIndex+4, endIndex);
			if(!replacements.containsKey(id)) {
				String replacement = "VAR"+idCounter++;
				replacements.put(id, replacement);
				result = result.replaceAll(mathIdTagOpening+id+mathIdTagEnding, mathIdTagOpening+replacement+mathIdTagEnding);
			}
			fromIndex = foundIndex +1;
		}
		return result;
	}
	
	public static void main(String[] args) {
		for (String expression : Constants.SAMPLE_EQUATIONS) {
			System.out.println(expression);
			System.out.println(generalizateExpression(expression.replace("\n", "")));
			System.out.println("------------------------");
		}
	}
}

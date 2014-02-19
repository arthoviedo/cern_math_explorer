package cern.ch.mathexplorer.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Contains the patterns that are used in the notational processing of the 
 * math expression. Used for extract symbols, operators, numbers and
 * other subelements form the MthML expression
 */
public class Regex {
	
	public final static List<Pattern> PATTERNS = initPatternsList();
	public final static Pattern TRIVIAL_EQUATION = 
			Pattern.compile("<math[^<]*?<m.[^<]*?</m.></math>");
	public final static Pattern NUMBER_ELEMENT = 
			Pattern.compile("<mn[^<]*?</mn>");
	
	
	public final static int GROUP = 0;
	private static List<Pattern> initPatternsList() {
		List<Pattern> result = new ArrayList<Pattern>();
		result.add(Pattern.compile("<math.*?</math>"));
		result.add(Pattern.compile("<mi[^<]*?</mi>"));
		result.add(Pattern.compile("<mo[^<]*?</mo>"));
		result.add(Pattern.compile("<mn[^<]*?</mn>"));
		result.add(Pattern.compile("<msub>.*?</msub>"));
		result.add(Pattern.compile("<msup>.*?</msup>"));
		result.add(Pattern.compile("<mfrac>.*?</mfrac>"));
		result.add(Pattern.compile("<mroot>.*?</mroot>"));
		result.add(Pattern.compile("<msqrt>.*?</msqrt>"));
		result.add(Pattern.compile("<msubsup>.*?</msubsup>"));
		result.add(Pattern.compile("<mrow>.*?</mrow>"));
		// "bigrams"
		result.add(Pattern.compile("<m.>[^<]*?</m.><m.>[^<]*?</m.>"));
		//result.add(Pattern.compile("<mi>[^<]*?</mi><mi>[^<]*?</mi>"));
		//result.add(Pattern.compile("<mo>[^<]*?</mo><mi>[^<]*?</mi>"));
		
		
		// "trigrams"
		result.add(Pattern.compile("<m.>[^<]*?</m.><m.>[^<]*?</m.><m.>[^<]*?</m.>"));
		return result;
	}
	
	public boolean isTrivialEquation(String mathML) {
		return TRIVIAL_EQUATION.matcher(mathML).matches();
	}
	
	/**
	 * Extracts the elements present in the MathML expression 
	 */
	public static Collection<String> extractElements(String expression) {
		List<String> result = new ArrayList<String>(); 
		
		for (Pattern p: PATTERNS) {
			Matcher matcher = p.matcher(expression);
			for(int i = 0; i< expression.length(); i++) {
				if (matcher.find(i)) {
					i = matcher.start()+1;
					result.add(matcher.group(GROUP));
				}
			}
		}
		return result;
	}
	
	public static String cleanQuery(String queryString) {
		queryString = queryString.replaceAll("\\r|\\n|  +", "");
		queryString = queryString.replaceAll(" selected=\".*?\"", "");
		queryString = queryString.replaceAll("<mtext.*?</mtext>", "");
		queryString = queryString.replaceAll(" mathvariant=\".*?\"", "");
		queryString = queryString.replaceAll("<mstyle>", "");
		queryString = queryString.replaceAll("</mstyle>", "");
		queryString = queryString.replaceAll("<mo>=</mo>", "<mo>==</mo>");
		queryString = queryString.replaceAll("<mo>≡</mo>", "<mo>==</mo>");
		queryString = queryString.replaceAll("<mspace.*?>", "");
		queryString = queryString.replaceAll("</mspace>", "");
		queryString = queryString.replaceAll("<mo>.</mo></math>", "</math>");
		queryString = queryString.replaceAll("<mo>,</mo></math>", "</math>");
		queryString = queryString.replaceAll("> <", "><");

		
		//queryString = queryString.replaceAll("&theta;", "θ");
		queryString = StringEscapeUtils.unescapeHtml4(queryString);
		
		return queryString;
	}
	
	public static void main(String[] args) {
		Console.print(extractElements("<math><mn>2</mn><mo>+</mo><mn>3</mn></math>"));
	}
}

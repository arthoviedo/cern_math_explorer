package cern.ch.mathexplorer.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;


public class Regex {
//	public final static Pattern PATTERN = Pattern.compile("<(mi|mo|mn|msup|msub)>(?=(.*?)</\\1>)");
	
	public final static List<Pattern> PATTERNS = initPatternsList();
	public final static int GROUP = 0;
	private static List<Pattern> initPatternsList() {
		List<Pattern> result = new ArrayList<Pattern>();
		result.add(Pattern.compile("<mi>.*?</mi>"));
		result.add(Pattern.compile("<mo>.*?</mo>"));
		result.add(Pattern.compile("<mn>.*?</mn>"));
		result.add(Pattern.compile("<msub>.*?</msub>"));
		result.add(Pattern.compile("<msup>.*?</msup>"));
		result.add(Pattern.compile("<mfrac>.*?</mfrac>"));
		result.add(Pattern.compile("<mroot>.*?</mroot>"));
		result.add(Pattern.compile("<msqrt>.*?</msqrt>"));
		result.add(Pattern.compile("<msubsup>.*?</msubsup>"));
		//result.add(Pattern.compile("<mrow>.*?</mrow>"));
		// "bigrams"
		result.add(Pattern.compile("<mi>.*?</mi><mo>.*?</mo>"));
		// "trigrams"
		result.add(Pattern.compile("<mi>.*?</mi><mo>.*?</mo><mi>.*?</mi>"));
		return result;
	}
	
	public static Collection<String> extractElements(String equation) {
		List<String> result = new ArrayList<String>(); 
		
		for (Pattern p: PATTERNS) {
			Matcher matcher = p.matcher(equation);
			while (matcher.find()) {
				result.add(matcher.group(GROUP));
			}
		}
		return result;
	}
	
	public static String cleanQuery(String queryString) {
		queryString = queryString.replaceAll("\\r|\\n|  +", "");
		queryString = queryString.replaceAll(" selected=\".*?\"", "");
		queryString = queryString.replaceAll("mtext", "mi");
		
		//queryString = queryString.replaceAll("&theta;", "θ");
		queryString = StringEscapeUtils.unescapeHtml4(queryString);
		
		return queryString;
	}
}

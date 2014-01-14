package cern.ch.mathexplorer.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;


public class Regex {
//	public final static Pattern PATTERN = Pattern.compile("<(mi|mo|mn|msup|msub)>(?=(.*?)</\\1>)");
	public final static Pattern PATTERN1 = Pattern.compile("<mi>.*?</mi>");
	public final static Pattern PATTERN2 = Pattern.compile("<mo>.*?</mo>");
	public final static Pattern PATTERN3 = Pattern.compile("<mn>.*?</mn>");
	public final static Pattern PATTERN4 = Pattern.compile("<msub>.*?</msub>");
	public final static Pattern PATTERN5 = Pattern.compile("<msup>.*?</msup>");
	public final static Pattern PATTERN6 = Pattern.compile("<mfrac>.*?</mfrac>");
	public final static Pattern PATTERN7 = Pattern.compile("<mroot>.*?</mroot>");
	public final static Pattern PATTERN8 = Pattern.compile("<msqrt>.*?</msqrt>");
	public final static Pattern PATTERN9 = Pattern.compile("<msubsup>.*?</msubsup>");
	
	public final static List<Pattern> PATTERNS = initPatternsList();
	public final static int GROUP = 0;
	private static List<Pattern> initPatternsList() {
		List<Pattern> result = new ArrayList<Pattern>();
		result.add(PATTERN1);
		result.add(PATTERN2);
		result.add(PATTERN3);
		result.add(PATTERN4);
		result.add(PATTERN5);
		result.add(PATTERN6);
		result.add(PATTERN7);
		result.add(PATTERN8);
		result.add(PATTERN9);
		
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

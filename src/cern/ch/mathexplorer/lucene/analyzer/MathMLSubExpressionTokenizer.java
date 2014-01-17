package cern.ch.mathexplorer.lucene.analyzer;

import java.util.ArrayList;
import java.util.LinkedList;

public class MathMLSubExpressionTokenizer {
	public ArrayList<String> findMRowSubExpressions(String equation) {
		String startTag = "<mrow>";
		String endTag = "</mrow>";
		
		ArrayList<String> result = new ArrayList<>();
		
		LinkedList<Integer> stack = new LinkedList<>();
		for (int i = 0; i< equation.length()-endTag.length(); i++) {
			if(	equation.charAt(i)=='<' 	&& equation.charAt(i+1)=='m' && equation.charAt(i+2)=='r' &&
				equation.charAt(i+3)=='o' 	&& equation.charAt(i+4)=='w' && equation.charAt(i+5)=='>') {
				stack.push(i);
			}
			if(	equation.charAt(i)=='<' 	&& equation.charAt(i+1)=='/' && equation.charAt(i+2)=='m' &&
					equation.charAt(i+3)=='r' 	&& equation.charAt(i+4)=='o' && equation.charAt(i+5)=='w'
					&& equation.charAt(i+6)=='>') {
					int lastOpen = stack.pop();
					result.add(equation.substring(lastOpen, i+endTag.length()));
			}
		}
		return result;
	}
	
	/**
	 * Represents one occurrence of the search pattern in the string
	 * Its type and position are stored
	 */
	class Element {
		boolean open;
		int position;
	}
	
	
}

package cern.ch.mathexplorer.mathematica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Patterns {
	private static final ArrayList<StructuralPattern> patterns = new ArrayList<>();
	
	static {
		loadPatterns();
	}
	
	static 	private void loadPatterns() {
		patterns.add(new StructuralPattern("x_+y_"));
		patterns.add(new StructuralPattern("x_-y_"));
		patterns.add(new StructuralPattern("x_*y_"));
		patterns.add(new StructuralPattern("x_/y_"));
		patterns.add(new StructuralPattern("x_Integer/y_Integer"));
		patterns.add(new StructuralPattern("1/x_Integer"));
		patterns.add(new StructuralPattern("Sin[x_]"));
		patterns.add(new StructuralPattern("Cos[x_]"));
		patterns.add(new StructuralPattern("a_.*Cos[x_] + b_.*Sin[x_]"));
		patterns.add(new StructuralPattern("a_.*Cos[x_] * b_.*Sin[x_]"));
		patterns.add(new StructuralPattern("a_.*Cosh[x_] + b_.*Sinh[x_]"));
		patterns.add(new StructuralPattern("a_.*Cosh[x_] * b_.*Sinh[x_]"));
		patterns.add(new StructuralPattern("Tan[x_]"));
		patterns.add(new StructuralPattern("_.*e^x_"));
		patterns.add(new StructuralPattern("a_.+b_.x_+c_.x_^2"));
		patterns.add(new StructuralPattern("a_.* Subscript[x_, y_] + b_.* Subscript[x_, z_]"));
		patterns.add(new StructuralPattern("a_.* Subscript[x_, y_] * b_.* Subscript[x_, z_]"));
		patterns.add(new StructuralPattern("a_.* Subscript[v1_, v2_]^v3_  * Subscript[v1_, v3_]^v2_"));
		patterns.add(new StructuralPattern("a_.* Subscript[v1_, v2_]^v3_  +  b_.* Subscript[v1_, v3_]^v2_"));
		patterns.add(new StructuralPattern("N == N_Integer"));
		patterns.add(new StructuralPattern("d*x_*t*(d^-1)"));
		//patterns.add(new StructuralPattern("d*x_*x*(d^-1)"));
		patterns.add(new StructuralPattern("X_ == Subscript[ X_, Z_]*Y_"));
		patterns.add(new StructuralPattern("X_ == Subscript[ X_, Z_]*Y_.*e^(-1*W_)"));
		patterns.add(new StructuralPattern("_.*SuperMinus[X_]*SuperPlus[X_]"));
		patterns.add(new StructuralPattern("_.*SuperPlus[X_]*SuperMinus[X_]"));
		patterns.add(new StructuralPattern("_.*SubMinus[X_]*SubPlus[X_]"));
		patterns.add(new StructuralPattern("_.*SubPlus[X_]*SubMinus[X_]"));
		patterns.add(new StructuralPattern("_.*SubPlus[X_]*SubMinus[X_]"));
		patterns.add(new StructuralPattern("_.* X_* Overscript[X_, S_]"));
		patterns.add(new StructuralPattern("a_. Subscript[X_, s1_]*b_. Subscript[X_, s2_]^-1"));
		patterns.add(new StructuralPattern("a_. Superscript[X_, s1_]*b_. Superscript[X_, s2_]^-1"));
		patterns.add(new StructuralPattern("Log[a_. Subscript[X_, s1_]*b_. Subscript[X_, s2_]^-1]" ));
		patterns.add(new StructuralPattern("Log[a_. Superscript[X_, s1_]*b_. Superscript[X_, s2_]^-1]" ));
		patterns.add(new StructuralPattern("Log[a_. Superscript[X_, s1_] * Superscript[X_, s2_]]" ));
		patterns.add(new StructuralPattern("Log[a_. Subscript[X_, s1_] * Subscript[X_, s2_]]" ));
		patterns.add(new StructuralPattern("Subscript[X_, max] - Subscript[X_, min]" ));
		
		
		for (String s: add2ArgsOperators()) {
			patterns.add(new StructuralPattern(s));
			
		}
	}
	
	static List<String> singleOperations(String arg) {
		return Arrays.asList(
				String.format("Log[%s]", arg),
				String.format("Sin[%s]", arg), 
				String.format("Cos[%s]", arg), 
				String.format("Tan[%s]", arg), 
				String.format("Sinh[%s]", arg), 
				String.format("Cosh[%s]", arg), 
				String.format("Tanh[%s]", arg), 
				String.format("Exp[%s]", arg) 
				);
	}
	
	static List<String> oneArgVariations(String secondArg) {
		return Arrays.asList("X_", 
				String.format("Subscript[X_,%s]",secondArg), 
				String.format("Superscript[X_,%s]",secondArg), 
				String.format("Superscript[X_,%s]",secondArg),
				String.format("Power[X_,%s]",secondArg),
				"SubPlus[X_]", 
				"SubMinus[X_]", 
				"SubStar[X_]", 
				"SuperPlus[X_]", 
				"SuperMinus[X_]", 
				"SuperStar[X_]", 
				"SuperDagger[X_]", 
				String.format("Overscript[X_, %s]",secondArg), 
				String.format("Underscript[X_,%s]",secondArg),
				"OverBar[X_]", 
				"OverVector[X_]", 
				"OverHat[X_]", 
				"OverDot[X_]", 
				"UnderBar[X_]");
	}
	
	static List<String> sameArg2Variations() {
		List<String> variations = new ArrayList<String>();
		List<String> list1 = oneArgVariations("s1_");
		List<String> list2 = oneArgVariations("s2_");
		
		for(int i = 0; i< list1.size(); i++){
			String a1 = list1.get(i);
			for (int j = i; j<list2.size(); j++) {
				
				variations.add(a1+","+list2.get(j));
			}
		}
		return variations;
	}
	
	static List<String> add2ArgsOperators() {
		ArrayList<String> ret = new ArrayList<>();
		List<String> operators = Arrays.asList("Plus[%s]", "Times[%s]", "Divide[%s]");
		for(String operator: operators) {
			for(String variation: sameArg2Variations()) {
				ret.add(String.format(operator, variation));
			}
		}
		return ret;
	}

	public static ArrayList<StructuralPattern> getPatterns() {
		return patterns;
	}
	
	public static void main(String[] args) {
		for(StructuralPattern p: patterns) {
			System.out.println(p);
		}
	}
}

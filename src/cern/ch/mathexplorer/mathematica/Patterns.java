package cern.ch.mathexplorer.mathematica;

import java.util.ArrayList;

public class Patterns {
	private static final ArrayList<StructuralPattern> patterns = new ArrayList<>();
	
	static {
		loadPatterns();
	}
	
	static 	private void loadPatterns() {
		patterns.add(new StructuralPattern("simple_sum", "x_+y_"));
		patterns.add(new StructuralPattern("simple_substraction", "x_-y_"));
		patterns.add(new StructuralPattern("simple_product", "x_*y_"));
		patterns.add(new StructuralPattern("simple_division", "x_/y_"));
		patterns.add(new StructuralPattern("numeric_fraction",
				"x_Integer/y_Integer"));
		patterns.add(new StructuralPattern("numeric_fraction_1_numerator",
				"1/x_Integer"));
		patterns.add(new StructuralPattern("sine", "Sin[x_]"));
		patterns.add(new StructuralPattern("cosine", "Cos[x_]"));
		patterns.add(new StructuralPattern("sum sine cosine", "a_.*Cos[x_] + b_.*Sin[x_]"));
		patterns.add(new StructuralPattern("product sine cosine", "a_.*Cos[x_] * b_.*Sin[x_]"));
		patterns.add(new StructuralPattern("sum sineh cosine h", "a_.*Cosh[x_] + b_.*Sinh[x_]"));
		patterns.add(new StructuralPattern("product sineh cosine h", "a_.*Cosh[x_] * b_.*Sinh[x_]"));
		patterns.add(new StructuralPattern("tangent", "Tan[x_]"));
		patterns.add(new StructuralPattern("exponential", "e^x_"));
		patterns.add(new StructuralPattern("quadratic", "a_.+b_.x_+c_.x_^2"));
		patterns.add(new StructuralPattern(
				"sum_same_symbol_different_subscript",
				"a_.* Subscript[x_, y_] + b_.* Subscript[x_, z_]"));
		patterns.add(new StructuralPattern(
				"product_same_symbol_different_subscript",
				"a_.* Subscript[x_, y_] * b_.* Subscript[x_, z_]"));
		patterns.add(new StructuralPattern("product_crossed_sub_super_index",
				"a_.* Subscript[v1_, v2_]^v3_  * Subscript[v1_, v3_]^v2_"));
		patterns.add(new StructuralPattern("sum_crossed_sub_super_index",
				"a_.* Subscript[v1_, v2_]^v3_  +  b_.* Subscript[v1_, v3_]^v2_"));
		patterns.add(new StructuralPattern("n_equals", "N == N_Integer"));
		patterns.add(new StructuralPattern("derivative_over_time",
				"d*x_*t*(d^-1)"));
		patterns.add(new StructuralPattern("X equals X sub something",
				"X_ == Subscript[ X_, Z_]*Y_"));
		patterns.add(new StructuralPattern(
				"X equals X sub something times neg exponential",
				"X_ == Subscript[ X_, Z_]*Y_.*e^(-1*W_)"));
		patterns.add(new StructuralPattern("X_to_neg_pos", "_.*SuperMinus[X_]*SuperPlus[X_]"));
		patterns.add(new StructuralPattern("X_to_pos_neg", "_.*SuperPlus[X_]*SuperMinus[X_]"));
		patterns.add(new StructuralPattern("X_sub_neg_pos", "_.*SubMinus[X_]*SubPlus[X_]"));
		patterns.add(new StructuralPattern("X_sub_pos_neg", "_.*SubPlus[X_]*SubMinus[X_]"));
		patterns.add(new StructuralPattern("X_sub_pos_neg", "_.*SubPlus[X_]*SubMinus[X_]"));
		patterns.add(new StructuralPattern("X times X with SuperScript", "_.* X_* Overscript[X_, S_]"));
	}

	public static ArrayList<StructuralPattern> getPatterns() {
		return patterns;
	}
}

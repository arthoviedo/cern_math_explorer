package cern.ch.mathexplorer.lucene.analysis.tokenizers;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;

import cern.ch.mathexplorer.mathematica.MathematicaConfig;
import cern.ch.mathexplorer.mathematica.MathematicaEngine;
import cern.ch.mathexplorer.utils.Regex;

public abstract class MathTokenizer extends Tokenizer {

	protected StringBuilder str = new StringBuilder();
	final char[] buffer = new char[8192];
	protected MathematicaConfig.NORMALIZATION_MODE normalizationMode = MathematicaConfig.NORMALIZATION_MODE.NO_SIMPLIFICATION;
	protected MathTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
	}
	
	protected MathTokenizer(AttributeFactory factory, Reader input, MathematicaConfig.NORMALIZATION_MODE normalizationMode) {
		super(factory, input);
		this.normalizationMode = normalizationMode;
	}

	// TODO: we should see if we can make this tokenizer work without reading
	// the entire document into RAM, perhaps with Matcher.hitEnd/requireEnd ?

	protected void fillBuffer(StringBuilder sb, Reader input)
			throws IOException {
		int len;
		sb.setLength(0);
		while ((len = input.read(buffer)) > 0) {
			sb.append(buffer, 0, len);
		}
		String tmp = str.toString();
		if (normalizationMode.equals(MathematicaConfig.NORMALIZATION_MODE.NO_SIMPLIFICATION)){
			
		}
		if (normalizationMode.equals(MathematicaConfig.NORMALIZATION_MODE.SIMPLE_SIMPLIFICATION)){
			tmp = MathematicaEngine.getInstance("INDEXING").simplyExpressionWithTimeout(tmp);
		}
		if (normalizationMode.equals(MathematicaConfig.NORMALIZATION_MODE.FULL_SIMPLIFICATION)){
			tmp = MathematicaEngine.getInstance("INDEXING").simplyExpressionWithTimeout(tmp);
		}
		str = new StringBuilder(Regex.cleanQuery(tmp));

	}
}

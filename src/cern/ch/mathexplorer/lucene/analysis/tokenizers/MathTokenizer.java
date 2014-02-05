package cern.ch.mathexplorer.lucene.analysis.tokenizers;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;

import cern.ch.mathexplorer.utils.Regex;

public abstract class MathTokenizer extends Tokenizer {

	protected StringBuilder str = new StringBuilder();
	final char[] buffer = new char[8192];

	protected MathTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
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
		str = new StringBuilder(Regex.cleanQuery(str.toString()));

	}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cern.ch.mathexplorer.lucene.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import com.wolfram.jlink.MathLinkException;

import cern.ch.mathexplorer.mathematica.MathematicaIntegrator;
import cern.ch.mathexplorer.mathematica.StructuralFeature;

/**
 * This tokenizer uses regex pattern matching to construct distinct tokens for
 * the input stream. It takes two arguments: "pattern" and "group".
 * <p/>
 * <ul>
 * <li>"pattern" is the regular expression.</li>
 * <li>"group" says which group to extract into tokens.</li>
 * </ul>
 * <p>
 * group=-1 (the default) is equivalent to "split". In this case, the tokens
 * will be equivalent to the output from (without empty tokens):
 * {@link String#split(java.lang.String)}
 * </p>
 * <p>
 * Using group >= 0 selects the matching group as the token. For example, if you
 * have:<br/>
 * 
 * <pre>
 *  pattern = \'([^\']+)\'
 *  group = 0
 *  input = aaa 'bbb' 'ccc'
 * </pre>
 * 
 * the output will be two tokens: 'bbb' and 'ccc' (including the ' marks). With
 * the same input but using group=1, the output would be: bbb and ccc (no '
 * marks)
 * </p>
 * <p>
 * NOTE: This Tokenizer does not output tokens that are of zero length.
 * </p>
 * 
 * @see Pattern
 */
public final class StructuralFeaturesTokenizer extends Tokenizer {

	private final CharTermAttribute featuresAtt = addAttribute(CharTermAttribute.class);
	private MathematicaIntegrator mi = MathematicaIntegrator.getInstance();
	private final StringBuilder str = new StringBuilder();
	private List<StructuralFeature> features;
	int currentFeature = 0;

	/**
	 * creates a new PatternTokenizer returning tokens from group (-1 for split
	 * functionality)
	 */
	public StructuralFeaturesTokenizer(Reader input) {
		this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input);
	}

	/**
	 * creates a new PatternTokenizer returning tokens from group (-1 for split
	 * functionality)
	 */
	public StructuralFeaturesTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
	}

	
	@Override
	public boolean incrementToken() {
		System.out.println("Incrementing token");
		if(currentFeature >= features.size())
			return false;
		clearAttributes();
		featuresAtt.setEmpty().append(features.get(currentFeature).getName());
		currentFeature++;
		return true;

	}

	@Override
	public void end() throws IOException {
		super.end();
		// final int ofs = correctOffset(str.length());
		// offsetAtt.setOffset(ofs, ofs);
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		fillBuffer(str, input);
		currentFeature = 0;
	}

	// TODO: we should see if we can make this tokenizer work without reading
	// the entire document into RAM, perhaps with Matcher.hitEnd/requireEnd ?
	final char[] buffer = new char[8192];

	private void fillBuffer(StringBuilder sb, Reader input) throws IOException {
		int len;
		sb.setLength(0);
		while ((len = input.read(buffer)) > 0) {
			sb.append(buffer, 0, len);
		}
		try {
			if (mi == null)
				mi = MathematicaIntegrator.getInstance();
			mi.importString(str.toString());
			features = mi.getPatterns();
		} catch (MathLinkException e) {
			e.printStackTrace();
			throw new IOException("Problem while processing with Mathematica: "
					+ e.getMessage());
		}

	}
}

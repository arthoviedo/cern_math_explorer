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

package cern.ch.mathexplorer.lucene.analysis.tokenizers;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cern.ch.mathexplorer.mathematica.MathematicaEngine;
import cern.ch.mathexplorer.mathematica.StructuralPattern;
import cern.ch.mathexplorer.utils.Regex;

/**
 * Extracts structural features from the given MathML expression/
 * The tokenizer tries to interpret the expression in Mathematica
 * and then looks for a serie of predefined patterns in the given expression
 */
public final class StructuralPatternsTokenizer extends MathTokenizer {

	private final CharTermAttribute featuresAtt = addAttribute(CharTermAttribute.class);
	private MathematicaEngine mi = MathematicaEngine.getInstance("INDEXING");
	private List<StructuralPattern> features;
	int currentFeature = 0;

	/**
	 * Creates a new StructuralFeaturesTokenizer
	 */
	public StructuralPatternsTokenizer(Reader input) {
		this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input);
	}

	/**
	 * Creates a new PatternTokenizer returning tokens from group (-1 for split
	 * functionality)
	 */
	public StructuralPatternsTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
	}

	@Override
	public boolean incrementToken() {
		if(currentFeature >= features.size()) {
			return false;
		}
		clearAttributes();
		featuresAtt.setEmpty().append(features.get(currentFeature).getName());
		currentFeature++;
		return true;

	}

	@Override
	public void end() throws IOException {
		super.end();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		fillBuffer(str, input);
		currentFeature = 0;
		try {
			if (mi == null) {
				mi = MathematicaEngine.getInstance("INDEXING");
			}
			features = mi.getPatternsWithTimeout(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Problem while processing with Mathematica: "
					+ e.getMessage());
		}
	}

}

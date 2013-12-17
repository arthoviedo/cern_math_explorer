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

package cern.ch.mathexplorer.lucene;

import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public final class UnicodeNormalizingTokenizer extends TokenFilter {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	
	
		/**
	 * creates a new PatternTokenizer returning tokens from group (-1 for split
	 * functionality)
	 */
	public UnicodeNormalizingTokenizer(TokenStream input) {
		super(input);
	}

	boolean firstTime = true;
	String originalString;
	String normalizedString;
	int charIndex = 0;
	String originalTerm="";
	@Override
	public boolean incrementToken() throws IOException {
		// System.out.println("Incrementing token");
		originalString = originalTerm.toString();
		normalizedString = Normalizer.normalize(originalString, Form.NFKD);
		if (firstTime ) {
			firstTime = false;
			boolean ret = input.incrementToken();
			originalTerm = termAtt.toString();
			return ret;
		}
		if (originalString.equals(normalizedString)) {
			boolean ret = input.incrementToken();
			originalTerm = termAtt.toString();
			return ret;
		}
		else {
			String openingTag = "<"+normalizedString.substring(1, 3)+">";
			String closingTag = "</"+normalizedString.substring(1, 3)+">";
			String content = normalizedString.replaceAll("<m.>", "");
			content = content.replaceAll("</m.>", "");
			if (charIndex < content.length()) {
				termAtt.setEmpty().append(openingTag+content.charAt(charIndex)+closingTag);
				charIndex++;
				if (charIndex == content.length()){
					firstTime=true;
					charIndex = 0;
				}
				return true;
			}
			firstTime = true;
			return true;
		}
		//clearAttributes();
	}

	@Override
	public void end() throws IOException {
		super.end();
		//final int ofs = correctOffset(str.length());
		//offsetAtt.setOffset(ofs, ofs);
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		charIndex = 0;
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
	}
}

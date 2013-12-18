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

/**
 * Given a MathML token, creates tokens for each of the Non-Control/Non-Combining 
 * (Letters/Numbers/Math Symbols, etc) 
 * Unicode characters in the NFKD (Normal Compatibility Decomposition form).
 * The new tokens are enclosed in the same tag as the original one.
 * Given the token <mi>Å</mi> (Latin capital letter a with ring above) outputs additionally
 * the token <mi>A</mi> since its decomposition consists of the LATIN CAPITAL LETTER A and 
 * the \\u30a Combining Ring Above
 * @author Arthur Ovieco (arthoviedo@gmail.com)
 *
 */
public final class UnicodeNormalizingFilter extends TokenFilter {

	/**
	 * The character attribute that is generated by this token
	 */
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	public UnicodeNormalizingFilter(TokenStream input) {
		super(input);
	}

	/**
	 * Used for the control flow:
	 * The first time this filter is called, it consumes the token from the input.
	 * Next invocations iterate over all the characters of the NFKD form.
	 */
	boolean firstTime = true;
	/**
	 * Indicated the index of the character we are visiting in the NFKD 
	 * form of the original token
	 */
	int charIndex = 0;
	String originalString;
	String normalizedString;
	String originalTerm = "";

	@Override
	public boolean incrementToken() throws IOException {
		originalString = originalTerm.toString();
		normalizedString = Normalizer.normalize(originalString, Form.NFKD);
		if (firstTime) { //We only consume the token from the input
			firstTime = false;
			boolean ret = input.incrementToken();
			originalTerm = termAtt.toString();
			return ret;
		}
		if (originalString.equals(normalizedString)) { 	//Original string is already in normalized decomposed form
														//There's no point in repeating work.
			boolean ret = input.incrementToken();
			originalTerm = termAtt.toString();
			return ret;
		} else {
			String openingTag = "<" + normalizedString.substring(1, 3) + ">";
			String closingTag = "</" + normalizedString.substring(1, 3) + ">";
			String content = normalizedString.replaceAll("<m.>", "");
			content = content.replaceAll("</m.>", "");
			if (charIndex < content.length()) {
				termAtt.setEmpty().append(
						openingTag + content.charAt(charIndex) + closingTag);
				if (Character.getType(content.charAt(charIndex)) == Character.NON_SPACING_MARK) {
					termAtt.setEmpty();
				}
				charIndex++;
				if (charIndex == content.length()) {
					firstTime = true;
					charIndex = 0;
				}
				return true;
			}
			firstTime = true;
			return true;
		}
	}

	@Override
	public void end() throws IOException {
		super.end();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		charIndex = 0;
	}
}
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Regex;

/**
 * Generates different approximations for the <mn><mn/> tokens. by iteratively
 * applying the round function <mn>1.38197</mn> generates the following tokens:
 * <mn>1.3820</mn>, <mn>1.382</mn>, <mn>1.38</mn>, <mn>1.4</mn>, <mn>1.0</mn> and <mn>1</mn> 
 * 
 * @author Arthur Oviedo (arthoviedo@gmail.com)
 * 
 */
public final class NumericRoundFilter extends TokenFilter {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	protected NumericRoundFilter(TokenStream input) {
		super(input);
	}

	BigDecimal _0 = new BigDecimal(0);
	BigDecimal bd = _0;
	String currentToken;
	boolean firstTime = true;
	BigDecimal previousValue = new BigDecimal(Integer.MAX_VALUE);
	// boolean restart = false;
	@Override
	public boolean incrementToken() throws IOException {
		if (firstTime) {
			currentToken = termAtt.toString();
			firstTime = false;

			if (currentToken.startsWith("<mn")
					&& currentToken.endsWith("</mn>")) {
				String suspectedNumber = currentToken.replace("<mn>", "");
				suspectedNumber = suspectedNumber.replace("</mn>", "");
				try {
					Double d = Double.parseDouble(suspectedNumber);
					bd = new BigDecimal(suspectedNumber);
				} catch (Exception e) {
					// Expected if the inside content was not a valid number
					firstTime = true;
					return input.incrementToken(); // The element could not be
													// interpreted as a number,
													// so we continue
				}

			}
		}
		if (currentToken.startsWith("<mn") && currentToken.endsWith("</mn>")) {
			if (bd.equals(_0)) {
				String suspectedNumber = currentToken.replace("<mn>", "");
				suspectedNumber = suspectedNumber.replace("</mn>", "");
				try {
					Double d = Double.parseDouble(suspectedNumber);
					bd = new BigDecimal(suspectedNumber);
				} catch (Exception e) {
					System.out.println("Problematic number: "+suspectedNumber);
					e.printStackTrace();
					firstTime = true;
					return input.incrementToken(); // The element could not be
													// interpreted as a number,
													// so we continue
				}

			}
			//Console.print("********" + bd + " Scale:" + bd.scale());

			if (bd.scale() == 0 || bd.equals(bd.round(new MathContext(bd.scale())))) {
				firstTime = true;
				// bd = _0;
				return input.incrementToken();
			}
			previousValue = bd;
			try {
				bd = bd.round(new MathContext(bd.scale()));
				
			} catch (Exception e){
				System.out.println("*****************: " + bd.toString() + "    ---   " + currentToken);
				throw e;
			}
			termAtt.setEmpty().append("<mn>" + bd + "</mn>");
			
			
			return true;

			// return input.incrementToken();
		} else {
			firstTime = true;
			return input.incrementToken();

		}
	}

}

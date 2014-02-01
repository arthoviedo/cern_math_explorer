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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.Constants.CHARACTER_CATEGORIES;

/**
 * Filter that allows grouping similar tokens according to their use in
 * mathematics. Some categories are: Basic Arithmetic, Logical Operators,
 * Integrals, Set theory.
 * http://symbolcodes.tlt.psu.edu/bylanguage/mathchart.html#fractions
 */
public final class RelatedOperatorsFilter extends TokenFilter {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	protected RelatedOperatorsFilter(TokenStream input) {
		super(input);
	}

	//private static Set<String> INTEGRALS = ;

	
		
	
	
	BigDecimal _0 = new BigDecimal(0);
	String currentToken;
	boolean firstTime = true;
	BigDecimal previousValue = new BigDecimal(Integer.MAX_VALUE);

	// boolean restart = false;

	@Override
	public boolean incrementToken() throws IOException {
		if (firstTime) {
			currentToken = termAtt.toString();
			firstTime = false;
		}
		if (currentToken.startsWith("<mo") && currentToken.endsWith("</mo>")) {
			String operator = currentToken.replace("<mo>", "");
			operator = operator.replace("</mo>", "");
			boolean foundMatch = false;
			
			CHARACTER_CATEGORIES category = null;
			if ( (category = (Constants.characterToCategoryMap.get(operator))) != null) {
				termAtt.setEmpty().append("<mo>"+category.name()+"</mo>");
				foundMatch = true;
			}
			
			if(foundMatch) {
				firstTime = true;
				return true;
			}
			firstTime=true;
			return input.incrementToken();
		} else {
			firstTime = true;
			return input.incrementToken();
		}
	}

}

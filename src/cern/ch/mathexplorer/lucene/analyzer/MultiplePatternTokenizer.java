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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import cern.ch.mathexplorer.utils.Console;

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
public final class MultiplePatternTokenizer extends Tokenizer {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

	private final StringBuilder str = new StringBuilder();

	// Current position where we are looking for next match
	private int stringIndex;

	private final int group;
	private final List<Matcher> matchers;

	private LinkedList<Match> matches;
	boolean firstTimeToken = true;

	/**
	 * creates a new PatternTokenizer returning tokens from group (-1 for split
	 * functionality)
	 */
	public MultiplePatternTokenizer(Reader input, List<Pattern> patterns,
			int group) {
		this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input, patterns, group);
	}

	/**
	 * creates a new PatternTokenizer returning tokens from group (-1 for split
	 * functionality)
	 */
	public MultiplePatternTokenizer(AttributeFactory factory, Reader input,
			List<Pattern> patterns, int group) {
		super(factory, input);
		this.group = group;

		// Use "" instead of str so don't consume chars
		// (fillBuffer) from the input on throwing IAE below:
		matchers = new ArrayList<>();
		for (Pattern p : patterns) {
			Matcher m = p.matcher("");
			// confusingly group count depends ENTIRELY on the pattern but is
			// only accessible via matcher
			if (group >= 0 && group > m.groupCount()) {
				throw new IllegalArgumentException(
						"invalid group specified: pattern " + p.toString()
								+ "only has: " + m.groupCount()
								+ " capturing groups");
			}
			matchers.add(m);
		}
	}

	int matchIndex = 0;
	
	class Match{
		int startIndex;
		int endIndex;
		String match;
	}
	
	@Override
	public boolean incrementToken() {
		// System.out.println("Incrementing token");
		if (matches.isEmpty())
			return false;
		clearAttributes();

		if (group >= 0) {
			Match m = matches.removeFirst();
			termAtt.setEmpty().append(str, m.startIndex, m.endIndex);
			offsetAtt.setOffset(correctOffset(m.startIndex),
					correctOffset(m.endIndex));

			//stringIndex = minStartIndex + 1;
			//Console.print("Index:" + stringIndex);
			//firstTimeToken = true;
			return true;
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	
	public boolean incrementTokenBK() {
		// System.out.println("Incrementing token");
		if (stringIndex >= str.length())
			return false;
		clearAttributes();

		if (group >= 0) {
			boolean atLeastOneMatch = false;

			// try to match the first regex
			int minStartIndex = Integer.MAX_VALUE;
			int minEndIndex = Integer.MAX_VALUE;
			for (Matcher m : matchers) {
				if (m.find(stringIndex)) {
					int start = m.start(group);
					int end = m.end(group);
					if (start < minStartIndex && start != end) {
						minEndIndex = m.end(group);
						minStartIndex = m.start(group);
						atLeastOneMatch = true;
					}
				}
			}
			// match a specific group
			if (!atLeastOneMatch) {
				stringIndex = Integer.MAX_VALUE; // mark exhausted
				return false;
			}

			termAtt.setEmpty().append(str, minStartIndex, minEndIndex);
			offsetAtt.setOffset(correctOffset(minStartIndex),
					correctOffset(minEndIndex));

			stringIndex = minStartIndex + 1;
			Console.print("Index:" + stringIndex);
			firstTimeToken = true;
			return true;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void end() throws IOException {
		super.end();
		final int ofs = correctOffset(str.length());
		offsetAtt.setOffset(ofs, ofs);
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		fillBuffer(str, input);
		for (Matcher m : matchers) {
			m.reset(str);
		}
		getAllMatches();
		stringIndex = 0;
	}
	
	private void getAllMatches() {
		matches = new LinkedList<>();
		for (Matcher m : matchers) {
			while (m.find())
			{
				int start = m.start(group);
				int end = m.end(group);
				Match toAdd = new Match();
				toAdd.startIndex = start;
				toAdd.endIndex = end;
				toAdd.match = str.substring(start, end);
				matches.add(toAdd);
			}
		}
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

package cern.ch.mathexplorer.lucene.analyzer;
import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.SolrAnalyzer;

import cern.ch.mathexplorer.utils.Regex;

public final class SolrMathMLAnalyzer extends SolrAnalyzer {
  
  private final Version matchVersion;
  private final CharArraySet stopWords; 
  
  /**
   * Creates a new {@link WhitespaceAnalyzer}
   * @param matchVersion Lucene version to match See {@link <a href="#version">above</a>}
   */
  public SolrMathMLAnalyzer(Version matchVersion) {
    this.matchVersion = matchVersion;
    String [] stopWordsArray = {"math", "mrow", "mfenced", "mstyle", "mpadded", "mtd" , "mtable", "mtr"};
    stopWords = new CharArraySet(matchVersion, Arrays.asList(stopWordsArray), true);
  }
  
  
  @Override
  protected TokenStreamComponents createComponents(final String fieldName,
      final Reader reader) {
	  Tokenizer source2 = new MultiplePatternTokenizer(reader, Regex.PATTERNS, Regex.GROUP);
	  //TokenStream stopFilter = new StopFilter(matchVersion, source2, stopWords);
	  TokenStream unicodeNormalizer = new UnicodeNormalizingFilter(source2);
	  return new TokenStreamComponents(source2, unicodeNormalizer);
  }
}

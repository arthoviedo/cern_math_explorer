package cern.ch.mathexplorer.lucene.analysis.analyzers;
import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.SolrAnalyzer;

import cern.ch.mathexplorer.lucene.analysis.filters.NumericRoundFilter;
import cern.ch.mathexplorer.lucene.analysis.filters.RelatedOperatorsFilter;
import cern.ch.mathexplorer.lucene.analysis.filters.UnicodeNormalizingFilter;
import cern.ch.mathexplorer.lucene.analysis.tokenizers.MultiplePatternTokenizer;
import cern.ch.mathexplorer.mathematica.MathematicaConfig.NORMALIZATION_MODE;
import cern.ch.mathexplorer.utils.Regex;

public final class SolrNormalizerNotationalAnalyzer extends SolrAnalyzer {
  
  private final Version matchVersion;
  
  /**
   * Creates a new {@link SolrNormalizerNotationalAnalyzer}
   * @param matchVersion Lucene version to match See {@link <a href="#version">above</a>}
   * Provides the same functionality as {@link SolrNotationalAnalyzer}
   * but over a normalized/simplified version of the original string 
   */
  public SolrNormalizerNotationalAnalyzer(Version matchVersion) {
    this.matchVersion = matchVersion;
  }
  
  @Override
  protected TokenStreamComponents createComponents(final String fieldName,
      final Reader reader) {
	  Tokenizer source2 = new MultiplePatternTokenizer(reader, Regex.PATTERNS, Regex.GROUP,
			  NORMALIZATION_MODE.SIMPLE_SIMPLIFICATION);
	  TokenStream unicodeNormalizer = new UnicodeNormalizingFilter(source2);
	  TokenStream numericRounder 	= new NumericRoundFilter(unicodeNormalizer);
	  TokenStream relatedOperators 	= new RelatedOperatorsFilter(numericRounder);
	  
	  
	  return new TokenStreamComponents(source2, relatedOperators);
  }
}

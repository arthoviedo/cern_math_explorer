package cern.ch.mathexplorer.lucene.analyzer;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.SolrAnalyzer;

public final class SolrStructuralAnalyzer extends SolrAnalyzer {
  
  
  /**
   * Creates a new {@link WhitespaceAnalyzer}
   * @param matchVersion Lucene version to match See {@link <a href="#version">above</a>}
   */
  public SolrStructuralAnalyzer(Version matchVersion) {
  }
  
  @Override
  protected TokenStreamComponents createComponents(final String fieldName,
      final Reader reader) {
	  
	  Tokenizer source2 = new StructuralFeaturesTokenizer(reader);
	  return new TokenStreamComponents(source2, source2);
  }
}

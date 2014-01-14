package cern.ch.mathexplorer.lucene.query;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

// Based on the code in 
// http://java.dzone.com/articles/create-custom-solr-queryparser

public class MathQueryParserPlugin extends QParserPlugin {
	public void init(NamedList namedList) {
	}

	@Override
	public QParser createParser(String s, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		return new MathQueryParser(s, localParams, params, req);
	}
}

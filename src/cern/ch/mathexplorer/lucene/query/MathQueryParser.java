package cern.ch.mathexplorer.lucene.query;

import java.util.Collection;
import java.util.logging.Logger;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.Regex;

public class MathQueryParser extends QParser {
	static Logger aLogger = java.util.logging.Logger
			.getLogger(MathQueryParser.class.getName());
	
	public MathQueryParser(String qstr, SolrParams localParams, SolrParams params,
			SolrQueryRequest req) {
		super(qstr, localParams, params, req);
	}

	@Override
	public Query parse() throws SyntaxError {
		aLogger.info("Query before: " + qstr);
		qstr = Regex.cleanQuery(qstr);
		aLogger.info("Query after: " + qstr);

		// QueryParser parser = new QueryParser(matchVersion, DUMMY_FIELDNAME,
		// analyzer);
		// Query query = parser.parse(eq2);
		// SpanTermQuery query = new SpanTermQuery(new Term(DUMMY_FIELDNAME,
		// "39"));

		/**
		 * PhraseQuery query = new PhraseQuery(); query.setSlop(100);
		 * Collection<String> termsInQuery = Utils.extractElements(queryString);
		 * System.out.println("New query:"); for (String s: termsInQuery) {
		 * System.out.println("Query term: " + s); query.add(new
		 * Term(EQUATION_ELEMENT, s)); }
		 */

		BooleanQuery query = new BooleanQuery();
		Collection<String> termsInQuery = Regex.extractElements(qstr);

		for (String s : termsInQuery) {
			query.add(new BooleanClause(new TermQuery(new Term(
					Constants.MATH_ML_FIELD, s)), Occur.SHOULD));
		}

		/**
		 * Collection<String> termsInQuery = Utils.extractElements(eq2);
		 * ArrayList<SpanQuery> spanQueries = Lists.newArrayList();
		 * 
		 * for (String s: termsInQuery) { spanQueries.add(new SpanTermQuery(new
		 * Term(DUMMY_FIELDNAME, s))); } SpanQuery [] a =
		 * spanQueries.toArray(new SpanQuery [1]); SpanNearQuery query = new
		 * SpanNearQuery(a, 5, false); System.out.println(query.toString());
		 */
		/**
		 * MoreLikeThis mlt = new MoreLikeThis(ireader);
		 * mlt.setAnalyzer(analyzer); Query query = mlt.like(new
		 * StringReader(eq2), EQUATION_ELEMENT);
		 */

		return query;
	}
}
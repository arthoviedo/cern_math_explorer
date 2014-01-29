package cern.ch.mathexplorer.lucene.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
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

import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import cern.ch.mathexplorer.mathematica.MathematicaEngine;
import cern.ch.mathexplorer.mathematica.StructuralFeature;
import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.Regex;

public class MathQueryParser extends QParser {
	static Logger aLogger = java.util.logging.Logger
			.getLogger(MathQueryParser.class.getName());
	
	public MathQueryParser(String qstr, SolrParams localParams, SolrParams params,
			SolrQueryRequest req) {
		super(qstr, localParams, params, req);
	}

	private static SnuggleEngine engine = new SnuggleEngine();
	
	@Override
	public Query parse() throws SyntaxError {
		aLogger.info("Query before: " + qstr);
		qstr = Regex.cleanQuery(qstr);

		if (qstr.contains("FORMAT(latex)")) {
			qstr = qstr.replace("FORMAT(latex)", "");
			if (engine == null) {
				engine = new SnuggleEngine();
			}
			try {
				qstr = texToMathML(qstr);
			} catch (IOException e) {
				e.printStackTrace();
				throw new SyntaxError("Error parsing LaTeX: " + e.getMessage());
			}
		} else {
			qstr = qstr.replace("FORMAT(mathml)", "");
		}
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
		Collection<String> tokenInQuery = Regex.extractElements(qstr);

		for (String mathMLToken : tokenInQuery) {
			query.add(new BooleanClause(new TermQuery(new Term(
					Constants.MATH_NOTATIONAL_FIELD, mathMLToken)), Occur.SHOULD));
		}
		
		if (Constants.USE_MATHEMATICA) {
			List<StructuralFeature> patterns = new ArrayList<>();
			try {
				patterns = MathematicaEngine.getInstance("QUERY").getPatterns(qstr);
				for (StructuralFeature pattern : patterns ) {
					query.add(new BooleanClause(new TermQuery(new Term(
							Constants.MATH_STRUCTURAL_FIELD, pattern.getName())), Occur.SHOULD));
				}
			} catch (Exception e) {
			}
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
	
	public static String texToMathML(String texText) throws IOException {
		
		
		texText.replaceAll("\\$\\$", "$");
		if (!texText.endsWith("$")) {
			texText += "$";
		}
		
		if (!texText.startsWith("$")) {
			texText = "$" + texText;
		}
		
		SnuggleSession session = engine.createSession();
		session.parseInput(new SnuggleInput(texText));
		String result = session.buildXMLString();
		aLogger.info(texText +" --> \n" +result);
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		Scanner s = new Scanner(System.in);
		String eq = s.nextLine();
		while (eq != null) {
			System.out.println(texToMathML(eq));
			eq = s.nextLine();
		}
	}
}